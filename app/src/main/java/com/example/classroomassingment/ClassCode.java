package com.example.classroomassingment;

public class ClassCode {
    private String classroomID;
    private String classroomCode;
    private String classroomSubject;
    private String classroomTeacher;
    private String batch;
    private String course;

    public ClassCode() {
        //no argument constructor needed
    }

    public ClassCode(String classroomID, String classroomCode, String classroomSubject, String classroomTeacher, String batch, String course) {
        this.classroomID = classroomID;
        this.classroomCode = classroomCode;
        this.classroomSubject = classroomSubject;
        this.classroomTeacher = classroomTeacher;
        this.batch = batch;
        this.course = course;
    }

    public String getClassroomID() {
        return classroomID;
    }

    public String getClassroomCode() {
        return classroomCode;
    }

    public String getClassroomSubject() {
        return classroomSubject;
    }

    public String getClassroomTeacher() {
        return classroomTeacher;
    }

    public String getBatch() {
        return batch;
    }

    public String getCourse() {
        return course;
    }
}
