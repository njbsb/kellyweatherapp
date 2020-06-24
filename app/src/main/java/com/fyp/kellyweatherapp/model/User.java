package com.fyp.kellyweatherapp.model;

import java.util.List;

public class User {

    private static User userSingleton;

    private String name;
    private String userID;
    private String email;
    private List<Notes> notesList;

    public static User getInstance() {
        if(userSingleton == null) {
            userSingleton = new User();
        }
        return userSingleton;
    }

    public String getName() {
        if(this.name == null) {
            this.name = "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Notes> getNotesList() {
        return notesList;
    }

    public void setNotesList(List<Notes> notesList) {
        this.notesList = notesList;
    }
}
