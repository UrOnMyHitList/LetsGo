package t.systematic.letsgo.MeetingActivities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

import t.systematic.letsgo.R;
import t.systematic.letsgo.UserObject.User;

public class ViewCalendarActivity extends AppCompatActivity {
    private User user;
    private ListView meetings_listView;
    private String NO_MEETINGS_DISPLAY_MESSAGE = "No meetings on this day!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar);

        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("USER_OBJECT");

        ArrayList<String> meetingsToday = user.getMeetingNamesWithStartDateAt(Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.YEAR);
        init_listView(checkIfNoMeetings(meetingsToday));
        init_calendarView();
    }

    /**
     * Initialize listView, values displayed will depend if user has a meeting/s on that date.
     * If the user clicks on a row, show them the details in a new activity.
     * @param meetingNames - name of meetings to display on listView.
     */
    private void init_listView(final ArrayList<String> meetingNames){
        meetings_listView = (ListView)findViewById(R.id.meetingsOnSelectedDate_listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.up_coming_meeting_list, R.id.singleMeetingRow, meetingNames);
        meetings_listView.setAdapter(adapter);
        meetings_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /* When user has no meetings, make listView display NO_MEETINGS_DISPLAY_MESSAGE but don't
                *  allow anything to happened when clicked. */
                if(meetings_listView.getItemAtPosition(i).toString().equals(NO_MEETINGS_DISPLAY_MESSAGE)){
                    return;
                }
                Intent intent = new Intent(ViewCalendarActivity.this, ViewEditMeetingActivity.class);
                intent.putExtra("ACTIVITY_MODE", "TEXT_VIEW_MODE");
                intent.putExtra("USER_OBJECT", user);
                intent.putExtra("MEETING_NAME", (String)meetings_listView.getItemAtPosition(i));
                startActivity(intent);
            }
        });
    }

    /**
     * Initialize the calendar view. Each time a user clicks on a date, check if the user has a meeting
     * on that date.
     */
    private void init_calendarView(){
        CalendarView calendarView = (CalendarView)findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                ArrayList<String> meetingsOnDate = user.getMeetingNamesWithStartDateAt(month, dayOfMonth, year);
                meetings_listView = null;
                init_listView(checkIfNoMeetings(meetingsOnDate));

            }
        });
    }

    /**
     *
     * The purpose of this method is to check if there are no meetings, if so add a message.
     * @param meetingsOnDate - used to display meetings names, or empty message on listView.
     * @return - return array of strings.
     */
    private ArrayList<String> checkIfNoMeetings(ArrayList<String> meetingsOnDate){
        if(meetingsOnDate.size() == 0){
            meetingsOnDate.add(NO_MEETINGS_DISPLAY_MESSAGE);
        }
        return meetingsOnDate;
    }
}
