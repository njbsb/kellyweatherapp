package com.fyp.kellyweatherapp.model;

import java.util.Date;

public class Notes {
    private String userID;
    private String notes;
    private Date noteDate;
    // notesID = userID + date.toString()
//    private List<String> eachdayNotes;


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

//    public String getNoteID() {
//        return noteID;
//    }
//
//    public void setNoteID(String noteID) {
//        this.noteID = noteID;
//    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(Date noteDate) {
        this.noteDate = noteDate;
    }

    //    public List<String> getEachdayNotes() {
//        return eachdayNotes;
//    }
//
//    public void setEachdayNotes(List<String> eachdayNotes) {
//        this.eachdayNotes = eachdayNotes;
//    }
}
