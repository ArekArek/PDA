package com.arek314.pda.db.model;

import java.sql.Timestamp;

public class MessageModel {
    private int id;
    private int userId;
    private Timestamp date;
    private String sender;
    private String message;

    public MessageModel() {
    }

    public MessageModel(int id, int userId, Timestamp date, String sender, String message) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.sender = sender;
        this.message = message;
    }

    public MessageModel(int userId, Timestamp date, String sender, String message) {
        this.userId = userId;
        this.date = date;
        this.sender = sender;
        this.message = message;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageModel that = (MessageModel) o;

        if (id != that.id) return false;
        if (userId != that.userId) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (sender != null ? !sender.equals(that.sender) : that.sender != null) return false;
        return message != null ? message.equals(that.message) : that.message == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + userId;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (sender != null ? sender.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "id=" + id +
                ", userId=" + userId +
                ", date=" + date +
                ", sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
