package android.bignerdranch.mycheckins.database;

import android.bignerdranch.mycheckins.CheckIn;
import android.bignerdranch.mycheckins.database.CheckInDbSchema.CheckInTable;
import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

public class CheckInCursorWrapper extends CursorWrapper {
    public CheckInCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public CheckIn getCheckIn() {
        String uuidString = getString(getColumnIndex(CheckInTable.Cols.UUID));
        String title = getString(getColumnIndex(CheckInTable.Cols.TITLE));
        String place = getString(getColumnIndex(CheckInTable.Cols.PLACE));
        String details =getString(getColumnIndex(CheckInTable.Cols.DETAILS));
        long date = getLong(getColumnIndex(CheckInTable.Cols.DATE));
        String suspect = getString(getColumnIndex(CheckInTable.Cols.SUSPECT));
        Double latitude = getDouble(getColumnIndex(CheckInTable.Cols.LATITUDE));
        Double longitude = getDouble(getColumnIndex(CheckInTable.Cols.LONGITUDE));

        CheckIn checkIn = new CheckIn(UUID.fromString(uuidString));
        checkIn.setTitle(title);
        checkIn.setDate(new Date(date));
        checkIn.setSuspect(suspect);
        checkIn.setPlace(place);
        checkIn.setDetails(details);
        checkIn.setLatitude(latitude);
        checkIn.setLongitude(longitude);

        return checkIn;
    }
}
