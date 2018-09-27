package com.example.android.hope;

public class NotificationPost extends ToUserID {


    public String amount, from ;

    public NotificationPost() {}

    public NotificationPost(String amount, String from) {
        this.amount = amount ;
        this.from = from;

    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
