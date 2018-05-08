package t.systematic.letsgo.FriendActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import t.systematic.letsgo.R;
import t.systematic.letsgo.UserObject.User;

public class RemoveFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_friend);
        final DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        final EditText removefriendbox = findViewById(R.id.RemoveFriendBox);
        Button b1 = findViewById(R.id.SubmitRemoveFriendButton);
        final String TAG = "RemoveFriendActivity";
        User user = (User)intent.getSerializableExtra("USER_OBJECT");
        final String username = user.getUsername();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String removeFriend = removefriendbox.getText().toString();
                myDb.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //look through user's friends
                        //remove friend with entered username
                        //go to other user (with entered username)
                        //remove current user from their friend list also
                        //look through meetings each user has active
                        //remove other user from meetings they admin - do later?
                        Boolean found = false;
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            String name = snapshot.getKey();
                            if(name.equals(removeFriend)) {
                                found = true;
                                String childKey = "";
                                for(DataSnapshot friendList: dataSnapshot.child(username).child("friends").getChildren()) {
                                    if(((String)friendList.getValue()).equals(removeFriend)) {
                                        childKey = friendList.getKey();
                                    }
                                }
                                myDb.child("users").child(username).child("friends").child(childKey).removeValue();
                                //remove user from friend list
                                //get key of user in other friend list
                                //iterate through other friend list
                                for (DataSnapshot subList: dataSnapshot.child(removeFriend).child("friends").getChildren()) {
                                    //if you find the user in their friend list, remove them from it
                                    if(username.equals(subList.getValue())) {
                                        childKey = subList.getKey();
                                    }
                                }
                                myDb.child("users").child(removeFriend).child("friends").child(childKey).removeValue();
                            }
                        }
                        if (found) {
                            Toast.makeText(getApplicationContext(),"Friend removed!",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"No such friend in list!",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadUsers:onCancelled", databaseError.toException());
                    }
                });
            }
        });
    }
}
