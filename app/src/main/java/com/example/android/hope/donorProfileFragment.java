package com.example.android.hope;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class donorProfileFragment extends Fragment {

    private FirebaseAuth mAuth ;
    private FirebaseFirestore firebaseFirestore ;
    private TextView donorName ;
    private ImageView donorImage ;
    private RatingBar ratingBar ;
    private TextView counterText ;
    public Context context ;
    public int counter ;

    public donorProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donor_profile, container, false);


        mAuth = FirebaseAuth.getInstance() ;
        firebaseFirestore = firebaseFirestore.getInstance() ;
        donorName = (TextView) view.findViewById(R.id.donorName) ;
        counterText = (TextView) view.findViewById(R.id.counterText) ;
        donorImage =(ImageView) view.findViewById(R.id.donorImg);
        ratingBar =(RatingBar) view.findViewById(R.id.ratingBar);

        String current_user = mAuth.getCurrentUser().getUid() ;
        firebaseFirestore.collection("Users").document(current_user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {

                    String donorNameDB = task.getResult().getString("name");
                    String donorImgDB = task.getResult().getString("image");
                    donorName.setText(donorNameDB);
                    RequestOptions placeholderOptions = new RequestOptions();
                    placeholderOptions.placeholder(R.drawable.profile_placeholder);
                    Glide.with(donorProfileFragment.this).applyDefaultRequestOptions(placeholderOptions).load(donorImgDB).into(donorImage);

                }

            }
        });
        firebaseFirestore.collection("donationCounter").document(current_user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    String counterDB = task.getResult().getString("counter");
                    counter = Integer.parseInt(counterDB) - 1 ;
                    counterText.setText("You Donate On '"+String.valueOf(counter)+"' Post");

                    String count = counterDB+".0";
                    Float f= Float.parseFloat(count)/8;
                    ratingBar.setRating(f);





                }
            }
        });


        return view ;

    }

}
