package com.example.android.hope;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SendActivity extends AppCompatActivity {

    private TextView user_id_view;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        user_id_view = (TextView) findViewById(R.id.user_id_view);

        mUserId = getIntent().getStringExtra("user_id");
        user_id_view.setText(mUserId);
    }
}
