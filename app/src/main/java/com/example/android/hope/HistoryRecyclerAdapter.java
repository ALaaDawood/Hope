package com.example.android.hope;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder> {

    public List<HistoryPost> blog_list;
    public Context context;
    public FirebaseFirestore firebaseFirestore;
    public Dialog myDialog;
    private FirebaseAuth firebaseAuth;
    private android.support.v4.app.Fragment showNeederAccount;
    private FragmentActivity myContext;


    public HistoryRecyclerAdapter(List<HistoryPost> blog_list) {
        this.blog_list = blog_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.histroy_list_item, parent, false);

        context = parent.getContext();

        return new HistoryRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRecyclerAdapter.ViewHolder holder, int position) {

        firebaseFirestore = FirebaseFirestore.getInstance() ;
        String donate_id = blog_list.get(position).getDonate_id() ;
        String user_id = blog_list.get(position).getUser_id() ;
        long millisecond = blog_list.get(position).getDonate_timestamp().getTime();
        String dateString = DateFormat.format("dd/MM/yyyy", new Date(millisecond)).toString();
        holder.setText(user_id,dateString);

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView data;



        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setText(String user_id, final String dateString) {

            data = mView.findViewById(R.id.textInfo);

            firebaseFirestore = FirebaseFirestore.getInstance() ;
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        String Name = task.getResult().getString("name");
                        data.setText("You Donated To '"+Name+"' On "+dateString);

                    }
                }
            });
        }
    }//close of viewholder
}