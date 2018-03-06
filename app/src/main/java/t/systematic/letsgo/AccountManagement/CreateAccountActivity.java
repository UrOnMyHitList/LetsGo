package t.systematic.letsgo.AccountManagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

        username = findViewById(R.id.createAccountUserName);
        password = findViewById(R.id.createAccountEnterPassword);
        confirm_pw = findViewById(R.id.createAccountConfirmPassword);
        email = findViewById(R.id.createAccountEmail);
        phone = findViewById(R.id.enterPhone);
    }

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onFailure(String failure) {

    }

    public void createAccount(View view){

        //TODO check if username exists

        if ( password.getText().toString().equals(confirm_pw.getText().toString())) {

            //TelephonyManager tMananger = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

            User user = new User(username.getText().toString(),
                    new ArrayList<String>(),
                    new ArrayList<Meeting>(),
                    email.getText().toString(),
                    phone.getText().toString());
            /**
            //Toast.makeText(getApplicationContext(), "Creating account", Toast.LENGTH_LONG).show();
            User user = new User();
            userName = username.getText().toString();
            userName = userName.toLowerCase();
            user.setUsername(userName);
            user.setPassword(password.getText().toString());
            user.setEmail(email.getText().toString());
            user.setFriends(new ArrayList<String>());
            //Used to get the user's phone number.
            TelephonyManager tMananger = (TelephonyManager) getApplicationContext().getSystemService(
                    Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                //Android complains without this check.
                Toast.makeText(getApplicationContext(), "Database Permission Error!", Toast.LENGTH_LONG).show();
                return;
            }
            user.setPhonenumber(tMananger.getLine1Number());
            user.setmMeetingWith("NULL");
            user.setMeetingReq("NULL");
            **/

            DatabaseHelper.getInstance().createAccount(user, password.getText().toString(), this);
        }
        else{
            Toast.makeText(this, "Password does not match!", Toast.LENGTH_SHORT).show();
        }
    }
}
