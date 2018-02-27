package t.systematic.letsgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import t.systematic.letsgo.AccountManagement.ChangeAccountInfoActivity;
import t.systematic.letsgo.AccountManagement.LogInActivity;

/**
 * Created by mathe on 2/19/2018.
 */

public class SettingsActivity extends AppCompatActivity {
    private String userName;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.changeDisplayName){
            Intent intent = new Intent(getApplicationContext(), ChangeAccountInfoActivity.class);
            intent.putExtra("username", userName);
            intent.putExtra("action", "username");
            startActivity(intent);
        }
        else if(id == R.id.changePhoneNumber){
            Intent intent = new Intent(getApplicationContext(), ChangeAccountInfoActivity.class);
            intent.putExtra("username", userName);
            intent.putExtra("action", "phonenumber");
            startActivity(intent);
        }
        else if(id == R.id.changePassword){
            Intent intent = new Intent(getApplicationContext(), ChangeAccountInfoActivity.class);
            intent.putExtra("username", userName);
            intent.putExtra("action", "password");
            startActivity(intent);
        }
        else if(id == R.id.logout){
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("username", "");
            editor.commit();

            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
