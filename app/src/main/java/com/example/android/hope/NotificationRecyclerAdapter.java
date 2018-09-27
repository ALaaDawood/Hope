package com.example.android.hope;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class NotificationRecyclerAdapter  extends RecyclerView.Adapter<NotificationRecyclerAdapter.ViewHolder> {

    public List<NotificationPost> notification_list;
    public Context context;
    public FirebaseFirestore firebaseFirestore;
    public Dialog myDialog;
    private FirebaseAuth firebaseAuth;



    public NotificationRecyclerAdapter(List<NotificationPost> notification_list) {

        this.notification_list = notification_list;

    }
    @NonNull
    @Override
    public NotificationRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_item, parent, false);

        context = parent.getContext();

        return new NotificationRecyclerAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull NotificationRecyclerAdapter.ViewHolder holder, int position) {

        final String toUserId = notification_list.get(position).ToUserID;
        final String amount = notification_list.get(position).getAmount();
        final String from = notification_list.get(position).getFrom();
        holder.setInfo(amount,from);

    }

    @Override
    public int getItemCount() {
        return notification_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CardView item_contact;
        private TextView info;
        private CircleImageView senderImage;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            item_contact = (CardView) itemView.findViewById(R.id.main_notification_post);
        }

        public void setInfo(final String amount, String from) {

            firebaseFirestore =FirebaseFirestore.getInstance() ;
            info = (TextView)mView.findViewById(R.id.textInfo);
            senderImage =(CircleImageView)mView.findViewById(R.id.senderImage);
            firebaseFirestore.collection("Users").document(from).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image") ;
                        info.setText("You Recived "+amount+" L.E From "+name);

                        RequestOptions placeholderOptions = new RequestOptions();
                        placeholderOptions.placeholder(R.drawable.profile_placeholder);
                        Glide.with(context.getApplicationContext()).applyDefaultRequestOptions(placeholderOptions).load(image).into(senderImage);

                    }
                }
            });

        }

    }


}
