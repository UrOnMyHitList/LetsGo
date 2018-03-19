package t.systematic.letsgo.Database;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    public void changePhoneNumber(final String username, final String number, final OnGetDataListener listener){

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

    public void notificationRepliedYes(final String username, final String id, final OnGetDataListener listener){
        final DatabaseReference userRef = ref.child("users").child(username).child("notifications").child(id);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    userRef.child("read").setValue("Y");
                    userRef.child("reply").setValue("Y");
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
                        if((snapshot.child("read").getValue(String.class)).toString().equals("N")){
                            listener.onSuccess(dataSnapshot);
                            return;
                        }
                    }
                    listener.onFailure("Read");
                }
                else{
                    listener.onFailure("Error retrieving unread notifications.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure("Error retrieving unread notifications.");
            }
        });
    }

    public void changeUsername(final String oldUsername, final String newUsername, final OnGetDataListener listener){
        //TODO Get user and create accout for the user.
    }
}