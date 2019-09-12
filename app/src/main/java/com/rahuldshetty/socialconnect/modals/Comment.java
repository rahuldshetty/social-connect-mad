package com.rahuldshetty.socialconnect.modals;

public class Comment {

    long timestamp;
    String msg,user,image,uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Comment(long timestamp, String msg, String user, String image, String uid) {
        this.timestamp = timestamp;
        this.msg = msg;
        this.user = user;
        this.image = image;
        this.uid = uid;
    }

    public Comment(){

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Comment(long timestamp, String msg, String user, String image) {
        this.timestamp = timestamp;
        this.msg = msg;
        this.user = user;
        this.image = image;
    }
}
