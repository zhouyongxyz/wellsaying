package com.example.snow.wellsaying;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Created by zhouyong on 12/28/15.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "WellSaying";
    private static final String DATABASE_NAME = "wellsaying.db";
    private static final int DATABASE_VERSION = 113;

    private Context mContext;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        loadDatas(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading settings database from version " + oldVersion + " to "
                + newVersion);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE wellsaying (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "content TEXT," +
                "author TEXT" +
                ");");
        db.execSQL("CREATE TABLE comments (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "content_id TEXT," +
                "comment TEXT," +
                "author TEXT," +
                "hits INTEGER" +
                ");");
    }

    private void loadDatas(SQLiteDatabase db) {
        SQLiteStatement stmt = null;
        try {
            stmt = db.compileStatement("INSERT OR IGNORE INTO wellsaying(content,author)"
                    + " VALUES(?,?);");
            stmt.bindString(1, "日暮诗成天又雪，与梅并作十分香");
            stmt.bindString(2, "卢梅坡");
            stmt.execute();
        }finally {
            if (stmt != null) stmt.close();
        }
    }
}
