package org.android.menorcabeaches.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.android.menorcabeaches.R;
import org.android.menorcabeaches.adapter.PostAdapter;
import org.android.menorcabeaches.model.Post;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private org.android.menorcabeaches.adapter.PostAdapter PostAdapter;
    private List<Post> posts;

    private List<String> followerList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_home);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        posts = new ArrayList<>();
        PostAdapter = new PostAdapter(getContext(), posts);
        recyclerView.setAdapter(PostAdapter);

        estaSeguint();

        return view;
    }

    private void estaSeguint(){
        followerList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followerList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    followerList.add(dataSnapshot.getKey());
                }
                readPosts();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        Log.e("asa", reference.toString());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //post post = dataSnapshot.getValue(post.class);
                    Log.e("asa", dataSnapshot.toString());
                    Post post = new Post(dataSnapshot.child("id").getValue().toString(),dataSnapshot.child("img_path").getValue().toString(),
                            dataSnapshot.child("description").getValue().toString(),dataSnapshot.child("user_id").getValue().toString());
                    //for (String id : followerList){
                        //if (post.getUserName().equals(id)){
                            posts.add(post);
                        //}
                    //}
                }
                PostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}