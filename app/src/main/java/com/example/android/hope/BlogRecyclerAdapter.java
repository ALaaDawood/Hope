package com.example.android.hope;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder>{

    public List<BlogPost> blog_list;
    public Context context;
    public FirebaseFirestore firebaseFirestore;


    public BlogRecyclerAdapter(List<BlogPost> blog_list){

        this.blog_list = blog_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);

        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String image_url = blog_list.get(position).getImage_url();
        holder.setBlogImage(image_url);

        String user_id = blog_list.get(position).getUser_id();
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

        private TextView descView;
        private ImageView blogImageView;
        private TextView blogDate;
        private CircleImageView blogUserImage;
        private TextView blogUserName;
        private TextView userLocation;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDescText(String descText) {

            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);
        }

        public void setBlogImage(String downloadUri){


            blogImageView = mView.findViewById(R.id.blog_image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.img_placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).into(blogImageView);


        }

        public void setTime(String date){

            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);
        }
        //هنا هضيف براميتر للوكيشن برضه
        public void setUserData(String name, String image){

            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);

            blogUserName.setText(name);

            RequestOptions placeholderOptions = new RequestOptions();
            placeholderOptions.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderOptions).load(image).into(blogUserImage);
        }

        public void setUserLocate(String city, String govern){


            userLocation = mView.findViewById(R.id.blog_location);

            userLocation.setText(city + "," + govern);


        }

    }

}
