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

import t.systematic.letsgo.R;
import t.systematic.letsgo.UserObject.User;

public class AddFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        //TODO: fix formatting on xml
        final DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        final EditText addFriendBox = findViewById(R.id.AddFriendBox);
        Button b1 = findViewById(R.id.SubmitFriendButton);
        final String TAG = "AddFriendActivity";
        User user = (User)intent.getSerializableExtra("USER_OBJECT");
        final String username = user.getUsername();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //thank god for auto-boilerplate
                myDb.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Boolean found = false;
                        String friendName = addFriendBox.getText().toString();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            String name = snapshot.getKey();
                            if (name.equals(friendName)) {
                                //add the friend
                                //add a new key/value under friends
                                //replace below line when i figure out how to do notifications.
                                myDb.child("users").child(username).child("friends").push().setValue(friendName);
                                found = true;
                                //TODO send a notification to friended user????
                                //TODO don't add if duplicate friend

                                Toast toast = Toast.makeText(getApplicationContext(), "Friend request sent!" , Toast.LENGTH_LONG);
                                toast.show();
                                break;
                            }
                        }
                        if(!found) {
                            Toast toast = Toast.makeText(getApplicationContext(), "No such user found!" , Toast.LENGTH_LONG);
                            toast.show();
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
