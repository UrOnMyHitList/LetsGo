package t.systematic.letsgo.MeetingActivities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import t.systematic.letsgo.Meeting.Meeting;
import t.systematic.letsgo.R;

public class MeetingDestinationNonAdminActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Meeting meeting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_destination_non_admin);
        Intent intent = getIntent();
        meeting = (Meeting)intent.getSerializableExtra("meeting");

        TextView meetingLocationTextView = findViewById(R.id.meetingLocation);
        TextView meetingNameTextView = findViewById(R.id.meetingName);

        meetingNameTextView.setText(meeting.getMeetingName());
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(meeting.getLat(), meeting.getLong(), 1);
        } catch (IOException e){
            Toast.makeText(this, "Cannot display meeting address", Toast.LENGTH_SHORT).show();
            meetingLocationTextView.setVisibility(View.INVISIBLE);
        }
        if(addresses != null && addresses.size() > 0) {
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            String display = "Click to view destination";

            if(address != null){
                display = address + "\n" + city + ", " + state + ", " + country;
                meetingLocationTextView.setText(display);
                meetingLocationTextView.setGravity(Gravity.CENTER);
            }
            else if(city != null && state != null && country != null){
                display = city + ", " + state + ", " + country;
                meetingLocationTextView.setText(display);
                meetingLocationTextView.setGravity(Gravity.CENTER);
            }
            else{
                meetingLocationTextView.setText(display);
                meetingLocationTextView.setGravity(Gravity.CENTER);
            }
        }
        else{
            meetingLocationTextView.setVisibility(View.INVISIBLE);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng meetingLatLng = meeting.getLatLng();
        mMap.addMarker(new MarkerOptions().position(meetingLatLng).title(meeting.getMeetingName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(meetingLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(8.0f));

    }
}
