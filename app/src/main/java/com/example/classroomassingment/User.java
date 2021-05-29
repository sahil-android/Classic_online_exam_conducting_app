package com.example.classroomassingment;

public class User {
    private String name;
    private String profession;
    private String emailID;

    public User(){
        // no argument constructor needed
    }

    public User(String name, String profession, String emailID) {
        this.name = name;
        this.profession = profession;
        this.emailID = emailID;
    }

    public String getName() {
        return name;
    }

    public String getProfession() {
        return profession;
    }

    public String getEmailID() {
        return emailID;
    }
}
