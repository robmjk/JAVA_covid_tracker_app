package com.example.mycovidtracker.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.mycovidtracker.Covid;
import com.example.mycovidtracker.database.CovidDbSchema.CovidTable;

import java.util.Date;
import java.util.UUID;

public class CovidCursorWrapper extends CursorWrapper {
    public CovidCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Covid getCovid() {
        String uuidString =
                getString(getColumnIndex(CovidTable.Cols.UUID));
        String title =
                getString(getColumnIndex(CovidTable.Cols.TITLE));
        long date =
                getLong(getColumnIndex(CovidTable.Cols.DATE));
        int isSolved =
                getInt(getColumnIndex(CovidTable.Cols.SOLVED));
        String suspect =
                getString(getColumnIndex(CovidTable.Cols.SUSPECT));

        Covid covid = new
                Covid(UUID.fromString(uuidString));
        covid.setTitle(title);
        covid.setDate(new Date(date));
        covid.setSolved(isSolved != 0);
        covid.setSuspect(suspect);

        return covid;
    }
}

