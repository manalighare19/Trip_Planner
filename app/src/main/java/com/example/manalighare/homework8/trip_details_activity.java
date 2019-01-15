package com.example.manalighare.homework8;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.manalighare.homework8.util.constants.MAPVIEW_BUNDLE_KEY;

public class trip_details_activity extends AppCompatActivity implements OnMapReadyCallback {

    private DatabaseReference myRef;

    private RecyclerView place_recycler_view;
    private GoogleMap googleMap;
    private MapView mymapView;
    private MarkerOptions markerOptions=new MarkerOptions();


    ArrayList<places_to_visit> list_of_places=new ArrayList<>();


    private RecyclerView.LayoutManager mLayoutManager;
    private placesRecyclerViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details_activity);


        myRef=FirebaseDatabase.getInstance().getReference();
        place_recycler_view=(RecyclerView)findViewById(R.id.places_recycler);
        mymapView=(MapView)findViewById(R.id.mymapView);


        place_recycler_view.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        place_recycler_view.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new placesRecyclerViewAdapter(list_of_places);
        place_recycler_view.setAdapter(mAdapter);

        String key=getIntent().getExtras().getString("trip_id");


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mymapView.onCreate(mapViewBundle);

        mymapView.getMapAsync(this);


        myRef=myRef.child("Trips");

        myRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot_inside) {

                setTitle(dataSnapshot_inside.child("trip_name").getValue().toString());
                Log.d("key inside is ",""+dataSnapshot_inside.getKey());
                item_in_trip tmp2 = new item_in_trip();
                long i = dataSnapshot_inside.getChildrenCount() - 4;


                for (int j = 0; j < i; j++) {
                    places_to_visit tmp = new places_to_visit();
                    tmp.place_name = dataSnapshot_inside.child("destination " + (j + 1)).child("place").getValue().toString();
                    tmp.lat = dataSnapshot_inside.child("destination " + (j + 1)).child("lat").getValue().toString();
                    tmp.lng = dataSnapshot_inside.child("destination " + (j + 1)).child("long").getValue().toString();

                    list_of_places.add(tmp);

                }



                mAdapter.notifyDataSetChanged();


                LatLng position_of_point;
                LatLngBounds.Builder builder= new LatLngBounds.Builder();

                for(int count=0;count<list_of_places.size();count++) {

                    position_of_point=new LatLng(Double.parseDouble(list_of_places.get(count).lat), Double.parseDouble(list_of_places.get(count).lng));
                    builder.include(position_of_point);


                    googleMap.addMarker(new MarkerOptions()
                            .title(list_of_places.get(count).place_name)
                            .position(position_of_point));


                }

                LatLngBounds latLngBounds=builder.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(),10),900,null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mymapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mymapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mymapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mymapView.onStop();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;

        Log.d("size is :",""+list_of_places.size());
        LatLng latLng=new LatLng(39,98);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }
}
