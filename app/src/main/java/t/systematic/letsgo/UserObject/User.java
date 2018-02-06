package t.systematic.letsgo.UserObject;

import java.util.ArrayList;
import java.util.Collections;

import t.systematic.letsgo.Meeting.Meeting;

/**
 * Created by Jorge B Martinez on 2/1/2018.
 */

public class User {

    private String mUserName;
    private ArrayList<User> mFriends;
    private ArrayList<Meeting> mMeetings;

    /* Constructor */
    public User (String userName, ArrayList<User> friends, ArrayList<Meeting> meetings){
        mUserName = userName;
        mFriends = friends;
        mMeetings = meetings;
    }

    /* Setters */
    public void setFriends(ArrayList<User> friends){ mFriends = friends; }
    public void setMeetings(ArrayList<Meeting> meetings){ mMeetings = meetings; }

    /* Getters */
    public String getUserName(){ return mUserName; }
    public ArrayList<User> getFriends() { return mFriends; }
    public ArrayList<Meeting> getMeetings() { return mMeetings; }

    /* Functions */
    public void addFriend(User newFriend){
        mFriends.add(newFriend);
    }
    public void removeFriend(User removeFriend){
        mFriends.remove(removeFriend);
    }
    public void addNewMeeting(Meeting newMeeting){
        mMeetings.add(newMeeting);
        Collections.sort(mMeetings);
    }
}
