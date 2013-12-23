package com.jyc.launcher.view;

import java.util.ArrayList;

import com.jyc.launcher.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class CellLayout extends ViewGroup {

    private static final String TAG = "CellLayout";
    private int columnCount = 0;    //列数
    private int rowCount = 0;   //行数
    
    private int mCellWidth = 0;
    private int mCellHeight = 0;  
    
    private ArrayList<CellView> mCellList;
    private boolean[][] mOccupied;
    
    public CellLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,  
                R.styleable.cell_layout);  
        columnCount = a.getInt(R.styleable.cell_layout_columnNum, 4);
        rowCount = a.getInt(R.styleable.cell_layout_rowNum, 5);
        mCellList = new ArrayList<CellView>();
        mOccupied = new boolean[columnCount][rowCount];
    }
    
    public void addCell(CellView cellView) {
        mCellList.add(cellView);
        this.addView(cellView.getCellView());
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mCellWidth = (MeasureSpec.getSize(widthMeasureSpec) - this.getPaddingLeft() - this.getPaddingRight())/columnCount;
        mCellHeight = (MeasureSpec.getSize(heightMeasureSpec) - this.getPaddingTop() - this.getPaddingBottom()) /rowCount; 
        Log.v(TAG, "width:"+mCellWidth+" rowCount:"+rowCount);
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "column:"+columnCount +" cellHeight:"+mCellHeight+" width"+this.getWidth());
        for(int i = 0; i< mCellList.size(); i++){
            CellView cellView = mCellList.get(i);
            cellView.setCellColumn(i%columnCount);
            cellView.setCellRow(i/columnCount);
            cellView.getCellView().layout(cellView.getCellColumn()*mCellWidth+this.getPaddingLeft(), cellView.getCellRow()*mCellHeight+this.getPaddingTop(), (cellView.getCellColumn()+1)*mCellWidth+this.getPaddingLeft(), (cellView.getCellRow()+1)*mCellHeight+this.getPaddingTop());
        }
    }

}
