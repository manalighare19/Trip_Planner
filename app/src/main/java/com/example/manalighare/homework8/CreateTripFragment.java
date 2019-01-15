package com.example.manalighare.homework8;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.view.textclassifier.TextLinks;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.example.manalighare.homework8.util.constants.MAPVIEW_BUNDLE_KEY;


public class CreateTripFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,OnMapReadyCallback,putnearbyMarkers,GoogleMap.OnInfoWindowClickListener {

    private EditText Datepicker;
    private EditText Tripname;
    private FloatingActionButton Save;


    private PlaceAutoCompleteAdapter mplaceAutoCompleteAdapter;
    private AutoCompleteTextView search_city;
    private GoogleApiClient mGoogleApiClient;
    private  static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));

    ArrayList<Point> places_nearby = new ArrayList<>();
    private MapView mMapView;
    private GoogleMap googleMap;
    private Spinner type_spinner;
    private Marker marker;
    private ArrayList<Marker> markerList=new ArrayList<>();
    private ArrayList<Marker> selected_markers=new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private LatLng selected_city=new LatLng(37,-95);
    int PROXIMITY_RADIUS=2000;




    public CreateTripFragment() {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_trip, container, false);
        getActivity().setTitle("Plan Trip");
        Datepicker=(EditText)view.findViewById(R.id.trip_date);
        search_city = (AutoCompleteTextView) view.findViewById(R.id.search_city);
        type_spinner=(Spinner)view.findViewById(R.id.type_spinner);
        mAuth=FirebaseAuth.getInstance();
        myRef=FirebaseDatabase.getInstance().getReference();
        Save=(FloatingActionButton) view.findViewById(R.id.Save_floatingActionButton);
        Tripname=(EditText)view.findViewById(R.id.trip_name);



        mMapView = (MapView) view.findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);



        mGoogleApiClient = new GoogleApiClient
                .Builder(view.getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage((FragmentActivity) view.getContext(), this)
                .build();

        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("US")
                .setTypeFilter(5)
                .build();

        mplaceAutoCompleteAdapter = new PlaceAutoCompleteAdapter(view.getContext(), mGoogleApiClient, LAT_LNG_BOUNDS,autocompleteFilter);
        search_city.setAdapter(mplaceAutoCompleteAdapter);


        type_spinner.setEnabled(false);


        search_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_city=getLocationFromAddress(view.getContext(),String.valueOf(parent.getItemAtPosition(position)));

                LatLngBounds.Builder builder= new LatLngBounds.Builder();
                builder.include(selected_city);
                LatLngBounds bounds=builder.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(),15),900,null);




                type_spinner.setEnabled(true);
                hideKeyboard(getActivity());
                selected_markers.clear();
            }
        });



        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position!=0) {


                    StringBuilder nearbyplaces = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                    nearbyplaces.append("location="+selected_city.latitude+","+selected_city.longitude);
                    nearbyplaces.append("&radius="+PROXIMITY_RADIUS);
                    nearbyplaces.append("&type="+type_spinner.getItemAtPosition(position).toString());
                    nearbyplaces.append("&sensor=true");
                    nearbyplaces.append("&key=AIzaSyDr_6qYhbkBGSqNwRuObW6OniBJ41Gl1CY");

                    new getNearbyPlacesAsync(CreateTripFragment.this,type_spinner.getItemAtPosition(position).toString()).execute(nearbyplaces.toString());


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {



            }
        });



        Datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar myCalendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "MM/dd/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        Datepicker.setText(sdf.format(myCalendar.getTime()));
                    }

                };
                new DatePickerDialog(v.getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                hideKeyboard(getActivity());
            }
        });



        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isEverythingFilled()){

                    String key=myRef.child("Trips").push().getKey();



                    myRef.child("Trips").child(key).child("userID").setValue(mAuth.getCurrentUser().getDisplayName());
                    myRef.child("Trips").child(key).child("trip_name").setValue(Tripname.getText().toString());


                    myRef.child("Trips").child(key).child("trip_date").setValue(Datepicker.getText().toString());
                    myRef.child("Trips").child(key).child("travel_destination").setValue(search_city.getText().toString());



                    for(int i=0;i<selected_markers.size();i++) {

                        myRef.child("Trips").child(key).child("destination "+(i+1)).child("place")
                                .setValue(selected_markers.get(i).getTitle());


                        myRef.child("Trips").child(key).child("destination "+(i+1))
                                .child("lat").setValue(selected_markers.get(i).getPosition().latitude);

                        myRef.child("Trips").child(key).child("destination "+(i+1))
                                .child("long").setValue(selected_markers.get(i).getPosition().longitude);


                    }


                    AllTripsFragment fragment = new AllTripsFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.FragmentContainer, fragment);
                    fragmentTransaction.commit();

                }
            }
        });

        return view;
    }

    private boolean isEverythingFilled() {

        int tmp=0;


        if(Tripname.getText().toString().equals("")){
            tmp=1;
            Tripname.setError("Please enter trip name");
        }
        if(Datepicker.getText().toString().trim().length()==0){
            tmp=1;
            Datepicker.setError("Please enter date for the trip");
        }else {
            Datepicker.setError(null);
        }
        if(search_city.getText().toString().equals("")){
            tmp=1;
            search_city.setError("Please enter city to visit");
        }
        if(type_spinner.getSelectedItemId()==0 && markerList.size()==0){
            tmp=1;
            ((TextView)type_spinner.getSelectedView()).setError("Please choose type of place");
        }
        if(selected_markers.size()==0){
            tmp=1;
            Toast toast=Toast.makeText(getActivity(),"Select some places to add to the trip",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.LEFT,80,860);
            toast.show();

        }




        if(tmp==0){
            return true;
        }else{
            return false;
        }


    }


    public static void hideKeyboard(Activity activity){
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);


        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap=map;
        LatLng latLng=new LatLng(39,98);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.setOnInfoWindowClickListener(this);


    }


    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    @Override
    public void putmarkers(ArrayList<place_attibutes> nearbyPlaces) {



        Toast toast=Toast.makeText(getActivity(),"Click on place description to select/deselect",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.LEFT,70,800);
        toast.show();


        for (int i=0;i<nearbyPlaces.size();i++){

            int decision_variable=0;

            if(markerList.size()>0){

                for(int j=0;j<markerList.size();j++){
                    LatLng tmp=markerList.get(j).getPosition();
                    if((tmp.latitude==Double.parseDouble(nearbyPlaces.get(i).latitude)) && (tmp.longitude==Double.parseDouble(nearbyPlaces.get(i).longitude))){
                        decision_variable=1;
                    }
                }

            }

            if(decision_variable==0) {
                switch (nearbyPlaces.get(i).type_of_place) {

                    case "restaurant":

                        marker = googleMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                                .title(nearbyPlaces.get(i).name_of_place)
                                .snippet("Rating : " + nearbyPlaces.get(i).rating)
                                .position(new LatLng(Double.parseDouble(nearbyPlaces.get(i).latitude), Double.parseDouble(nearbyPlaces.get(i).longitude))));
                        marker.setTag(1);
                        markerList.add(marker);
                        break;
                    case "airport":

                        marker=googleMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .title(nearbyPlaces.get(i).name_of_place)
                                .snippet("Rating : " + nearbyPlaces.get(i).rating)
                                .position(new LatLng(Double.parseDouble(nearbyPlaces.get(i).latitude), Double.parseDouble(nearbyPlaces.get(i).longitude))));
                        marker.setTag(2);
                        markerList.add(marker);
                        break;

                    case "museum":
                        marker=googleMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .title(nearbyPlaces.get(i).name_of_place)
                                .snippet("Rating : " + nearbyPlaces.get(i).rating)
                                .position(new LatLng(Double.parseDouble(nearbyPlaces.get(i).latitude), Double.parseDouble(nearbyPlaces.get(i).longitude))));
                        marker.setTag(3);
                        markerList.add(marker);

                        break;
                    case "city hall":
                        marker=googleMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                                .title(nearbyPlaces.get(i).name_of_place)
                                .snippet("Rating : " + nearbyPlaces.get(i).rating)
                                .position(new LatLng(Double.parseDouble(nearbyPlaces.get(i).latitude), Double.parseDouble(nearbyPlaces.get(i).longitude))));

                        marker.setTag(4);
                        markerList.add(marker);

                        break;

                    case "shopping mall":
                        marker=googleMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                .title(nearbyPlaces.get(i).name_of_place)
                                .snippet("Rating : " + nearbyPlaces.get(i).rating)
                                .position(new LatLng(Double.parseDouble(nearbyPlaces.get(i).latitude), Double.parseDouble(nearbyPlaces.get(i).longitude))));

                        marker.setTag(5);
                        markerList.add(marker);
                        break;


                }
                ;
            }

        }
    }




    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d("marker clicked is : ",""+marker.getTitle().toString());



            int tmp = (Integer) marker.getTag();
            if (tmp == 1 || tmp == 2 || tmp == 3 || tmp == 4 || tmp == 5) {


                if(selected_markers.size()<15) {
                    tmp *= 10;
                    marker.setTag(tmp);
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarkerchecklarge));

                    selected_markers.add(marker);
                }
                else {


                    Toast toast=Toast.makeText(getActivity(),"You can only select only 15 places at a time",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.LEFT,70,900);
                    toast.show();

                }

            } else {

                tmp = tmp / 10;
                marker.setTag(tmp);

                int index = -1;

                Log.d("lat : ", "" + marker.getPosition().latitude);

                for (int i = 0; i < selected_markers.size(); i++) {

                    LatLng latLng = selected_markers.get(i).getPosition();
                    Log.d("long", "" + latLng.longitude);
                    if (latLng.latitude == marker.getPosition().latitude && latLng.longitude == marker.getPosition().longitude) {
                        selected_markers.remove(i);
                    }

                }


                if (tmp == 1) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                }

                if (tmp == 2) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                }
                if (tmp == 3) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
                if (tmp == 4) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                }
                if (tmp == 5) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                }


            }



        Log.d("values in arraylist:",""+selected_markers.size());

        marker.hideInfoWindow();
    }
}
