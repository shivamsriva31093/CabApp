package com.example.cab.cabapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int MAP_ZOOM_LEVEL = 10;
    private GoogleMap mMap;
    private static Firebase myFirebase;
    private static Firebase CustomerFirebase;
    private static Firebase retrieveCustomerFirebase;
    private String email;
    private String name;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout dlayout;
    private LinearLayoutManager linearLayoutManager;

    private String startLoggingTime;
    private ArrayList<Marker> markerList = new ArrayList<>();
    private ArrayList<Marker> removeMarkerList = new ArrayList<>();
    private ArrayList<Double> latitudeList = new ArrayList<>();
    private ArrayList<Double> longitudeList = new ArrayList<>();
    private static final String TAG = "MApsActivity";
    private Firebase test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Establish Firebase Connection
        Firebase.setAndroidContext(this);
         test = new Firebase("https://catchacab12.firebaseio.com/-KB7GgPXatFubuenzNcd/");
        Log.d("Firebase", test.child("timeStamp").getPath()+"");
        myFirebase = new Firebase("https://catchacab12.firebaseio.com/");
        CustomerFirebase = new Firebase("https://customersdb.firebaseio.com/");
        retrieveCustomerFirebase = new Firebase("https://customersdb.firebaseio.com/customerID/");
        Log.d(TAG,myFirebase.getKey()+"");


        // Show mapActivity
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //set up Drawer
        dlayout = (DrawerLayout) findViewById(R.id.drawer);
        setupDrawer();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //retrieve DriverMarker
        retrieval();
        Intent intent = getIntent();



        //save CustomerData in Database
        if (intent != null && intent.hasExtra("Pemail")&&intent.hasExtra("Name")) {
            email = intent.getStringExtra("Pemail");
            name = intent.getStringExtra("Name");

        }
        saveCustomerData();
        retrieveCustomerData();

        //set up navigationDrawer RecyclerView
        ArrayList<String> dataset = new ArrayList<>();
        ArrayList<Integer> imageset = new ArrayList<>();
        dataset.add("Sign Out");
        dataset.add("Settings");
        imageset.add(R.drawable.logout);
        imageset.add(R.drawable.settings);
        //Retrieve profile info from Google SignIn

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.cab);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(dataset,imageset,name,email,bitmap);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        //select a cab
        FloatingActionButton button = (FloatingActionButton)findViewById(R.id.floatingbutton);
        button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        button.setRippleColor(getResources().getColor(R.color.cardview_light_background));

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        //noinspection SimplifiableIfStatement


        //noinspection SimplifiableIfStatement

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume(){
        super.onResume();
        retrieval();
    }


    private void setupDrawer() {

        toggle = new ActionBarDrawerToggle(this, dlayout,
                R.string.drawer_open, R.string.drawer_close) {


            /**
             * Called when a drawer has settled in a completely open state.
             */


            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("options");
                invalidateOptionsMenu();
                drawerView.setClickable(true);


                // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("CabApp");
                invalidateOptionsMenu();
            }


        };
        dlayout.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);



    }
    private void retrieval() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        startLoggingTime = dateFormat.format(date).toString();
        drawLocations();
    }

    public void saveCustomerData(){

        Customer customer = new Customer(name,"telephoneNo",email);
        Map<String, String> CustomerMap= new HashMap<String,String>();
        CustomerMap.put("name",customer.getName());
        CustomerMap.put("telephoneNo", customer.getTelephoneNo());
        CustomerMap.put("email", customer.getEmail());
        Map<String, Map<String, String>> customers = new HashMap<String, Map<String, String>>();
        customers.put("customerID", CustomerMap);

        CustomerFirebase.setValue(customers);
    }

    private void retrieveCustomerData(){
        retrieveCustomerFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    retrieveCustomer customer = dataSnapshot.getValue(retrieveCustomer.class);
                   // Log.d("customerid", customer.getEmail());

                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("customerid", "error");
            }
        });
    }





    //called in retrieval method
    private void drawLocations() {
        // Get only latest logged locations - since 'START' button clicked
        myFirebase.addChildEventListener(new ChildEventListener() {
            LatLngBounds bounds;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            LatLng mLatLng;
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Object bookingStatus = dataSnapshot.child("/bookingStatus/").getValue();
                String bs = bookingStatus.toString();
                Log.d("cabStatus",bs);

            }

            private void mark(double latitude, double longitude, String timestamp) {
                // Create LatLng for each locations
                mLatLng = new LatLng(latitude, longitude);

                // Make sure the map boundary contains the location
                builder.include(mLatLng);
                bounds = builder.build();

                // Add a marker for each logged location
                MarkerOptions mMarkerOption = new MarkerOptions()
                        .position(mLatLng)
                        .title(timestamp)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi2));
                Marker mMarker = mMap.addMarker(mMarkerOption);
                markerList.add(mMarker);

                for(int i=0;i<markerList.size()-1;i++){
                    markerList.get(i).remove();
                }

                // Zoom map to the boundary that contains every logged location
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                        MAP_ZOOM_LEVEL));
            }
            private void removeMarker(double latitude, double longitude, String timestamp) {
                // Create LatLng for each locations
                mLatLng = new LatLng(latitude, longitude);



                // Add a marker for each logged location
                MarkerOptions mMarkerOption = new MarkerOptions()
                        .position(mLatLng)
                        .title(timestamp)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi2));
                Marker mMarker = mMap.addMarker(mMarkerOption);
                markerList.add(mMarker);


                // Zoom map to the boundary that contains every logged location
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                        MAP_ZOOM_LEVEL));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
                    CabLocation location = dataSnapshot.child("/trackingLocation/").getValue(CabLocation.class);
                    Object bookingStatus = dataSnapshot.child("/bookingStatus/").getValue();
                    String bs = bookingStatus.toString();
                    Log.d("cabStatus",bs);
                    if(bs=="false") {
                        double latitude = Double.valueOf(location.getLatitude());
                        double longitude = Double.valueOf(location.getLongitude());


                        String timestamp = "Hi";
                        Log.d("Latitude currently", latitude + "");
                        mark(latitude, longitude, timestamp);


                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

            //Override other abstract methods for addChildEventListener below
        });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getApplicationContext(), "Current location", Toast.LENGTH_SHORT);
        return true;
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);

        }
        else if(mMap!=null){
            mMap.setMyLocationEnabled(true);
        }
        GPSTracker gpsTracker = new GPSTracker(this);


        // Add a marker in Sydney and move the camera

        LatLng sydney = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sydney).title("YourLocation"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 12.0f));

    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();


    }

}
