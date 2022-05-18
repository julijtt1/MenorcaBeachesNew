package org.android.menorcabeaches.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.android.menorcabeaches.R;
import org.android.menorcabeaches.fragment.ProfileFragment;
import org.android.menorcabeaches.model.User;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<User> Users;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context context, List<User> Users) {
        this.context=context;
        this.Users=Users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User User = Users.get(position);

        holder.follow.setVisibility(View.VISIBLE);

        holder.userName.setText(User.getUser_id());
        holder.name.setText(User.getName());
        Picasso.get().load(User.getImg_path()).into(holder.imageView);
        isFollowed(User.getId(), holder.follow);

        if (User.getId().equals(firebaseUser.getUid())){
            holder.follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("id", User.getId());
                editor.apply();

                ProfileFragment fragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("user_id", User.getId());
                fragment.setArguments(bundle);
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.follow.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("followed").child(User.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(User.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("followed").child(User.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(User.getId()).child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return Users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userName;
        public TextView name;
        public ImageView imageView;
        public Button follow;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.usernameSearch);
            name = itemView.findViewById(R.id.nameSearch);
            imageView = itemView.findViewById(R.id.image_profileSearch);
            follow = itemView.findViewById(R.id.btn_followSearch);
        }
    }
    private void isFollowed(final String id, final Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("followed");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(id).exists()){
                    button.setText("following");
                } else {
                    button.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}