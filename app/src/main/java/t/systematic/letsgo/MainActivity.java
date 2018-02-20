package t.systematic.letsgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import t.systematic.letsgo.AccountManagement.LogInActivity;
import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.Database.OnGetDataListener;
import t.systematic.letsgo.MeetingActivities.MeetingManagerActivity;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper database = new DatabaseHelper();
       // database.writeToDB("Ivan does not suck");

        /*
        For Testing - This activity takes user straight to the Log In screen

        No need to pass anything into intent
         */

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE);
        String defaultValue = "";
        String username = sharedPref.getString("username", defaultValue);

        if(!username.equals(defaultValue)){
            Intent goToMainMenu = new Intent(MainActivity.this, MeetingManagerActivity.class);
            goToMainMenu.putExtra("username", username);
            MainActivity.this.startActivity(goToMainMenu);
        }
        else {
            Intent i = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(i);
        }



        /* Check if user is logged in if yes, send to MeetingManagerActivity,
        *  if no, send to Login Screen*/
        Intent i;
        if(true){
            i = new Intent(MainActivity.this, MeetingManagerActivity.class);
            startActivity(i);
        }
    }


}
