package com.jyc.launcher.widget;

import com.jyc.launcher.database.DatabaseManager;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Administrator
 * 
 */
public class WidgetManager {

    private static final String TAG = "WidgetManager";

    private static final int HOST_ID = 10000;
    public static final int REQUEST_ID = 100;
    public static final int REQUEST_CREATE_APPWIDGET = 1;

    
    private Context mContext;
    AppWidgetHost mHost;
    Activity mActivity;

    public WidgetManager(Context context) {
        mContext = context;
        mHost = new AppWidgetHost(context, HOST_ID);
    }

    /**
     * ��Activity��widget�����ݸ�����Ҫ��EXTRA_APPWIDGET_ID�������󷵻�һ��widget��id������mHost.
     * createView(mContext, appWidgetId, appWidgetInfo);�е�apWidgetIDÿ�����󵽵�ֵ����һ��
     * �����仰˵���id����Ψһ�ģ���id�Ŷ�ӦAppWidgetManager.EXTRA_APPWIDGET_ID�Ĳ���ֵ��
     * HOST_ID�������id��HOST_ID�Բ��ϣ������ǲ�����µġ� ����֮ǰû�д�widgetѡ���б��м���Widget������û����ʾ�κ�����
     * 
     * @param activity
     */
    public void bindActivity(Activity activity) {
        mActivity = activity;
        mHost.startListening();// ����widgetUI���µĹ㲥
    }

    /**
     * ����widgetѡ���б�
     * 
     * @param activity
     */
    public void selectWidget(Activity activity) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                mHost.allocateAppWidgetId());
        activity.startActivityForResult(intent, REQUEST_ID);
    }

    /**
     * 
     * ������б���ѡ��widget�󷵻ص�����
     * 
     * @param container
     *            ������
     * @param requestCode
     * @param resultCode
     *            100�򷵻سɹ�
     * @param data
     *            ���ص�intent����
     */
    public void onSelectResult(ViewGroup container, int requestCode,
            int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            case REQUEST_ID:
                addAppWidget(mActivity, container, data);// ����widget
                break;
            case REQUEST_CREATE_APPWIDGET:
                container.addView(getAppWidgetView(data));// ֱ�Ӽ���widget������
                break;
            }
        }
        Log.v(TAG, "requestCode:" + requestCode + " resultCode:" + resultCode);

    }

    /**
     * ����widget��View
     * 
     * @param appWidgetId
     *            ͨ��@selectWidget(Activity
     *            activity)���󵽵�id�����󵽵�id��������������´��ټ��ص�ʱ��ֱ�ӵ��ô˺������������widget
     * @return
     */
    public View getAppWidgetView(int appWidgetId) {
        AppWidgetProviderInfo appWidgetInfo = AppWidgetManager.getInstance(
                mContext).getAppWidgetInfo(appWidgetId);

        View hostView = mHost.createView(mContext, appWidgetId, appWidgetInfo);
        Log.v(TAG, "appWidgetId:" + appWidgetId);
        hostView.setMinimumHeight(appWidgetInfo.minHeight);
        hostView.setMinimumWidth(appWidgetInfo.minWidth);
        DatabaseManager.getInstance(mContext).insertWidget(mContext, appWidgetId);
        return hostView;
    }

    public View getAppWidgetView(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras
                .getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        return getAppWidgetView(appWidgetId);
    }

    /**
     * 
     * ����widget��ĳЩwidget�ڴ���configure�����������������ý��棬���غ�����ӵ�viewContainer������
     * 
     * @param activity
     * @param viewContainer
     *            ������
     * @param data
     */
    public void addAppWidget(Activity activity, ViewGroup viewContainer,
            Intent data) {
        int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                -1);
        Log.v(TAG, "appWidgetId:" + appWidgetId);
        AppWidgetProviderInfo appWidget = AppWidgetManager
                .getInstance(mContext).getAppWidgetInfo(appWidgetId);

        Log.d("addAppWidget", "configure:" + appWidget.configure);
        if (appWidget.configure != null) {
            // �������ý���
            Intent intent = new Intent(
                    AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidget.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            activity.startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            // ֱ����ӵ�����
            viewContainer.addView(getAppWidgetView(data));
        }
    }

}
