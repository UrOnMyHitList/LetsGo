package t.systematic.letsgo.AccountManagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import t.systematic.letsgo.R;

public class ForgotInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_info);
        final DatabaseReference myDb;
        myDb = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        final String flavor = intent.getStringExtra(LogInActivity.EXTRA_FLAVOR);
        final String TAG = "ForgotInfoActivity";
        TextView text = findViewById(R.id.ForgotLoginTextView);
        text.setText(flavor);
        final EditText enterInfoBox = findViewById(R.id.enterInfoBox);
        final EditText enterEmailBox = findViewById(R.id.enterEmailBox);
        final String flavorCheck = "Enter your username to recover your password:";
        if (flavor.equals(flavorCheck)) {
            enterInfoBox.setVisibility(View.VISIBLE);
        }
        else {
            enterEmailBox.setVisibility(View.VISIBLE);
        }
        Button b1 = (Button)findViewById(R.id.forgotLoginButton);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Boolean found = false;
                        for (DataSnapshot userList: dataSnapshot.getChildren()) {
                            String username = userList.getKey();
                            if (flavor.equals(flavorCheck)) {
                                if (username.equals(enterInfoBox.getText().toString())) {
                                    found = true;
                                    DataSnapshot temp = userList.child("password"); //returns key = password value = "whatever"
                                    String pw = temp.getValue(String.class);
                                    Toast toast = Toast.makeText(getApplicationContext(), pw , Toast.LENGTH_LONG);
                                    toast.show();
                                    //TODO update this logic with what we actually want to do later
                                    break;
                                }
                            }
                            else {
                                DataSnapshot temp = userList.child("email");
                                String userEmail = temp.getValue(String.class);
                                if (userEmail.equals(enterEmailBox.getText().toString())) {
                                    found = true;
                                    Toast toast = Toast.makeText(getApplicationContext(), username, Toast.LENGTH_LONG);
                                    toast.show();
                                    //TODO update this logic with what we actually want to do later
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            String toastText = "No such User in System";
                            Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG);
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
