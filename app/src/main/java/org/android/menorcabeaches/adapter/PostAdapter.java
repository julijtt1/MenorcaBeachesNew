package org.android.menorcabeaches.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
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

import de.hdodenhof.circleimageview.CircleImageView;


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
        final Post post = Posts.get(position);

        Picasso.get().load(post.getImg_path()).into(holder.image);

        if (post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        infoUser(holder.profile_image, holder.userName, holder.user, post.getUser_id());
        setLiked(post.getId(), holder.like);
        setSaved(post.getId(), holder.save);
        setRating(post.getId(), Double.parseDouble(post.getRating().toString()), holder.rating);
        infoBeach(post.getBeach_id(), holder.beachName);
        setAddedToList(post.getBeach_id(), holder.add_list_post);
        numberOfLikes(holder.likes, post.getId());

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getId())
                            .child(firebaseUser.getUid())
                            .setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getId())
                            .child(firebaseUser.getUid())
                            .removeValue();
                }
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("SavedPosts")
                            .child(firebaseUser.getUid())
                            .child(post.getId())
                            .setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("SavedPosts")
                            .child(firebaseUser.getUid())
                            .child(post.getId())
                            .removeValue();
                }
            }
        });

        holder.add_list_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.add_list_post.getTag().equals("addToList")){
                    FirebaseDatabase.getInstance().getReference().child("Lists")
                            .child(firebaseUser.getUid())
                            .child(post.getBeach_id())
                            .setValue(post.getBeach_id());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Lists")
                            .child(firebaseUser.getUid())
                            .child(post.getBeach_id())
                            .removeValue();
                }
            }
        });

    }

    private void setRating(String idPost, double rating, RatingBar ratingBar) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .child(idPost);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("rating").exists()){
                    ratingBar.setRating((float) rating);
                } else {
                    ratingBar.setRating(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void infoBeach(String beachId, final TextView beachName){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Beaches").child(beachId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("name").exists()){
                    beachName.setText(dataSnapshot.child("name").getValue().toString());
                } else {
                    beachName.setText(" ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void infoUser(final ImageView profileImage, final TextView userName, final TextView user, String iduser){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(iduser);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //user u = snapshot.getValue(user.class);
                try {
                    User u = new User(dataSnapshot.child("id").getValue().toString(), dataSnapshot.child("user").getValue().toString(),
                            dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("img_path").getValue().toString(),
                            dataSnapshot.child("description").getValue().toString());
                    try {
                        Picasso.get().load(u.getImg_path()).into(profileImage);
                    } catch (Exception e){
                        Log.e("Picasso",e.getMessage());
                    }


                    Picasso.get().load(u.getImg_path()).into(profileImage);
                    userName.setText(u.getName());
                    user.setText(u.getUser_id());
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

    private void setSaved(String idPost, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("SavedPosts")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(idPost).exists()){
                    imageView.setImageResource(R.drawable.ic_saved);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAddedToList(String idBeach, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Lists")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(idBeach).exists()){
                    imageView.setImageResource(R.drawable.ic_added_list);
                    imageView.setTag("addedToList");
                } else {
                    imageView.setImageResource(R.drawable.ic_add_list);
                    imageView.setTag("addToList");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void numberOfLikes(final TextView likes, String idPost){
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

        public ImageView image, like, comment, save, add_list_post;
        public TextView userName, likes, user, description, comments, beachName;
        public CircleImageView profile_image;
        public RatingBar rating;

        public ViewHolder(View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.image_profile_post);
            image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like_post);
            comment = itemView.findViewById(R.id.comment_post);
            save = itemView.findViewById(R.id.save_post);
            add_list_post = itemView.findViewById(R.id.add_list_post);
            likes = itemView.findViewById(R.id.likes_post);
            user = itemView.findViewById(R.id.user_post);
            description = itemView.findViewById(R.id.description_post);
            comments = itemView.findViewById(R.id.comments_post);
            userName = itemView.findViewById(R.id.username_post);
            beachName = itemView.findViewById(R.id.beach_post);
            rating = itemView.findViewById(R.id.rating);
        }
    }


}