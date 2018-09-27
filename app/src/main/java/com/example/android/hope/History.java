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
public class History extends Fragment {

    private RecyclerView history_list_view;
    private List<HistoryPost> blog_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private HistoryRecyclerAdapter historyRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    public History() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        blog_list = new ArrayList<>();
        history_list_view = view.findViewById(R.id.history_list_view);

        firebaseAuth = FirebaseAuth.getInstance();


        historyRecyclerAdapter = new HistoryRecyclerAdapter(blog_list);
        history_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        history_list_view.setAdapter(historyRecyclerAdapter);
        history_list_view.setHasFixedSize(true);



        if(firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();

            history_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
               final String Current = firebaseAuth.getCurrentUser().getUid() ;

                Query firstQuery = firebaseFirestore.collection("posts")
                        .whereEqualTo("donate_id",Current)
                        .orderBy("donate_timestamp", Query.Direction.DESCENDING)
                        .limit(10);
                //limit دى يعني انا بقول الصفحه اكتر حاجه تتحمل فيها 5 بس

                firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (documentSnapshots != null) {
                            if (isFirstPageFirstLoad) {

                                lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            }

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String toUser_id = doc.getDocument().getId();
                                    HistoryPost historyPost = doc.getDocument().toObject(HistoryPost.class).withId(toUser_id);


                                    if (isFirstPageFirstLoad) {

                                        blog_list.add(historyPost);
                                    } else {
                                        blog_list.add(0, historyPost);
                                    }

                                    historyRecyclerAdapter.notifyDataSetChanged();

                                }

                            }
                            isFirstPageFirstLoad = false;
                        }
                    }
                });

            }
        }


        return view ;
    }

    public void loadMorePost() {


        if (firebaseAuth.getCurrentUser() != null) {
            String Current = firebaseAuth.getCurrentUser().getUid();
            Query nextQuery = firebaseFirestore.collection("posts")
                    .whereEqualTo("donate_id",Current)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(10);
            //limit دى يعني انا بقول الصفحه اكتر حاجه تتحمل فيها 5 بس

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (documentSnapshots != null) {


                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String toUser_id = doc.getDocument().getId();

                                HistoryPost historyPost = doc.getDocument().toObject(HistoryPost.class).withId(toUser_id);
                                blog_list.add(historyPost);

                                historyRecyclerAdapter.notifyDataSetChanged();

                            }

                        }
                    }
                }
            });

        }
    }

}
