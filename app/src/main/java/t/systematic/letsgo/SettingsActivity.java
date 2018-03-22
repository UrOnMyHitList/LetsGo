package t.systematic.letsgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import t.systematic.letsgo.AccountManagement.ChangeAccountInfoActivity;
import t.systematic.letsgo.AccountManagement.ChangeUsername;
import t.systematic.letsgo.AccountManagement.LogInActivity;
import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.Database.OnGetDataListener;
import t.systematic.letsgo.NotificationActivities.NotificationActivity;

/**
 * Created by mathe on 2/19/2018.
 */

public class SettingsActivity extends AppCompatActivity {
    private String userName;
    boolean unreadNotifs;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE);
        String defaultValue = "";
        userName = sharedPref.getString("username", defaultValue);
        unreadNotifs = false;

        getMenuInflater().inflate(R.menu.settings_menu, menu);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        hasUnreadNotifs(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.changeDisplayName){
            Intent intent = new Intent(getApplicationContext(), ChangeUsername.class);
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
        else if(id == R.id.badge){
            Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
            intent.putExtra("username", userName);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * This method checks both notifications for friend requests and meetings and chnages the icon on Action bar accordingly.
     * @param menu
     */
    public void hasUnreadNotifs(final Menu menu){
        DatabaseHelper.getInstance().hasUnreadNotifs(userName, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                //Toast.makeText(SettingsActivity.this, "Unread Notification!", Toast.LENGTH_SHORT).show();
                MenuItem settingsItem = menu.findItem(R.id.badge);
                settingsItem.setIcon(R.drawable.unread_notif_bell);
            }

            @Override
            public void onFailure(String failure) {
                if(failure.equals("Read")){
                    //Toast.makeText(SettingsActivity.this, "All notifications are read!", Toast.LENGTH_SHORT).show();
                    MenuItem settingsItem = menu.findItem(R.id.badge);
                    settingsItem.setIcon(R.drawable.notif_bell);
                }
                else{
                    Toast.makeText(SettingsActivity.this, ""+failure, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
