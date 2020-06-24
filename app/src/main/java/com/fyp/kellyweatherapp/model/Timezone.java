package com.fyp.kellyweatherapp.model;

import com.fyp.kellyweatherapp.model.pojo.Daily;

import java.util.ArrayList;

public class Timezone {
    private Integer timezone_offset;

    private ArrayList<Daily> dailyList;

    private static Timezone timezone_singleton;

    public Timezone() {
        timezone_offset = 0;
    }

    public static Timezone getInstance() {
        if (timezone_singleton == null) {
            timezone_singleton = new Timezone();
        }
        return timezone_singleton;
    }

    public Integer getTimezone_offset() {
        return timezone_offset;
    }

    public void setTimezone_offset(Integer timezone_offset) {
        this.timezone_offset = timezone_offset;
    }

    public ArrayList<Daily> getDailyList() {
        return dailyList;
    }

    public void setDailyList(ArrayList<Daily> dailyList) {
        this.dailyList = dailyList;
    }
}
