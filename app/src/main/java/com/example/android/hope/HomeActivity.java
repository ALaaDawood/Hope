package com.example.android.hope;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private Toolbar mainToolbar;
    private FloatingActionButton addPostBtn;
    private String current_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Hope Book");



        addPostBtn = findViewById(R.id.add_post_btn);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newPostIntent = new Intent(HomeActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser== null){

            sendToLogin();

        }else{

            current_user_id = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().exists()){

                            Intent setupIntent = new Intent(HomeActivity.this, setup.class);
                            startActivity(setupIntent);
                            finish();
                        }

                    }else{

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(HomeActivity.this,"Error: " + errorMessage, Toast.LENGTH_LONG).show();


                    }
                }
            });
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())){
            case R.id.action_logout_btn:
                logOut();
                return true;

            case R.id.action_settings_btn:

                Intent settingsIntent = new Intent(HomeActivity.this, setup.class);
                startActivity(settingsIntent);

                return true;

                default:
                    return false;
        }
    }

    private void logOut() {

        mAuth.signOut();
        sendToLogin();

    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(HomeActivity.this, Login.class);
        startActivity(loginIntent);
        finish();
    }
}
