package t.systematic.letsgo.MeetingActivities;

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
import java.util.ArrayList;
import t.systematic.letsgo.FriendActivities.FriendsManagerActivity;
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
        Toast.makeText(getApplicationContext(), "Meeting Manager Activity", Toast.LENGTH_LONG).show();

        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("USER_OBJECT");

        /* Get name of meetings to display in listView. */
        init_listView(user.getAllMeetingNames());
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
                Intent intent = new Intent(MeetingManagerActivity.this, MapActivity.class);
                //  intent.putExtra("USER_OBJECT", user);
                startActivity(intent);
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
                intent.putExtra("ACTIVITY_MODE", "EDIT_TEXT_MODE");
                intent.putExtra("USER_OBJECT", user);
                startActivity(intent);
            }
        });
    }

}
