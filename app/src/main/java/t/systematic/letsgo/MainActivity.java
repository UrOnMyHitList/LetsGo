package t.systematic.letsgo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.MeetingActivities.MapActivity;
import t.systematic.letsgo.MeetingActivities.MeetingManagerActivity;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(getApplicationContext(), "HELLO WORLD", Toast.LENGTH_LONG).show();
        ref = FirebaseDatabase.getInstance().getReference();
        DatabaseHelper database = new DatabaseHelper();
        database.writeToDB("Ivan does not suck");

        /* Check if user is logged in if yes, send to MeetingManagerActivity,
        *  if no, send to Login Screen*/
        Intent i;
        if(true){
            i = new Intent(MainActivity.this, MeetingManagerActivity.class);
            startActivity(i);
        }
    }

}
