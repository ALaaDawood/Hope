package com.example.android.hope;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {



    private Toolbar newPostToolbar;
    private ImageView newPostImage;
    private EditText newPostDesc;
    private Button newPostBtn;
    private Uri postImageUri = null;
    private ProgressBar newPostProgress;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Geocoder geocoder;
    private List<Address> addresses;


    private StorageReference storageRefrence;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;

    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        storageRefrence = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        current_user_id = firebaseAuth.getCurrentUser().getUid();

        newPostToolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newPostImage = findViewById(R.id.new_post_image);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostBtn = findViewById(R.id.post_btn);
        newPostProgress = findViewById(R.id.new_post_progress);





///////////////////////////////////////////////////////////////////////

        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(NewPostActivity.this);
            }
        });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // الحاجات اللى هستخدمها فى تحديد الموقع
                geocoder = new Geocoder(NewPostActivity.this, Locale.getDefault());
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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

                            String user_id = firebaseAuth.getCurrentUser().getUid();

                            Map<String, Object> locationMap = new HashMap<>();
                            locationMap.put("city", city);
                            locationMap.put("govern", govern);
                            locationMap.put("address", address);


                            firebaseFirestore.collection("Location").document(user_id).set(locationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        final String desc = newPostDesc.getText().toString();

                                        if (!TextUtils.isEmpty(desc) && postImageUri != null) {

                                            newPostProgress.setVisibility(View.VISIBLE);

                                            final String randomName = UUID.randomUUID().toString();

                                            StorageReference filePath = storageRefrence.child("post_images").child(randomName + ".jpg");
                                            filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                                                    final String downloadUri = task.getResult().getDownloadUrl().toString();

                                                    if (task.isSuccessful()) {

                                                        File newImageFile = new File(postImageUri.getPath());

                                                        try {
                                                            compressedImageFile = new Compressor(NewPostActivity.this)
                                                                    .setMaxHeight(100)
                                                                    .setMaxWidth(100)
                                                                    .setQuality(2)
                                                                    .compressToBitmap(newImageFile);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }

                                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                        byte[] thumbData = baos.toByteArray();

                                                        UploadTask uploadTask = storageRefrence.child("post_images/thumbs")
                                                                .child(randomName + ".jpg").putBytes(thumbData);

                                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                                String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();


                                                                Map<String, Object> postMap = new HashMap<>();
                                                                postMap.put("image_url", downloadUri);
                                                                postMap.put("image_thumb", downloadthumbUri);
                                                                postMap.put("desc", desc);
                                                                postMap.put("user_id", current_user_id);
                                                                postMap.put("timestamp", FieldValue.serverTimestamp());


                                                                firebaseFirestore.collection("posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                                                        if (task.isSuccessful()) {

                                                                            Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                                                                            Intent homeIntent = new Intent(NewPostActivity.this, HomeActivity.class);
                                                                            startActivity(homeIntent);
                                                                            finish();

                                                                        } else {


                                                                        }

                                                                        newPostProgress.setVisibility(View.INVISIBLE);

                                                                    }
                                                                });

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });


                                                    } else {

                                                        newPostProgress.setVisibility(View.INVISIBLE);

                                                    }
                                                }
                                            });

                                        }
                                    } else {

                                        String error = task.getException().getMessage();
                                        Toast.makeText(NewPostActivity.this, "Error :" + error, Toast.LENGTH_LONG).show();
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
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewPostActivity.this); //هى المشكلة فى الحتة دى

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




            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }
///////////////////////////////////////////////////////////////////////////

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

}
