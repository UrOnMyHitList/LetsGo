package t.systematic.letsgo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import t.systematic.letsgo.Database.DatabaseHelper;

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
    }
}
