package t.systematic.letsgo.AccountManagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.Database.OnGetDataListener;
import t.systematic.letsgo.MainActivity;
import t.systematic.letsgo.Meeting.Meeting;
import t.systematic.letsgo.MeetingActivities.MeetingManagerActivity;
import t.systematic.letsgo.R;
import t.systematic.letsgo.UserObject.User;

/**
 * Created by Ivan on 2/8/18.
 */

public class LogInActivity extends AppCompatActivity implements OnGetDataListener {
    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.userLoginEditText);
        password = (EditText) findViewById(R.id.userPasswordEditText);
    }

    public void validateLogin(View view){
        //calls Presenter to check database for login details
        System.out.println("validateLogin CALLED");

        String uname = username.getText().toString();
        String pass = password.getText().toString();
        if(!(TextUtils.isEmpty(uname)) && !(TextUtils.isEmpty(pass))){
            DatabaseHelper.getInstance().validateUser(uname.toLowerCase(), pass, this);
        }
        else{
            //display error
            Toast.makeText(getApplicationContext(), "Input password/username", Toast.LENGTH_LONG).show();
        }
    }
    public static final String EXTRA_FLAVOR = "t.systematic.letsgo.ActivityFlavor";
    public void startForgotInfoActivity(View view){

        //Toast.makeText(this, "Forgot Info activity... Under Construction!", Toast.LENGTH_SHORT).show();
        Intent forgotInfoActivity = new Intent(LogInActivity.this, ForgotInfoActivity.class);
        if(view.getId() == R.id.forgotPasswordButton){
            forgotInfoActivity.putExtra(EXTRA_FLAVOR, "Enter your username to recover your password:");

        }
        else if(view.getId() == R.id.forgotUserNameButton){
            forgotInfoActivity.putExtra(EXTRA_FLAVOR, "Enter your email to recover your username:");
        }
        LogInActivity.this.startActivity(forgotInfoActivity);
    }

    public void sendToCreateAccActivity(View view){
        Intent i = new Intent(LogInActivity.this, CreateAccountActivity.class);
        startActivity(i);
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username.getText().toString());
        editor.commit();

        final User user = new User(dataSnapshot.getKey(), new ArrayList<String>(), new ArrayList<Meeting>(), dataSnapshot.child("email").toString(),
                dataSnapshot.child("phone").toString());

        /* Add user's friends. */
        for(DataSnapshot friend : dataSnapshot.child("friends").getChildren()){
            user.addFriend(friend.getValue().toString());
        }

        /* Logic below is to pull all of the user's meetings's information. */
        final int numOfMeetings = (int)dataSnapshot.child("meetings").getChildrenCount() - 1;
        int completed = 0;
        for(DataSnapshot meetings : dataSnapshot.child("meetings").getChildren()){
            final int current = completed;
            DatabaseHelper.getInstance().getUserMeetings(meetings.getValue().toString(), new OnGetDataListener() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
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

                        ArrayList<String> participants = new ArrayList<String>();
                        for(DataSnapshot dbParticipants : dataSnapshot.child("participants").getChildren()){
                            participants.add(dbParticipants.getValue().toString());
                        }
                        /* Adding a new meeting with info pulled from DB. */
                        user.addNewMeeting(new Meeting(meetingName, participants, calendar,
                                Double.parseDouble(dataSnapshot.child("Lat").getValue().toString()),
                                Double.parseDouble(dataSnapshot.child("Long").getValue().toString()),
                                dataSnapshot.getKey(), admin));
                    }else{
                        Toast.makeText(getApplicationContext(), "Failed to pull user meetings.", Toast.LENGTH_LONG).show();
                    }
                    /* Once we are done pulling all info from user go to MeetingManangerActivity. */
                    if(current == numOfMeetings){
                        Intent i = new Intent(LogInActivity.this, MeetingManagerActivity.class);
                        i.putExtra("USER_OBJECT", user);
                        startActivity(i);
                    }
                }
                @Override
                public void onFailure(String failure) {
                    //TODO: onFailure - go to MeetingsManagerActivity with no meetings for the user
                    Intent i = new Intent(LogInActivity.this, MeetingManagerActivity.class);
                    i.putExtra("USER_OBJECT", user);
                    startActivity(i);
                }
            });//Database
            completed++;
        }//Forloop
    }

    @Override
    public void onFailure(String failure) {
        if(failure != null)
            Toast.makeText(this, "" + failure, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Error Logging in, Please try again later.", Toast.LENGTH_SHORT).show();
    }

}
