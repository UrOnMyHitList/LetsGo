package t.systematic.letsgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import t.systematic.letsgo.AccountManagement.LogInActivity;
import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.Database.OnGetDataListener;
import t.systematic.letsgo.Meeting.Meeting;
import t.systematic.letsgo.MeetingActivities.MeetingManagerActivity;
import t.systematic.letsgo.UserObject.User;

public class MainActivity extends AppCompatActivity implements  OnGetDataListener{
    private DatabaseReference ref;
    private User user;
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE);
        String defaultValue = "";
        String username = sharedPref.getString("username", defaultValue);

        if(!username.equals(defaultValue)){
            mUsername = username;
            DatabaseHelper.getInstance().getUserInfo(username, this);
        }
        else {
            Intent i = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
        user = new User(mUsername, new ArrayList<String>(), new ArrayList<Meeting>(), dataSnapshot.child("email").toString(),
                dataSnapshot.child("phone").toString());
        /* Add user's friends. */
        for(DataSnapshot friend : dataSnapshot.child("friends").getChildren()){
            user.addFriend(friend.getValue().toString());
        }

        ArrayList<String> allUserMeetings = new ArrayList<>();
        final int numOfMeetings = (int)dataSnapshot.child("meetings").getChildrenCount() - 1;
        int completed = 0;
        String key;
        for(DataSnapshot meetings : dataSnapshot.child("meetings").getChildren()){
            key = meetings.getKey().toString();
            Log.d("MEETINGKEY", ""+ meetings.getKey().toString());
            final int current = completed;

            DatabaseHelper.getInstance().getUserMeetings(meetings.getValue().toString(), new OnGetDataListener() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {

                    Log.d("FUCKING", "" + dataSnapshot);
                    if(dataSnapshot.exists()){
                        String meetingName = dataSnapshot.child("meetingName").getValue().toString();
                        String calendarValues = dataSnapshot.child("startTime").getValue().toString();
                        String admin = dataSnapshot.child("admin").getValue().toString();
                        /* Set date into Calendar object. */
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                        try {
                            calendar.setTime(sdf.parse(calendarValues));// all done
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        /* Set meeting params. */
                        Location location = new Location("meetingLocation");
                        location.setLongitude(Double.parseDouble(dataSnapshot.child("Long").getValue().toString()));
                        location.setLatitude(Double.parseDouble(dataSnapshot.child("Lat").getValue().toString()));

                        ArrayList<String> participants = new ArrayList<String>();
                        for(DataSnapshot dbParticipants : dataSnapshot.child("participants").getChildren()){
                            participants.add(dbParticipants.getValue().toString());
                        }
                        Log.d("FU1", "Meeting: " + meetingName);
                        user.addNewMeeting(new Meeting(meetingName, participants, calendar, location.getLatitude(),
                                location.getLongitude(), dataSnapshot.getKey().toString(), admin));


                    }else{
                        Toast.makeText(getApplicationContext(), "Failed to pull user meetings.", Toast.LENGTH_LONG).show();
                    }
                    Log.d("FU2", ""+ user.getNumberOfMeetings());
                    Log.d("CURRENT", "CUR: " + current + " Num: " + numOfMeetings);

                    if(current == numOfMeetings){

                        Intent i = new Intent(MainActivity.this, MeetingManagerActivity.class);
                        i.putExtra("USER_OBJECT", user);
                        startActivity(i);
                    }



                }

                @Override
                public void onFailure(String failure) {

                }

            });//Database
            completed++;
        }//Forloop




    }

    @Override
    public void onFailure(String failure) {
        Toast.makeText(getApplicationContext(), failure, Toast.LENGTH_SHORT).show();
    }

}
