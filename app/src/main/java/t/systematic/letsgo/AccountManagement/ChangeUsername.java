package t.systematic.letsgo.AccountManagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.Database.OnGetDataListener;
import t.systematic.letsgo.R;

/**
 * Created by Ivan on 3/18/18.
 */

public class ChangeUsername extends AppCompatActivity implements OnGetDataListener {

    EditText new_username;
    TextView message;
    String old_username;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE);
        String defaultValue = "";
        old_username = sharedPref.getString("username", defaultValue);

        new_username = findViewById(R.id.new_username);
        message = findViewById(R.id.confirmation_message);

    }
    public void checkAvailability(View view){
        DatabaseHelper.getInstance().checkForUsername(new_username.getText().toString(), this);

    }


    public void changeUsername(View view){
        DatabaseHelper.getInstance().changeUsername(old_username, new_username.getText().toString(), this);
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
        String s = "Username is available!";
        message.setText(s);

    }

    @Override
    public void onFailure(String failure) {
        message.setText(failure);

    }
}
