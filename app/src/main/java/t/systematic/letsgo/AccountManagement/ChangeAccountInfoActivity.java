package t.systematic.letsgo.AccountManagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.Database.OnGetDataListener;
import t.systematic.letsgo.Meeting.Meeting;
import t.systematic.letsgo.R;
import t.systematic.letsgo.SettingsActivity;

/**
 * Created by mathe on 2/19/2018.
 */

public class ChangeAccountInfoActivity extends SettingsActivity {
    private String username, action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE);
//        String defaultValue = "";
//        username = sharedPref.getString("username", defaultValue);

        username = getIntent().getStringExtra("username");
        action = getIntent().getStringExtra("action");

        if(action.equals("password")){
            setContentView(R.layout.activity_change_password);
        }
        else if(action.equals("phonenumber")){
            setContentView(R.layout.activity_change_phone);
        }
        else if(action.equals("username")){
            setContentView(R.layout.activity_change_username);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onClickPasswordChange(View view){
        EditText currentPasswrdEditText = (EditText) findViewById(R.id.inputCurrentPswrd);
        final String currentPassword = currentPasswrdEditText.getText().toString();
        /*//encryption block
        String currentPassword = "";
        try {
            currentPassword = DatabaseHelper.getInstance().encrypt(currentPasswrdEditText.getText().toString());
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Error verifying Password",Toast.LENGTH_SHORT).show();
        }
        //end encryption block*/

        EditText newPasswrdEditText = (EditText) findViewById(R.id.inputNewPswrd);
        final String newPasswrd = newPasswrdEditText.getText().toString();

        EditText reenterNewPasswrdEditText = (EditText) findViewById(R.id.reinputNewPswrd);
        String reenteredNewPasswrd = reenterNewPasswrdEditText.getText().toString();

        if (currentPassword.matches("") || newPasswrd.matches("") || reenteredNewPasswrd.matches("")) {
            Toast.makeText(this, "Please enter your current and new password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPasswrd.matches(reenteredNewPasswrd)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(newPasswrd.matches(currentPassword)){
            Toast.makeText(this, "New and old passwords are the same", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper.getInstance().validateUser(username, currentPassword, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                DatabaseHelper.getInstance().changePassword(username, newPasswrd, new OnGetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        Toast.makeText(ChangeAccountInfoActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String failure) {
                        Toast.makeText(ChangeAccountInfoActivity.this, ""+failure, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String failure) {

            }
        });

        Toast.makeText(this, "Password changed", Toast.LENGTH_SHORT).show();
    }

    protected void onClickPhoneNumber(View view){
        String number = ((EditText) findViewById(R.id.inputPhoneNum)).getText().toString();
        DatabaseHelper.getInstance().changePhoneNumber(username, number, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Toast.makeText(ChangeAccountInfoActivity.this, "Phone Number Changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String failure) {
                Toast.makeText(ChangeAccountInfoActivity.this, ""+failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onClickUsername(View view){
        //String newUsername = ((EditText) findViewById(R.id.inputDisplayName)).getText().toString();

    }
}
