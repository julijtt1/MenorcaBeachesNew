package org.android.menorcabeaches.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.android.menorcabeaches.ExploreActivity;
import org.android.menorcabeaches.R;
import org.android.menorcabeaches.adapter.UserAdapter;
import org.android.menorcabeaches.model.User;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private org.android.menorcabeaches.adapter.UserAdapter UserAdapter;
    private List<User> Users;
    private TextView t;
    private ImageButton explorar;

    EditText cercador;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container,false);

        explorar = v.findViewById(R.id.explore);
        explorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ExploreActivity.class));
            }
        });

        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cercador = v.findViewById(R.id.search_bar);

        Users = new ArrayList<>();
        llegirUsers();
        UserAdapter = new UserAdapter(getContext(), Users);
        recyclerView.setAdapter(UserAdapter);


        cercador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cercarUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;
    }

    private void cercarUsers(String s){
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("User").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //User u = dataSnapshot.getValue(User.class);
                    User u = new User(dataSnapshot.child("id").getValue().toString(), dataSnapshot.child("User").getValue().toString(),
                            dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("img_path").getValue().toString(),
                            dataSnapshot.child("description").getValue().toString());
                    Users.add(u);
                }

                UserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void llegirUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (cercador.getText().toString().equals("")){
                    Users.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        //User u = dataSnapshot.getValue(User.class);
                        User u = new User(dataSnapshot.child("id").getValue().toString(), dataSnapshot.child("user").getValue().toString(),
                                dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("img_path").getValue().toString(),
                                dataSnapshot.child("description").getValue().toString());
                        Users.add(u);
                    }
                    UserAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
