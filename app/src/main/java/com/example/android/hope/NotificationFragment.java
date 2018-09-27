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
public class NotificationFragment extends Fragment {

    private RecyclerView notification_list_view;
    private List<NotificationPost> notification_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private NotificationRecyclerAdapter notificationRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notification, container, false);

        notification_list = new ArrayList<>();
        notification_list_view = view.findViewById(R.id.notification_list_view);

        firebaseAuth = FirebaseAuth.getInstance();


        notificationRecyclerAdapter = new NotificationRecyclerAdapter(notification_list);
       notification_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        notification_list_view.setAdapter(notificationRecyclerAdapter);
        notification_list_view.setHasFixedSize(true);



        if(firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();

            notification_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                firebaseAuth = FirebaseAuth.getInstance() ;
                String current = firebaseAuth.getCurrentUser().getUid() ;
                Query firstQuery = firebaseFirestore.collection("Users")
                        .document(current)
                        .collection("Notifications")
                        //.orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(10);
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
                                    NotificationPost notificationPost = doc.getDocument().toObject(NotificationPost.class).withId(toUser_id);


                                    if (isFirstPageFirstLoad) {

                                        notification_list.add(notificationPost);
                                    } else {
                                        notification_list.add(notificationPost);
                                    }

                                    notificationRecyclerAdapter.notifyDataSetChanged();

                                }

                            }
                            isFirstPageFirstLoad = false;
                        }
                    }
                });

            }
        }



        return view  ;

    }
    public void loadMorePost() {


        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth = FirebaseAuth.getInstance() ;
            String current = firebaseAuth.getCurrentUser().getUid() ;
            Query nextQuery = firebaseFirestore.collection("Users")
                    .document(current)
                    .collection("Notifications")
                    // .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(10);
            //limit دى يعني انا بقول الصفحه اكتر حاجه تتحمل فيها 5 بس

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {


                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String toUser_id = doc.getDocument().getId();

                                NotificationPost notificationPost = doc.getDocument().toObject(NotificationPost.class).withId(toUser_id);
                                notification_list.add(notificationPost);

                                notificationRecyclerAdapter.notifyDataSetChanged();

                            }

                        }
                    }
                }
            });

        }
    }


}
