package org.android.menorcabeaches;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.android.menorcabeaches.adapter.PostAdapter;
import org.android.menorcabeaches.model.Post;

import java.util.ArrayList;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private org.android.menorcabeaches.adapter.PostAdapter PostAdapter;
    private List<Post> Posts;

    private List<String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_explore);
        recyclerView = findViewById(R.id.recycler_view_home);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        Posts = new ArrayList<>();
        PostAdapter = new PostAdapter(getApplicationContext(), Posts);
        recyclerView.setAdapter(PostAdapter);

        readUsers();

    }

    private void readUsers(){
        userList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    userList.add(dataSnapshot.getKey());
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

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Posts.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //Post Post = dataSnapshot.getValue(Post.class);
                    Post Post = new Post(dataSnapshot.child("idPost").getValue().toString(),dataSnapshot.child("image").getValue().toString(),
                            dataSnapshot.child("description").getValue().toString(),dataSnapshot.child("user").getValue().toString());
                    for (String id : userList){
                        if (Post.getId().equals(id)){
                            Posts.add(Post);
                        }
                    }
                }

                PostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
