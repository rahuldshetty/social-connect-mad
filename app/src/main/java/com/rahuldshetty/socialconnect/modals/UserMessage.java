package com.rahuldshetty.socialconnect.modals;

public class UserMessage {

    String image,name,uid,desc,timestamp;

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
}
