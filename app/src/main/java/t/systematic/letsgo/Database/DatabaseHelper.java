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

    public void changeUsername(final String oldUsername, final String newUsername, final OnGetDataListener listener){
        //TODO Get user and create accout for the user.
    }
}
