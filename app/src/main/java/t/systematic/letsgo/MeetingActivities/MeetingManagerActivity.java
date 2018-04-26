package t.systematic.letsgo.MeetingActivities;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import t.systematic.letsgo.FriendActivities.FriendsManagerActivity;
import t.systematic.letsgo.Meeting.Meeting;
import t.systematic.letsgo.R;
import t.systematic.letsgo.UserObject.User;
import t.systematic.letsgo.SettingsActivity;


public class MeetingManagerActivity extends SettingsActivity {

    private ListView meetings_listView;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_manager);

        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("USER_OBJECT");
        if(user == null){
            System.out.print("Nothing passed");
        }

        /* Get name of meetings to display in listView. */
        if (user.hasMeetings())
            init_listView(user.getAllMeetingNames());
        else{
            //TODO: write "No Meetings Scheduled" to listView
        }
        /* Add listener to Scheduled Meeting button. */
        init_viewScheduledMeetingsButton();
        /* Add listener to Schedule Meeting button. */
        init_createNewMeetingButton();

        init_viewActiveMeetingButton();
    }

    public boolean onTouchEvent(MotionEvent touchEvent){
        float x1 = 0;
        float x2 = 0;
        switch (touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();

                if(x1 > x2){
                    Intent intent = new Intent(MeetingManagerActivity.this, FriendsManagerActivity.class);
                    intent.putExtra("USER_OBJECT", user);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
        }
        return false;
    }

    private void init_listView(ArrayList<String> meetingNames){
        if(meetingNames.size() == 0){
            meetingNames.add("No meetings scheduled!");
        }
        meetings_listView = (ListView)findViewById(R.id.upComingMeetingListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.up_coming_meeting_list, R.id.singleMeetingRow, meetingNames);
        meetings_listView.setAdapter(adapter);

        if(meetingNames.get(0).equals("No meetings scheduled!")){
            return;
        }
        meetings_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MeetingManagerActivity.this, ViewEditMeetingActivity.class);
                intent.putExtra("ACTIVITY_MODE", "TEXT_VIEW_MODE");
                intent.putExtra("USER_OBJECT", user);
                intent.putExtra("MEETING_NAME", (String)meetings_listView.getItemAtPosition(i));
                startActivity(intent);
            }
        });
    }

    private void init_viewActiveMeetingButton(){
        Button button = (Button) findViewById(R.id.viewActiveMeetingButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Meeting> userMeetings = user.getMeetings();
                Log.d("NUMBEROFMEETINGS", "" + userMeetings.size());
                int me = userMeetings.size();

                for(int i = 0; i < me; i++){
                    Log.d("CHECKINGMEETINGITER", "IN");
                    Date x = Calendar.getInstance().getTime();

                    long ONE_MINUTE_IN_MILLIS=60000;//millisecs

                    long t= userMeetings.get(i).getDateTime().getTimeInMillis();
                    Date afterAddingOneMin = new Date(t + ONE_MINUTE_IN_MILLIS);

                    if (x.after(userMeetings.get(i).getDateTime().getTime()) && x.before(afterAddingOneMin)) {
                        Log.d("CHECKING MEETING!", userMeetings.get(i).getMeetingName());
                    }
                }

   //             if(isServicesOK()){}


//                Intent intent = new Intent(MeetingManagerActivity.this, MapActivity.class);
//                intent.putExtra("USER_OBJECT", user);
//                intent.putExtra("MEETING_NAME", "Demo");
//                startActivity(intent);
            }
        });
    }

    private void init_viewScheduledMeetingsButton(){
        Button button = (Button)findViewById(R.id.viewScheduledMeetingsButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeetingManagerActivity.this, ViewCalendarActivity.class);
                intent.putExtra("USER_OBJECT", user);
                startActivity(intent);
            }
        });
    }

    private void init_createNewMeetingButton(){
        Button button = (Button)findViewById(R.id.createMeetingButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(MeetingManagerActivity.this, ViewEditMeetingActivity.class);
                    intent.putExtra("ACTIVITY_MODE", "CREATE_MEETING_MODE");
                    intent.putExtra("USER_OBJECT", user);
                    startActivity(intent);

            }
        });
    }


    /*    GOOGLE SERVICES CHECKS    */
    private static final String TAG = "MeetingManagerActivity";
    private static final int ERROR_DAILOG_REQUEST = 9001;

    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MeetingManagerActivity.this);
        if(available == ConnectionResult.SUCCESS){
            //Everything is fine and user can make map requests.
            Log.d(TAG, "Connection was available for Google API.");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //Error occured but user can fix it.
            Log.d(TAG, "Google API error, dialog invoked.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MeetingManagerActivity.this, available, ERROR_DAILOG_REQUEST);
            dialog.show();
        } else {
            //Failure, nothing we can do.
            Log.d(TAG, "Google API failure.");
            Toast.makeText(this, "ERROR: MAP REQUESTS FAILURE", Toast.LENGTH_LONG).show();
        }
        return false;
    }


}
