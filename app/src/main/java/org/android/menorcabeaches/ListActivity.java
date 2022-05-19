package org.android.menorcabeaches;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap map;
    private final LatLng menorca = new LatLng(39.967815654570565, 4.110750066334182);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(menorca, 10));

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
                        map.addMarker(new MarkerOptions()
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

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setCompassEnabled(true);
        }
    }


    @Override public void onMapClick(LatLng puntPitjat) {

    }
}

