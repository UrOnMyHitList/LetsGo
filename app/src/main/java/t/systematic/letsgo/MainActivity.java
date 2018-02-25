package t.systematic.letsgo;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        For Testing - This activity takes user straight to the Log In screen

        No need to pass anything into intent
         */
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 2,2, 14,42,00);

        Log.d("TIMEEE", "" + calendar.get(Calendar.DATE));



        // TODO finish updating code to handle calendar time and into DB


       DatabaseHelper.getInstance().getUserInfo("stefin", this);
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
        user = new User("stefin", new ArrayList<String>(), new ArrayList<Meeting>());
        Log.d("FIRST", "USER INIT");
        /* Add user's friends. */
        for(DataSnapshot friend : dataSnapshot.child("friends").getChildren()){
            Log.d("FRIEND", friend.getValue().toString() );
            user.addFriend(friend.getValue().toString());
        }

        ArrayList<String> allUserMeetings = new ArrayList<>();
        for(DataSnapshot meetings : dataSnapshot.child("meetings").getChildren()){
            allUserMeetings.add(meetings.getKey().toString());
        }
        DatabaseHelper.getInstance().getUserMeetings(allUserMeetings, this);
    }

    @Override
    public void onFailure(String failure) {
        Toast.makeText(getApplicationContext(), failure, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess_initializeUserMeetings(Meeting newMeeting, int lastMeeting) {
        /* Check if user is logged in if yes, send to MeetingManagerActivity,
        *  if no, send to Login Screen*/
        user.addNewMeeting(newMeeting);
        Intent i;
        if(user.getNumberOfMeetings() == lastMeeting){
            i = new Intent(MainActivity.this, MeetingManagerActivity.class);
            i.putExtra("USER_OBJECT", user);
            startActivity(i);
        }
    }
}
