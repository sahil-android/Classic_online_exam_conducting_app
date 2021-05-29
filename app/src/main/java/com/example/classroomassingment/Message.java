package com.example.classroomassingment;

import com.google.firebase.Timestamp;

public class Message {

    private String message;
    private String nameofauthor;
    private Timestamp messgaedate;

    public Message() {
    }

    public Message(String message, String nameofauthor, Timestamp messgaedate) {
        this.message = message;
        this.nameofauthor = nameofauthor;
        this.messgaedate = messgaedate;
    }

    public String getMessage() {
        return message;
    }

    public String getNameofauthor() {
        return nameofauthor;
    }

    public Timestamp getMessgaedate() {
        return messgaedate;
    }
}
