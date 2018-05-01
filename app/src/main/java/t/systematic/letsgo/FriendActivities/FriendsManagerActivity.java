package t.systematic.letsgo.FriendActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import t.systematic.letsgo.MeetingActivities.MeetingManagerActivity;
import t.systematic.letsgo.MeetingActivities.ViewEditMeetingActivity;
import t.systematic.letsgo.ParentChildActivities.ParentChildManagerActivity;
import t.systematic.letsgo.R;
import t.systematic.letsgo.UserObject.User;

public class FriendsManagerActivity extends AppCompatActivity {
    private float x1,x2,y1,y2;
    private ListView friendsList;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_manager);

        friendsList = findViewById(R.id.friendsListView);
        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("USER_OBJECT");
        if(user == null){
            System.out.print("Nothing passed");
        }
        if (user.hasFriends())
            initializeFriendsList(user.getFriends());
        else{
            //TODO: write "No friends" to listView
        }
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
        removeFriendActivity.putExtra("USER_OBJECT", user);
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

    private void initializeFriendsList(ArrayList<String> friends){
        if(friends.size() == 0){
            friends.add("Add new friends below");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                friends );

        friendsList.setAdapter(arrayAdapter);

    }
}
