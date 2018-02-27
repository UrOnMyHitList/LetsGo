package t.systematic.letsgo.AccountManagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.Database.OnGetDataListener;
import t.systematic.letsgo.MeetingActivities.MeetingManagerActivity;
import t.systematic.letsgo.R;

/**
 * Created by Ivan on 2/8/18.
 */

public class LogInActivity extends AppCompatActivity implements OnGetDataListener {
    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.userLoginEditText);
        password = (EditText) findViewById(R.id.userPasswordEditText);
    }

    public void validateLogin(View view){
        //calls Presenter to check database for login details
        String uname = username.getText().toString();
        String pass = password.getText().toString();
        if(!(TextUtils.isEmpty(uname)) && !(TextUtils.isEmpty(pass))){
            DatabaseHelper.getInstance().validateUser(uname.toLowerCase(), pass.toString(), this);
        }
        else{
            //display error
            Toast.makeText(getApplicationContext(), "Input password/username", Toast.LENGTH_LONG).show();
        }
    }

    public void startForgotInfoActivity(View view){
//        TODO: Create Forgot Info activity
        Toast.makeText(this, "Forgot Info activity... Coming Soon!", Toast.LENGTH_SHORT).show();
/**
        Intent forgotInfoActivity = new Intent(LogInActivity.this, ForgotInfoActivity.class);
        if(view.getId() == R.id.forgotPasswordButton){
            forgotInfoActivity.putExtra("ActivityFlavor", "Password");

        }
        else if(view.getId() == R.id.forgotUserNameButton){
            forgotInfoActivity.putExtra("ActivityFlavor", "Username");
        }
        LogInActivity.this.startActivity(forgotInfoActivity);
 **/
    }

    public void sendToCreateAccActivity(View view){
        Intent i = new Intent(LogInActivity.this, CreateAccountActivity.class);
        startActivity(i);
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
        //Toast.makeText(getApplicationContext(), "Logging in", Toast.LENGTH_LONG).show();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username.getText().toString());
        editor.commit();

        Intent goToMainMenu = new Intent(LogInActivity.this, MeetingManagerActivity.class);
        goToMainMenu.putExtra("username", username.getText().toString());
        LogInActivity.this.startActivity(goToMainMenu);
        finish();
    }

    @Override
    public void onFailure(String failure) {
        if(failure != null)
            Toast.makeText(this, "" + failure, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Error Logging in, Please try again later.", Toast.LENGTH_SHORT).show();
    }
}
