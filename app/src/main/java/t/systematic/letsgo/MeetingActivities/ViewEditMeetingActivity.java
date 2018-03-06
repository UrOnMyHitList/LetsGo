package t.systematic.letsgo.MeetingActivities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import t.systematic.letsgo.R;

/**
 * Activity will be called on at different areas whenever a meeting
 * is to be edited by users.
 */
public class ViewEditMeetingActivity extends AppCompatActivity {

    /* TextView and EditText will go hand in hand for user
    *  to see and be able to edit if they have admin/edit privilege.*/
    private TextView meetingName_textView;
    private EditText meetingName_editText;

    private TextView destination_textView;
    private EditText destination_editText;

    private TextView date_textView;
    private EditText date_editText;

    private TextView time_textView;
    private EditText time_editText;

    /* Data structures needed for listView containing participants
    *  name. Will show participant's name and a delete button in
    *  each row. */
    private ArrayList<String> participants = new ArrayList<String>();
    private ListView participants_listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_single_meeting);

        String name = getIntent().getStringExtra("MEETING_NAME");
        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();

        init_editAndTextView();

        participants.add("Jerry");
        participants.add("Max");
        participants.add("Isabella");

        participants_listView = (ListView)findViewById(R.id.participants_listView);
        participants_listView.setAdapter(new MyListAdapter(this, R.layout.textview_with_button, participants));
    }

    /**
     * Made into function to not clutter up onCreate. Initializes
     * textViews and editViews.
     */
    private void init_editAndTextView(){
        meetingName_editText = (EditText)findViewById(R.id.meetingName_editText);
        meetingName_textView = (TextView) findViewById(R.id.meetingName_textView);

        destination_editText = (EditText)findViewById(R.id.destination_editText);
        destination_textView = (TextView)findViewById(R.id.destination_textView);

        date_editText = (EditText)findViewById(R.id.date_editText);
        date_textView = (TextView)findViewById(R.id.date_textView);

        time_editText = (EditText)findViewById(R.id.time_editText);
        time_textView = (TextView)findViewById(R.id.time_textView);

        init_textViewOnLongClickListener(meetingName_textView, meetingName_editText);
        init_editTextOnFocusChangeListener(meetingName_editText, meetingName_textView);

        init_textViewOnLongClickListener(destination_textView, destination_editText);
        init_editTextOnFocusChangeListener(destination_editText, destination_textView);

        init_textViewOnLongClickListener(date_textView, date_editText);
        init_editTextOnFocusChangeListener(date_editText, date_textView);

        init_textViewOnLongClickListener(time_textView, time_editText);
        init_editTextOnFocusChangeListener(time_editText, time_textView);
    }

    /**
     * Initializes textViews OnLongClickListener for whenever a user
     * with privilege wants to change a meeting variable they can do so.
     *
     * Will simply hide the textView and bring up an editView.
     * @param textView - contains a value of meeting variable.
     * @param pairEditText - to be used for user to type in new value.
     */
    private void init_textViewOnLongClickListener(final TextView textView, final EditText pairEditText){
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                pairEditText.setText(textView.getText());
                textView.setVisibility(View.GONE);
                pairEditText.setVisibility(View.VISIBLE);
                pairEditText.requestFocus();
                return false;
            }
        });
    }

    /**
     * Initializes editTexts OnFocusChangeListener for when user is done
     * editing a value for the meeting.
     *
     * Once the editText loses focus, hides it. Then brings back its paired
     * textView with the newly entered value.
     * @param editText - to be used for user to type in new value.
     * @param pairTextView - contains a value of meeting variable.
     */
    private void init_editTextOnFocusChangeListener(final EditText editText, final TextView pairTextView){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    pairTextView.setText(editText.getText());
                    editText.setVisibility(View.GONE);
                    pairTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Since using a custom listView, a custom adapter is needed as well.
     */
    private class MyListAdapter extends  ArrayAdapter<String>{
        private int layout;
        public MyListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            ViewHolder mainViewHolder = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                /* Initialize each textView and imageView for each row in the listView. */
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.deleteIcon = (ImageView)convertView.findViewById(R.id.list_imageView);
                /* Add listener to imageView so when clicked it will delete entire row from participants_listView.
                *  Will also update meeting object as well and update meeting in the DB. */
                viewHolder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Removed: " + position, Toast.LENGTH_LONG).show();
                        /* TODO - delete user from meeting object, and update meeting in DB */
                        participants.remove(position);
                        notifyDataSetChanged();
                    }
                });
                viewHolder.participantName = (TextView)convertView.findViewById(R.id.list_textView);
                viewHolder.participantName.setText(getItem(position));
                convertView.setTag(viewHolder);
            }else{
                mainViewHolder = (ViewHolder)convertView.getTag();
                mainViewHolder.participantName.setText(getItem(position));
                notifyDataSetChanged();
            }
            return convertView;
        }
    }

    /**
     * Used to hold anything else that will go into a row of the listView.
     */
    public class ViewHolder {
        ImageView deleteIcon;
        TextView participantName;
    }
}
