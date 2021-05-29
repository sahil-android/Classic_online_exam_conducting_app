package com.example.classroomassingment;

public class Classroom {
    private String subject;
    private String teacher;
    private String batch;
    private String course;
    private String classroomCode;

    public Classroom() {
        //no argument constructor needed
    }

    public Classroom(String subject, String teacher, String batch, String course, String classroomCode) {
        this.subject = subject;
        this.teacher = teacher;
        this.batch = batch;
        this.course = course;
        this.classroomCode = classroomCode;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getBatch() {
        return batch;
    }

    public String getCourse() {
        return course;
    }

    public String getClassroomCode() {
        return classroomCode;
    }
}
