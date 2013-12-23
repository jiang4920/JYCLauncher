package com.jyc.launcher;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.jyc.launcher.database.DatabaseManager;
import com.jyc.launcher.view.CellLayout;
import com.jyc.launcher.view.CellView;
import com.jyc.launcher.widget.WidgetManager;

public class MainActivity extends Activity implements OnLongClickListener {

    private WidgetManager mWidgetManager;

    RelativeLayout mWorkspace;

    private static final String TAG = "Launcher Widget";
    CellLayout mCellLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWorkspace = (RelativeLayout) findViewById(R.id.layout);
        mCellLayout = (CellLayout)findViewById(R.id.cell_layout);
        for(int i = 0; i < 25; i++){
        mCellLayout.addCell(new CellView(new Button(this)));
        }
        mWorkspace.setOnLongClickListener(this);
        mWidgetManager = new WidgetManager(this);
        mWidgetManager.bindActivity(this);
        // ��ʼ���Ѽ��ص�widget
        for (int widget : DatabaseManager.getInstance(this)
                .getAddedWidgetArray(this)) {
            Log.v(TAG, "db_widget:" + widget);
            mWorkspace.addView(mWidgetManager.getAppWidgetView(widget));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mWidgetManager
                .onSelectResult(mWorkspace, requestCode, resultCode, data);
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
        case R.id.layout:
            // ������������widget�б�
            mWidgetManager.selectWidget(this);
            break;
        }
        return false;
    };

}
