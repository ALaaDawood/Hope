package com.example.android.hope;


import java.util.Date;

/**
 * Created by mima on 6/28/2018.
 */

public class AccountPost {

    public String user_id, image_url, desc, image_thumb  ,donate_id , city , govern ;
    public Date timestamp ;


    public AccountPost() {}

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGovern() {
        return govern;
    }

    public void setGovern(String govern) {
        this.govern = govern;
    }

    public AccountPost(String user_id, String image_url, String desc, String donate_id , String city, String govern, String image_thumb , Date timestamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.image_thumb = image_thumb;
        this.donate_id = donate_id ;

        this.timestamp = timestamp;

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }


    public String getDonate_id() {
        return donate_id;
    }

    public void setDonate_id(String donate_id) {
        this.donate_id = donate_id;
    }


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }








}
