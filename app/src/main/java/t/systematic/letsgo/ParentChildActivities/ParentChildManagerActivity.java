package t.systematic.letsgo.ParentChildActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import t.systematic.letsgo.FriendActivities.FriendsManagerActivity;
import t.systematic.letsgo.MeetingActivities.MeetingManagerActivity;
import t.systematic.letsgo.R;

public class ParentChildManagerActivity extends AppCompatActivity {
    private float x1,x2,y1,y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_child_manager);
        Toast.makeText(getApplicationContext(), "Parent Child Manager Activity", Toast.LENGTH_LONG).show();
    }

    public boolean onTouchEvent(MotionEvent touchEvent){
        Intent i;
        switch (touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if(x1 < x2){
                    i= new Intent(ParentChildManagerActivity.this, FriendsManagerActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
        }
        return false;
    }
}
