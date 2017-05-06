package com.arek314.pda.Chat.MessagesList;


public class MessageRowBean {
    private int id;
    private int userId;
    private String date;
    private String sender;
    private String content;

    public MessageRowBean() {
    }

    public MessageRowBean(int id, int userId, String date, String sender, String content) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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
