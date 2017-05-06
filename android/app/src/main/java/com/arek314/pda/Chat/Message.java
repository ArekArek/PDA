package com.arek314.pda.Chat;

public class Message {
    private int id;
    private int userId;
    private long date;
    private String sender;
    private String content;

    public Message(int id, int userId, long date, String sender, String content) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.sender = sender;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
