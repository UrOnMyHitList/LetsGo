package t.systematic.letsgo.Meeting;

import android.location.Location;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;

import t.systematic.letsgo.UserObject.User;

/**
 * Created by Jorge B Martinez on 2/1/2018.
 */

public class Meeting implements Comparable<Meeting>{

    /* Name of the meeting given by Admin of meeting.*/
    private final String mMeetingName;
    /* Name of destination - Example: McDonalds, Mount Baldy, LAX Airport
    *  Will probably be pulled from google maps if available.*/
    private String mDestination;
    private ArrayList<User> mParticipants;
    /* We can do something like the top answer below to hold the date and time.
    *  We can separate them with a character like _ to parse when we only need one.
    *
    *  https://stackoverflow.com/questions/36257085/set-date-and-desired-time-in-android
    */
    private Calendar mDateTime;
    /* Don't remember which exactly was best, can change dataType once we get there. */
    private Location mLocation;

    /* Constructor  */
    public Meeting(String meetingName, String destination, ArrayList<User> participants, Calendar dateTime,
                    Location location){
        mMeetingName = meetingName;
        mDestination = destination;
        mParticipants = participants;
        mDateTime = dateTime;
        mLocation = location;
    }

    /* Setters */
    public void setDestination(String destination){ mDestination = destination; }
    public void setParticipants(ArrayList<User> participants){ mParticipants = participants; }
    public void setDateTime(Calendar dateTime){ mDateTime = dateTime; }
    public void setLocation(Location location){ mLocation = location; }

    /* Getters*/
    public String getDestination(){ return mDestination; }
    public ArrayList<User> getParticipants(){ return mParticipants; }
    public Calendar getDateTime() { return mDateTime; }
    public Location getLocation() { return mLocation; }

    /* Functions */
    public int numberOfUsersInMeeting(){
        return mParticipants.size();
    }
    public void addUser(User newUser){
        mParticipants.add(newUser);
    }
    public void removeUser(User deleteUser){
        mParticipants.remove(deleteUser);
    }

    /* Comparable so that we can organize ArrayList<Meeting> from nearest dateTime to furthest. */
    @Override
    public int compareTo(@NonNull Meeting meeting) {
        return getDateTime().compareTo(meeting.getDateTime());
    }
}
