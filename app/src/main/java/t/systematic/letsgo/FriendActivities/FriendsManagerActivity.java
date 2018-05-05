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

    private ListView friendsList;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_manager);

        friendsList = findViewById(R.id.friendsListView);
        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("USER_OBJECT");
        //Toast.makeText(getApplicationContext(), "Init Friends List", Toast.LENGTH_SHORT).show();
        if(user == null){
            System.out.print("No user passed");
        }
        initializeFriendsList(user.getFriends());


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

    private void initializeFriendsList(ArrayList<String> friends){
        if(friends.get(0).equals("null")){
            friends.set(0, "Add friends below!");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.friends_text_view,
                friends );

        friendsList.setAdapter(arrayAdapter);

    }
}
