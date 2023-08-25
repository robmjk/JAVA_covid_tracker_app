package com.example.mycovidtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mycovidtracker.database.CovidBaseHelper;
import com.example.mycovidtracker.database.CovidCursorWrapper;
import com.example.mycovidtracker.database.CovidDbSchema.CovidTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CovidLab {
    private static CovidLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CovidLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CovidLab(context);
        }
        return sCrimeLab;
    }

    private CovidLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CovidBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addCovid(Covid c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(CovidTable.NAME, null, values);
    }

    public List<Covid> getCovids() {
        // return new ArrayList<>();
        List<Covid> covids = new ArrayList<>();

        CovidCursorWrapper cursor = queryCovids(null,
                null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                covids.add(cursor.getCovid());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return covids;
    }

    public Covid getCovid(UUID id) {
        CovidCursorWrapper cursor = queryCovids(
                CovidTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCovid();
        } finally {
            cursor.close();
        }
    }
    public File getPhotoFile(Covid covid) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir,
                covid.getPhotoFilename());
    }

    public void updateCovid(Covid covid) {
        String uuidString = covid.getId().toString();
        ContentValues values = getContentValues(covid);
        mDatabase.update(CovidTable.NAME, values,
                CovidTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    private CovidCursorWrapper queryCovids(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CovidTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new CovidCursorWrapper(cursor);
    }

    private static ContentValues
    getContentValues(Covid covid) {
        ContentValues values = new ContentValues();
        values.put(CovidTable.Cols.UUID,
                covid.getId().toString());
        values.put(CovidTable.Cols.TITLE,
                covid.getTitle());
        values.put(CovidTable.Cols.DATE,
                covid.getDate().getTime());
        values.put(CovidTable.Cols.SOLVED,
                covid.isSolved() ? 1 : 0);
        values.put(CovidTable.Cols.SUSPECT,
                covid.getSuspect());

        return values;
    }
}