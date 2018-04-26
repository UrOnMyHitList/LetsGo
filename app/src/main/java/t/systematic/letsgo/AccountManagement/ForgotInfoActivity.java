package t.systematic.letsgo.AccountManagement;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import java.util.concurrent.ThreadLocalRandom;

public class ForgotInfoActivity extends AppCompatActivity {
    final int MY_PERMISSIONS_REQUEST_SEND_NUDES = 8008;
    Button b1;
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_NUDES: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // task you need to do.
                    b1.callOnClick();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast toast = Toast.makeText(getApplicationContext(), "Error resetting password, can't send reset text message.", Toast.LENGTH_LONG);
                    toast.show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    //TODO allow this activity to send SMS to user's phone.  permissions necessary +sms +access contacts (already implemented?)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_info);
        b1 = findViewById(R.id.forgotLoginButton);
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
        final Activity thisActivity = this;
        if (flavor.equals(flavorCheck)) {
            enterInfoBox.setVisibility(View.VISIBLE);
        }
        else {
            enterEmailBox.setVisibility(View.VISIBLE);
        }


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
                                    //SEND THE THING HERE AND RESET THE PASSWORD
                                    //check for and get permissions for SEND_SMS
                                    if (ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.SEND_SMS)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        // Permission is not granted
                                        ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.SEND_SMS},MY_PERMISSIONS_REQUEST_SEND_NUDES);
                                    }
                                    else {
                                        //pull phone number for user from database
                                        DataSnapshot temp1 = userList.child("phone");
                                        String phoneNum = temp1.getValue(String.class); //get phone number associated with that user
                                        //generate random 6 digit code for temp password
                                        //resets to start of seed when relaunching app.
                                        int randPass = ThreadLocalRandom.current().nextInt(111111, 1000000);
                                        //setup Sms manager
                                        SmsManager sms = SmsManager.getDefault();
                                        //FIRE THE CANNONS
                                        sms.sendTextMessage(phoneNum, null, "Your password is set to " + randPass + ". Please change it immediately", null, null);
                                        //set user password to new password
                                        myDb.child("users").child(username).child("password").setValue("" + randPass);
                                        //DataSnapshot temp = userList.child("password"); //returns key = password value = "whatever" - old logic
                                        //String pw = temp.getValue(String.class); - old logic
                                        Toast toast = Toast.makeText(getApplicationContext(), "Password reset! Check your text messages", Toast.LENGTH_LONG);
                                        toast.show();
                                        //update this logic with what we actually want to do later - DONE
                                    }
                                    break;
                                }
                            }
                            else {
                                String userEmail = userList.child("email").getValue(String.class);
                                if (userEmail.equals(enterEmailBox.getText().toString())) {
                                    found = true;
                                    Toast toast = Toast.makeText(getApplicationContext(), username, Toast.LENGTH_LONG);
                                    toast.show();
                                    //TODO update this logic with what we actually want to do later
                                    //send text to user with (link/signal/etc) to reset password
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
