package com.example.android.hope;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {

    private TextView mNotifData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

       /* String dataMessage= getIntent().getStringExtra("message");
        String dataFrom= getIntent().getStringExtra("from_user_id");


        mNotifData = (TextView) findViewById(R.id.notif_text);

        mNotifData.setText("You Recieved " + dataMessage + "| From : " + dataFrom);*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent homeIntent = new Intent(NotificationActivity.this,HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }
}
