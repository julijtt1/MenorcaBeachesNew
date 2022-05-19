package org.android.menorcabeaches;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mapa;
    private final LatLng menorca = new LatLng(39.967815654570565, 4.110750066334182);

    Uri image;
    String url;
    String beachLocation;
    String beachId;
    StorageTask task;
    StorageReference sr;

    ImageView close;
    ImageView addedImage;
    TextView post;
    EditText description;
    RatingBar rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        close = findViewById(R.id.close);
        addedImage = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        rating = findViewById(R.id.rating);

        sr = FirebaseStorage.getInstance().getReference("Posts");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPost();
            }
        });

        CropImage.activity()
                .setAspectRatio(1,1)
                .start(PostActivity.this);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadPost(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (image != null & beachLocation != null){
            final StorageReference reference = sr.child(System.currentTimeMillis()
                    + "." + getFileExtension(image));

            task = reference.putFile(image);
            task.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        DatabaseReference referenceB = FirebaseDatabase.getInstance().getReference()
                                .child("Beaches");
                        referenceB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                                        if(ds.child("geo").getValue().equals(beachLocation)) {
                                            beachId = ds.getKey();
                                            break;
                                        }
                                    }
                                    Uri descarga = task.getResult();
                                    url = descarga.toString();

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                                    String postId = reference.push().getKey();

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("id", postId);
                                    hashMap.put("img_path", url);
                                    hashMap.put("description", description.getText().toString());
                                    hashMap.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap.put("beach_id", beachId);
                                    hashMap.put("rating", rating.getRating());

                                    reference.child(postId).setValue(hashMap);

                                    progressDialog.dismiss();

                                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                                    finish();

                                } catch (NullPointerException e){
                                    Log.e("error",e.getMessage()+" "+e.getCause());
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                    } else {
                        Toast.makeText(PostActivity.this, "Could not upload",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(PostActivity.this, "No image selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("aaaaa", String.valueOf(requestCode));
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            image = result.getUri();
            addedImage.setImageURI(image);


        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }
    @Override public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.getUiSettings().setZoomControlsEnabled(false);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(menorca, 10));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Beaches");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //user u = snapshot.getValue(user.class);
                try {

                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        String[] latlong =  ds.child("geo").getValue().toString().split(",");
                        double latitude = Double.parseDouble(latlong[0]);
                        double longitude = Double.parseDouble(latlong[1]);
                        LatLng location = new LatLng(latitude, longitude);
                        mapa.addMarker(new MarkerOptions()
                                .position(location)
                                .title(String.valueOf(ds.child("name").getValue()))
                                .anchor(0.5f, 0.5f));
                    }


                } catch (NullPointerException e){
                    Log.e("error",e.getMessage()+" "+e.getCause());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latLng = marker.getPosition();
                beachLocation = latLng.latitude + ", " + latLng.longitude;
                return false;
            }
        });
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);
        }
    }


    @Override public void onMapClick(LatLng puntPitjat) {

    }
}
