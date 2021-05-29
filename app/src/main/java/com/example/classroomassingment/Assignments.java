package com.example.classroomassingment;

import android.content.Intent;

import com.google.firebase.Timestamp;

public class Assignments {

    private String assignmentName;
    private Timestamp assignmentDate;
    private Timestamp submissionDate;
    private String commentAssign;
    private String assignmentTopic;
    private String pdfName;
    private String downloadURL;

    public Assignments() {
        //no argument constructor needed
    }

    public Assignments(String assignmentName, Timestamp assignmentDate, Timestamp submissionDate,
                       String commentAssign, String assignmentTopic, String downloadURL, String pdfName) {
        this.assignmentName = assignmentName;
        this.assignmentDate = assignmentDate;
        this.submissionDate = submissionDate;
        this.commentAssign = commentAssign;
        this.assignmentTopic = assignmentTopic;
        this.downloadURL = downloadURL;
        this.pdfName = pdfName;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public Timestamp getAssignmentDate() {
        return assignmentDate;
    }

    public Timestamp getSubmissionDate() {
        return submissionDate;
    }

    public String getCommentAssign() {
        return commentAssign;
    }

    public String getAssignmentTopic() {
        return assignmentTopic;
    }

    public String getPdfName() {
        return pdfName;
    }
}
