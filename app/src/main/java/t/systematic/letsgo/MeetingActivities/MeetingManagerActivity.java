package t.systematic.letsgo.MeetingActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import t.systematic.letsgo.FriendActivities.FriendsManagerActivity;
import t.systematic.letsgo.MainActivity;
import t.systematic.letsgo.R;

public class MeetingManagerActivity extends AppCompatActivity {
    private float x1,x2,y1,y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_manager);
        Toast.makeText(getApplicationContext(), "Meeting Manager Activity", Toast.LENGTH_LONG).show();
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
                    Intent i = new Intent(MeetingManagerActivity.this, FriendsManagerActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
        }
        return false;
    }
}
