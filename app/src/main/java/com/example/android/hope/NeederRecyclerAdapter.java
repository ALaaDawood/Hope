package com.example.android.hope;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class NeederRecyclerAdapter extends RecyclerView.Adapter<NeederRecyclerAdapter.ViewHolder> {


    public List<NeederPost> blog_list;
    public Context context;
    public FirebaseFirestore firebaseFirestore;
    public Dialog myDialog ;
    private FirebaseAuth firebaseAuth;
    private AlertDialog alertDialog ;




    public NeederRecyclerAdapter(List<NeederPost> blog_list){

        this.blog_list = blog_list;



    }



    @NonNull
    @Override
    public NeederRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.needer_list_item,parent,false);

        context = parent.getContext() ;

        return new NeederRecyclerAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        final String toUserId = blog_list.get(position).ToUserID;
        final  String user_id = blog_list.get(position).getUser_id() ;
        holder.setUserInfo(user_id) ;
        firebaseFirestore =FirebaseFirestore.getInstance() ;
        firebaseAuth =FirebaseAuth.getInstance() ;

        final  String donate_id  = blog_list.get(position).getDonate_id();
        if(!donate_id.equals("empty"))
        {
            holder.hideCard();
        }

        /**firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    String name = task.getResult().getString("name") ;
                    String image = task.getResult().getString("image");
                    holder.setUserData(name,image);
                }

            }
        }); **/

        String current_user = firebaseAuth.getCurrentUser().getUid() ;
        if(!current_user.equals(user_id))
        {
            holder.editPostIc.setVisibility(View.GONE);
            holder.deletePostIc.setVisibility(View.GONE);
        }

        holder.deletePostIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                alertDialog.setTitle("Delete Post ");

                alertDialog.setMessage("Are you sure you want to delete this post?");

                alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });

                alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseFirestore.collection("posts").document(toUserId).delete() ;
                        notifyDataSetChanged();


                    }
                });

                alertDialog.create().show();
            }
        });

        holder.editPostIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent updatePostIntent = new Intent(context, updatePost.class);
                updatePostIntent.putExtra("toUserId", toUserId);
                context.startActivity(updatePostIntent);
            }
        });

        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String image_url = blog_list.get(position).getImage_url();
        String thumbUri = blog_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUri);

        String user = blog_list.get(position).getUser_id() ;

        String city = blog_list.get(position).getCity() ;
        String govern = blog_list.get(position).getGovern() ;
        holder.setUserLocate(city,govern);


        //retreive name and image of the user...
/*        firebaseFirestore.collection("Users").document(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

        firebaseFirestore.collection("Location").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
        });

*/
        long millisecond = blog_list.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("dd/MM/yyyy", new Date(millisecond)).toString();
        holder.setTime(dateString);



    }




    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private View mView;
        private CardView item_contact ;
        private TextView descView;
        private ImageView blogImageView;
        private TextView blogDate;
        private CircleImageView blogUserImage;
        private TextView blogUserName;
        private TextView userLocation;
        private ImageView editPostIc, deletePostIc;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            item_contact = (CardView)itemView.findViewById(R.id.needer_home_post);
            editPostIc = mView.findViewById(R.id.editpostic);
            deletePostIc = mView.findViewById(R.id.deletepostic);



        }

        public void setDescText(String descText) {

            descView = mView.findViewById(R.id.needer_desc);
            descView.setText(descText);
        }

        public void setBlogImage(String downloadUri, String thumbUri){


            blogImageView = mView.findViewById(R.id.needer_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.img_placeholder);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);


        }

        public void setTime(String date){

            blogDate = mView.findViewById(R.id.needer_date);
            blogDate.setText(date);
        }
        //هنا هضيف براميتر للوكيشن برضه
        public void setUserData(final String name, String image){

            blogUserImage = mView.findViewById(R.id.needer_user_image);
            blogUserName = mView.findViewById(R.id.needer_user_name);

            blogUserName.setText(name);


            RequestOptions placeholderOptions = new RequestOptions();
            placeholderOptions.placeholder(R.drawable.profile_placeholder);

            Glide.with(context.getApplicationContext()).applyDefaultRequestOptions(placeholderOptions).load(image).into(blogUserImage);
        }

        public void setUserLocate(String city, String govern){


            userLocation = mView.findViewById(R.id.needer_location);

            userLocation.setText(city + ", " + govern);


        }

        public void setUserInfo(String user_id) {

            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        String name = task.getResult().getString("name");
                        String image =task.getResult().getString("image") ;
                        blogUserImage = mView.findViewById(R.id.needer_user_image);
                        blogUserName = mView.findViewById(R.id.needer_user_name);

                        blogUserName.setText(name);
                        RequestOptions placeholderOptions = new RequestOptions();
                        placeholderOptions.placeholder(R.drawable.profile_placeholder);

                        Glide.with(context.getApplicationContext()).applyDefaultRequestOptions(placeholderOptions).load(image).into(blogUserImage);

                    }

                }
            });
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
            //notifyDataSetChanged();
            //itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

}



