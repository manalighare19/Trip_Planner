package com.example.manalighare.homework8;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Trip_Home extends AppCompatActivity {



    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {


                case R.id.navigation_home:


                    if(getSupportFragmentManager().findFragmentByTag("AllTripsFragment") != null) {
                        //if the fragment exists, show it.
                        getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("AllTripsFragment")).commit();
                    } else {
                        //if the fragment does not exist, add it to fragment manager.
                        getSupportFragmentManager().beginTransaction().add(R.id.FragmentContainer, new AllTripsFragment(), "AllTripsFragment").commit();
                    }
                    if(getSupportFragmentManager().findFragmentByTag("CreateTripFragment") != null){
                        //if the other fragment is visible, hide it.
                        getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("CreateTripFragment")).commit();
                    }





                    break;
                case R.id.navigation_add_trip:

                    if(getSupportFragmentManager().findFragmentByTag("CreateTripFragment") != null) {
                        //if the fragment exists, show it.
                        getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("CreateTripFragment")).commit();
                    } else {
                        //if the fragment does not exist, add it to fragment manager.
                        getSupportFragmentManager().beginTransaction().add(R.id.FragmentContainer, new CreateTripFragment(), "CreateTripFragment").commit();
                    }
                    if(getSupportFragmentManager().findFragmentByTag("AllTripsFragment") != null){
                        //if the other fragment is visible, hide it.
                        getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag("AllTripsFragment")).commit();
                    }

                    break;
               /* case R.id.navigation_profile:

                    if(getSupportFragmentManager().findFragmentByTag("ProfileFragment")==null) {

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.FragmentContainer, new ProfileFragment(), "ProfileFragment")
                                .addToBackStack("ProfileFragment")
                                .commit();

                    }else{

                        getSupportFragmentManager().popBackStackImmediate("ProfileFragment",0);

                    }
                    break;*/

            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_titlebar, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.logout_menu_item){
            mAuth.signOut();
            Intent login_screen_intent = new Intent(Trip_Home.this, MainActivity.class);
            startActivity(login_screen_intent);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip__home);
        setTitle("Home");



        mAuth=FirebaseAuth.getInstance();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);




        getSupportFragmentManager().beginTransaction()
                .add(R.id.FragmentContainer,new AllTripsFragment(),"AllTripsFragment")
                .commit();


    }

}
