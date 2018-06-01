package com.example.android.hope;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText loginEmailText, loginPassText;
    private Button loginbtn, loginregbtn;
    private ProgressBar loginProgress;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginEmailText =(EditText) findViewById(R.id.login_email);
        loginPassText = (EditText) findViewById(R.id.login_password);
        loginbtn = (Button) findViewById(R.id.login_btn);
        loginregbtn = (Button) findViewById(R.id.login_reg_btn);
        loginProgress =(ProgressBar) findViewById(R.id.login_progress);

        loginregbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent regIntent = new Intent (Login.this, Rigesteration.class);
                startActivity(regIntent);
            }
        });


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String loginEmail= loginEmailText.getText().toString();
                String loginPass = loginPassText.getText().toString();

                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)){
                    loginProgress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                sendToHome();

                            } else{

                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(Login.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                            loginProgress.setVisibility(View.INVISIBLE);

                        }
                    });


                }else {
                    Toast.makeText(Login.this, "please fill in the required feilds", Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        //check user logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            sendToHome();
        }
    }

    private void sendToHome() {
        Intent homeIntent = new Intent(Login.this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }
}
