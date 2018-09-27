package com.example.android.hope;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class ShowNeederAccount extends Fragment {

    private ImageView Profile_Image ;
    private TextView Profile_Name  ;
    private  TextView Profile_Phone ;
    public Context context;
    private FirebaseAuth mAuth ;
    private FirebaseFirestore firebaseFirestore ;




    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private RecyclerView account_list_view ;
    private List<AccountPost> account_list ;
    private AccountRecyclerAdapter accountRecyclerAdapter ;






    public ShowNeederAccount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_show_needer_account, container, false);


        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore = firebaseFirestore.getInstance() ;
        //Bundle bundle = new Bundle() ;
        //final String current_user = String.valueOf(this.getArguments().getInt("NAME_KEY"));
        final String current_user = this.getArguments().getString("NAME_KEY");
        //final String current_user = mAuth.getCurrentUser().getUid() ;
        Profile_Name = view.findViewById(R.id.donorName);
        Profile_Phone = view.findViewById(R.id.donorPhone);
        Profile_Image = view.findViewById(R.id.donorImg);


        account_list = new ArrayList<>() ;
        account_list_view = view.findViewById(R.id.account_list_view);
        accountRecyclerAdapter = new AccountRecyclerAdapter(account_list);
        account_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        account_list_view.setAdapter(accountRecyclerAdapter);
        account_list_view.setHasFixedSize(true);



/**************************************************************************************************/


        firebaseFirestore.collection("Users").document(current_user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String Name = task.getResult().getString("name") ;
                    String Phone = task.getResult().getString("phone");
                    String Image = task.getResult().getString("image");
                    Profile_Name.setText(Name);
                    Profile_Phone.setText(Phone);

                    // mainImageURI = Uri.parse(Image);


                    RequestOptions placeholderOptions = new RequestOptions();
                    placeholderOptions.placeholder(R.drawable.profile_placeholder);

                    Glide.with(ShowNeederAccount.this).applyDefaultRequestOptions(placeholderOptions).load(Image).into(Profile_Image);



                }
            }
        });



/***************************************************************************************/




        if(mAuth.getCurrentUser() != null)

        {
            firebaseFirestore = FirebaseFirestore.getInstance();
            account_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {
                        loadMorePost(current_user);
                    }

                }
            });

            if(mAuth.getCurrentUser() != null){


                final Query firstQuery = firebaseFirestore.collection("posts")
                        .whereEqualTo("user_id",current_user)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(3);
                firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if ( documentSnapshots != null ) {
                            //account_list.clear();
                            if (isFirstPageFirstLoad) {

                                lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            }

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    AccountPost accountPost = doc.getDocument().toObject(AccountPost.class);
                                    if (isFirstPageFirstLoad) {

                                        account_list.add(accountPost);
                                    } else {
                                        account_list.add(0, accountPost);
                                    }

                                    accountRecyclerAdapter.notifyDataSetChanged();

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

    public void loadMorePost(String current_user) {


        if (mAuth.getCurrentUser() != null) {

            Query nextQuery = firebaseFirestore.collection("posts")
                    .whereEqualTo("user_id",current_user)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);
            //limit دى يعني انا بقول الصفحه اكتر حاجه تتحمل فيها 5 بس

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (documentSnapshots != null) {


                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                AccountPost accountPost = doc.getDocument().toObject(AccountPost.class);
                                account_list.add(accountPost);

                                accountRecyclerAdapter.notifyDataSetChanged();

                            }

                        }
                    }
                }
            });

        }
    }




}
