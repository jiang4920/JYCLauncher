package com.jyc.launcher.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {

    public static final String DB_NAME = "launcher";
    public static final String TABLE_WIDGET = "widget";
    public static final String TABLE_WIDGET_WIDGETID = "widgetID";
    public static final String TABLE_WIDGET_VISIBILITY = "visiblility";
    private static final String SQL_TABLE_WIDGET_CREATE = "create table " + TABLE_WIDGET + " ("
            + "_id INTEGER PRIMARY KEY autoincrement,"
            + TABLE_WIDGET_WIDGETID + " INTEGER NOT NULL, "
            + TABLE_WIDGET_VISIBILITY + " INTEGER" + " default 1)";
    
    private static SQLiteDatabase mSQLiteDatabase;
    private static DatabaseManager mDatabaseManager;
    private static final String TAG = "DatabaseManager";
    private static final int VERSION = 2;

    public DatabaseManager(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }
    
    
    /**
     * 重载简化构造函数
     * @param context
     */
    public DatabaseManager(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    
    public static DatabaseManager getInstance(Context context){
        if (mDatabaseManager == null) {
            mDatabaseManager = new DatabaseManager(context);
        }
        return mDatabaseManager;
    }
    
    /**
     * 获取数据库实例，这里用两个静态变量是为了APK在运行是始终只有一个SQLiteDatabase实例，
     * 在退出APK的时候能安全的关闭SQLiteDatabase；
     * 如果觉得有必要Cursor的管理可以在这个类中创建一个Cursor管理器,因为cursor也需要及时的关闭
     * ，否则会有大量的错误日志打印，不过也不必太担心， 因为系统对此做了处理，但是会比较消耗内存，健康的设计必须很好的管理SQLiteDatabase
     * 
     * @param context
     * @return 返回SQLiteDatabase实例
     */
    public static SQLiteDatabase getDatabase(Context context) {
        if (mDatabaseManager == null) {
            mDatabaseManager = new DatabaseManager(context);
        }
        if (mSQLiteDatabase == null) {
            mSQLiteDatabase = mDatabaseManager.getWritableDatabase();
        }
        return mSQLiteDatabase;
    }

    
    /**
     * 保存widgetid到数据库
     * @param context
     * @param widgetId
     */
    public void insertWidget(Context context, int widgetId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseManager.TABLE_WIDGET_WIDGETID, widgetId);
        String where = TABLE_WIDGET_WIDGETID + " = " + widgetId;
        Cursor cursor = getDatabase(context).query(TABLE_WIDGET, null, where,
                null, null, null, null);
        if (cursor != null && cursor.getCount() >= 1) {
            getDatabase(context).update(TABLE_WIDGET, values, where, null);
        } else {
            getDatabase(context).insert(TABLE_WIDGET, null, values);
        }
    }
    
    
    /**
     * 获取已加载的widget
     * @param context
     * @return
     */
    public int[] getAddedWidgetArray(Context context){
        Cursor cursor = getDatabase(context).query(TABLE_WIDGET, new String[]{TABLE_WIDGET_WIDGETID}, null, null, null, null, null);
        if(cursor != null){
            Log.v(TAG, "cursor:"+cursor.getCount());
            int[] widgets = new int[cursor.getCount()];
            int i =0;
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
                widgets[i] = cursor.getInt(cursor.getColumnIndex(TABLE_WIDGET_WIDGETID));
                ++i;
            }
            cursor.close();
            return widgets;
        }
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 在这里创建表
        db.execSQL(SQL_TABLE_WIDGET_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            // todo sth，有时在更新APK的时候数据库改变较大，需要更新某些表，在这里作相应处理，比如拷贝用户数据到新的表中
            db.execSQL("drop table "+TABLE_WIDGET);
            db.execSQL(SQL_TABLE_WIDGET_CREATE);
        }
    }

}
