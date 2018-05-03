package t.systematic.letsgo.NotificationActivities;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.Database.OnGetDataListener;
import t.systematic.letsgo.R;

/**
 * Created by mathe on 3/17/2018.
 */

public class CustomNotificationAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Notification> list = new ArrayList<Notification>();
    private Context context;

    public CustomNotificationAdapter(ArrayList<Notification> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_notif_list_layout, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);

        if(list.get(position).getNotifType().equals("friendRequest")) {
            listItemText.setText(list.get(position).getRequestor() + " wants to\nadd you as a friend!");
        }
        else if (list.get(position).getNotifType().equals("meetingRequest")){
            listItemText.setText(list.get(position).getRequestor() + " wants to\nadd you to a meeting!");
        }

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        Button addBtn = (Button)view.findViewById(R.id.add_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preference), Context.MODE_PRIVATE);
                String defaultValue = "";
                String userName = sharedPref.getString("username", defaultValue);

                DatabaseHelper.getInstance().notificationRepliedNo(userName, list.get(position).getId(), new OnGetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        list.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String failure) {
                        Toast.makeText(context, ""+failure, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preference), Context.MODE_PRIVATE);
                String defaultValue = "";
                String userName = sharedPref.getString("username", defaultValue);

                DatabaseHelper.getInstance().notificationRepliedYes(userName, list.get(position).getId(), new OnGetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        list.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String failure) {
                        Toast.makeText(context, ""+failure, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }
}
