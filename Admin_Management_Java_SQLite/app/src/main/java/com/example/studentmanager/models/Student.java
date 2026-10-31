package com.example.studentmanager.models;

public class Student {
    private final long id;
    private final String studentCode;
    private final String fullname;
    private final String email;
    private final String phone;
    private final long accountId;
    private final String username;
    public Student(long id, String studentCode, String fullname, String email, String phone, long accountId, String username) {
        this.id = id;
        this.studentCode = studentCode;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.accountId = accountId;
        this.username = username;
    }
    public long getId() { return id; }
    public String getStudentCode() { return studentCode; }
    public String getFullname() { return fullname; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public long getAccountId() { return accountId; }
    public String getUsername() { return username; }
}
