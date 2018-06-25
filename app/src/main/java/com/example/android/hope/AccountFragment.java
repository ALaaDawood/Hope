package com.example.android.hope;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private  ImageView Profile_Image ;
    private  TextView Profile_Name  ;
    private  TextView Profile_Email ;
    private  TextView Profile_Phone ;
    public   Context context;
    private  FirebaseAuth mAuth ;
    private  FirebaseFirestore firebaseFirestore ;


    private RecyclerView blog_list_view;
    private List<BlogPost> blog_list;
    private ProfileRecyclerAdapter profileRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;




    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_account, container, false);


        mAuth=FirebaseAuth.getInstance();
        String current_user = mAuth.getCurrentUser().getUid() ;

        Profile_Name = (TextView) view.findViewById(R.id.needer_name);
        Profile_Email = (TextView) view.findViewById(R.id.needer_mail);
        Profile_Phone = (TextView) view.findViewById(R.id.needer_phone);
        Profile_Image = (ImageView) view.findViewById(R.id.needer_img);

/*
        firebaseFirestore.collection("Users").document(current_user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    String name  = task.getResult().getString("name");
                    String mail  = task.getResult().getString("role");
                    String phone = task.getResult().getString("phone");
                    String image = task.getResult().getString("image");


                    Profile_Name.setText(name);
                    Profile_Email.setText(mail);
                    Profile_Phone.setText(phone);
                    RequestOptions placeholderOptions = new RequestOptions();
                    placeholderOptions.placeholder(R.drawable.profile_placeholder);
                    Glide.with(context.getApplicationContext()).applyDefaultRequestOptions(placeholderOptions).load(image).into(Profile_Image);

                }
            }
        });*/




        blog_list = new ArrayList<>();
        blog_list_view = view.findViewById(R.id.blog_list_view);




        profileRecyclerAdapter = new ProfileRecyclerAdapter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        blog_list_view.setAdapter(profileRecyclerAdapter);
        blog_list_view.setHasFixedSize(true);

        if(mAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();

            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {
                        loadMorePost();
                    }

                }
            });

            if (mAuth.getCurrentUser()!=null) {
                firebaseFirestore = FirebaseFirestore.getInstance();

                Query firstQuery = firebaseFirestore.collection("posts").whereEqualTo("user_id",current_user).orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
                //limit ?? ???? ??? ???? ?????? ???? ???? ????? ???? 5 ??

                firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {
                            if (isFirstPageFirstLoad) {

                                lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            }

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                                    if (isFirstPageFirstLoad) {

                                        blog_list.add(blogPost);
                                    } else {
                                        blog_list.add(0, blogPost);
                                    }

                                    profileRecyclerAdapter.notifyDataSetChanged();

                                }

                            }
                            isFirstPageFirstLoad = false;
                        }
                    }
                });

            }
        }


        // Inflate the layout for this fragment
        return view;



        //return inflater.inflate(R.layout.fragment_account, container, false);
    }

    public void loadMorePost() {


        if (mAuth.getCurrentUser() != null) {
            String current_user = mAuth.getCurrentUser().getUid() ;
            Query nextQuery = firebaseFirestore.collection("posts")
                    .whereEqualTo("user_id",current_user)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);
            //limit ?? ???? ??? ???? ?????? ???? ???? ????? ???? 5 ??

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {


                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                                blog_list.add(blogPost);

                                profileRecyclerAdapter.notifyDataSetChanged();

                            }

                        }
                    }
                }
            });

        }
    }


}



