package t.systematic.letsgo.Database;

import android.support.v4.app.FragmentActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHelper extends FragmentActivity{
    private static DatabaseHelper mDatabaseHelper;
    private FirebaseApp mFirebaseApp;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    public DatabaseHelper(){
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

    }

    public void writeToDB(String str){
        ref.setValue(str);
    }

}
