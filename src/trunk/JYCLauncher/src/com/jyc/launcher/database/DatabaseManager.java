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
     * ���ؼ򻯹��캯��
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
     * ��ȡ���ݿ�ʵ����������������̬������Ϊ��APK��������ʼ��ֻ��һ��SQLiteDatabaseʵ����
     * ���˳�APK��ʱ���ܰ�ȫ�Ĺر�SQLiteDatabase��
     * ��������б�ҪCursor�Ĺ��������������д���һ��Cursor������,��ΪcursorҲ��Ҫ��ʱ�Ĺر�
     * ��������д����Ĵ�����־��ӡ������Ҳ����̫���ģ� ��Ϊϵͳ�Դ����˴������ǻ�Ƚ������ڴ棬��������Ʊ���ܺõĹ���SQLiteDatabase
     * 
     * @param context
     * @return ����SQLiteDatabaseʵ��
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
     * ����widgetid�����ݿ�
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
     * ��ȡ�Ѽ��ص�widget
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
        // �����ﴴ����
        db.execSQL(SQL_TABLE_WIDGET_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            // todo sth����ʱ�ڸ���APK��ʱ�����ݿ�ı�ϴ���Ҫ����ĳЩ������������Ӧ�������翽���û����ݵ��µı���
            db.execSQL("drop table "+TABLE_WIDGET);
            db.execSQL(SQL_TABLE_WIDGET_CREATE);
        }
    }

}
