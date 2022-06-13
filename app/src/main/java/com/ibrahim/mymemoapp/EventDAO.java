package com.ibrahim.mymemoapp;

public class EventDAO {
    private int id;
    private String title;
    private String date;
    private String time;
    private String place;
    private int priority;

    public EventDAO(int id, String title, String date, String time, String place, int priority) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.place = place;
        this.priority = priority;
    }
    public EventDAO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
