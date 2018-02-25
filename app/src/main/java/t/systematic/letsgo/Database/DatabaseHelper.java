package t.systematic.letsgo.Database;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import t.systematic.letsgo.Meeting.Meeting;

import java.util.ArrayList;

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



    public void validateUser(final String username, final String password, final OnGetDataListener listener){
        DatabaseReference userRef = ref.child("users").child(username);

        ref.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(dataSnapshot.exists()){
                    if(dataSnapshot.getKey().equals(username)){
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
        String username = user.getUsername();

        //Add user and values to database
        ref.child("users").child(username).setValue(username);
        ref.child("users").child(username).child("email").setValue(user.getEmail_addr());
        ref.child("users").child(username).child("phone").setValue(user.getPhone_number());
        //TODO add password

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
     * @param allMeetingNames - all meetings of a user.
     * @param listener - listener.
     */
    public void getUserMeetings(final ArrayList<String> allMeetingNames, final OnGetDataListener listener){
        final ArrayList<Meeting> allUserMeetings = new ArrayList<Meeting>();
        final int numOfMeetings = allMeetingNames.size();

        /* Pull all meetings in ArrayList from DB and convert into a Meeting object. */
        for(int i = 0; i < numOfMeetings; i++){
            ref.child("meetings").child(allMeetingNames.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        String meetingName = dataSnapshot.child("meetingName").getValue().toString();
                        String calendarValues = dataSnapshot.child("startTime").getValue().toString();

                        /* Set date into Calendar object. */
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                        try {
                            calendar.setTime(sdf.parse(calendarValues));// all done
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        /* Set meeting params. */
                        Location location = new Location("meetingLocation");
                        location.setLongitude(Double.parseDouble(dataSnapshot.child("Long").getValue().toString()));
                        location.setLatitude(Double.parseDouble(dataSnapshot.child("Lat").getValue().toString()));

                        ArrayList<String> participants = new ArrayList<String>();
                        for(DataSnapshot dbParticipants : dataSnapshot.child("participants").getChildren()){
                            participants.add(dbParticipants.getValue().toString());
                        }
                        listener.onSuccess_initializeUserMeetings(new Meeting(meetingName, participants, calendar, location), numOfMeetings);
                        //
                    }else{
                        listener.onFailure("Failed to pull user meetings from Firebase.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onFailure(databaseError.toString());
                }
            });


        }
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

}
