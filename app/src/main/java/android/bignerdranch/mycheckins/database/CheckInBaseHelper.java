package android.bignerdranch.mycheckins.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CheckInBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "checkInBase.db";

    public CheckInBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CheckInDbSchema.CheckInTable.NAME
                + "(" + " _id integer primary key autoincrement, " +
                CheckInDbSchema.CheckInTable.Cols.UUID + ", " +
                CheckInDbSchema.CheckInTable.Cols.TITLE + ", " +
                CheckInDbSchema.CheckInTable.Cols.DATE + ", " +
                CheckInDbSchema.CheckInTable.Cols.SUSPECT + "," +
                CheckInDbSchema.CheckInTable.Cols.PLACE + "," +
                CheckInDbSchema.CheckInTable.Cols.DETAILS + "," +
                CheckInDbSchema.CheckInTable.Cols.LATITUDE + "," +
                CheckInDbSchema.CheckInTable.Cols.LONGITUDE + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
