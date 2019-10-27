package android.bignerdranch.mycheckins;

import java.util.Date;
import java.util.UUID;

public class CheckIn {

    private String mTitle;
    private Date mDate;
    private String mPlace;
    private String mDetails;
    private String mSuspect;

    private double mLatitude;
    private double mLongitude;

    private UUID mId;

    public UUID getId() {
        return mId;
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getPlace() {
        return mPlace;
    }

    public void setPlace(String place) {
        mPlace = place;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public CheckIn() {
        this(UUID.randomUUID());
    }

    public CheckIn(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }
}
