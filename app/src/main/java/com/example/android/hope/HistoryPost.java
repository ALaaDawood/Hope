package com.example.android.hope;

import java.util.Date;

public class HistoryPost extends ToUserID {

    public String user_id, donate_id;
    public Date donate_timestamp;


    public HistoryPost() {}

    public HistoryPost(String user_id, String donate_id, Date donate_timestamp) {
        this.user_id = user_id ;
        this.donate_id = donate_id;
        this.donate_timestamp = donate_timestamp;

    }
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getDonate_id() {
        return donate_id;
    }

    public void setDonate_id(String donate_id) {
        this.donate_id = donate_id;
    }
    public Date getDonate_timestamp() {
        return donate_timestamp;
    }

    public void setDonate_timestamp(Date donate_timestamp) {
        this.donate_timestamp = donate_timestamp;
    }


}
