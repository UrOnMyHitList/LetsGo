package t.systematic.letsgo.NotificationActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.Database.OnGetDataListener;
import t.systematic.letsgo.MainActivity;
import t.systematic.letsgo.R;
import t.systematic.letsgo.SettingsActivity;

public class NotificationActivity extends SettingsActivity {

    private ArrayList<Notification> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //generate list
        list = new ArrayList<Notification>();

        String username = getIntent().getStringExtra("username");

        DatabaseHelper.getInstance().getUserNotifications(username, new OnGetDataListener(){
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    if(!snapshot.child("type").getValue().toString().equals("null")){
                        if(snapshot.child("reply").getValue(String.class).toString().equals("N")){
                            list.add(new Notification(snapshot.child("requestor").getValue(String.class).toString(),
                                    snapshot.child("type").getValue(String.class).toString(),
                                    snapshot.getKey().toString(),
                                    snapshot.child("read").getValue(String.class).toString(),
                                    snapshot.child("reply").getValue(String.class).toString()));
                        }
                    }
                }
                //Toast.makeText(this, ""+list.size(), Toast.LENGTH_SHORT).show();
                //instantiate custom adapter
                CustomNotificationAdapter adapter = new CustomNotificationAdapter(list, NotificationActivity.this);

                //handle listview and assign adapter
                ListView lView = (ListView)findViewById(R.id.notifList);
                lView.setAdapter(adapter);
                //sToast.makeText(NotificationActivity.this, ""+list.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String failure) {
                Toast.makeText(NotificationActivity.this, ""+failure, Toast.LENGTH_SHORT).show();
            }
        });



    }
}
