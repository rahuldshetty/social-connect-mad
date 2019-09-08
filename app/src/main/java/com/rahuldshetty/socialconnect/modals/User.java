package com.rahuldshetty.socialconnect.modals;

public class User {

    private String bgimage,city,desc,email,image,name,uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User(String bgimage, String city, String desc, String email, String image, String name, String uid) {
        this.bgimage = bgimage;
        this.city = city;
        this.desc = desc;
        this.email = email;
        this.image = image;
        this.name = name;
        this.uid = uid;
    }

    public User(String bgimage, String city, String desc, String email, String image, String name) {
        this.bgimage = bgimage;
        this.city = city;
        this.desc = desc;
        this.email = email;
        this.image = image;
        this.name = name;
    }

    public String getBgimage() {
        return bgimage;
    }

    public void setBgimage(String bgimage) {
        this.bgimage = bgimage;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public User(){

    }

}
