package android.bignerdranch.criminalintent;

import android.bignerdranch.criminalintent.database.CheckInBaseHelper;
import android.bignerdranch.criminalintent.database.CheckInCursorWrapper;
import android.bignerdranch.criminalintent.database.CheckInDbSchema;
import android.bignerdranch.criminalintent.database.CheckInDbSchema.CheckInTable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CheckInLab {
    private static CheckInLab sCheckInLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CheckInLab get(Context context) {
        if (sCheckInLab == null) {
            sCheckInLab = new CheckInLab(context);
        }

        return sCheckInLab;
    }

    private CheckInLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CheckInBaseHelper(mContext).getWritableDatabase();
    }

    public void addCrime(CheckIn c) {
        ContentValues values = getContentValues(c);

        mDatabase.insert(CheckInDbSchema.CheckInTable.NAME, null, values);
    }

    public List<CheckIn>getCrimes() {
        List<CheckIn> checkIns = new ArrayList<>();

        CheckInCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                checkIns.add(cursor.getCheckIn());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
            return checkIns;
        }

    public CheckIn getCrime(UUID id) {

        CheckInCursorWrapper cursor = queryCrimes(CheckInDbSchema.CheckInTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCheckIn();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(CheckIn checkIn) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, checkIn.getPhotoFilename());
    }

    public void updateCrime(CheckIn checkIn) {
        String uuidString = checkIn.getId().toString();
        ContentValues values = getContentValues(checkIn);

        mDatabase.update(CheckInDbSchema.CheckInTable.NAME, values,
                CheckInDbSchema.CheckInTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private CheckInCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(CheckInDbSchema.CheckInTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new CheckInCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(CheckIn checkIn) {
        ContentValues values = new ContentValues();
        values.put(CheckInDbSchema.CheckInTable.Cols.UUID, checkIn.getId().toString());
        values.put(CheckInDbSchema.CheckInTable.Cols.TITLE, checkIn.getTitle());
        values.put(CheckInDbSchema.CheckInTable.Cols.DATE, checkIn.getDate().getTime());
        values.put(CheckInDbSchema.CheckInTable.Cols.SUSPECT, checkIn.getSuspect());
        values.put(CheckInDbSchema.CheckInTable.Cols.PLACE, checkIn.getPlace());
        values.put(CheckInDbSchema.CheckInTable.Cols.DETAILS, checkIn.getDetails());
        values.put(CheckInDbSchema.CheckInTable.Cols.LATITUDE, checkIn.getLatitude());
        values.put(CheckInDbSchema.CheckInTable.Cols.LONGITUDE, checkIn.getLongitude());

        return values;
    }
}
