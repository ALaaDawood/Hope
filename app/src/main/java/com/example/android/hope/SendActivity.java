package com.example.android.hope;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SendActivity extends AppCompatActivity {

    LinearLayout l1 ,l2 ;
    private TextView count ;
    Animation uptodown , downtoup ;
    private FirebaseAuth mAuth ;
    private Button goHome ;
    private FirebaseFirestore firebaseFirestore ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        l1 = (LinearLayout)findViewById(R.id.l1);
        l2 = (LinearLayout)findViewById(R.id.l2);
        count = (TextView)findViewById(R.id.userCounter);
        goHome =(Button)findViewById(R.id.goHome);
        uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        l1.setAnimation(uptodown);
        l2.setAnimation(downtoup);
        String current_user = mAuth.getCurrentUser().getUid() ;


        firebaseFirestore.collection("donationCounter").document(current_user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    String counter = task.getResult().getString("counter");
                    count.setText(counter);
                }

            }
        });
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent homeIntent = new Intent(SendActivity.this,HomeDonorActivity.class);
                startActivity(homeIntent);
                finish();
            }
        });




    }
}
