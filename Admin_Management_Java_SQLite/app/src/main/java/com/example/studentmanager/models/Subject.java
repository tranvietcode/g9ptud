package com.example.studentmanager.models;

public class Subject {
    private final long id;
    private final String subjectCode;
    private final String subjectName;
    private final String description;
    private final int credits;
    public Subject(long id, String subjectCode, String subjectName, String description, int credits) {
        this.id = id;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.description = description;
        this.credits = credits;
    }
    public long getId() { return id; }
    public String getSubjectCode() { return subjectCode; }
    public String getSubjectName() { return subjectName; }
    public String getDescription() { return description; }
    public int getCredits() { return credits; }
}
