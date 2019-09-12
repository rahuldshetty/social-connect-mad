package com.rahuldshetty.socialconnect.modals;

import java.io.Serializable;

public class Post implements Serializable {

    public Post(){

    }

    String pid,uid,timestamp,desc,image,title;
    String likeCount,commentCount;
    String likeStatus;
    String userName,userImage;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Post(String pid, String uid, String timestamp, String desc, String image, String title, String likeCount, String commentCount, String likeStatus, String userName, String userImage, long time) {
        this.pid = pid;
        this.uid = uid;
        this.timestamp = timestamp;
        this.desc = desc;
        this.image = image;
        this.title = title;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.likeStatus = likeStatus;
        this.userName = userName;
        this.userImage = userImage;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Post(String pid, String uid, String timestamp, String desc, String image, String title, String likeCount, String commentCount, String likeStatus, long time) {
        this.pid = pid;
        this.uid = uid;
        this.timestamp = timestamp;
        this.desc = desc;
        this.image = image;
        this.title = title;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.likeStatus = likeStatus;
        this.time = time;
    }

    long time;

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
    }

    public Post(String pid, String uid, String timestamp, String desc, String image, String title, String likeCount, String commentCount, String likeStatus) {
        this.pid = pid;
        this.uid = uid;
        this.timestamp = timestamp;
        this.desc = desc;
        this.image = image;
        this.title = title;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.likeStatus = likeStatus;
    }

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
