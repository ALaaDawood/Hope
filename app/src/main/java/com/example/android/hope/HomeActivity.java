package com.example.android.hope;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private Toolbar mainToolbar;
    private FloatingActionButton addPostBtn;
    private String current_user_id;

    private BottomNavigationView mainBottomNav;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;
    private NeederFragment neederFragment ;
    private donorProfileFragment donorProfile ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Hope Book");

        if(mAuth.getCurrentUser() != null) {



            mainBottomNav = findViewById(R.id.mainBottomNav);

            //FRAGMENTS
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();
            neederFragment = new NeederFragment() ;
            donorProfile = new donorProfileFragment() ;


            replaceFragment(neederFragment);
            mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.bottom_action_home:
                            replaceFragment(neederFragment);
                            return true;

                        case R.id.bottom_action_notif:
                            replaceFragment(notificationFragment);
                            return true;

                        case R.id.bottom_action_account:

                            replaceFragment(accountFragment);
                            return true;

                        default:
                            return false;
                    }


                }
            });


            addPostBtn = findViewById(R.id.add_post_btn);
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent newPostIntent = new Intent(HomeActivity.this, NewPostActivity.class);
                    startActivity(newPostIntent);
                }
            });

        }



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

                            String current_user = mAuth.getCurrentUser().getUid();
                            Map<String,Object> counterMap = new HashMap<>();
                            counterMap.put("counter", "1");
                            firebaseFirestore.collection("donationCounter").document(current_user).set(counterMap);

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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this); //هى المشكلة فى الحتة دى

                alertDialog.setTitle("LogOut");

                alertDialog.setMessage("Are you sure you want to LogOut?");

                alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });

                alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       logOut();
                    }
                });

                alertDialog.create().show();
                return true;

            case R.id.action_settings_btn:

                Intent settingsIntent = new Intent(HomeActivity.this, SetupNormal.class);
                startActivity(settingsIntent);

                return true;

                default:
                    return false;
        }
    }

    private void logOut() {

        Map<String, Object> tokenMapRemove = new HashMap<>();
        tokenMapRemove.put("token_id", FieldValue.delete());
        firebaseFirestore.collection("Users").document(current_user_id).update(tokenMapRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mAuth.signOut();
                sendToLogin();

            }
        });


    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(HomeActivity.this, Login.class);
        startActivity(loginIntent);
        finish();
    }

    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();


    }

}
