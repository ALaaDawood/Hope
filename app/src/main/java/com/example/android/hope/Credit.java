package com.example.android.hope;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class Credit extends AppCompatActivity {
    private CreditCardFormatTextWatcher tv;
    private EditText cost, name, cardnumber, expiry, cvv;
    private String name1, cost1, cardn, cv, date;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    public int  counter ;
    public int counterInter;
    public String counterString ;

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditcard);

        Toolbar creditToolbar = findViewById(R.id.credit_toolbar);
        setSupportActionBar(creditToolbar);
        getSupportActionBar().setTitle("Make a Donation");

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        cost = (EditText) findViewById(R.id.cost);
        name = (EditText) findViewById(R.id.name);
        cardnumber = (EditText) findViewById(R.id.cardnumber);
        expiry = (EditText) findViewById(R.id.Expiry);
        cvv = (EditText) findViewById(R.id.cvv);
        btn = (Button) findViewById(R.id.donate);

        // to make Foramt for CardNumber ( make space every 4 digits)
        tv = new CreditCardFormatTextWatcher(cardnumber);
        cardnumber.addTextChangedListener(tv);

        // to make format for Expiry Date0
        expiry.addTextChangedListener(mDateEntryWatcher);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });

    }

    public void check() {

        cost1 = cost.getText().toString().trim();
        name1 = name.getText().toString().trim();
        cardn = cardnumber.getText().toString().trim();
        cv = cvv.getText().toString().trim();
        date = expiry.getText().toString().trim();

        if (validate()) {

            final String user_id = mAuth.getCurrentUser().getUid();

            Intent intent = getIntent();
            final String toUserId = intent.getStringExtra("toUserId");

            firebaseFirestore.collection("posts").document(toUserId).update("donate_id", user_id);
            firebaseFirestore.collection("posts").document(toUserId).update("donate_timestamp", FieldValue.serverTimestamp());




            String string_to_be_converted_to_MD5 = cardn;
            String MD5_Hash_String = md5(string_to_be_converted_to_MD5);
            //System.out.println(MD5_Hash_String);

            Map<String, Object> donationMap = new HashMap<>();
            donationMap.put("amount", cost1);
            donationMap.put("card_name", name1);
            donationMap.put("card_number", MD5_Hash_String);
            donationMap.put("cvv", cv);
            donationMap.put("expiry", date);
            donationMap.put("user_id", user_id);

            firebaseFirestore.collection("DonationData").add(donationMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {


                    if (task.isSuccessful()) {


                firebaseFirestore.collection("posts").document(toUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful())

                        {
                            Map<String, Object> notificationMap = new HashMap<>();
                            notificationMap.put("amount", cost1);
                            notificationMap.put("from", user_id);

                            String postUserID = task.getResult().getString("user_id");
                            firebaseFirestore.collection("Users/" + postUserID + "/Notifications").add(notificationMap);
                        }

                    }
                }) ;



                        /*firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){

                                    String namefrom = task.getResult().getString("name");

                                    Intent intent = getIntent();
                                    String toUserId = intent.getStringExtra("toUserId");

                                    Map<String, Object> notificationMap = new HashMap<>();
                                    notificationMap.put("name", namefrom);



                                    firebaseFirestore.collection("Notifications").document(toUserId).collection("/notification1").document(user_id).update(notificationMap);

                                }
                            }
                        });*/
                                increaseCounter();
                                Intent sendIntent = new Intent(Credit.this, SendActivity.class);
                                startActivity(sendIntent);

                                finish();



                    } else {

                        String error = task.getException().getMessage();
                        Toast.makeText(Credit.this, "Error :" + error, Toast.LENGTH_LONG).show();
                    }
                }

            });

        }else{
            Toast.makeText(Credit.this, "Failed", Toast.LENGTH_SHORT).show();

            }//this one



    }

    private String md5(String string_to_be_converted_to_md5) {

        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(string_to_be_converted_to_md5.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    // to make conditions to check the entered data

    public boolean validate() {
        boolean valid = true;

        if (cost1.isEmpty() || cost1.equals("0")) {
            cost.setError("please Enter Valid Number");
            valid = false;
        }
        if (name1.isEmpty()|| name1.length()>32) {
            name.setError("Please Enter Valid  Card Name");
            valid = false;
        }

        if (cardn.isEmpty() || cardn.length()!=16) {
            cardnumber.setError("Please Enter Valid  Card Number OR Valid Format");
            valid = false;
        }
        if (cv.isEmpty()|| cv.length() > 3) {
            cvv.setError("please Enter Valid CVV Number");
            valid = false;
        }
        if (date.isEmpty()) {
            expiry.setError("please Enter Valid Expiry Date Format");
            valid = false;
        }

        return valid;

    }

    private void increaseCounter() {

        String current_user = mAuth.getCurrentUser().getUid() ;
        firebaseFirestore.collection("donationCounter").document(current_user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                String counterDatebase = task.getResult().getString("counter");
                counter = Integer.parseInt(counterDatebase);
                counterInter = counter + 1 ;
                counterString = String.valueOf(counterInter) ;

                addToDate(counterString);

            }
        });
    }

    private void addToDate(String counterString) {
        String current_user = mAuth.getCurrentUser().getUid() ;
        Map<String,Object> donationMap = new HashMap<>();
        donationMap.put("counter",counterString);
        firebaseFirestore.collection("donationCounter").document(current_user).set(donationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(Credit.this, "counter was increase", Toast.LENGTH_LONG).show();
                }

            }
        });

    }



    //class from textWatcher to make format for EXpiry Date

    private  TextWatcher mDateEntryWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String working = s.toString();
            boolean isValid = true;
            if (working.length()==2 && before ==0) {
                if (Integer.parseInt(working) < 1 || Integer.parseInt(working)>12) {
                    isValid = false;
                } else {
                    working+="/";
                    expiry.setText(working);
                    expiry.setSelection(working.length());
                }
            }
            else if (working.length()==5 && before ==0) {
                String enteredday = working.substring(3);
                if (Integer.parseInt(enteredday) > 30 ||Integer.parseInt(enteredday) < 1 ) {
                    isValid = false;
                }
            } else if (working.length()!=5) {
                isValid = false;
            }

            if (!isValid) {
                expiry.setError("Enter a valid date: MM/dd");
            }

        }

        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    };

}
