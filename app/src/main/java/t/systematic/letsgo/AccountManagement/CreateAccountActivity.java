package t.systematic.letsgo.AccountManagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import t.systematic.letsgo.MainActivity;
import t.systematic.letsgo.Meeting.Meeting;
import t.systematic.letsgo.UserObject.User;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.Database.OnGetDataListener;
import t.systematic.letsgo.R;

public class CreateAccountActivity extends AppCompatActivity implements OnGetDataListener{

    EditText password;
    EditText confirm_pw;
    EditText email;
    EditText username;
    EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        username = findViewById(R.id.createAccountLoginID);
        password = findViewById(R.id.createAccountEnterPassword);
        confirm_pw = findViewById(R.id.createAccountConfirmPassword);
        email = findViewById(R.id.createAccountEmail);
        phone = findViewById(R.id.enterPhone);
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
        Toast.makeText(getApplicationContext(), "Account Created!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(CreateAccountActivity.this, MainActivity.class);
        startActivity(i);

    }

    @Override
    public void onFailure(String failure) {
        Toast.makeText(getApplicationContext(), failure, Toast.LENGTH_SHORT).show();
    }

    public void createAccount(View view){

        //Verify there are no empty fields
        if (username.getText().toString().matches("")){
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
        } else if (password.getText().toString().matches("")){
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        } else if (confirm_pw.getText().toString().matches("")){
            Toast.makeText(this, "Please confirm password", Toast.LENGTH_SHORT).show();
        } else if (email.getText().toString().matches("")){
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
        } else if (phone.getText().toString().matches("")){
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
        } else if (!password.getText().toString().equals(confirm_pw.getText().toString())){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }else {

            User user = new User(username.getText().toString(),
                    new ArrayList<String>(),
                    new ArrayList<Meeting>(),
                    email.getText().toString(),
                    phone.getText().toString()
            );

            //TODO decide if to user input for phone number or use the phone iteself

            DatabaseHelper.getInstance().createAccount(user, password.getText().toString(), this);


        }
    }
}
