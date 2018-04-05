package t.systematic.letsgo.MeetingActivities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.Database.OnGetDataListener;
import t.systematic.letsgo.Meeting.Meeting;
import t.systematic.letsgo.R;
import t.systematic.letsgo.UserObject.User;
import t.systematic.letsgo.Utils.DirectionsParser;

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


        //TODO add logic in the case that the user is not the admin but instead a participant.
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("USER_OBJECT");
        String meetingName = (String) intent.getSerializableExtra("MEETING_NAME");
        mMeeting = user.getMeeting(meetingName);
        participants = mMeeting.getParticipants();

        /* In the case the user is not admin, remove user from 'participants' and add admin. */
        if(!mMeeting.getAdmin().equals(user.getUsername())){
            participants.remove(user.getUsername());
            participants.add(mMeeting.getAdmin());
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocationPermission();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        /* Time is in milliseconds, distance in meters. */
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 20, (android.location.LocationListener) this);
        for(int i = 0; i < participants.size(); i++){
            initParticipantsLocationOnDataChangeListeners(participants.get(i));
            pullUserLatlngFromDB(participants.get(i));
        }

       // getDeviceLocation();
    }

    private void displayUserRoute(LatLng latLng){
        String url = getRequestUrl(latLng, mMeeting.getLatLng());
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.execute(url);
    }

    private void pullUserLatlngFromDB(final String username){
        DatabaseHelper.getInstance().getUserLocation(username, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                addUserMarker(Double.parseDouble(dataSnapshot.child("latitude").getValue().toString()),
                        Double.parseDouble(dataSnapshot.child("longitude").getValue().toString()), username, true);

                displayUserRoute(new LatLng(Double.parseDouble(dataSnapshot.child("latitude").getValue().toString()),
                        Double.parseDouble(dataSnapshot.child("longitude").getValue().toString())));
            }

            @Override
            public void onFailure(String failure) {
                Toast.makeText(getApplicationContext(), "Failed to get " + username + "'s location!", Toast.LENGTH_SHORT).show();
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
            //getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }
            mMap.setMyLocationEnabled(true);

        }
        addMeetingMarker(mMeeting.getLat(), mMeeting.getLong(), mMeeting.getMeetingName());

    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            /* Get response result. */
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    private String getRequestUrl(LatLng origin, LatLng destination){
        /* Value of origin. */
        String str_org = "origin=" + origin.latitude +"," + origin.longitude;
        /* Value of destination. */
        String str_destin = "destination=" + destination.latitude +"," + destination.longitude;
        /* Set value. */
        String sensor = "sensor=false";
        /* Mode for finding direction. */
        String mode = "mode=driving";
        /* Build the full params. */
        String param = str_org + "&" + str_destin + "&" + sensor + "&" + mode;
        /* Output format. */
        String output = "json";
        /* Create url to request. */
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
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
            ActivityCompat.requestPermissions(this, new String[]{FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
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
                addUserMarker(userLoc.latitude, userLoc.longitude, userName, true);
            }

            @Override
            public void onFailure(String failure) {
                Toast.makeText(getApplicationContext(), "Failed gettinng " + userName + "'s location.", Toast.LENGTH_LONG).show();
            }
        });

    }

    /* When users's location changes, rather than clearing entire map of markers, we simply clear/delete specific user marker and add a new one
    *  in the updated location. Marks for entire map is stored in hasMapMarkers. */
    public void addUserMarker(Double latitude, Double longitude, String username, boolean visible){

        if(hashMapMarkers.containsKey(username)){
            Log.d("ADDUSERMARKER", "HashMap already contained: " + username + ". Removing marker.");
            Marker marker = hashMapMarkers.get(username);
            marker.remove();
            hashMapMarkers.remove(username);
        }

        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(username).visible(visible));
        hashMapMarkers.put(username, marker);
        moveCamera(new LatLng(latitude, longitude), 14f);
        Log.d("ADDUSERMARKER", username + " " + latitude.toString() + " " + longitude.toString());
    }

    public void addMeetingMarker(Double latitude, Double longitude, String meetingName){
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(meetingName).visible(true));
        marker.showInfoWindow();
        hashMapMarkers.put(meetingName, marker);
    }

    @Override
    public void onLocationChanged(Location location) {
        Double lat, lng;
        lat = location.getLatitude();
        lng = location.getLongitude();
        user.setLocation(lat, lng);

        /* User will have the blue dot, so set marker visibility to false. Need the marker to calculate the
        *  camera zoom. */
        addUserMarker(lat, lng, user.getUsername(), false);
        displayUserRoute(new LatLng(lat,lng));

        /* Update user location into the database for other participants to see. */
        DatabaseHelper.getInstance().updateUserLocation(user.getUsername(), lat, lng);
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



    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            /* Parse JSON. */
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            /* Get list route and display it into the map. */
            Log.d("DIDROUTE", "DID");
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for(List<HashMap<String,String>> path : lists){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for(HashMap<String,String> point : path){
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.CYAN);
                polylineOptions.geodesic(true);
            }

            if(polylineOptions != null){
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Directions not found!", Toast.LENGTH_LONG).show();
            }
        }
    }

}
