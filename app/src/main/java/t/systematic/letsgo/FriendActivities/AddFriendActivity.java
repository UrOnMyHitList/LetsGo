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

import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.R;
import t.systematic.letsgo.UserObject.User;

public class AddFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        //TODO: fix formatting on xml
        //final DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        final EditText addFriendBox = findViewById(R.id.AddFriendBox);
        Button b1 = findViewById(R.id.SubmitFriendButton);
        final String TAG = "AddFriendActivity";
        final User user = (User)intent.getSerializableExtra("USER_OBJECT");
        final String username = user.getUsername();
        final String friendName = addFriendBox.getText().toString();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //thank god for auto-boilerplate

                DatabaseHelper.getInstance().addFriend(friendName, username, user, TAG);
                Toast toast = Toast.makeText(getApplicationContext(), "Friend request sent!", Toast.LENGTH_LONG);
                toast.show();
                //TODO send notification to added user
            }
        });
    }
}
