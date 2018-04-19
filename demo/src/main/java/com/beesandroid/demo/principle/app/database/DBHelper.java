package com.beesandroid.demo.principle.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * For more information, you can visit https://github.com/guoxiaoxing or contact me by
 * guoxiaoxingse@163.com.
 *
 * @author guoxiaoxing
 * @since 2018/4/19 下午3:37
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Table.TABLE_NAME + " (" +
                    Table._ID + " INTEGER PRIMARY KEY," +
                    Table.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    Table.COLUMN_NAME_SUBTITLE + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Table.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void insert(SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(Table.COLUMN_NAME_TITLE, "your title");

        String selection = Table.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = {"MyTitle"};

        int count = db.update(
                Table.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public void delete(SQLiteDatabase db) {
        String selection = Table.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = {"MyTitle"};
        db.delete(Table.TABLE_NAME, selection, selectionArgs);

    }

    public void query(SQLiteDatabase db) {
        String[] projection = {
                Table._ID,
                Table.COLUMN_NAME_TITLE,
                Table.COLUMN_NAME_SUBTITLE
        };

        String selection = Table.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = {"My Title"};

        String sortOrder =
                Table.COLUMN_NAME_SUBTITLE + " DESC";

        Cursor c = db.query(
                Table.TABLE_NAME,                      // The table to query
                projection,                            // The columns to return
                selection,                             // The columns for the WHERE clause
                selectionArgs,                         // The values for the WHERE clause
                null,                          // don't group the rows
                null,                           // don't filter by row groups
                sortOrder                               // The sort order
        );
        c.close();
    }
}
