package t.systematic.letsgo.FriendActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import t.systematic.letsgo.MeetingActivities.MeetingManagerActivity;
import t.systematic.letsgo.ParentChildActivities.ParentChildManagerActivity;
import t.systematic.letsgo.R;
import t.systematic.letsgo.UserObject.User;

public class FriendsManagerActivity extends AppCompatActivity {
    private float x1,x2,y1,y2;
    Intent intent = getIntent();
    User user = (User)intent.getSerializableExtra("USER_OBJECT");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_manager);
        Toast.makeText(getApplicationContext(), "Friends Manager Activity", Toast.LENGTH_LONG).show();
    }

    public void addFriendButtonClicked(View view){
        Intent addFriendActivity = new Intent(FriendsManagerActivity.this, AddFriendActivity.class);
        addFriendActivity.putExtra("USER_OBJECT", user);
        FriendsManagerActivity.this.startActivity(addFriendActivity);
        //input friend username
        //search database for match
        //if match add that username string to list in user object
        //if no match give error
    }

    public void removeFriendButtonClicked(View view){
        Intent removeFriendActivity = new Intent(FriendsManagerActivity.this, RemoveFriendActivity.class);
        FriendsManagerActivity.this.startActivity(removeFriendActivity);
        //select friend from list
        //search for meetings with either user as owner
        //remove user or old friend from any meetings found
        //remove old friend from friend list in user object
        //
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
                    i= new Intent(FriendsManagerActivity.this, MeetingManagerActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else if(x1 > x2){
                    i = new Intent(FriendsManagerActivity.this, ParentChildManagerActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
        }
        return false;
    }
}
