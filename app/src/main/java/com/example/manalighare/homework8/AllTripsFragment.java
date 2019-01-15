package com.example.manalighare.homework8;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllTripsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private RecyclerView RecyclerView;

    private ArrayList<item_in_trip> item_in_trip_arraylist=new ArrayList<>();
    private ArrayList<places_to_visit> places_to_visit=new ArrayList<>();

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerViewAdapter mAdapter;

    public AllTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Home");
        View view= inflater.inflate(R.layout.fragment_all_trips, container, false);

        mAuth=FirebaseAuth.getInstance();
        myRef=FirebaseDatabase.getInstance().getReference();

        myRef=myRef.child("Trips");


        RecyclerView=(RecyclerView)view.findViewById(R.id.Trip_Recycler_View);


        RecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecyclerViewAdapter(item_in_trip_arraylist);
        RecyclerView.setAdapter(mAdapter);


        places_to_visit.clear();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                item_in_trip_arraylist.clear();
                Log.d("demo","keys are");
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {


                        String key=dataSnapshot1.getKey();
                        Log.d("key is ",""+key);
                    Log.d("demo","Keys inside are ");

                        myRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot_inside) {

                                Log.d("key inside is ",""+dataSnapshot_inside.getKey());
                                item_in_trip tmp2 = new item_in_trip();
                                long i = dataSnapshot_inside.getChildrenCount() - 4;


                                for (int j = 0; j < i; j++) {
                                    places_to_visit tmp = new places_to_visit();
                                    tmp.place_name = dataSnapshot_inside.child("destination " + (j + 1)).child("place").getValue().toString();
                                    tmp.lat = dataSnapshot_inside.child("destination " + (j + 1)).child("lat").getValue().toString();
                                    tmp.lng = dataSnapshot_inside.child("destination " + (j + 1)).child("long").getValue().toString();

                                    places_to_visit.add(tmp);

                                }

                                tmp2.travel_date = dataSnapshot_inside.child("trip_date").getValue().toString();
                                tmp2.trip_id = dataSnapshot_inside.getKey().toString();
                                tmp2.trip_Name = dataSnapshot_inside.child("trip_name").getValue().toString();
                                tmp2.travel_destination = dataSnapshot_inside.child("travel_destination").getValue().toString();
                                tmp2.user_name = dataSnapshot_inside.child("userID").getValue().toString();
                                tmp2.places_to_visit.addAll(places_to_visit);

                                item_in_trip_arraylist.add(tmp2);

                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }



                }




            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        return view;
    }





}
