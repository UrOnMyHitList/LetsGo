package t.systematic.letsgo.NotificationActivities;

/**
 * Created by mathe on 3/17/2018.
 */

public class Notification {
    private String mRequestor;
    private String mNotifType;
    private String mId;
    private String mRead;
    private String mReply;


    public Notification(String requestor, String notifType, String meetingName, String read, String reply){
        mRead = read;
        mReply = reply;
        mRequestor = requestor;
        mNotifType = notifType;
        if(notifType.equals("friendRequest"))
            mId = requestor;
        else
            mId = meetingName;
    }

    public String getRequestor() {
        return mRequestor;
    }

    public String getNotifType() {
        return mNotifType;
    }

    public String getId() {
        return mId;
    }

    public String getRead() {
        return mRead;
    }

    public void setRequestor(String mRequestor) {
        this.mRequestor = mRequestor;
    }

    public void setNotifType(String mNotifType) {
        this.mNotifType = mNotifType;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public void setRead(String mRead) {
        this.mRead = mRead;
    }

    public void setReply(String mReply) {
        this.mReply = mReply;
    }

    public String getReply() {
        return mReply;
    }
}
