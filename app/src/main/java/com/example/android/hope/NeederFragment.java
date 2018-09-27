package com.example.android.hope;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class NeederFragment extends Fragment {


    private RecyclerView needer_list_view;
    private List<NeederPost> blog_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private NeederRecyclerAdapter neederRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;


    public NeederFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_needer_home, container, false);

        blog_list = new ArrayList<>();
        needer_list_view = view.findViewById(R.id.needer_list_view);

        firebaseAuth = FirebaseAuth.getInstance();


        neederRecyclerAdapter = new NeederRecyclerAdapter(blog_list);
        needer_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        needer_list_view.setAdapter(neederRecyclerAdapter);
        needer_list_view.setHasFixedSize(true);



        if(firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();

            needer_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {
                        loadMorePost();
                    }

                }
            });

            if (firebaseAuth.getCurrentUser()!=null) {
                firebaseFirestore = FirebaseFirestore.getInstance();

                Query firstQuery = firebaseFirestore.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
                //limit دى يعني انا بقول الصفحه اكتر حاجه تتحمل فيها 5 بس

                firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {
                            if (isFirstPageFirstLoad) {

                                lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            }

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String toUser_id = doc.getDocument().getId();
                                    NeederPost blogPost = doc.getDocument().toObject(NeederPost.class).withId(toUser_id);


                                    if (isFirstPageFirstLoad) {

                                        blog_list.add(blogPost);
                                    } else {
                                        blog_list.add(0, blogPost);
                                    }

                                    neederRecyclerAdapter.notifyDataSetChanged();

                                }

                            }
                            isFirstPageFirstLoad = false;
                        }
                    }
                });

            }
        }


        // Inflate the layout for this fragment
        return view ;
    }



    public void loadMorePost() {


        if (firebaseAuth.getCurrentUser() != null) {
            Query nextQuery = firebaseFirestore.collection("posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);
            //limit دى يعني انا بقول الصفحه اكتر حاجه تتحمل فيها 5 بس

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {


                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String toUser_id = doc.getDocument().getId();

                                NeederPost blogPost = doc.getDocument().toObject(NeederPost.class).withId(toUser_id);
                                blog_list.add(blogPost);

                                neederRecyclerAdapter.notifyDataSetChanged();

                            }

                        }
                    }
                }
            });

        }
    }



}
