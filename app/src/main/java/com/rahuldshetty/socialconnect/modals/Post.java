package com.rahuldshetty.socialconnect.modals;

public class Post {

    public Post(){

    }

    String pid,uid,timestamp,desc,image,title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Post(String pid, String uid, String timestamp, String desc, String image, String title) {
        this.pid = pid;
        this.uid = uid;
        this.timestamp = timestamp;
        this.desc = desc;
        this.image = image;
        this.title = title;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Post(String pid, String uid, String timestamp, String desc, String image) {
        this.pid = pid;
        this.uid = uid;
        this.timestamp = timestamp;
        this.desc = desc;
        this.image = image;
    }
}
