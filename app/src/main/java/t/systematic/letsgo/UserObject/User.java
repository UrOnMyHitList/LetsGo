package t.systematic.letsgo.UserObject;

import android.location.Location;
import android.util.Log;
import android.widget.CalendarView;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import t.systematic.letsgo.Meeting.Meeting;

/**
 * Created by Jorge B Martinez on 2/1/2018.
 */

public class User implements Serializable{

    private String mUserName;
    private ArrayList<String> mFriends;
    private ArrayList<Meeting> mMeetings;
    private String email_addr;
    private String phone_number;

    private double mLatitude;

    private double mLongitude;
    /* Constructor */
    public User (String userName, ArrayList<String> friends, ArrayList<Meeting> meetings, String email, String phone){
        mUserName = userName;
        mFriends = friends;
        mMeetings = meetings;
        email_addr = email;
        phone_number = phone;
    }


    /* Setters */
    public void setFriends(ArrayList<String> friends){ mFriends = friends; }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }
    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }
    public void setMeetings(ArrayList<Meeting> meetings){ mMeetings = meetings; }
    public void setLocation(double latitude, double longitude) { mLatitude = latitude; mLongitude = longitude; }

    /* Getters */
    public String getUsername(){ return mUserName; }
    public String getEmail_addr(){return email_addr;}
    public String getPhone_number(){return phone_number;}
    public ArrayList<String> getFriends() { return mFriends; }
    public ArrayList<Meeting> getMeetings() { return mMeetings; }
    public int getNumberOfMeetings() { return mMeetings.size(); }
    public ArrayList<String> getAllMeetingNames(){
        ArrayList<String> meetingNames = new ArrayList<String>();
        for(int i = 0; i < mMeetings.size(); i++){
            meetingNames.add(mMeetings.get(i).getMeetingName());
        }
        return meetingNames;
    }

    public boolean hasMeetings(){
        return !mMeetings.isEmpty();
    }
    public boolean hasFriends() {return !mFriends.isEmpty();}

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public Meeting getMeeting(String meetingName){
        for(int i = 0; i < mMeetings.size(); i++){
            if(mMeetings.get(i).getMeetingName().equals(meetingName)){
                return mMeetings.get(i);
            }
        }
        return null;
    }
    public ArrayList<String> getMeetingNamesWithStartDateAt(int month, int day, int year){
        ArrayList<String> meetingsNamesToday = new ArrayList<String>();
        int numberOfMeetings = mMeetings.size();

        Calendar tempMeeting;
        for(int i = 0; i < numberOfMeetings; i++){
            tempMeeting = mMeetings.get(i).getDateTime();
            if(monthDayYearMatches(tempMeeting.get(Calendar.MONTH), month, tempMeeting.get(Calendar.DATE), day,
                    tempMeeting.get(Calendar.YEAR), year)){
                meetingsNamesToday.add(mMeetings.get(i).getMeetingName());
            }
        }
        return meetingsNamesToday;
    }
    public LatLng getLatLng(){
        return new LatLng(mLatitude, mLongitude);
    }
    /* Functions */
    public void addFriend(String newFriend){
        mFriends.add(newFriend);
    }
    public void removeFriend(String removeFriend){mFriends.remove(removeFriend);}
    public void addNewMeeting(Meeting newMeeting){
        mMeetings.add(newMeeting);
        Collections.sort(mMeetings);
    }
    private boolean monthDayYearMatches(int month1,int month2, int day1, int day2, int year1, int year2){
        if((month1 == month2) && (day1 == day2) && (year1 == year2)){
            return true;
        }
        return false;
    }
    public void removeMeeting(Meeting meeting){
        mMeetings.remove(meeting);
    }

}
