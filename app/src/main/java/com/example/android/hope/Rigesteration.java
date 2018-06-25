package com.example.android.hope;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Rigesteration extends AppCompatActivity {

    private EditText nameField, emailField, passwordField, confirmPasswordField;
    private TextView signIn;
    private Button rigesterbtn;
    private ProgressBar regProgress;
    private Spinner roleSpinner ;

    private FirebaseAuth mAuth;

    private Geocoder geocoder;
    private List<Address> addresses;
    private LocationManager locationManager;
    private LocationListener locationListener;


    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageRefrence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rigesteration);

        mAuth = FirebaseAuth.getInstance();

        nameField = (EditText) findViewById(R.id.nameET);
        emailField = (EditText) findViewById(R.id.emailET);
        passwordField = (EditText) findViewById(R.id.passwordET);
        confirmPasswordField=(EditText) findViewById(R.id.confirmPasswordET) ;
        regProgress = (ProgressBar) findViewById(R.id.reg_progress);
        rigesterbtn =(Button) findViewById(R.id.btnsu);
        roleSpinner = (Spinner) findViewById(R.id.roleSpinner);
        signIn = (TextView) findViewById(R.id.signin);

        // الحاجات اللى هستخدمها فى تحديد الموقع
        geocoder = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageRefrence = FirebaseStorage.getInstance().getReference();

        Spinner spinner = (Spinner) findViewById(R.id.roleSpinner);
       // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.role, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToLogin();
            }
        });


        rigesterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name= nameField.getText().toString();
                String email = emailField.getText().toString();
                String password= passwordField.getText().toString();
                String role = roleSpinner.getSelectedItem().toString();
                String cPassword = confirmPasswordField.getText().toString();


                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)&& !TextUtils.isEmpty(password) && !TextUtils.isEmpty(cPassword)) {
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

        locationListener = new LocationListener() {

            //it called when location is updated , changed (هى دى ال فانكشن اللى بتطلع النتيجة فى الاخر)
            @Override
            public void onLocationChanged(Location location) {

                // احداثيات الطول والعرض بتاعة الموقع ، دول اللى المفروض نخزنهم فى الداتا بيز بتاعة ال سين ان وال بوستات
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                try {

                    // هنا بجيب بقى العنوان اللى بتدل عليه الاحداثيات دى ، انا هنا سبتهم ليكى زيادة عشان لو عايزاهم ولا حاجة
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String address = addresses.get(0).getAddressLine(0); // عشان اجيب العنوان كلها
                    String city = addresses.get(0).getLocality(); // الشارع
                    String govern = addresses.get(0).getAdminArea(); // المدينة
                    String country = addresses.get(0).getCountryName(); //البلد


                    String user_id = mAuth.getCurrentUser().getUid();

                    Map<String, Object> locationMap = new HashMap<>();
                    locationMap.put("city", city);
                    locationMap.put("govern", govern);
                    locationMap.put("address", address);


                    firebaseFirestore.collection("Location").document(user_id).set(locationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Toast.makeText(Rigesteration.this, "The location updated", Toast.LENGTH_LONG).show();

                            }else{

                                String error = task.getException().getMessage();
                                Toast.makeText(Rigesteration.this, "Error :" + error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });



                } catch (IOException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            //checks if network provider is turned off , if turned off go to settings to turn on
            @Override
            public void onProviderDisabled(String s) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Rigesteration.this); //هى المشكلة فى الحتة دى

                alertDialog.setTitle("Network" + " SETTINGS");

                alertDialog.setMessage("Network" + " is not enabled! Want to go to settings menu?");

                alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.create().show();

            }
        };
        configurelocation();

        Intent homeIntent = new Intent(Rigesteration.this,setup.class);
        startActivity(homeIntent);
        finish();



    }//send to home

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configurelocation();
                break;
            default:
                break;
        }

    }


    public void configurelocation() {

        // first check for permissions for internet and gps
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.INTERNET}, 10);
            }
            return;
        }
        // Location Network
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100, locationListener);

    }

}//class
