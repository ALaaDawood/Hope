package com.example.android.hope;

import com.google.firebase.firestore.Exclude;

import io.reactivex.annotations.NonNull;

public class ToUserID {
    @Exclude
    public String ToUserID;

    public <T extends ToUserID> T withId(@NonNull final String id){
        this.ToUserID = id;
        return (T) this;
    }
}
