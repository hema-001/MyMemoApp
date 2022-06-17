package com.ibrahim.mymemoapp;

public class EventDAO {
    private int id;
    private String title;
    private String date;
    private String time;
    private String place;
    private int priority;
    private int notify;
    private String event_img_uri;

    public EventDAO(int id, String title, String date, String time, String place, int priority,
                    int notify, String event_img_uri) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.place = place;
        this.priority = priority;
        this.notify = notify;
        this.event_img_uri = event_img_uri;
    }
    public EventDAO() { }

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

    public int getNotify(){ return this.notify;}

    public void setNotify(int notifyTime){this.notify = notifyTime;}

    public String getEvent_img_uri() { return event_img_uri; }

    public void setEvent_img_uri(String event_img_uri) {
        this.event_img_uri = event_img_uri;
    }
}
