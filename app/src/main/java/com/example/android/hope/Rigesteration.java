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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Rigesteration extends AppCompatActivity {

    private EditText nameField, emailField, passwordField, confirmPasswordField;
    private TextView signIn;
    private Button rigesterbtn;
    private ProgressBar regProgress;

    private FirebaseAuth mAuth;

    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageRefrence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rigesteration);

        mAuth = FirebaseAuth.getInstance();


        emailField = (EditText) findViewById(R.id.emailET);
        passwordField = (EditText) findViewById(R.id.passwordET);
        confirmPasswordField=(EditText) findViewById(R.id.confirmPasswordET) ;
        regProgress = (ProgressBar) findViewById(R.id.reg_progress);
        rigesterbtn =(Button) findViewById(R.id.btnsu);

        signIn = (TextView) findViewById(R.id.signin);



        firebaseFirestore = FirebaseFirestore.getInstance();
        storageRefrence = FirebaseStorage.getInstance().getReference();


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToLogin();
            }
        });


        rigesterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String email = emailField.getText().toString();
                String password= passwordField.getText().toString();
                String cPassword = confirmPasswordField.getText().toString();


                if (!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(password) && !TextUtils.isEmpty(cPassword)) {
                        if(password.equals(cPassword)){

                            regProgress.setVisibility(View.VISIBLE);
                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){
                                        sendToLogin();

                                    }else{
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(Rigesteration.this,"Error: " + errorMessage, Toast.LENGTH_LONG).show();

                                    }
                                    regProgress.setVisibility(View.INVISIBLE);
                                }
                            });

                        }else{
                            Toast.makeText(Rigesteration.this,"password doesn't match", Toast.LENGTH_LONG).show();
                        }
                }
                else
                {
                    Toast.makeText(Rigesteration.this,"please fill in required Fields", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //check if the user is already signed in
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToHome();
        }
    }

    private void sendToLogin(){

        Intent loginIntent = new Intent(Rigesteration.this,Login.class);
        startActivity(loginIntent);
        finish();


    }
    private void sendToHome() {
        Intent homeIntent = new Intent(Rigesteration.this,setup.class);
        startActivity(homeIntent);
        finish();



    }//send to home
}//class
