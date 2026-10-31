package com.example.studentmanager.dal;

import com.example.studentmanager.models.Student;
import com.example.studentmanager.models.Subject;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Patterns;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private final Context context;
    public DBHelper(Context c) { this(c, "student_manager_admin_demo.db"); }
    DBHelper(Context c, String name) { super(c, name, null, 1); context = c.getApplicationContext(); }
    @Override public void onConfigure(SQLiteDatabase db) { db.setForeignKeyConstraintsEnabled(true); }
    @Override public void onCreate(SQLiteDatabase db) {
        try (InputStream in = context.getAssets().open("schema.sql")) {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            byte[] buf = new byte[4096]; int count;
            while ((count = in.read(buf)) != -1) out.write(buf, 0, count);
            for (String sql : new String(out.toByteArray(), StandardCharsets.UTF_8).split("-- statement")) db.execSQL(sql.trim());
        } catch (Exception e) { throw new IllegalStateException("Không thể tạo database", e); }
        db.insertOrThrow("Account", null, values("username", "admin", "password", Passwords.hash("Admin@123"), "role", "Admin"));
        long student = db.insertOrThrow("Account", null, values("username", "student", "password", Passwords.hash("Student@123"), "role", "Student"));
        db.insertOrThrow("Student", null, values("studentCode", "SV001", "fullname", "Sinh viên mẫu", "email", "student@example.com", "phone", "0901234567", "accountId", student));
        db.insertOrThrow("Subject", null, values("subjectCode", "MOB101", "subjectName", "Lập trình Android", "description", "Java, SQLite và giao diện Android", "credits", 3));
        db.insertOrThrow("Subject", null, values("subjectCode", "DB101", "subjectName", "Cơ sở dữ liệu", "description", "Thiết kế và truy vấn dữ liệu", "credits", 3));
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { throw new IllegalStateException("Cần bổ sung migration trước khi tăng version"); }
    static ContentValues values(Object... pairs) {
        ContentValues v = new ContentValues();
        for (int i=0; i<pairs.length; i+=2) v.put(pairs[i].toString(), pairs[i+1].toString());
        return v;
    }
    List<Row> query(String sql, String... args) {
        List<Row> rows = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery(sql, args)) {
            while (c.moveToNext()) {
                Row row = new Row();
                for (int i=0; i<c.getColumnCount(); i++) row.put(c.getColumnName(i), c.getString(i));
                rows.add(row);
            }
        }
        return rows;
    }
    private Row first(List<Row> rows) { return rows.isEmpty() ? null : rows.get(0); }
    public Row account(long id) { return first(query("SELECT id,username,role FROM Account WHERE id=?", ""+id)); }
    public Row login(String username, String password) {
        Row a = first(query("SELECT * FROM Account WHERE username=?", username.trim()));
        if (a == null || !Passwords.verify(password, a.s("password"))) return null;
        a.remove("password"); return a;
    }
    private void admin(long actor) {
        Row a = account(actor);
        if (a == null || !a.s("role").equals("Admin")) throw new IllegalArgumentException("Chỉ Admin được thao tác");
    }
    public long studentId(long actor) {
        Row a = account(actor);
        if (a == null || !a.s("role").equals("Student")) throw new IllegalArgumentException("Cần đăng nhập Student");
        Row s = first(query("SELECT id FROM Student WHERE accountId=?", ""+actor));
        if (s == null) throw new IllegalArgumentException("Không tìm thấy sinh viên");
        return s.id("id");
    }
    public List<Row> students(long actor, String key) {
        admin(actor); String k = "%"+key.trim()+"%";
        return query("SELECT s.*,a.username FROM Student s JOIN Account a ON s.accountId=a.id WHERE s.studentCode LIKE ? OR s.fullname LIKE ? OR s.email LIKE ? OR s.phone LIKE ? ORDER BY s.fullname",k,k,k,k);
    }
    public List<Row> subjects(long actor, String key) {
        if (account(actor) == null) throw new IllegalArgumentException("Cần đăng nhập");
        String k="%"+key.trim()+"%";
        return query("SELECT * FROM Subject WHERE subjectCode LIKE ? OR subjectName LIKE ? OR description LIKE ? ORDER BY subjectName",k,k,k);
    }
    static String required(String s, String label) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException("Vui lòng nhập " + label);
        return s.trim();
    }
    public void saveStudent(long actor, long id, String code, String name, String email, String phone, String username, String password) {
        admin(actor);
        code=required(code,"mã sinh viên"); name=required(name,"họ tên"); email=required(email,"email"); phone=required(phone,"số điện thoại"); username=required(username,"tên đăng nhập");
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) throw new IllegalArgumentException("Email không hợp lệ");
        if (!phone.matches("[+0-9 ()-]{8,20}")) throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        if ((id == 0 || !password.isEmpty()) && password.length()<6) throw new IllegalArgumentException("Mật khẩu tối thiểu 6 ký tự");
        SQLiteDatabase db=getWritableDatabase(); db.beginTransaction();
        try {
            long accountId;
            if (id == 0) accountId=db.insertOrThrow("Account",null,values("username",username,"password",Passwords.hash(password),"role","Student"));
            else {
                Row old=first(query("SELECT accountId FROM Student WHERE id=?",""+id));
                if (old == null) throw new IllegalArgumentException("Sinh viên không còn tồn tại");
                accountId=old.id("accountId");
                ContentValues av=values("username",username);
                if (!password.isEmpty()) av.put("password",Passwords.hash(password));
                db.update("Account",av,"id=?",new String[]{""+accountId});
            }
            ContentValues v=values("studentCode",code,"fullname",name,"email",email,"phone",phone,"accountId",accountId);
            if (id == 0) db.insertOrThrow("Student",null,v); else db.update("Student",v,"id=?",new String[]{""+id});
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    public void deleteStudent(long actor,long id) {
        admin(actor);
        getWritableDatabase().execSQL("DELETE FROM Account WHERE id IN (SELECT accountId FROM Student WHERE id=?)",new Object[]{id});
    }
    public void saveSubject(long actor,long id,String code,String name,String description,String credits) {
        admin(actor); int n;
        try { n=Integer.parseInt(credits.trim()); } catch (Exception e) { throw new IllegalArgumentException("Số tín chỉ phải là số nguyên"); }
        if (n<1 || n>30) throw new IllegalArgumentException("Số tín chỉ từ 1 đến 30");
        ContentValues v=values("subjectCode",required(code,"mã môn"),"subjectName",required(name,"tên môn"),"description",description.trim(),"credits",n);
        if (id==0) getWritableDatabase().insertOrThrow("Subject",null,v);
        else if (getWritableDatabase().update("Subject",v,"id=?",new String[]{""+id})!=1) throw new IllegalArgumentException("Môn học không còn tồn tại");
    }
    public void deleteSubject(long actor,long id) {
        admin(actor);
        if (!query("SELECT id FROM StudySchedule WHERE subjectId=? LIMIT 1",""+id).isEmpty()) throw new IllegalArgumentException("Môn đã có lịch học, không thể xóa. Hãy xử lý các lịch liên quan trước.");
        getWritableDatabase().delete("Subject","id=?",new String[]{""+id});
    }
    public List<Student> getStudents(long actor, String key) {
        List<Student> result = new ArrayList<>();
        for (Row r : students(actor, key)) result.add(new Student(r.id("id"), r.s("studentCode"), r.s("fullname"), r.s("email"), r.s("phone"), r.id("accountId"), r.s("username")));
        return result;
    }
    public List<Subject> getSubjects(long actor, String key) {
        admin(actor);
        List<Subject> result = new ArrayList<>();
        for (Row r : subjects(actor, key)) result.add(new Subject(r.id("id"), r.s("subjectCode"), r.s("subjectName"), r.s("description"), Integer.parseInt(r.s("credits"))));
        return result;
    }
    public boolean isAdmin(long actor) {
        Row a = account(actor);
        return a != null && "Admin".equals(a.s("role"));
    }
}
