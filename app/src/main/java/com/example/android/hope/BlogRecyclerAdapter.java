package com.example.android.hope;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder>{

    public List<BlogPost> blog_list;
    public Context context;
    public FirebaseFirestore firebaseFirestore;
    public Dialog myDialog ;
    private FirebaseAuth firebaseAuth;
    private android.support.v4.app.Fragment showNeederAccount  ;
    private FragmentActivity myContext;



    public BlogRecyclerAdapter(List<BlogPost> blog_list){

        this.blog_list = blog_list;



    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        final ViewHolder vHolder =new ViewHolder(view);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        showNeederAccount = new ShowNeederAccount() ;


        myDialog = new Dialog(parent.getContext());
        myDialog.setContentView(R.layout.dialog_contact);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        vHolder.item_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final TextView dialog_name_tv = (TextView) myDialog.findViewById(R.id.dialog_name);
                final TextView dialog_phone_tv = (TextView) myDialog.findViewById(R.id.dialog_phone);
                final CircleImageView dialog_image_tv = (CircleImageView) myDialog.findViewById(R.id.dialog_image);
                final TextView dialog_location = (TextView) myDialog.findViewById(R.id.dialog_location) ;
                final  Button viewProfile = (Button) myDialog.findViewById(R.id.viewProfile);
                final String contact_id =blog_list.get(vHolder.getAdapterPosition()).getUser_id() ;


                firebaseFirestore.collection("Users").document(contact_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){

                            String userName = task.getResult().getString("name");
                            String userImage = task.getResult().getString("image");
                            String userPhone = task.getResult().getString("phone");
                            dialog_name_tv.setText(userName);
                            dialog_phone_tv.setText(userPhone);
                            RequestOptions placeholderOptions = new RequestOptions();
                            placeholderOptions.placeholder(R.drawable.profile_placeholder);
                            Glide.with(context).applyDefaultRequestOptions(placeholderOptions).load(userImage).into(dialog_image_tv);

                        }else{
                            //Firebase Exception
                        }


                    }
                });
               /* firebaseFirestore.collection("Location").document(contact_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            String city = task.getResult().getString("city") ;
                            String govern = task.getResult().getString("govern") ;
                            dialog_location.setText(city + "," +govern);
                        }

                    }
                }); */
                viewProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myDialog.hide();

                        Bundle bundle = new Bundle();
                        bundle.putString("NAME_KEY",contact_id);
                        //PASS OVER THE BUNDLE TO OUR FRAGMENT
                        ShowNeederAccount myFragment = new ShowNeederAccount();
                        myFragment.setArguments(bundle);
                        sendToNeederProfile(myFragment) ;






                    }
                });
                //Toast.makeText(parent.getContext(),"test click"+String.valueOf(vHolder.getAdapterPosition()),Toast.LENGTH_SHORT).show();
                myDialog.show();
            }
        });


        return vHolder;
    }

    private void sendData(String contact_id) {

        Bundle bundle = new Bundle();
        bundle.putString("NAME_KEY",contact_id);
        //PASS OVER THE BUNDLE TO OUR FRAGMENT
        ShowNeederAccount myFragment = new ShowNeederAccount();
        myFragment.setArguments(bundle);

    }

    private void sendToNeederProfile(android.support.v4.app.Fragment fragment) {
        FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    private void replaceFragment(donorProfileFragment donorProfileFragment) {

     /*   FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, donorProfileFragment);
        fragmentTransaction.commit();*/

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final String toUserId = blog_list.get(position).ToUserID;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        /*String donate_id = blog_list.get(position).getDonate_id() ;
        holder.hideCard(donate_id); */

        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String image_url = blog_list.get(position).getImage_url();
        String thumbUri = blog_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUri);

        final String user_id = blog_list.get(position).getUser_id();

        final  String donate_id  = blog_list.get(position).getDonate_id();
        if(!donate_id.equals("empty"))
        {
            holder.hideCard();
        }

        //retreive name and image of the user...
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");


                    holder.setUserData(userName, userImage);

                }else{
                    //Firebase Exception
                }


            }
        });
        ////////////////////////////////////////////////////////////////

       /* firebaseFirestore.collection("Location").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String city = task.getResult().getString("city");
                    String governrate = task.getResult().getString("govern");

                    holder.setUserLocate(city, governrate);


                }else{

                    //Firebase Exception
                }
            }
        });*/

       String city = blog_list.get(position).getCity() ;
       String governrate = blog_list.get(position).getGovern() ;
       holder.setUserLocate(city,governrate);


        long millisecond = blog_list.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("dd/MM/yyyy", new Date(millisecond)).toString();
        holder.setTime(dateString);

        holder.donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent creditIntent = new Intent(context, Credit.class);
                creditIntent.putExtra("toUserId",  toUserId );
                context.startActivity(creditIntent);

            }
        });

    }



    @Override
    public int getItemCount() {
        return blog_list.size();
    }



        public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        public CardView item_contact ;
        private TextView descView;
        private ImageView blogImageView;
        private TextView blogDate;
        private CircleImageView blogUserImage;
        private TextView blogUserName;
        private TextView userLocation;
        public Button donateButton;
        public  CardView main_blog_post ;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            item_contact = (CardView)itemView.findViewById(R.id.main_blog_post);
            donateButton = mView.findViewById(R.id.donate_button);

        }

        public void setDescText(String descText) {

            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);
        }

        public void setBlogImage(String downloadUri, String thumbUri){


            blogImageView = mView.findViewById(R.id.blog_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.img_placeholder);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);


        }

        public void setTime(String date){

            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);
        }
        //هنا هضيف براميتر للوكيشن برضه
        public void setUserData(final String name, String image){

            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);

            blogUserName.setText(name);
            

            RequestOptions placeholderOptions = new RequestOptions();
            placeholderOptions.placeholder(R.drawable.profile_placeholder);

            Glide.with(context.getApplicationContext()).applyDefaultRequestOptions(placeholderOptions).load(image).into(blogUserImage);
        }

        public void setUserLocate(String city, String govern){


            userLocation = mView.findViewById(R.id.blog_location);

            userLocation.setText(city + ", " + govern);


        }
        public  void hideCard()
        {

            //itemView.setVisibility((!donate_id.equals("empty")) ? View.INVISIBLE : View.VISIBLE);
            //itemView.setLayoutParams(new RelativeLayout.LayoutParams(20, 20));
           /* RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            itemView.setLayoutParams(layoutParams);
//            notifyDataSetChanged();*/
            itemView.setVisibility(View.GONE) ;
            itemView.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
//            notifyDataSetChanged();
            //itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

    }


}
