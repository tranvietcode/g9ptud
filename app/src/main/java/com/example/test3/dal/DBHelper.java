package com.example.test3.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.test3.models.Account;
import com.example.test3.models.Student;
import com.example.test3.models.StudySchedule;
import com.example.test3.models.Subject;
import com.example.test3.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ScheduleManagement.db";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table Account
        String createTableAccount = "CREATE TABLE Account (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL)";
        db.execSQL(createTableAccount);

        // Table Student
        String createTableStudent = "CREATE TABLE Student (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "studentCode TEXT UNIQUE NOT NULL, " +
                "fullname TEXT NOT NULL, " +
                "email TEXT, " +
                "phone TEXT, " +
                "accountId INTEGER, " +
                "FOREIGN KEY(accountId) REFERENCES Account(id) ON DELETE CASCADE)";
        db.execSQL(createTableStudent);

        // Table Subject
        String createTableSubject = "CREATE TABLE Subject (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "subjectCode TEXT UNIQUE NOT NULL, " +
                "subjectName TEXT NOT NULL, " +
                "description TEXT, " +
                "credits INTEGER NOT NULL)";
        db.execSQL(createTableSubject);

        // Table StudySchedule
        String createTableStudySchedule = "CREATE TABLE StudySchedule (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "studentId INTEGER, " +
                "subjectId INTEGER, " +
                "studyDate TEXT NOT NULL, " +
                "startTime TEXT NOT NULL, " +
                "endTime TEXT NOT NULL, " +
                "location TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "FOREIGN KEY(studentId) REFERENCES Student(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(subjectId) REFERENCES Subject(id) ON DELETE CASCADE)";
        db.execSQL(createTableStudySchedule);

        // Seed Sample Data
        seedData(db);
    }

    private void seedData(SQLiteDatabase db) {
        // 1. Admin Accounts
        ContentValues adminAcc = new ContentValues();
        adminAcc.put("username", "admin");
        adminAcc.put("password", "123456");
        adminAcc.put("role", Constants.ROLE_ADMIN);
        db.insert("Account", null, adminAcc);

        // 2. Student Account 1 (student1 / 123456)
        ContentValues studentAcc1 = new ContentValues();
        studentAcc1.put("username", "student1");
        studentAcc1.put("password", "123456");
        studentAcc1.put("role", Constants.ROLE_STUDENT);
        long studentAccId1 = db.insert("Account", null, studentAcc1);

        if (studentAccId1 != -1) {
            ContentValues studentProfile1 = new ContentValues();
            studentProfile1.put("studentCode", "SV001");
            studentProfile1.put("fullname", "Nguyễn Văn A");
            studentProfile1.put("email", "sv001@example.com");
            studentProfile1.put("phone", "0912345678");
            studentProfile1.put("accountId", studentAccId1);
            db.insert("Student", null, studentProfile1);
        }

        // 3. Student Account 2 (student01 / student123)
        ContentValues studentAcc2 = new ContentValues();
        studentAcc2.put("username", "student01");
        studentAcc2.put("password", "student123");
        studentAcc2.put("role", Constants.ROLE_STUDENT);
        long studentAccId2 = db.insert("Account", null, studentAcc2);

        if (studentAccId2 != -1) {
            ContentValues studentProfile2 = new ContentValues();
            studentProfile2.put("studentCode", "SV002");
            studentProfile2.put("fullname", "Trần Thị B");
            studentProfile2.put("email", "sv002@example.com");
            studentProfile2.put("phone", "0987654321");
            studentProfile2.put("accountId", studentAccId2);
            db.insert("Student", null, studentProfile2);
        }

        // 4. Sample Subjects
        ContentValues sub1 = new ContentValues();
        sub1.put("subjectCode", "SUB001");
        sub1.put("subjectName", "Lập trình Android");
        sub1.put("description", "Lập trình ứng dụng di động Android với Java");
        sub1.put("credits", 3);
        long subId1 = db.insert("Subject", null, sub1);

        ContentValues sub2 = new ContentValues();
        sub2.put("subjectCode", "SUB002");
        sub2.put("subjectName", "Cơ sở dữ liệu");
        sub2.put("description", "Thiết kế và quản trị CSDL SQLite");
        sub2.put("credits", 3);
        long subId2 = db.insert("Subject", null, sub2);

        ContentValues sub3 = new ContentValues();
        sub3.put("subjectCode", "SUB003");
        sub3.put("subjectName", "Mạng máy tính");
        sub3.put("description", "Kiến thức mạng máy tính cơ bản");
        sub3.put("credits", 2);
        db.insert("Subject", null, sub3);

        // 5. Sample StudySchedules for Student 1
        if (studentAccId1 != -1 && subId1 != -1) {
            ContentValues sch1 = new ContentValues();
            sch1.put("studentId", 1);
            sch1.put("subjectId", subId1);
            sch1.put("studyDate", "2025-03-20");
            sch1.put("startTime", "07:30");
            sch1.put("endTime", "09:50");
            sch1.put("location", "Phòng 201");
            sch1.put("status", "Confirmed");
            db.insert("StudySchedule", null, sch1);
        }

        if (studentAccId1 != -1 && subId2 != -1) {
            ContentValues sch2 = new ContentValues();
            sch2.put("studentId", 1);
            sch2.put("subjectId", subId2);
            sch2.put("studyDate", "2025-03-20");
            sch2.put("startTime", "10:00");
            sch2.put("endTime", "12:15");
            sch2.put("location", "Phòng 305");
            sch2.put("status", "Pending");
            db.insert("StudySchedule", null, sch2);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS StudySchedule");
        db.execSQL("DROP TABLE IF EXISTS Subject");
        db.execSQL("DROP TABLE IF EXISTS Student");
        db.execSQL("DROP TABLE IF EXISTS Account");
        onCreate(db);
    }

    // ==========================================
    // 1. AUTHENTICATION & USER PROFILE
    // ==========================================

    public Account checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Account account = null;
        Cursor cursor = db.rawQuery("SELECT id, username, password, role FROM Account WHERE username = ? AND password = ?",
                new String[]{username, password});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                account = new Account(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                );
            }
            cursor.close();
        }
        return account;
    }

    public Student getStudentByAccountId(int accountId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Student student = null;
        Cursor cursor = db.rawQuery("SELECT id, studentCode, fullname, email, phone, accountId FROM Student WHERE accountId = ?",
                new String[]{String.valueOf(accountId)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                student = new Student(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5)
                );
            }
            cursor.close();
        }
        return student;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM Account WHERE username = ?", new String[]{username});
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    // ==========================================
    // 2. STUDENT MANAGEMENT (FOR PERSON 2)
    // ==========================================

    public long addStudentWithAccount(String username, String password, String studentCode, String fullname, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long studentId = -1;
        try {
            ContentValues accValues = new ContentValues();
            accValues.put("username", username);
            accValues.put("password", password);
            accValues.put("role", Constants.ROLE_STUDENT);
            long accId = db.insert("Account", null, accValues);

            if (accId != -1) {
                ContentValues stdValues = new ContentValues();
                stdValues.put("studentCode", studentCode);
                stdValues.put("fullname", fullname);
                stdValues.put("email", email);
                stdValues.put("phone", phone);
                stdValues.put("accountId", accId);
                studentId = db.insert("Student", null, stdValues);

                if (studentId != -1) {
                    db.setTransactionSuccessful();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            studentId = -1;
        } finally {
            db.endTransaction();
        }
        return studentId;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, studentCode, fullname, email, phone, accountId FROM Student ORDER BY fullname ASC", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Student student = new Student(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5)
                );
                list.add(student);
            }
            cursor.close();
        }
        return list;
    }

    public Student getStudentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Student student = null;
        Cursor cursor = db.rawQuery("SELECT id, studentCode, fullname, email, phone, accountId FROM Student WHERE id = ?",
                new String[]{String.valueOf(id)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                student = new Student(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5)
                );
            }
            cursor.close();
        }
        return student;
    }

    public boolean updateStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("studentCode", student.getStudentCode());
        values.put("fullname", student.getFullname());
        values.put("email", student.getEmail());
        values.put("phone", student.getPhone());
        int rows = db.update("Student", values, "id = ?", new String[]{String.valueOf(student.getId())});
        return rows > 0;
    }

    public List<Student> searchStudents(String keyword) {
        List<Student> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id, studentCode, fullname, email, phone, accountId FROM Student WHERE studentCode LIKE ? OR fullname LIKE ? ORDER BY fullname ASC";
        String pattern = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(query, new String[]{pattern, pattern});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Student student = new Student(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5)
                );
                list.add(student);
            }
            cursor.close();
        }
        return list;
    }

    public boolean deleteStudent(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Check if student has schedules to prevent orphaned records
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM StudySchedule WHERE studentId = ?", new String[]{String.valueOf(studentId)});
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            if (count > 0) {
                return false; // Cannot delete student with existing schedules
            }
        }
        // Get accountId to delete associated account
        Student student = getStudentById(studentId);
        int rows = db.delete("Student", "id = ?", new String[]{String.valueOf(studentId)});
        if (rows > 0 && student != null) {
            db.delete("Account", "id = ?", new String[]{String.valueOf(student.getAccountId())});
        }
        return rows > 0;
    }

    // ==========================================
    // 3. SUBJECT MANAGEMENT (FOR PERSON 2)
    // ==========================================

    public long addSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("subjectCode", subject.getSubjectCode());
        values.put("subjectName", subject.getSubjectName());
        values.put("description", subject.getDescription());
        values.put("credits", subject.getCredits());
        return db.insert("Subject", null, values);
    }

    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, subjectCode, subjectName, description, credits FROM Subject ORDER BY subjectCode ASC", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Subject subject = new Subject(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                );
                list.add(subject);
            }
            cursor.close();
        }
        return list;
    }

    public Subject getSubjectById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject subject = null;
        Cursor cursor = db.rawQuery("SELECT id, subjectCode, subjectName, description, credits FROM Subject WHERE id = ?",
                new String[]{String.valueOf(id)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                subject = new Subject(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                );
            }
            cursor.close();
        }
        return subject;
    }

    public boolean updateSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("subjectCode", subject.getSubjectCode());
        values.put("subjectName", subject.getSubjectName());
        values.put("description", subject.getDescription());
        values.put("credits", subject.getCredits());
        int rows = db.update("Subject", values, "id = ?", new String[]{String.valueOf(subject.getId())});
        return rows > 0;
    }

    public List<Subject> searchSubjects(String keyword) {
        List<Subject> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id, subjectCode, subjectName, description, credits FROM Subject WHERE subjectCode LIKE ? OR subjectName LIKE ? ORDER BY subjectCode ASC";
        String pattern = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(query, new String[]{pattern, pattern});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Subject subject = new Subject(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                );
                list.add(subject);
            }
            cursor.close();
        }
        return list;
    }

    public boolean deleteSubject(int subjectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Check if subject has schedules to prevent orphaned records
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM StudySchedule WHERE subjectId = ?", new String[]{String.valueOf(subjectId)});
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            if (count > 0) {
                return false; // Cannot delete subject referenced in schedules
            }
        }
        int rows = db.delete("Subject", "id = ?", new String[]{String.valueOf(subjectId)});
        return rows > 0;
    }

    // ==========================================
    // 4. STUDY SCHEDULE MANAGEMENT & CONFLICT CHECK (FOR PERSON 1 & PERSON 3)
    // ==========================================

    /**
     * Checks if a schedule conflicts with existing non-cancelled schedules for the same student and date.
     * Overlap condition: newStart < existingEnd AND newEnd > existingStart.
     * Adjacent schedules like 08:00-10:00 and 10:00-11:00 are allowed.
     *
     * @param studentId Logged-in student ID
     * @param studyDate Date string (yyyy-MM-dd)
     * @param startTime Start time string (HH:mm)
     * @param endTime End time string (HH:mm)
     * @param excludeScheduleId Schedule ID to ignore when editing (-1 for new schedule)
     * @return true if conflict exists, false otherwise
     */
    public boolean isScheduleConflict(int studentId, String studyDate, String startTime, String endTime, int excludeScheduleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM StudySchedule " +
                "WHERE studentId = ? " +
                "AND studyDate = ? " +
                "AND status != '" + Constants.STATUS_CANCELLED + "' " +
                "AND id != ? " +
                "AND (? < endTime AND ? > startTime)";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(studentId),
                studyDate,
                String.valueOf(excludeScheduleId),
                startTime,
                endTime
        });

        boolean conflict = false;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                conflict = (cursor.getInt(0) > 0);
            }
            cursor.close();
        }
        return conflict;
    }

    /**
     * Adds a new study schedule. Performs conflict check at database layer.
     *
     * @return Row ID of inserted schedule, -2 if schedule conflicts, -1 on database error.
     */
    public long addSchedule(StudySchedule schedule) {
        if (!Constants.STATUS_CANCELLED.equalsIgnoreCase(schedule.getStatus())) {
            if (isScheduleConflict(schedule.getStudentId(), schedule.getStudyDate(), schedule.getStartTime(), schedule.getEndTime(), -1)) {
                return -2; // Conflict code
            }
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("studentId", schedule.getStudentId());
        values.put("subjectId", schedule.getSubjectId());
        values.put("studyDate", schedule.getStudyDate());
        values.put("startTime", schedule.getStartTime());
        values.put("endTime", schedule.getEndTime());
        values.put("location", schedule.getLocation());
        values.put("status", schedule.getStatus() != null ? schedule.getStatus() : Constants.STATUS_PENDING);

        return db.insert("StudySchedule", null, values);
    }

    /**
     * Updates an existing study schedule. Performs conflict check at database layer.
     *
     * @return Number of rows updated, -2 if schedule conflicts, -1 on database error.
     */
    public int updateSchedule(StudySchedule schedule) {
        if (!Constants.STATUS_CANCELLED.equalsIgnoreCase(schedule.getStatus())) {
            if (isScheduleConflict(schedule.getStudentId(), schedule.getStudyDate(), schedule.getStartTime(), schedule.getEndTime(), schedule.getId())) {
                return -2; // Conflict code
            }
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("subjectId", schedule.getSubjectId());
        values.put("studyDate", schedule.getStudyDate());
        values.put("startTime", schedule.getStartTime());
        values.put("endTime", schedule.getEndTime());
        values.put("location", schedule.getLocation());
        values.put("status", schedule.getStatus());

        return db.update("StudySchedule", values, "id = ?", new String[]{String.valueOf(schedule.getId())});
    }

    public StudySchedule getScheduleById(int scheduleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        StudySchedule schedule = null;
        String query = "SELECT sc.id, sc.studentId, sc.subjectId, sc.studyDate, sc.startTime, sc.endTime, sc.location, sc.status, sj.subjectName, st.fullname " +
                "FROM StudySchedule sc " +
                "LEFT JOIN Subject sj ON sc.subjectId = sj.id " +
                "LEFT JOIN Student st ON sc.studentId = st.id " +
                "WHERE sc.id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(scheduleId)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                schedule = new StudySchedule(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                );
                schedule.setSubjectName(cursor.getString(8));
                schedule.setStudentName(cursor.getString(9));
            }
            cursor.close();
        }
        return schedule;
    }

    public List<StudySchedule> getSchedulesByStudentId(int studentId) {
        List<StudySchedule> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT sc.id, sc.studentId, sc.subjectId, sc.studyDate, sc.startTime, sc.endTime, sc.location, sc.status, sj.subjectName, st.fullname " +
                "FROM StudySchedule sc " +
                "LEFT JOIN Subject sj ON sc.subjectId = sj.id " +
                "LEFT JOIN Student st ON sc.studentId = st.id " +
                "WHERE sc.studentId = ? " +
                "ORDER BY sc.studyDate ASC, sc.startTime ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                StudySchedule schedule = new StudySchedule(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                );
                schedule.setSubjectName(cursor.getString(8));
                schedule.setStudentName(cursor.getString(9));
                list.add(schedule);
            }
            cursor.close();
        }
        return list;
    }

    public List<StudySchedule> getSchedulesByStudentAndDate(int studentId, String studyDate) {
        List<StudySchedule> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT sc.id, sc.studentId, sc.subjectId, sc.studyDate, sc.startTime, sc.endTime, sc.location, sc.status, sj.subjectName, st.fullname " +
                "FROM StudySchedule sc " +
                "LEFT JOIN Subject sj ON sc.subjectId = sj.id " +
                "LEFT JOIN Student st ON sc.studentId = st.id " +
                "WHERE sc.studentId = ? AND sc.studyDate = ? " +
                "ORDER BY sc.startTime ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId), studyDate});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                StudySchedule schedule = new StudySchedule(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                );
                schedule.setSubjectName(cursor.getString(8));
                schedule.setStudentName(cursor.getString(9));
                list.add(schedule);
            }
            cursor.close();
        }
        return list;
    }

    public List<StudySchedule> searchSchedules(int studentId, String keyword) {
        List<StudySchedule> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT sc.id, sc.studentId, sc.subjectId, sc.studyDate, sc.startTime, sc.endTime, sc.location, sc.status, sj.subjectName, st.fullname " +
                "FROM StudySchedule sc " +
                "LEFT JOIN Subject sj ON sc.subjectId = sj.id " +
                "LEFT JOIN Student st ON sc.studentId = st.id " +
                "WHERE sc.studentId = ? AND (sj.subjectName LIKE ? OR sc.location LIKE ?) " +
                "ORDER BY sc.studyDate ASC, sc.startTime ASC";
        String pattern = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId), pattern, pattern});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                StudySchedule schedule = new StudySchedule(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                );
                schedule.setSubjectName(cursor.getString(8));
                schedule.setStudentName(cursor.getString(9));
                list.add(schedule);
            }
            cursor.close();
        }
        return list;
    }

    public boolean updateScheduleStatus(int scheduleId, int studentId, String newStatus) {
        StudySchedule schedule = getScheduleById(scheduleId);
        if (schedule == null) return false;
        if (studentId > 0 && schedule.getStudentId() != studentId) return false;

        // If changing status back to non-Cancelled (Pending or Confirmed), check conflict!
        if (!Constants.STATUS_CANCELLED.equalsIgnoreCase(newStatus)) {
            if (isScheduleConflict(schedule.getStudentId(), schedule.getStudyDate(), schedule.getStartTime(), schedule.getEndTime(), scheduleId)) {
                return false; // Conflict prevented status change
            }
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        int rows = db.update("StudySchedule", values, "id = ?", new String[]{String.valueOf(scheduleId)});
        return rows > 0;
    }

    public boolean deleteSchedule(int scheduleId, int studentId) {
        StudySchedule schedule = getScheduleById(scheduleId);
        if (schedule == null) return false;
        if (studentId > 0 && schedule.getStudentId() != studentId) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("StudySchedule", "id = ?", new String[]{String.valueOf(scheduleId)});
        return rows > 0;
    }
}
