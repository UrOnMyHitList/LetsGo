package t.systematic.letsgo.AccountManagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import t.systematic.letsgo.R;

public class ForgotInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_info);

        Intent intent = getIntent();
        String flavor = intent.getStringExtra(LogInActivity.EXTRA_FLAVOR);

        TextView text = findViewById(R.id.ForgotLoginTextView);
        text.setText(flavor);

        if (flavor.equals("Enter your username to recover your password:")) {
            EditText enterBox = findViewById(R.id.enterInfoBox);
            enterBox.setVisibility(View.VISIBLE);
        }
        else {
            EditText enterBox = findViewById(R.id.enterEmailBox);
            enterBox.setVisibility(View.VISIBLE);
        }
    }
}
