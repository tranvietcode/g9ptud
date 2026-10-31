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

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudySchedule.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng
    public static final String TABLE_ACCOUNT = "Account";
    public static final String TABLE_STUDENT = "Student";
    public static final String TABLE_SUBJECT = "Subject";
    public static final String TABLE_SCHEDULE = "StudySchedule";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng Account
        String createAccountTable = "CREATE TABLE " + TABLE_ACCOUNT + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL)";
        db.execSQL(createAccountTable);

        // 2. Tạo bảng Student
        String createStudentTable = "CREATE TABLE " + TABLE_STUDENT + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "studentCode TEXT UNIQUE NOT NULL, " +
                "fullname TEXT NOT NULL, " +
                "email TEXT, " +
                "phone TEXT, " +
                "accountId INTEGER, " +
                "FOREIGN KEY(accountId) REFERENCES " + TABLE_ACCOUNT + "(id))";
        db.execSQL(createStudentTable);

        // 3. Tạo bảng Subject
        String createSubjectTable = "CREATE TABLE " + TABLE_SUBJECT + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "subjectCode TEXT UNIQUE NOT NULL, " +
                "subjectName TEXT NOT NULL, " +
                "description TEXT, " +
                "credits INTEGER)";
        db.execSQL(createSubjectTable);

        // 4. Tạo bảng StudySchedule
        String createScheduleTable = "CREATE TABLE " + TABLE_SCHEDULE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "studentId INTEGER, " +
                "subjectId INTEGER, " +
                "studyDate TEXT NOT NULL, " +
                "startTime TEXT NOT NULL, " +
                "endTime TEXT NOT NULL, " +
                "location TEXT, " +
                "status TEXT NOT NULL, " +
                "FOREIGN KEY(studentId) REFERENCES " + TABLE_STUDENT + "(id), " +
                "FOREIGN KEY(subjectId) REFERENCES " + TABLE_SUBJECT + "(id))";
        db.execSQL(createScheduleTable);

        // Chèn dữ liệu mẫu ban đầu
        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Tạo tài khoản Admin & Student mẫu
        db.execSQL("INSERT INTO " + TABLE_ACCOUNT + " (username, password, role) VALUES ('admin', '123456', 'Admin')");
        db.execSQL("INSERT INTO " + TABLE_ACCOUNT + " (username, password, role) VALUES ('student1', '123456', 'Student')");

        // Tạo thông tin Sinh viên
        db.execSQL("INSERT INTO " + TABLE_STUDENT + " (studentCode, fullname, email, phone, accountId) VALUES ('SV001', 'Nguyen Van A', 'a@gmail.com', '0912345678', 2)");

        // Tạo thông tin Môn học mẫu
        db.execSQL("INSERT INTO " + TABLE_SUBJECT + " (subjectCode, subjectName, description, credits) VALUES ('PRM392', 'Lap trinh Android', 'Android cho nguoi moi', 3)");
        db.execSQL("INSERT INTO " + TABLE_SUBJECT + " (subjectCode, subjectName, description, credits) VALUES ('SWP391', 'Software Development', 'Du an phan mem', 3)");

        // Tạo Lịch học mẫu cho SV1
        db.execSQL("INSERT INTO " + TABLE_SCHEDULE + " (studentId, subjectId, studyDate, startTime, endTime, location, status) VALUES (1, 1, '2025-03-20', '07:30', '09:50', 'Phong 201', 'Confirmed')");
        db.execSQL("INSERT INTO " + TABLE_SCHEDULE + " (studentId, subjectId, studyDate, startTime, endTime, location, status) VALUES (1, 2, '2025-03-20', '10:00', '12:15', 'Phong 305', 'Pending')");
        db.execSQL("INSERT INTO " + TABLE_SCHEDULE + " (studentId, subjectId, studyDate, startTime, endTime, location, status) VALUES (1, 1, '2025-03-22', '13:30', '15:50', 'Lab 1', 'Confirmed')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        onCreate(db);
    }

    // =========================================================================
    // HÀM CHO NGƯỜI 3: LẤY DANH SÁCH LỊCH HỌC CỦA SINH VIÊN (KÈM JOIN TÊN MÔN)
    // =========================================================================
    public List<StudySchedule> getSchedulesByStudentId(int studentId) {
        List<StudySchedule> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT sc.*, sj.subjectName, sj.subjectCode " +
                "FROM " + TABLE_SCHEDULE + " sc " +
                "INNER JOIN " + TABLE_SUBJECT + " sj ON sc.subjectId = sj.id " +
                "WHERE sc.studentId = ? " +
                "ORDER BY sc.studyDate ASC, sc.startTime ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
        if (cursor.moveToFirst()) {
            do {
                StudySchedule schedule = parseScheduleFromCursor(cursor);
                list.add(schedule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // =========================================================================
    // HÀM CHO NGƯỜI 3: LẤY LỊCH HỌC THEO NGÀY CỤ THỂ (DÀNH CHO CALENDAR VIEW)
    // =========================================================================
    public List<StudySchedule> getSchedulesByStudentAndDate(int studentId, String studyDate) {
        List<StudySchedule> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT sc.*, sj.subjectName, sj.subjectCode " +
                "FROM " + TABLE_SCHEDULE + " sc " +
                "INNER JOIN " + TABLE_SUBJECT + " sj ON sc.subjectId = sj.id " +
                "WHERE sc.studentId = ? AND sc.studyDate = ? " +
                "ORDER BY sc.startTime ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId), studyDate});
        if (cursor.moveToFirst()) {
            do {
                StudySchedule schedule = parseScheduleFromCursor(cursor);
                list.add(schedule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // =========================================================================
    // HÀM CHO NGƯỜI 3: TÌM KIẾM LỊCH HỌC THEO TỪ KHÓA
    // =========================================================================
    public List<StudySchedule> searchSchedules(int studentId, String keyword) {
        List<StudySchedule> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT sc.*, sj.subjectName, sj.subjectCode " +
                "FROM " + TABLE_SCHEDULE + " sc " +
                "INNER JOIN " + TABLE_SUBJECT + " sj ON sc.subjectId = sj.id " +
                "WHERE sc.studentId = ? AND (sj.subjectName LIKE ? OR sc.location LIKE ? OR sj.subjectCode LIKE ?) " +
                "ORDER BY sc.studyDate ASC";

        String keyPattern = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId), keyPattern, keyPattern, keyPattern});
        if (cursor.moveToFirst()) {
            do {
                StudySchedule schedule = parseScheduleFromCursor(cursor);
                list.add(schedule);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // =========================================================================
    // HÀM CHO NGƯỜI 3: CẬP NHẬT TRẠNG THÁI LỊCH (Pending, Confirmed, Cancelled)
    // =========================================================================
    public boolean updateScheduleStatus(int scheduleId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);

        int rows = db.update(TABLE_SCHEDULE, values, "id = ?", new String[]{String.valueOf(scheduleId)});
        return rows > 0;
    }

    // =========================================================================
    // HÀM CHO NGƯỜI 3: XÓA LỊCH HỌC
    // =========================================================================
    public boolean deleteSchedule(int scheduleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_SCHEDULE, scheduleId + " = id", null);
        return rows > 0;
    }

    private StudySchedule parseScheduleFromCursor(Cursor cursor) {
        StudySchedule schedule = new StudySchedule();
        schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        schedule.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow("studentId")));
        schedule.setSubjectId(cursor.getInt(cursor.getColumnIndexOrThrow("subjectId")));
        schedule.setStudyDate(cursor.getString(cursor.getColumnIndexOrThrow("studyDate")));
        schedule.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow("startTime")));
        schedule.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow("endTime")));
        schedule.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
        schedule.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
        schedule.setSubjectName(cursor.getString(cursor.getColumnIndexOrThrow("subjectName")));
        schedule.setSubjectCode(cursor.getString(cursor.getColumnIndexOrThrow("subjectCode")));
        return schedule;
    }
}
