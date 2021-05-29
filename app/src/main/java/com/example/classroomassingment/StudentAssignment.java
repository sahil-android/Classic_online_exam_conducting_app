package com.example.classroomassingment;

import com.google.firebase.Timestamp;

public class StudentAssignment {

    private String studentDownloadurl;
    private String studentName;
    private String pdfName;
    private Boolean completed;
    private Timestamp timeofsubmission;

    public StudentAssignment() {
        //no argument constructor needed
    }

    public StudentAssignment(String studentDownloadurl, String studentName, String pdfName, Boolean completed, Timestamp timeofsubmission) {
        this.studentDownloadurl = studentDownloadurl;
        this.studentName = studentName;
        this.pdfName = pdfName;
        this.completed = completed;
        this.timeofsubmission = timeofsubmission;
    }

    public String getStudentDownloadurl() {
        return studentDownloadurl;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getPdfName() {
        return pdfName;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public Timestamp getTimeofsubmission() {
        return timeofsubmission;
    }
}
