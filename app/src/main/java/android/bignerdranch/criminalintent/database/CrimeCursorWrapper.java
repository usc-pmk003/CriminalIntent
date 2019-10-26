package android.bignerdranch.criminalintent.database;

import android.bignerdranch.criminalintent.Crime;
import android.bignerdranch.criminalintent.database.CrimeDbSchema.CrimeTable;
import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        String place = getString(getColumnIndex(CrimeTable.Cols.PLACE));
        String details =getString(getColumnIndex(CrimeTable.Cols.DETAILS));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        Double latitude = getDouble(getColumnIndex(CrimeTable.Cols.LATITUDE));
        Double longitude = getDouble(getColumnIndex(CrimeTable.Cols.LONGITUDE));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSuspect(suspect);
        crime.setPlace(place);
        crime.setDetails(details);
        crime.setLatitude(latitude);
        crime.setLongitude(longitude);

        return crime;
    }
}
