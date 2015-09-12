package com.gmo.buckwise.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gmo.buckwise.model.Overview;
import com.gmo.buckwise.util.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by GMO on 5/29/2015.
 */
public class OverviewDAO {
    private SQLiteDatabase database;
    private MySQLiteHelper databaseHelper;

    public OverviewDAO(Context context){
        databaseHelper = new MySQLiteHelper(context);

    }

    public void open() throws SQLException{
        database = databaseHelper.getWritableDatabase();
    }

    public void close(){
        databaseHelper.close();
    }

    public String getLatestDate(){
        String latestDate = null;
        String sql = "SELECT date FROM overview ORDER BY date DESC LIMIT 1";
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor!=null && cursor.moveToFirst()){
            latestDate = cursor.getString(0);
        }

        return latestDate;
    }

    public void processAdditionOfIncome(Overview overview){
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Date currentDate = Util.stringToDate(overview.getDateCreated());
        if(getLatestDate() == null){
            insertOverview(overview);
        }
        else{
            Date oldDate = Util.stringToDate(getLatestDate());
            if(currentDate.after(oldDate)){
                Log.d("-----Insert", "yeah");
                insertOverview(overview);
            }
            else{
                Log.d("-----Update", "Yeah");
                updateOverview(overview);
            }
        }
        close();
    }

    public void insertOverview(Overview overview) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(MySQLiteHelper.COLUMN_AVERAGE_NET_INCOME, overview.getAverageNetIncome());
        insertValues.put(MySQLiteHelper.COLUMN_NET_INCOME, overview.getNetIncome());
        insertValues.put(MySQLiteHelper.COLUMN_LAST_MONTH_NET_INCOME, overview.getLastMonthNetIncome());
        insertValues.put(MySQLiteHelper.COLUMN_INCOME, overview.getIncome());
        insertValues.put(MySQLiteHelper.COLUMN_EXPENSES, overview.getExpenses());
        insertValues.put(MySQLiteHelper.COLUMN_BANK, overview.getBank());
        insertValues.put(MySQLiteHelper.COLUMN_DATE, overview.getDateCreated());
        database.insert("overview", null, insertValues);
    }

    private void updateOverview(Overview overview) {
        String strFilter = "date = '" +getLatestDate()+"'";
        ContentValues updateValues = new ContentValues();
        updateValues.put("income", overview.getIncome());
        updateValues.put("net_income", overview.getNetIncome());
        database.update("overview", updateValues, strFilter, null);
    }

    public void updateNetIncome(double totalExpensesAmount){
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "SELECT income FROM overview ORDER BY date DESC LIMIT 1";
        Cursor cursor = database.rawQuery(sql, null);
        double latestIncome = 0.0;
        if(cursor!=null && cursor.moveToFirst()){
            latestIncome = cursor.getDouble(0);
        }

        double newNetIncome = latestIncome - totalExpensesAmount;
        if(getLatestDate() != null){
            String strFilter = "date = '"+getLatestDate()+"'";
            ContentValues updateValues = new ContentValues();
            updateValues.put("net_income", newNetIncome);
            database.update("overview", updateValues, strFilter, null);
        }
        else{
            Overview overview = new Overview();
            overview.setNetIncome(newNetIncome);
            overview.setDateCreated(Util.getCurrentDateTime());
            insertOverview(overview);
        }
        close();
    }

    public Overview getLatestOverview() {
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Overview latestOverview = new Overview();
        String sql = "SELECT * FROM overview ORDER BY date DESC LIMIT 1";
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor != null && cursor.moveToFirst()){
            latestOverview.setNetIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NET_INCOME)));
            latestOverview.setIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INCOME)));
            latestOverview.setBank(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_BANK)));
            latestOverview.setExpenses(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EXPENSES)));
            latestOverview.setDateCreated(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_DATE)));
            latestOverview.setId(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
            latestOverview.setAverageNetIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_AVERAGE_NET_INCOME)));
            latestOverview.setLastMonthNetIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_LAST_MONTH_NET_INCOME)));
        }
        close();
        return latestOverview;
    }

    public void printDatabase(String tableName){
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        String[] columnArray = cursor.getColumnNames();
        String colNames = "";
        for(int i = 0; i<columnArray.length; i++){
            String formattedCol = "     |   "+columnArray[i];
            colNames += formattedCol;
        }
        int colCount = columnArray.length;
        System.out.println("---------------------------------------OVERVIEW------------------------------------");
        System.out.println(colNames+"   |   ");

        String sql = "SELECT * FROM "+tableName;
        cursor = database.rawQuery(sql, null);
        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String row = "";
                for (int i = 0; i < colCount; i++) {
                    String rowItem = cursor.getString(i);
                    int rowValLength = 0;
                    if (rowItem != null) {
                        rowValLength = rowItem.length();
                    }
                    String formattedRow = "";
                    if (rowValLength <= 10) {
                        formattedRow = String.format("%-15s", cursor.getString(i));
                    } else if (rowValLength > 10) {
                        formattedRow = String.format("%-40s", cursor.getString(i));
                    }
                    row += formattedRow;
                }
                cursor.moveToNext();
                System.out.println(row);
            }
        }
        cursor.close();
        close();
    }
}
