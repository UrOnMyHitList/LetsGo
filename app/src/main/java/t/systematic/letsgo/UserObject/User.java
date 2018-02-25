package t.systematic.letsgo.UserObject;

import android.util.Log;
import android.widget.CalendarView;

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

    /* Constructor */
    public User (String userName, ArrayList<String> friends, ArrayList<Meeting> meetings){
        mUserName = userName;
        mFriends = friends;
        mMeetings = meetings;
    }

    /* Setters */
    public void setFriends(ArrayList<String> friends){ mFriends = friends; }
    public void setMeetings(ArrayList<Meeting> meetings){ mMeetings = meetings; }

    /* Getters */
    public String getUserName(){ return mUserName; }
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
           // Log.d("GOKU3", ""+tempMeeting);
            Log.d("GOKU2", " Month " + tempMeeting.get(Calendar.MONTH) + " day " + tempMeeting.get(Calendar.DATE) + " year " + tempMeeting.get(Calendar.YEAR));
            if(monthDayYearMatches(tempMeeting.get(Calendar.MONTH), month, tempMeeting.get(Calendar.DATE), day,
                    tempMeeting.get(Calendar.YEAR), year)){
                meetingsNamesToday.add(mMeetings.get(i).getMeetingName());
            }
        }
        return meetingsNamesToday;
    }

    /* Functions */
    public void addFriend(String newFriend){
        mFriends.add(newFriend);
    }
    public void removeFriend(User removeFriend){
        mFriends.remove(removeFriend);
    }
    public void addNewMeeting(Meeting newMeeting){
        mMeetings.add(newMeeting);
        Collections.sort(mMeetings);
    }
    private boolean monthDayYearMatches(int month1,int month2, int day1, int day2, int year1, int year2){
        Log.d("GOKU", "CHANGED DATE : m1 " + month1 + " m2 " + month2 + " d1 "+ day1 + " d2 " + day2
                + " y1 " + year1 + " year2 " +year2 + " RETURN TRUE");
        if((month1 == month2) && (day1 == day2) && (year1 == year2)){
            Log.d("GOKU", "RETURN TRUE");
            return true;
        }
        return false;
    }

}
