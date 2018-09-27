package com.example.android.hope;

import java.util.Date;

public class NeederPost extends ToUserID {



    public String user_id, image_url, desc, image_thumb , donate_id , city , govern ;
    public Date timestamp;




    public NeederPost() {}

    public String getDonate_id() {
        return donate_id;
    }

    public void setDonate_id(String donate_id) {
        this.donate_id = donate_id;
    }

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

    public NeederPost(String user_id, String image_url, String desc, String image_thumb, String city , String govern, String donate_id, Date timestamp) {
        this.user_id = user_id ;
        this.image_url = image_url;
        this.desc = desc;
        this.donate_id =donate_id ;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
        this.city = city ;
        this.govern = govern ;

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


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
