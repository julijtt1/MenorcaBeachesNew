package org.android.menorcabeaches.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import org.android.menorcabeaches.model.Post;
import org.android.menorcabeaches.model.User;

import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context context;
    public List<Post> Posts;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> Posts) {
        this.context = context;
        this.Posts = Posts;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post Post = Posts.get(position);

        Picasso.get().load(Post.getImage()).into(holder.image);

        if (Post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(Post.getDescription());
        }

        infouser(holder.image_perfil, holder.userName, holder.user, Post.getUserName());
        setLiked(Post.getId(), holder.like);
        namebreLikes(holder.likes, Post.getId());

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(Post.getId())
                            .child(firebaseUser.getUid())
                            .setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(Post.getId())
                            .child(firebaseUser.getUid())
                            .removeValue();
                }
            }
        });

    }

    private void infouser(final ImageView profileImage, final TextView userName, final TextView user, String iduser){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(iduser);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //user u = snapshot.getValue(user.class);
                try {
                    User u = new User(dataSnapshot.child("id").getValue().toString(), dataSnapshot.child("user").getValue().toString(),
                            dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("image_path").getValue().toString(),
                            dataSnapshot.child("description").getValue().toString());
                    try {
                        Picasso.get().load(u.getImage()).into(profileImage);
                        //new GlideImage(profileImage,u.getimage());
                    } catch (Exception e){
                        Log.e("glide",e.getMessage());
                    }


                    //Picasso.get().load(u.getimage()).into(profileImage);
                    userName.setText(u.getName());
                    Log.e("prova", u.getImage() + "");
                    user.setText(u.getUsername());
                } catch (NullPointerException e){
                    Log.e("error",e.getMessage()+" "+e.getCause());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setLiked(String idPost, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(idPost);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void namebreLikes(final TextView likes, String idPost){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(idPost);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return Posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image, like, comentari, guardar;
        public TextView userName, likes, user, description, commentaris;
        public ImageView image_perfil;

        public ViewHolder(View itemView) {
            super(itemView);

            image_perfil = itemView.findViewById(R.id.image_profile_post);
            image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like_post);
            comentari = itemView.findViewById(R.id.comment_post);
            guardar = itemView.findViewById(R.id.save_post);
            likes = itemView.findViewById(R.id.likes_post);
            user = itemView.findViewById(R.id.user_post);
            description = itemView.findViewById(R.id.description_post);
            commentaris = itemView.findViewById(R.id.comments_post);
            userName = itemView.findViewById(R.id.username_post);
        }
    }


}