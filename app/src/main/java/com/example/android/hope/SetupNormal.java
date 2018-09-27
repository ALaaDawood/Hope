package com.example.android.hope;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupNormal extends AppCompatActivity {

    private CircleImageView setupImage;
    private  Uri mainImageURI = null;
    private EditText setupName ;
    private EditText setupPhone;

    private Button setupBtn ;
    private String user_id;
    private Boolean isChanged = false;
    private ProgressBar setupProgress;
    private StorageReference storageReference ;
    private FirebaseAuth  firebaseAuth ;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_normal);

        Toolbar setupToolbar = findViewById(R.id.setupNormalToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Settings");

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        setupImage =  findViewById(R.id.setup_normal_image);
        setupName  =  findViewById(R.id.setup_normal_name);
        setupPhone = findViewById(R.id.setup_normal_phone);



        setupBtn   =   findViewById(R.id.setup_normal_btn) ;
        setupProgress = findViewById(R.id.setup_normal_progress);

        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        String phone =task.getResult().getString("phone");
                        String role = task.getResult().getString("role");

                        mainImageURI = Uri.parse(image);
                        setupName.setText(name);
                        setupPhone.setText(phone);


                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.profilesetup);

                        Glide.with(SetupNormal.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);



                    }
                }else{

                    String error = task.getException().getMessage();
                    Toast.makeText(SetupNormal.this, "FireStore Retrieve Error :" + error, Toast.LENGTH_LONG).show();

                }

                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);


            }
        });



        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String user_name = setupName.getText().toString();
                final String user_phone = setupPhone.getText().toString();



                if (!TextUtils.isEmpty(user_name) && !TextUtils.isEmpty(user_phone) && mainImageURI != null) {
                    setupProgress.setVisibility(View.VISIBLE);
                    if (isChanged) {

                        user_id = firebaseAuth.getCurrentUser().getUid();

                        StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    storeFirestore(task, user_name, user_phone);


                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(SetupNormal.this, "Image Error :" + error, Toast.LENGTH_LONG).show();

                                    setupProgress.setVisibility(View.INVISIBLE);
                                }

                            }
                        });


                    }
                    else{
                        storeFirestore(null, user_name, user_phone);

                    }

                }
            }
        });


        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if user using Marshamallow version or higher, requires permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(SetupNormal.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(SetupNormal.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetupNormal.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }else{
                        BringImagePicker();


                    }
                }else{
                    BringImagePicker();
                }
            }
        });

    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, final String user_name, final String user_phone) {

        final Uri download_uri;
        if(task!=null) {


            download_uri = task.getResult().getDownloadUrl();
        }else{

            download_uri = mainImageURI;
        }


        firebaseFirestore.collection("Users").document(user_id).update("name", user_name,
                "image", download_uri.toString(), "phone", user_phone).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(SetupNormal.this, "The user settings are updated", Toast.LENGTH_LONG).show();
                    firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                String Role = task.getResult().getString("role") ;
                                if(Role.equals("Donor"))
                                {
                                    Toast.makeText(SetupNormal.this, "The Donor Home", Toast.LENGTH_LONG).show();
                                    Intent mainIntent = new Intent(SetupNormal.this,HomeDonorActivity.class);
                                    startActivity(mainIntent);
                                    finish();
                                }
                                else{
                                    String Current = firebaseAuth.getCurrentUser().getUid() ;
                                    firebaseFirestore.collection("Users").document(Current).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful())
                                            {
                                                String Role = task.getResult().getString("role");
                                                if(Role.equals("Donor"))
                                                {
                                                    Intent mainIntent = new Intent(SetupNormal.this,HomeDonorActivity.class);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }
                                                else
                                                {
                                                    Intent mainIntent = new Intent(SetupNormal.this,HomeActivity.class);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }
                                            }
                                        }
                                    });

                                }
                            }
                        }
                    });


                }else{

                    String error = task.getException().getMessage();
                    Toast.makeText(SetupNormal.this, "FireStore Error :" + error, Toast.LENGTH_LONG).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void BringImagePicker() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupNormal.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();

                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }
}
