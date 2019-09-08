package com.rahuldshetty.socialconnect.modals;

public class NotificationModal {

    String image,name,status,uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public NotificationModal(String image, String name, String status, String uid, long timestamp) {
        this.image = image;
        this.name = name;
        this.status = status;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public NotificationModal(String image, String name, String status, long timestamp) {
        this.image = image;
        this.name = name;
        this.status = status;
        this.timestamp = timestamp;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public NotificationModal(String image, String name, String status) {
        this.image = image;
        this.name = name;
        this.status = status;
    }

    public NotificationModal(){

    }

}
