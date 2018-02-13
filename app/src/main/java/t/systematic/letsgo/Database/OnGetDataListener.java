package t.systematic.letsgo.Database;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by mathe on 2/10/2018.
 */

public interface OnGetDataListener {
    //this is for callbacks
    void onSuccess(DataSnapshot dataSnapshot);
    void onFailure(String failure);
}
