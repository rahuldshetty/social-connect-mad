package com.rahuldshetty.socialconnect.modals;

public class Message {

    public Message(){

    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Message(String msg, String status, long timestamp) {
        this.msg = msg;
        this.status = status;
        this.timestamp = timestamp;
    }

    String msg,status;
    long timestamp;



}
