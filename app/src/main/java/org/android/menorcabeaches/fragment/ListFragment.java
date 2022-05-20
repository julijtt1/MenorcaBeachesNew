package org.android.menorcabeaches.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.android.menorcabeaches.R;

public class ListFragment extends Fragment implements
        OnMapReadyCallback, GoogleMap.OnMapClickListener{

private GoogleMap map;
private final LatLng menorca = new LatLng(39.967815654570565, 4.110750066334182);
private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_list, container, false);

    SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
    FragmentTransaction fragmentTransaction =
            getChildFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.map, mMapFragment);
    fragmentTransaction.commit();
    mMapFragment.getMapAsync(this);

    return view;
}

    @Override public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(menorca, 10));

        DatabaseReference referenceList = FirebaseDatabase.getInstance().getReference("Lists").child(firebaseUser.getUid());;

        referenceList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    DatabaseReference referenceBeaches;
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        referenceBeaches = FirebaseDatabase.getInstance().getReference("Beaches").child(ds.getValue().toString());
                        referenceBeaches.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String[] latlong =  snapshot.child("geo").getValue().toString().split(",");

                                double latitude = Double.parseDouble(latlong[0]);
                                double longitude = Double.parseDouble(latlong[1]);
                                LatLng location = new LatLng(latitude, longitude);
                                map.addMarker(new MarkerOptions()
                                        .position(location)
                                        .title(String.valueOf(ds.child("name").getValue()))
                                        .anchor(0.5f, 0.5f));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }


                } catch (NullPointerException e){
                    Log.e("error",e.getMessage()+" "+e.getCause());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setCompassEnabled(true);
        }
    }


    @Override public void onMapClick(LatLng puntPitjat) {

    }
}

