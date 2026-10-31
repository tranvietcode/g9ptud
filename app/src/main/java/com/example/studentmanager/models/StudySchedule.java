package com.example.studentmanager.models;

public class StudySchedule {
    private final long id;
    private final long studentId;
    private final long subjectId;
    private final String studyDate;
    private final String startTime;
    private final String endTime;
    private final String location;
    private final String status;
    public StudySchedule(long id, long studentId, long subjectId, String studyDate, String startTime, String endTime, String location, String status) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.studyDate = studyDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.status = status;
    }
    public long getId() { return id; }
    public long getStudentId() { return studentId; }
    public long getSubjectId() { return subjectId; }
    public String getStudyDate() { return studyDate; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
}
