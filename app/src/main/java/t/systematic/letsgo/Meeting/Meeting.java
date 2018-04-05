package t.systematic.letsgo.Meeting;

import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import t.systematic.letsgo.UserObject.User;

/**
 * Created by Jorge B Martinez on 2/1/2018.
 */

public class Meeting implements Comparable<Meeting>, Serializable{
    /* Unique ID of meeting. */
    private final String mMeetingId;
    /* Name of the meeting given by Admin of meeting.*/
    private final String mMeetingName;
    /* All users in the meeting. */
    private ArrayList<String> mParticipants;
    /* Date info of when meeting is scheduled to take place.*/
    private Calendar mDateTime;
    /* Don't remember which exactly was best, can change dataType once we get there. */
    private Double mLat, mLong;
    /* The user name of */
    private String mAdmin;

    /* Constructor  */
    public Meeting(String meetingName, ArrayList<String> participants, Calendar dateTime,
                    Double Lat, Double Long, String meetingId, String admin){
        mMeetingName = meetingName;
        mParticipants = participants;
        mDateTime = dateTime;
        mLat = Lat;
        mLong = Long;
        mMeetingId = meetingId;
        mAdmin = admin;
    }

    /* Setters */
    public void setParticipants(ArrayList<String> participants){ mParticipants = participants; }
    public void setDateTime(Calendar dateTime){ mDateTime = dateTime; }
    public void setLocation(Location location){ mLong = location.getLongitude(); mLat = location.getLatitude(); }

    /* Getters*/
    public ArrayList<String> getParticipants(){ return mParticipants; }
    public Calendar getDateTime() { return mDateTime; }
    public Double getLong() { return mLong; }
    public Double getLat() { return mLat; }
    public String getMeetingName() { return mMeetingName; }
    public String getMeetingId() { return mMeetingId; }
    public String getAdmin() { return mAdmin; }
    public Location getLocation() {
        Location loc = new Location("MEETING_LOC");
        loc.setLatitude(mLat);
        loc.setLongitude(mLong);
        return loc;
    }
    public LatLng getLatLng(){
        return new LatLng(mLat, mLong);
    }

    /* Functions */
    public int numberOfUsersInMeeting(){
        return mParticipants.size();
    }
    public void addUser(String newUser){
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
