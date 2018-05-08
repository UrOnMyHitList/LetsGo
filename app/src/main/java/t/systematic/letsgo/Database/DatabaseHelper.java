package t.systematic.letsgo.Database;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import t.systematic.letsgo.Meeting.Meeting;

import java.util.ArrayList;
import java.util.Map;

import t.systematic.letsgo.UserObject.User;


public class DatabaseHelper extends FragmentActivity{
    private static DatabaseHelper mDatabaseHelper; //TODO [Ivan]: Delete?
    private FirebaseApp mFirebaseApp; //TODO [Ivan]: Delete?
    private FirebaseDatabase database;
    private DatabaseReference ref;

    public DatabaseHelper(){
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

    }

    public static DatabaseHelper getInstance() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper();
        }
        return mDatabaseHelper;
    }

    public void writeToDB(String str){
        ref.setValue(str);
    }

    public String encrypt(String x) throws Exception {
        java.security.MessageDigest digest;
        digest = java.security.MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(x.getBytes("UTF-8"));
        return digest.digest().toString();
    }

    public void addFriend(final String friendName, final String username, final String TAG, final OnGetDataListener listener) {
        ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean found = false;
                //String friendName = addFriendBox.getText().toString();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String name = snapshot.getKey();
                    if (name.equals(friendName)) {
                        //add the friend
                        //add a new key/value under friends
                        //replace below line when i figure out how to do notifications.
                        Boolean duplicate = false;
                        for (DataSnapshot subSnap: dataSnapshot.child(username).child("friends").getChildren()) {
                            if (subSnap.getValue().equals(friendName)) {
                                duplicate = true;
                                break;
                            }
                        }
                        if (!duplicate) {
                            found = true;
                            createFriendRequestNotification(friendName, username);
                            listener.onSuccess(snapshot);
                        }
                        else {
                            listener.onFailure("Friend already on list!");
                        }
                        break;
                    }
                }
                if(!found) {
                    listener.onFailure("No such user found!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadUsers:onCancelled", databaseError.toException());
            }
        });
    }

    public void removeFriend(final String friendName, final String username, final User user, final String TAG) {
        ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean found = false;
                //String friendName = addFriendBox.getText().toString();
                for(DataSnapshot snapshot: dataSnapshot.child(username).child("friends").getChildren()) {
                    String name = (String)snapshot.getValue();
                    if (name.equals(friendName)) {
                        found = true;
                        ref.child("users").child(username).child("friends").child(snapshot.getKey()).removeValue();
                        for (DataSnapshot otherSide: dataSnapshot.child(friendName).child("friends").getChildren()) {
                            if (otherSide.getValue().equals(username)) {
                                ref.child("users").child(friendName).child("friends").child(otherSide.getKey()).removeValue();
                                break;
                            }
                        }
                        break;
                    }
                }
                        /*if (!duplicate) {
                            ref.child("users").child(username).child("friends").push().setValue(friendName);
                            found = true;
                            user.addFriend(friendName);
                            // notification sending handled separately
                            // don't add if duplicate friend - DONE


                        }*/
                        /*Toast toast = Toast.makeText(getApplicationContext(), "Friend already on list!", Toast.LENGTH_LONG);
                        toast.show();
                        break;*/
                if(!found) {
                    Toast toast = Toast.makeText(getApplicationContext(), "No such user in friends list!" , Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(),"Friend removed!",Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadUsers:onCancelled", databaseError.toException());
            }
        });
    }

    public void validateUser(final String username, final String password, final OnGetDataListener listener){
        ref.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(dataSnapshot.exists()){
                    if(dataSnapshot.getKey().toLowerCase().equals(username)){
                        if(dataSnapshot.child("password").getValue().equals(password)){
                            listener.onSuccess(dataSnapshot);
                        }
                        else{
                            listener.onFailure("Password is incorrect");
                        }
                    }
                }
                else{
                    listener.onFailure("Account does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                listener.onFailure(null);
            }
        });
    }

    public void getUserInfo(String username, final OnGetDataListener listener) {
        ref.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listener.onSuccess(dataSnapshot);
                } else {
                    listener.onFailure("Failed to pull user info from Firebase.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure(databaseError.toString());
            }

        });
    }

    public void createAccount(final User user, final String password, final OnGetDataListener listener){
        final String username = user.getUsername();
        ref.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    if(dataSnapshot.getKey().equals(username)){
                        listener.onFailure("The username is taken. Please choose a different username.");
                    }
                }
                else{
                    DatabaseReference user_ref = ref.child("users").child(username);
                    //Add user and values to database
                    user_ref.setValue(username);
                    user_ref.child("email").setValue(user.getEmail_addr());
                    user_ref.child("phone").setValue(user.getPhone_number());
                    user_ref.child("password").setValue(password);
                    user_ref.child("latlng").child("latitude").setValue(user.getLatitude());
                    user_ref.child("latlng").child("longitude").setValue(user.getLongitude());
                    user_ref.child("friends").child("0").setValue("null");
                    user_ref.child("meetings").child("0").setValue("null");
                    user_ref.child("notifications").child("null").child("type").setValue("null");
                    listener.onSuccess(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                listener.onFailure(null);
            }
        });
    }

    public void changePhoneNumber(final String username, final String number, final OnGetDataListener listener){
        final DatabaseReference userRef = ref.child("users").child(username);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userRef.child("phone").setValue(number);
                    listener.onSuccess(dataSnapshot);
                }
                else{
                    listener.onFailure("Error changing phone number.");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure("Error changing phone number.");
            }
        });
    }

    /**
     * Query will be used to get all the meetings of a user. Converts JSON meeting into a Meeting as detailed
     * in meetings class.
     * @param meetingName - all meetings of a user.
     * @param listener - listener.
     */
    public void getUserMeetings(String meetingName, final OnGetDataListener listener) {

        /* Pull all meetings in ArrayList from DB and convert into a Meeting object. */
        ref.child("meetings").child(meetingName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listener.onSuccess(dataSnapshot);
                } else {
                    listener.onFailure("Error pulling user meetings. Snapshot does not exist.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure(databaseError.toString());
            }
        });
    }
    public void checkForUsername(final String username, final OnGetDataListener listener){
        ref.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    if(dataSnapshot.getKey().equals(username)){
                        listener.onFailure("The username is taken. Please choose a different username.");
                    }
                }
                else{
                    listener.onSuccess(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                listener.onFailure(null);
            }
        });
    }

    public void changeUsername(final String old_username, final String new_username, final OnGetDataListener listener){
        ref.child("users").child(old_username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    //TODO change username, not the value
                    //DatabaseReference user_ref = ref.child("users").child(old_username);
                    //user_ref.setValue(new_username);
                    //listener.onSuccess(dataSnapshot);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                listener.onFailure(null);
            }
        });
    }

    public void changePassword(final String username, final String newPassword, final OnGetDataListener listener) {

        final DatabaseReference userRef = ref.child("users").child(username);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userRef.child("password").setValue(newPassword);
                    listener.onSuccess(dataSnapshot);
                }
                else{
                    listener.onFailure("Error changing password.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure("Error changing password.");
            }
        });
    }

    public void changeMeetingLocation(final String meetingId, final LatLng location, final OnGetDataListener listener){
        final DatabaseReference meetingRef = ref.child("meetings").child(meetingId);
        meetingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    meetingRef.child("Lat").setValue(location.latitude);
                    meetingRef.child("Long").setValue(location.longitude);
                    listener.onSuccess(dataSnapshot);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure("Unable to change meeting location.");
            }
        });
    }


    public void createUpdateMeeting(String meetingId, double Lat, double Long, String admin, final String newMeetingName,
        ArrayList<String> participants, String startTime, final OnGetDataListener listener){

        /* If there is no oldMeetingName then we know we are going to create a new meeting. */
        ref.child("meetings").child(meetingId).child("Lat").setValue(Lat);
        ref.child("meetings").child(meetingId).child("Long").setValue(Long);
        ref.child("meetings").child(meetingId).child("admin").setValue(admin);
        ref.child("meetings").child(meetingId).child("meetingName").setValue(newMeetingName);
        ref.child("meetings").child(meetingId).child("participants").removeValue();
        for(int i = 0; i < participants.size(); i++){
            ref.child("meetings").child(meetingId).child("participants").child(Integer.toString(i)).setValue(participants.get(i));
        }
        ref.child("meetings").child(meetingId).child("startTime").setValue(startTime);


        //Then in success will need to send the requests to other users.

    }

    public void addMeetingToUser(final String meetingId, final String username){



        Query query = ref.child("users").child(username).child("meetings");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String lastMeetingKey = Long.toString(dataSnapshot.getChildrenCount());
                if(dataSnapshot.child("0").getValue().equals("null")){
                    lastMeetingKey = "0";
                }
                ref.child("users").child(username).child("meetings").child(lastMeetingKey).setValue(meetingId);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getUserNotifications(final String username, final OnGetDataListener listener){
        final DatabaseReference userRef = ref.child("users").child(username).child("notifications");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listener.onSuccess(dataSnapshot);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure("Error retrieving notifications.");
            }
        });
    }

    public void getMeetingName(final String meetingId, final OnGetDataListener listener){
        final DatabaseReference userRef = ref.child("meetings").child(meetingId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listener.onSuccess(dataSnapshot);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure("Error retrieving meeting name.");
            }
        });
    }

    public void checkIfUserHasNullFriend(final String username){
        ref.child("users").child(username).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("0").exists()){
                    ref.child("users").child(username).child("friends").child("0").removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void notificationRepliedYes(final String username, final String id, final OnGetDataListener listener){
        final DatabaseReference userRef = ref.child("users").child(username).child("notifications").child(id);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.getChildrenCount() == 1.0){
                        ref.child("users").child(username).child("notifications").child("null").child("type").setValue("null");
                    }
                    if(dataSnapshot.child("type").getValue().equals("meetingRequest")){
                        addMeetingToUser(id, username);
                        ref.child("meetings").child(id).child("participants").push().setValue(username);
                    }
                    else{
                        ref.child("users").child(username).child("friends").push().setValue(id);
                        ref.child("users").child(id).child("friends").push().setValue(username);
                        checkIfUserHasNullFriend(username);
                        checkIfUserHasNullFriend(id);
                    }

                    ref.child("users").child(username).child("notifications").child(id).removeValue();
                    listener.onSuccess(dataSnapshot);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure("Error with notifications.");
            }
        });
    }

    public void notificationRepliedNo(final String username, final String id, final OnGetDataListener listener){
        final DatabaseReference userRef = ref.child("users").child(username).child("notifications");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.getChildrenCount() == 1.0){
                        ref.child("users").child(username).child("notifications").child("null").child("type").setValue("null");
                    }
                    ref.child("users").child(username).child("notifications").child(id).removeValue();
                    listener.onSuccess(dataSnapshot);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure("Error with notifications.");
            }
        });
    }

    public void hasUnreadNotifs(final String username, final OnGetDataListener listener){
        final DatabaseReference userRef = ref.child("users").child(username).child("notifications");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        if(!snapshot.child("type").getValue().toString().equals("null")){
                            if((snapshot.child("read").getValue()).toString().equals("N")){
                                listener.onSuccess(dataSnapshot);
                                return;
                            }
                        }
                    }
                    listener.onFailure("Read");
                }
                else{
                    //listener.onFailure("Error retrieving unread notifications.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getUserLocation(String username, final OnGetDataListener listener){
        final DatabaseReference userRef = ref.child("users").child(username).child("latlng");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addOnDataChangeListenerToUserlatlng(String username, final OnGetDataListener listener){
        final DatabaseReference userRef = ref.child("users").child(username).child("latlng");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateUserLocation(String username, Double lat, Double lng){
        DatabaseReference userRef = ref.child("users").child(username).child("latlng").child("latitude");
        userRef.setValue(lat);
        userRef = ref.child("users").child(username).child("latlng").child("longitude");
        userRef.setValue(lng);
    }

    public void removeMeeting(String meetingId){
        ref.child("meetings").child(meetingId).removeValue();
    }

    public void removeMeetingFromUser(String meetingId, String username){
        ref.child("users").child(username).child("meetings").child(meetingId).removeValue();
    }

    public void createFriendRequestNotification(String toUser, String fromUser){
        ref.child("users").child(toUser).child("notifications").child(fromUser).child("read").setValue("N");
        ref.child("users").child(toUser).child("notifications").child(fromUser).child("reply").setValue("N");
        ref.child("users").child(toUser).child("notifications").child(fromUser).child("requestor").setValue(fromUser);
        ref.child("users").child(toUser).child("notifications").child(fromUser).child("type").setValue("friendRequest");

        ref.child("users").child(toUser).child("notifications").child("null").removeValue();
    }

    public void createMeetingNotification(String toUser, String fromUser, String meetingId){

        ref.child("users").child(toUser).child("notifications").child(meetingId).child("read").setValue("N");
        ref.child("users").child(toUser).child("notifications").child(meetingId).child("reply").setValue("N");
        ref.child("users").child(toUser).child("notifications").child(meetingId).child("requestor").setValue(fromUser);
        ref.child("users").child(toUser).child("notifications").child(meetingId).child("type").setValue("meetingRequest");

        ref.child("users").child(toUser).child("notifications").child("null").removeValue();
    }

}
