package t.systematic.letsgo.MeetingActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import t.systematic.letsgo.AccountManagement.ChangeUsername;
import t.systematic.letsgo.AccountManagement.LogInActivity;
import t.systematic.letsgo.FriendActivities.FriendsManagerActivity;
import t.systematic.letsgo.R;
import t.systematic.letsgo.SettingsActivity;

public class MeetingManagerActivity extends SettingsActivity {

    private Intent intent;
    private float x1,x2,y1,y2;
    private ListView meetings_listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_manager);
        Toast.makeText(getApplicationContext(), "Meeting Manager Activity", Toast.LENGTH_LONG).show();

        /* Temporary array list, will get real values from user object passed in.
        *  Going to only take a max of 5-10 so it fits nice in the UI screen. */
        String[] meetingNames = new String[]{"Hiking with friends", "Date night", "Work Carpool"};
        init_listView(meetingNames);


    }

    public void testTest(View view){
        Intent intent = new Intent(MeetingManagerActivity.this, ChangeUsername.class);
        this.startActivity(intent);
    }

    public boolean onTouchEvent(MotionEvent touchEvent){
        switch (touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if(x1 > x2){
                    intent = new Intent(MeetingManagerActivity.this, FriendsManagerActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
        }
        return false;
    }

    private void init_listView(String[] meetingNames){
        meetings_listView = (ListView)findViewById(R.id.upComingMeetingListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.up_coming_meeting_list, R.id.singleMeetingRow, meetingNames);
        meetings_listView.setAdapter(adapter);
        meetings_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent = new Intent(MeetingManagerActivity.this, ViewEditMeetingActivity.class);
                intent.putExtra("MEETING_NAME", (String)meetings_listView.getItemAtPosition(i));
                startActivity(intent);
            }
        });
    }
}
