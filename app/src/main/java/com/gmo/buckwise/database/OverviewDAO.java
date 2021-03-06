package com.gmo.buckwise.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.gmo.buckwise.activity.Dashboard;
import com.gmo.buckwise.implementation.ExpensesImpl;
import com.gmo.buckwise.model.Overview;
import com.gmo.buckwise.util.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            System.out.println("-----------"+overview.getDateCreated()+", "+getLatestDate());
            System.out.println("-----------"+currentDate+", "+oldDate);
            if(currentDate.after(oldDate)){
                insertOverview(overview);
            }
            else{
                updateOverview(overview);
            }
        }
        close();
    }

    public void insertOverview(Overview overview) {
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ContentValues insertValues = new ContentValues();
        insertValues.put(MySQLiteHelper.COLUMN_AVERAGE_NET_INCOME, overview.getAverageNetIncome());
        insertValues.put(MySQLiteHelper.COLUMN_NET_INCOME, overview.getNetIncome());
        insertValues.put(MySQLiteHelper.COLUMN_LAST_MONTH_NET_INCOME, overview.getLastMonthNetIncome());
        insertValues.put(MySQLiteHelper.COLUMN_INCOME, overview.getIncome());
        insertValues.put(MySQLiteHelper.COLUMN_EXPENSES, overview.getExpenses());
        insertValues.put(MySQLiteHelper.COLUMN_BANK, overview.getBank());
        insertValues.put(MySQLiteHelper.COLUMN_DATE, overview.getDateCreated());
        database.insert("overview", null, insertValues);

        close();
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
        else {
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
        String latestDate = getLatestDate();

        if(latestDate != null) {
            int latestMonth = Integer.parseInt(Arrays.asList(latestDate.split("-")).get(1));
            int latestYear = Integer.parseInt(Arrays.asList(latestDate.split("-")).get(0));
            int currentMonth = Integer.parseInt(Arrays.asList(Util.getCurrentDateTime().split("-")).get(1));
            int currentYear = Integer.parseInt(Arrays.asList(Util.getCurrentDateTime().split("-")).get(0));

            String sql = "SELECT * FROM overview ORDER BY date DESC LIMIT 1";
            Cursor cursor = database.rawQuery(sql, null);
            ExpensesImpl expensesImpl = new ExpensesImpl(Dashboard.context);

            if ((currentMonth > latestMonth)) {
                //new month, reset
                latestOverview.setNetIncome(0);
                latestOverview.setIncome(0);
                latestOverview.setExpenses(0);
                latestOverview.setDateCreated(Util.getCurrentDateTime());
                if (cursor != null && cursor.moveToFirst()) {
                    latestOverview.setBank(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_BANK)));
                    latestOverview.setId(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID) + 1);
                    latestOverview.setAverageNetIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_AVERAGE_NET_INCOME)));
                    latestOverview.setLastMonthNetIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_LAST_MONTH_NET_INCOME)));
                }

            } else if (currentMonth < latestMonth) {
                if (currentYear > latestYear) {
                    //new year, reset
                    latestOverview.setNetIncome(0);
                    latestOverview.setIncome(0);
                    latestOverview.setExpenses(0);
                    latestOverview.setDateCreated(Util.getCurrentDateTime());
                    if (cursor != null && cursor.moveToFirst()) {
                        latestOverview.setBank(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_BANK)));
                        latestOverview.setId(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID) + 1);
                        latestOverview.setAverageNetIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_AVERAGE_NET_INCOME)));
                        latestOverview.setLastMonthNetIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_LAST_MONTH_NET_INCOME)));
                    }
                }
            } else if (currentMonth == latestMonth) {
                if (cursor != null && cursor.moveToFirst()) {
                    latestOverview.setNetIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NET_INCOME)));
                    latestOverview.setIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INCOME)));
                    latestOverview.setBank(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_BANK)));
                    latestOverview.setExpenses(expensesImpl.getTotalExpensesAmount(Util.getCurrentDateTime()));
                    latestOverview.setDateCreated(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_DATE)));
                    latestOverview.setId(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
                    latestOverview.setAverageNetIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_AVERAGE_NET_INCOME)));
                    latestOverview.setLastMonthNetIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_LAST_MONTH_NET_INCOME)));
                }
            }
        }
        close();
        return latestOverview;
    }

    public Overview getSpecificOverview(String date) {
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Overview overview = new Overview();
        String sql = "SELECT * FROM overview WHERE date = '"+date+"';";
        System.out.println("QUERY: "+sql);
        Cursor cursor = database.rawQuery(sql, null);

        if(cursor != null && cursor.moveToFirst()){
            overview.setNetIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NET_INCOME)));
            overview.setIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INCOME)));
            overview.setBank(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_BANK)));
            overview.setExpenses(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EXPENSES)));
            overview.setDateCreated(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_DATE)));
            overview.setId(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
            overview.setAverageNetIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_AVERAGE_NET_INCOME)));
            overview.setLastMonthNetIncome(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.COLUMN_LAST_MONTH_NET_INCOME)));
        }

        System.out.println("FROM DATABASE");
        System.out.println(overview.toString());

        close();
        return overview;
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

    public String getLastNetIncomeForMonth(String month) {
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String netIncome = "";
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String sql = "SELECT MAX(strftime('%d', date)), net_income FROM overview WHERE strftime('%Y', date) = '"+year+"' AND strftime('%m', date) = '"+month+"' ORDER BY strftime('%d', date) DESC;";
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            netIncome = cursor.getString(1);
        }

        database.close();
        return netIncome;
    }

    public ArrayList<String> getPastMonthsWithDataThisYear() {
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ArrayList<String> months = new ArrayList<String>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String sql = "SELECT DISTINCT strftime('%m', date) FROM overview WHERE strftime('%Y', date) = '"+year+"' ORDER BY strftime('%m', date) ASC;";
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()) {
                months.add(cursor.getString(0));
                cursor.moveToNext();
            }
        }
        database.close();
        return months;
    }

    public String getNetIncomeFromLastMonth() {
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String netIncomeLastMonth = "";
        String lastMonth = "";
        String sql = "SELECT DISTINCT strftime('%m', date) FROM overview WHERE strftime('%Y', date) = '"+year+"' ORDER BY strftime('%m', date) DESC";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToFirst() && cursor.getCount() >= 2) {
            cursor.moveToNext();
            lastMonth = cursor.getString(0);
        }

        if(lastMonth != null) {
            sql = "SELECT MAX(strftime('%d', date)), net_income FROM overview WHERE strftime('%Y', date) = '" + year + "' AND strftime('%m', date) = '" + lastMonth + "'";
            cursor = database.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    netIncomeLastMonth = cursor.getString(1);
                    cursor.moveToNext();
                }
            }
        }

        close();
        return netIncomeLastMonth;
    }
}
