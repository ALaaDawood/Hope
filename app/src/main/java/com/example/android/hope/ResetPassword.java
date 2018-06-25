package com.example.android.hope;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private Button resetPassSendEmailbtn;
    private EditText resetPassInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        Toolbar mtoolbar = findViewById(R.id.forget_password_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Reset Password");

        resetPassSendEmailbtn = (Button) findViewById(R.id.reset_pass_Email);
        resetPassInput = (EditText) findViewById(R.id.fogetPass_Email);

        resetPassSendEmailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmail = resetPassInput.getText().toString();

                if(TextUtils.isEmpty(userEmail)){

                    Toast.makeText(ResetPassword.this, "Please Enter your valid Email Address First..", Toast.LENGTH_LONG).show();

                }else{

                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Toast.makeText(ResetPassword.this, "Email Sent, Please Check your Email Account to reset your Password", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ResetPassword.this, Login.class));

                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(ResetPassword.this, "Error:"+ error + "Please try again", Toast.LENGTH_LONG).show();


                            }

                        }
                    });
                }

            }
        });
    }
}
