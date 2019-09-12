package com.rahuldshetty.socialconnect.modals;

import java.util.Comparator;

public class UserMessage implements Comparable<UserMessage> {

    String image,name,uid,desc,timestamp;
    long time;

    public UserMessage(String image, String name, String uid, String desc, String timestamp, long time) {
        this.image = image;
        this.name = name;
        this.uid = uid;
        this.desc = desc;
        this.timestamp = timestamp;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public UserMessage(){

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public UserMessage(String image, String name, String uid, String desc, String timestamp) {
        this.image = image;
        this.name = name;
        this.uid = uid;
        this.desc = desc;
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(UserMessage o) {
        return (int)(o.getTime() - this.getTime());
    }
}
