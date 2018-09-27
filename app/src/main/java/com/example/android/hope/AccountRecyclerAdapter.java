package com.example.android.hope;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mima on 6/28/2018.
 */

public class AccountRecyclerAdapter extends RecyclerView.Adapter<AccountRecyclerAdapter.ViewHolder> {

    public List<AccountPost> account_list ;
    public Context context ;
    private FirebaseFirestore firebaseFirestore  ;
    private FirebaseAuth mAuth ;
    public Dialog myDialog ;

    public AccountRecyclerAdapter(List<AccountPost> account_list){

        this.account_list = account_list ;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_list_item,parent,false);

        final AccountRecyclerAdapter.ViewHolder vHolder =new AccountRecyclerAdapter.ViewHolder(view);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        myDialog = new Dialog(parent.getContext());
        myDialog.setContentView(R.layout.dialog_donor);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        vHolder.item_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final TextView dialog_name_tv = (TextView) myDialog.findViewById(R.id.dialog_donor_name);
                final TextView dialog_counter_tv = (TextView) myDialog.findViewById(R.id.dialog_donor_counter);
                final CircleImageView dialog_image_tv = (CircleImageView) myDialog.findViewById(R.id.dialog_donor_image);
                final String donate_id = account_list.get(vHolder.getLayoutPosition()).getDonate_id() ;
                if(!donate_id.equals("empty"))
                {
                    firebaseFirestore.collection("Users").document(donate_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if(task.isSuccessful())
                            {
                                String name = task.getResult().getString("name");
                                String counter = task.getResult().getString("counter");
                                String image = task.getResult().getString("image");



                                dialog_name_tv.setText(name);
                                RequestOptions placeholderOptions = new RequestOptions();
                                placeholderOptions.placeholder(R.drawable.profile_placeholder);
                                Glide.with(context).applyDefaultRequestOptions(placeholderOptions).load(image).into(dialog_image_tv);

                            }

                        }
                    });


                    firebaseFirestore.collection("donationCounter").document(donate_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                String counter = task.getResult().getString("counter");
                                dialog_counter_tv.setText("Donated on '"+counter+"' post");
                            }
                        }
                    });

                    myDialog.show();
                }


                }

                //Toast.makeText(parent.getContext(),"test click"+String.valueOf(vHolder.getAdapterPosition()),Toast.LENGTH_SHORT).show();


        });

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseFirestore = FirebaseFirestore.getInstance() ;
        String desc_data  = account_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String image_url = account_list.get(position).getImage_url();
        String thumbUri = account_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUri);

        String User_id = account_list.get(position).getUser_id() ;
        holder.setUserData(User_id);

        String donor_id = account_list.get(position).getDonate_id() ;

        String city = account_list.get(position).getCity() ;
        String govern = account_list.get(position).getGovern() ;
        holder.setLocation(city,govern);

        /* long donation_date = account_list.get(position).getDonate_timestamp().getTime();
        String DonationdateString = DateFormat.format("dd/MM/yyyy", new Date(donation_date)).toString(); */
        holder.setDonationDate(donor_id);
        holder.setUserLocate(User_id);
        firebaseFirestore = FirebaseFirestore.getInstance() ;
        //retreive name and image of the user...
        mAuth = FirebaseAuth.getInstance() ;
        String current = mAuth.getCurrentUser().getUid() ;
        ////////////////////////////////////////////////////////////////



        ////////////////////////////////////////////////////////////////


        long millisecond = account_list.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("dd/MM/yyyy", new Date(millisecond)).toString();
        holder.setTime(dateString);



    }

    @Override
    public int getItemCount() {
        return account_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView ;
        private TextView UserName ;
        private CircleImageView UserImage ;
        private TextView descView ;
        private TextView accountDate ;
        private ImageView postImageView ;
        private TextView userLocation;
        private TextView DonateUser ;
        private TextView DonationDate ;
        public View item_contact;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView ;
            item_contact = (CardView)itemView.findViewById(R.id.main_account_post);        }

        public  void setDescText(String descText){
            descView = mView.findViewById(R.id.account_desc);
            descView.setText(descText);
        }

        public void setUserData(String User_id) {

            UserName = mView.findViewById(R.id.account_name) ;
            UserImage = mView.findViewById(R.id.account_user_image);
            firebaseFirestore = FirebaseFirestore.getInstance() ;

            firebaseFirestore.collection("Users").document(User_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        String name = task.getResult().getString("name") ;
                        UserName.setText(name);
                        String userImage  = task.getResult().getString("image");
                        RequestOptions placeholderOptions = new RequestOptions();
                        placeholderOptions.placeholder(R.drawable.profile_placeholder);
                        Glide.with(context.getApplicationContext()).applyDefaultRequestOptions(placeholderOptions).load(userImage).into(UserImage);
                    }

                }
            }) ;






        }
        public void setTime(String date){

            accountDate = mView.findViewById(R.id.account_date);
            accountDate.setText(date);
        }

        public void setUserLocate(String User_id) {
            mAuth = FirebaseAuth.getInstance() ;
            String current = mAuth.getCurrentUser().getUid() ;
            firebaseFirestore.collection("Location").document(current).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        String address = task.getResult().getString("address");


                        //userLocation = mView.findViewById(R.id.account_location);
                        //userLocation.setText(address);


                    }else{

                        //Firebase Exception
                    }
                }
            });


        }

        public void setDonationDate(String donor_id) {

            DonateUser = mView.findViewById(R.id.donateUser);
            DonationDate = mView.findViewById(R.id.donationDate) ;
            firebaseFirestore = FirebaseFirestore.getInstance() ;

            if(!donor_id.equals("empty"))
            {

                firebaseFirestore.collection("Users").document(donor_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        String name = task.getResult().getString("name");

                        DonateUser.setText("Donated By "+ name );





                    }
                });

            }



        }


        public void setBlogImage(String image_url, String thumbUri) {

            postImageView = mView.findViewById(R.id.account_post_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.img_placeholder);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(image_url).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(postImageView);
        }

        public void setLocation(String city, String govern) {

            userLocation = mView.findViewById(R.id.account_location);
            userLocation.setText(city + " "+ govern);
        }
    }


}
