package com.example.mycovidtracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mycovidtracker.database.CovidDbSchema.CovidTable;

public class CovidBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME =
            "covidBase.db";
    public CovidBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CovidTable.NAME+ "(" +

                " _id integer primary key autoincrement, " +

                CovidTable.Cols.UUID + ", " +

                CovidTable.Cols.TITLE + ", " +

                CovidTable.Cols.DATE + ", " +

                CovidTable.Cols.SOLVED + ", " +

                CovidTable.Cols.SUSPECT +

                ")"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int
            oldVersion, int newVersion) {
    }
}
