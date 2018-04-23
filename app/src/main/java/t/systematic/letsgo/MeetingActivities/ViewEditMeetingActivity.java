package t.systematic.letsgo.MeetingActivities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import org.w3c.dom.Text;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import t.systematic.letsgo.Database.DatabaseHelper;
import t.systematic.letsgo.Database.OnGetDataListener;
import t.systematic.letsgo.Meeting.Meeting;
import t.systematic.letsgo.R;
import t.systematic.letsgo.UserObject.User;

/**
 * Activity will be called on at different areas whenever a meeting
 * is to be edited by users.
 */
public class ViewEditMeetingActivity extends AppCompatActivity implements OnGetDataListener {

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

    private Button updateCreateButton;
    private Button addFriendsButton;

    /* Data structures needed for listView containing participants
    *  name. Will show participant's name and a delete button in
    *  each row. */
    private ArrayList<String> participants;
    private ListView participants_listView;

    private User user;

    private String originalMeetingName;
    private Meeting meeting;

    private String mode;

    private ArrayList<Integer> mUserSelected = new ArrayList<>();
    boolean[] checkedItems;
    private String[] friendsPrev;

    /* Used to get current date and time. */
    Calendar calendar;
    /* Used to record last/current date and time. */
    Calendar newMeetingCalendar;
    /* Think of getting updated timeZone. */
    TimeZone timeZone = TimeZone.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_single_meeting);

        init_layoutElements();

        Intent intent = getIntent();
        intent.getExtras();

        mode = intent.getStringExtra("ACTIVITY_MODE");
        user = (User)intent.getSerializableExtra("USER_OBJECT");
        if(mode.equals("EDIT_TEXT_MODE")){
            init_EditTextMode(intent);
            addEditTextListeners();
            participants = new ArrayList<>();
            checkedItems = new boolean[user.getFriends().size()];
        } else if(mode.equals("TEXT_VIEW_MODE")){
            init_TextViewMode(intent);
        } else {
            Log.d("ERROR", "VIEWEDITMEETINGACTIVITY - onCreate");
            /* If time, create error logs table in some local DB. */
        }
        init_PageButtons();

        calendar = Calendar.getInstance();
        calendar.setTimeZone(timeZone);
        newMeetingCalendar = Calendar.getInstance();
        newMeetingCalendar.setTimeZone(timeZone);

    }


    /* Both methods are simply listeners to bring up the calendar and timepicker UI. When user selects
    *  a date/time it will appear in the approriate editText. */
    private void addEditTextListeners(){
        date_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE, d MMM yyyy");
                        /* As per docs, need to subtract 1900. */
                        Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
                        newMeetingCalendar.setTime(date);
                        String dayOfWeek = simpledateformat.format(date);
                        date_editText.setText(dayOfWeek);
                    }
                };
                DatePickerDialog popUpCalendar = new DatePickerDialog(ViewEditMeetingActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                popUpCalendar.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1000);
                popUpCalendar.show();
            }
        });

        time_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                        newMeetingCalendar.set(Calendar.HOUR_OF_DAY, hour);
                        newMeetingCalendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
                        String formatedTime = sdf.format(newMeetingCalendar.getTime());
                        time_editText.setText(formatedTime);

                    }
                };

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                boolean is24hour = false;
                TimePickerDialog timePickerDialog = new TimePickerDialog(ViewEditMeetingActivity.this, onTimeSetListener, hour, minute, is24hour);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });
    }

    private boolean isEmpty(String val){
        if(val.trim().length() > 0){
            return false;
        }
        return true;
    }

    private void init_PageButtons(){
        updateCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mode.equals("EDIT_TEXT_MODE")){
                    Log.d("LLL", "IN EDIT");
                    if(isEmpty(date_editText.getText().toString())|| isEmpty(meetingName_editText.getText().toString()) || isEmpty(time_editText.getText().toString()) ||
                            isEmpty(destination_editText.getText().toString())){
                        Toast.makeText(getApplicationContext(), "*** All fields must be filled in ***", Toast.LENGTH_LONG).show();
                        return;
                    }
                } else if(mode.equals("TEXT_VIEW_MODE")){
                    if(isEmpty(date_textView.getText().toString())|| isEmpty(meetingName_textView.getText().toString()) ||
                            isEmpty(time_textView.getText().toString()) || isEmpty(destination_textView.getText().toString())){
                        Toast.makeText(getApplicationContext(), "*** All fields must be filled in ***", Toast.LENGTH_LONG).show();
                        Log.d("RETTT", "DATE: "+date_textView.getText().toString() + " TIME: " + time_textView.getText().toString() +
                        " Meetingnam: " + meetingName_textView.getText().toString() + " dest: " + destination_textView.getText().toString());
                        return;
                    }
                }
                String meetingId = "";
                if(meeting == null){
                    meetingId = user.getUsername() + System.currentTimeMillis() + (new Date(System.currentTimeMillis())).toString();
                    meetingId = meetingId.replace(".","");
                }
                else if(meeting.getAdmin().equals(user.getUsername())){
                    meetingId = meeting.getMeetingId();
                } else {
                    meetingId = user.getUsername() + System.currentTimeMillis() + (new Date(System.currentTimeMillis())).toString();
                    meetingId = meetingId.replace(".","");
                }

                String meetingDate = "";
                String meetingTime = "";

                String meetingName = "ERROR GETTING MEETING NAME";
                if(mode.equals("EDIT_TEXT_MODE")){
                    meetingDate = date_editText.getText().toString();
                    meetingName = meetingName_editText.getText().toString();
                    meetingTime = time_editText.getText().toString();


                } else if(mode.equals("TEXT_VIEW_MODE")){
                    meetingDate = date_textView.getText().toString();
                    meetingName = meetingName_textView.getText().toString();
                    meetingTime = time_textView.getText().toString();
                }


                //TODO NEED TO GET LOCATION POINTS AND ADD THEM TO ARGUMENTS BELOW
                DatabaseHelper.getInstance().createUpdateMeeting(meetingId, 43.0, 34.0,user.getUsername(), meetingName,
                        participants, meetingDate + "@" + meetingTime, ViewEditMeetingActivity.this);

                Meeting modMeeting = new Meeting(meetingName, participants, newMeetingCalendar, 43.0, 32.0, meetingId, user.getUsername() );
                Log.d("CHECKING2", "" + newMeetingCalendar.getTime());
                if(meeting == null){
                    DatabaseHelper.getInstance().addMeetingToUser(meetingId, user.getUsername());
                }
                else if(meeting.getAdmin().equals(user.getUsername())){
                   user.removeMeeting(meeting);
                } else {
                    DatabaseHelper.getInstance().addMeetingToUser(meetingId, user.getUsername());
                }
                user.addNewMeeting(modMeeting);

                Intent i = new Intent(ViewEditMeetingActivity.this, MeetingManagerActivity.class);
                i.putExtra("USER_OBJECT", user);
                startActivity(i);
            }

        });

        addFriendsButton.setOnClickListener(new View.OnClickListener() {

            String[] friends = new String[user.getFriends().size()];

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewEditMeetingActivity.this);
                builder.setTitle("Select friends to invite");

                friends = user.getFriends().toArray(friends);
                friendsPrev = friends;
                builder.setMultiChoiceItems(friends, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            if(! mUserSelected.contains(position)){
                                mUserSelected.add(position);
                            }
                        }else if (mUserSelected.contains(position)){
                            mUserSelected.remove(mUserSelected.indexOf(position));
                        }

                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                       // participants_listView.setAdapter(new MyListAdapter(this, R.layout.textview_with_button, friends[i]));
                        String item = "";
                        participants.clear();
                        for(int i = 0; i < mUserSelected.size(); i++){
                            item = item + friends[mUserSelected.get(i)];
                            participants.add(friends[mUserSelected.get(i)]);
                        }
                        /* Set listView usingcustom adapter. */
                        participants_listView = (ListView)findViewById(R.id.participants_listView);
                        participants_listView.setAdapter(new MyListAdapter(ViewEditMeetingActivity.this, R.layout.textview_with_button, participants));
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void init_EditTextMode(Intent intent){
        setCreateMeetingLayout(meetingName_editText, meetingName_textView, "Meeting name");
        setCreateMeetingLayout(destination_editText, destination_textView, "Click to select destination");
        setCreateMeetingLayout(date_editText, date_textView, "Click to select a date");
        setCreateMeetingLayout(time_editText, time_textView, "Click to select a time");
        meetingName_editText.requestFocus();
        updateCreateButton.setText("Create Meeting");

    }

    private void setCreateMeetingLayout(EditText editText, TextView textView, String hint){
        editText.setHint(hint);
        editText.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
    }

    private void init_TextViewMode(Intent intent){
        /* Get the name of meeting, and then details from User Obj. */
        originalMeetingName = intent.getStringExtra("MEETING_NAME");
        meeting = user.getMeeting(originalMeetingName);

        /* If the user is an admin allow him/her to update the meeting info. */
        if(meeting.getAdmin().equals(user.getUsername())){
            init_editAndTextViewListeners();
        }

        destination_textView.setText(Double.toString(meeting.getLat()) + " " +
                Double.toString(meeting.getLong()));
        Log.d("TIMEVALUE" , meeting.getDateTime().toString());
        /* Initialize all editText, textView, and listView fields. */
        meetingName_textView.setText(originalMeetingName);
        participants = meeting.getParticipants();

        date_textView.setText(meeting.getMeetingDate());
        time_textView.setText(meeting.getMeetingTime());

        /* Set listView using custom adapter. */
        participants_listView = (ListView)findViewById(R.id.participants_listView);
        if(meeting.getAdmin().equals(user.getUsername())){
            participants_listView.setAdapter(new MyListAdapter(this, R.layout.textview_with_button, participants));

            updateCreateButton.setText("Update Meeting");
        } else {
            participants_listView.setAdapter(new ArrayAdapter<>(this, R.layout.up_coming_meeting_list,R.id.singleMeetingRow, participants));
            updateCreateButton.setVisibility(View.GONE);
            addFriendsButton.setVisibility(View.GONE);
        }

    }

    private void init_layoutElements(){
        meetingName_editText = (EditText)findViewById(R.id.meetingName_editText);
        meetingName_textView = (TextView) findViewById(R.id.meetingName_textView);

        destination_editText = (EditText)findViewById(R.id.destination_editText);
        destination_textView = (TextView)findViewById(R.id.destination_textView);

        date_editText = (EditText)findViewById(R.id.date_editText);
        date_textView = (TextView)findViewById(R.id.date_textView);

        time_editText = (EditText)findViewById(R.id.time_editText);
        time_textView = (TextView)findViewById(R.id.time_textView);

        updateCreateButton = (Button)findViewById(R.id.update_Button);
        addFriendsButton = (Button)findViewById(R.id.inviteFriends_Button);
    }

    /**
     * Made into function to not clutter up onCreate. Initializes
     * textViews and editViews.
     */
    private void init_editAndTextViewListeners(){

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

    @Override
    public void onSuccess(DataSnapshot dataSnapshot) {
    }

    @Override
    public void onFailure(String failure) {

    }

    /**
     * Since using a custom listView layout, a custom adapter is needed as well.
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
                final View finalConvertView = convertView;
                viewHolder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Removed: " + position, Toast.LENGTH_LONG).show();

                        String userID = ((TextView)finalConvertView.findViewById(R.id.list_textView)).getText().toString();
                        int index = Arrays.asList(friendsPrev).indexOf(userID);

                        /* Update variables used for add/remove friends dialog and listView*/
                        checkedItems[index] = false;
                        mUserSelected.remove(index);
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
