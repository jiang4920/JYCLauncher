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
     * 绑定Activity，widget的数据更新需要带EXTRA_APPWIDGET_ID参数请求返回一个widget的id，例如mHost.
     * createView(mContext, appWidgetId, appWidgetInfo);中的apWidgetID每次请求到的值都不一样
     * ，换句话说这个id号是唯一的，此id号对应AppWidgetManager.EXTRA_APPWIDGET_ID的参数值（
     * HOST_ID），如果id和HOST_ID对不上，数据是不会更新的。 所以之前没有从widget选择列表中加载Widget，界面没有显示任何数据
     * 
     * @param activity
     */
    public void bindActivity(Activity activity) {
        mActivity = activity;
        mHost.startListening();// 接收widgetUI更新的广播
    }

    /**
     * 弹出widget选择列表
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
     * 处理从列表中选择widget后返回的数据
     * 
     * @param container
     *            父布局
     * @param requestCode
     * @param resultCode
     *            100则返回成功
     * @param data
     *            返回的intent数据
     */
    public void onSelectResult(ViewGroup container, int requestCode,
            int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            case REQUEST_ID:
                addAppWidget(mActivity, container, data);// 加载widget
                break;
            case REQUEST_CREATE_APPWIDGET:
                container.addView(getAppWidgetView(data));// 直接加载widget到界面
                break;
            }
        }
        Log.v(TAG, "requestCode:" + requestCode + " resultCode:" + resultCode);

    }

    /**
     * 创建widget的View
     * 
     * @param appWidgetId
     *            通过@selectWidget(Activity
     *            activity)请求到的id，请求到的id如果保存下来，下次再加载的时候直接调用此函数则可完成添加widget
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
     * 加载widget，某些widget在带有configure参数，会先启动配置界面，返回后在添加到viewContainer界面上
     * 
     * @param activity
     * @param viewContainer
     *            父布局
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
            // 弹出配置界面
            Intent intent = new Intent(
                    AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidget.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            activity.startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            // 直接添加到界面
            viewContainer.addView(getAppWidgetView(data));
        }
    }

}
