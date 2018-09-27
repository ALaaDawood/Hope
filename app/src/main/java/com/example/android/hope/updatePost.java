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
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

public class updatePost extends AppCompatActivity {
    private Toolbar updatePostToolbar;
    private ImageView updatePostImage;
    private EditText updatePostDesc;
    private Button updatePostBtn;
    private ProgressBar updateProgress;
    private  Uri mainImageURI = null;
    private Boolean isChanged = false;

    private Uri postImageUri = null;
    private ProgressBar updatePostProgress;
    private StorageReference storageRefrence;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatepost);


        updatePostToolbar = findViewById(R.id.updatePToolbar);
        setSupportActionBar(updatePostToolbar);
        getSupportActionBar().setTitle("Update Post");

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageRefrence = FirebaseStorage.getInstance().getReference();

        updatePostImage = (ImageView) findViewById(R.id.update_post_image);
        updatePostDesc = (EditText) findViewById(R.id.update_post_desc);
        updatePostBtn = (Button) findViewById(R.id.update_post_btn);

        updateProgress = findViewById(R.id.update_progress);

        updateProgress.setVisibility(View.VISIBLE);
        updatePostBtn.setEnabled(false);

        Intent intent = getIntent();
        final String postId = intent.getStringExtra("toUserId");

        firebaseFirestore.collection("posts").document(postId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String desc = task.getResult().getString("desc");
                    String image = task.getResult().getString("image_url");


                    mainImageURI = Uri.parse(image);
                    updatePostDesc.setText(desc);



                    RequestOptions placeholderRequest = new RequestOptions();
                    placeholderRequest.placeholder(R.drawable.img_placeholder);

                    Glide.with(updatePost.this).setDefaultRequestOptions(placeholderRequest).load(image).into(updatePostImage);



                }else{

                    String error = task.getException().getMessage();
                    Toast.makeText(updatePost.this, "FireStore Retrieve Error :" + error, Toast.LENGTH_LONG).show();

                }

                updateProgress.setVisibility(View.INVISIBLE);
                updatePostBtn.setEnabled(true);


            }
        });


        updatePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String desc = updatePostDesc.getText().toString();

                if (!TextUtils.isEmpty(desc) &&  mainImageURI != null) {
//                    updatePostProgress.setVisibility(View.VISIBLE);
                    if (isChanged) {

                        StorageReference image_path = storageRefrence.child("post_images").child(postId + ".jpg");
                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    storeFirestore(task, desc);


                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(updatePost.this, "Image Error :" + error, Toast.LENGTH_LONG).show();

                                    updatePostProgress.setVisibility(View.INVISIBLE);
                                }

                            }
                        });


                    }
                    else{
                        storeFirestore(null, desc);

                    }

                }
            }
        });

        updatePostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if user using Marshamallow version or higher, requires permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(updatePost.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(updatePost.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(updatePost.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }else{
                        BringImagePicker();


                    }
                }else{
                    BringImagePicker();
                }
            }
        });



    }


    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, final String desc) {

        final Uri download_uri;
        if(task!=null) {


            download_uri = task.getResult().getDownloadUrl();
        }else{

            download_uri = mainImageURI;
        }


        Map<String, String> postMap = new HashMap<>();
        postMap.put("desc", desc);
        postMap.put("image_url", download_uri.toString());


        Intent intent = getIntent();
        final String postId = intent.getStringExtra("toUserId");

        firebaseFirestore.collection("posts").document(postId).update("desc",desc).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    firebaseFirestore.collection("posts").document(postId).update("image_url",download_uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(updatePost.this, "The Post is updated", Toast.LENGTH_LONG).show();
                                Intent mainIntent = new Intent(updatePost.this,HomeActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }

                        }
                    });


                }else{

                    String error = task.getException().getMessage();
                    Toast.makeText(updatePost.this, "FireStore Error :" + error, Toast.LENGTH_LONG).show();
                }
//                updatePostProgress.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void BringImagePicker() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(updatePost.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();

                updatePostImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }
}


