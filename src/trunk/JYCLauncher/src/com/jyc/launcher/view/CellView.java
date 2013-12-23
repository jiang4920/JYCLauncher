package com.jyc.launcher.view;

import android.view.View;

public class CellView {

    private View cellView;
    
    private int cellColumn = -1;
    private int cellRow = -1;
    
    public CellView(){
    }
    public CellView(View view){
        cellView = view;
    }
    public View getCellView() {
        return cellView;
    }
    public void setCellView(View cellView) {
        this.cellView = cellView;
    }
    public int getCellColumn() {
        return cellColumn;
    }
    public void setCellColumn(int cellColumn) {
        this.cellColumn = cellColumn;
    }
    public int getCellRow() {
        return cellRow;
    }
    public void setCellRow(int cellRow) {
        this.cellRow = cellRow;
    }
    
}
