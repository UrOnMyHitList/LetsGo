package t.systematic.letsgo.MeetingActivities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.Database.OnGetDataListener;
import t.systematic.letsgo.Meeting.Meeting;
import t.systematic.letsgo.R;
import t.systematic.letsgo.UserObject.User;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private User user;
    private LocationManager locationManager;

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int PERM_CODE = 0;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    Location currentLocation;

    private Meeting mMeeting;
    private ArrayList<String> participants;
    private HashMap<String, Marker> hashMapMarkers = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("USER_OBJECT");
        String meetingName = (String) intent.getSerializableExtra("MEETING_NAME");

        mMeeting = user.getMeeting(meetingName);
        participants = mMeeting.getParticipants();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocationPermission();
        getDeviceLocation();
        //Time is in milliseconds, distance in meters.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 20, (android.location.LocationListener) this);

        for(int i = 0; i < participants.size(); i++){
            initParticipantsLocationOnDataChangeListeners(participants.get(i));
            pullUserLatlngFromDB(participants.get(i));
        }

       // getDeviceLocation();
    }

    private void pullUserLatlngFromDB(final String username){
        DatabaseHelper.getInstance().getUserLocation(username, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Log.d("ADDING", "MARKER " + username + " with lat: " + dataSnapshot.child("latitude").getValue().toString() + " long: "
                + dataSnapshot.child("longitude").getValue().toString());
                Log.d("DOUBLE", "" + Double.parseDouble(dataSnapshot.child("longitude").getValue().toString()));
                addUserMarker(Double.parseDouble(dataSnapshot.child("latitude").getValue().toString()),
                        Double.parseDouble(dataSnapshot.child("longitude").getValue().toString()), username);
            }

            @Override
            public void onFailure(String failure) {

            }
        });
    }

    /* Might not need this since we are using a listener to get the updated location.*/
    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {

            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude()), 14f);
                            mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Your position")).showInfoWindow();
                        } else {
                            Toast.makeText(MapActivity.this, "Unable to get Location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "SecurityException: " + e.getMessage());
        }
    }

    /* Moves camera with adjusted zoom so that all markers show. */
    private void moveCamera(LatLng latLng, float zoom) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if(hashMapMarkers.size() == 0){
            return;
        }
        for(Marker marker : hashMapMarkers.values()){
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 70;
        Log.d("NUMBEROFUSERS", "" + hashMapMarkers.size());
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
    }

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("COURSELOCATION", "EDE");
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    //initiate map
                    initMap();
                }
            }
        }
    }

    //initialze the google map.
    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Log.d("SETTINGLOCATION", "DF");
            mMap.setMyLocationEnabled(true);

        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //Time is in milliseconds, distance is in meters.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 20, (android.location.LocationListener) this);
    }


    //adds marker on the map, pulls other users info from database
    public void initParticipantsLocationOnDataChangeListeners(final String userName){
        DatabaseHelper.getInstance().addOnDataChangeListenerToUserlatlng(userName, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                LatLng userLoc = new LatLng(Double.parseDouble(dataSnapshot.child("latitude").getValue().toString()),
                        Double.parseDouble(dataSnapshot.child("longitude").getValue().toString()));

                Log.d("USERNAME", userName + " Lat: " + userLoc.latitude + " Long: " + userLoc.longitude);
                addUserMarker(userLoc.latitude, userLoc.longitude, userName);
            }

            @Override
            public void onFailure(String failure) {
                Toast.makeText(getApplicationContext(), "Failed gettinng " + userName + "'s location.", Toast.LENGTH_LONG).show();
            }
        });

    }

    /* When users's location changes, rather than clearing entire map of markers, we simply clear/delete specific user marker and add a new one
    *  in the updated location. Marks for entire map is stored in hasMapMarkers. */
    public void addUserMarker(Double latitude, Double longitude, String username){

        if(hashMapMarkers.containsKey(username)){
            Log.d("ADDUSERMARKER", "HashMap already contained: " + username + ". Removing marker.");
            Marker marker = hashMapMarkers.get(username);
            marker.remove();
            hashMapMarkers.remove(username);
        }

        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(username));
        hashMapMarkers.put(username, marker);
        moveCamera(new LatLng(latitude, longitude), 14f);
        Log.d("ADDUSERMARKER", username + " " + latitude.toString() + " " + longitude.toString());
    }


    @Override
    public void onLocationChanged(Location location) {
        Double lat, lng;
        lat = location.getLatitude();
        lng = location.getLongitude();
        user.setLocation(lat, lng);

        LatLng userLoc = user.getLatLng();



    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
