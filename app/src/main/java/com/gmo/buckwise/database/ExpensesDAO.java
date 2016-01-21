package com.gmo.buckwise.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gmo.buckwise.model.Expense;
import com.gmo.buckwise.util.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by GMO on 6/6/2015.
 */
public class ExpensesDAO {

    private SQLiteDatabase database;
    private MySQLiteHelper databaseHelper;

    public ExpensesDAO(Context context){
        databaseHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close(){
        databaseHelper.close();
    }

    public Expense getLatestExpenses() {
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Expense latestExpense = new Expense();

        String latestDate = getLatestDate();
        int latestMonth = Integer.parseInt(Arrays.asList(latestDate.split("-")).get(1));
        int latestYear = Integer.parseInt(Arrays.asList(latestDate.split("-")).get(0));
        int currentMonth = Integer.parseInt(Arrays.asList(Util.getCurrentDateTime().split("-")).get(1));
        int currentYear = Integer.parseInt(Arrays.asList(Util.getCurrentDateTime().split("-")).get(0));

        String sql = "SELECT * FROM expenses ORDER BY date DESC LIMIT 1";
        Cursor cursor = database.rawQuery(sql, null);

        if(currentMonth > latestMonth) {
            latestExpense.setExpenseTotal(0);
            latestExpense.setExpenseCategory("");
            latestExpense.setExpenseAmount("");
            if(cursor != null && cursor.moveToFirst()) {
                latestExpense.setExpenseAverage(cursor.getDouble(cursor.getColumnIndex("expense_average")));
                latestExpense.setExpenseLastMonth(cursor.getDouble(cursor.getColumnIndex("expense_last_month")));
            }
        }
        else if (currentMonth < latestMonth){
            if(currentYear > latestYear) {
                latestExpense.setExpenseTotal(0);
                latestExpense.setExpenseCategory("");
                latestExpense.setExpenseAmount("");
                if(cursor != null && cursor.moveToFirst()) {
                    latestExpense.setExpenseAverage(cursor.getDouble(cursor.getColumnIndex("expense_average")));
                    latestExpense.setExpenseLastMonth(cursor.getDouble(cursor.getColumnIndex("expense_last_month")));
                }
            }
        }
        else if(currentMonth == latestMonth) {
            if(cursor != null && cursor.moveToFirst()){
                latestExpense.setExpenseTotal(cursor.getDouble(cursor.getColumnIndex("expense_total")));
                latestExpense.setExpenseCategory(cursor.getString(cursor.getColumnIndex("expense_categories")));
                latestExpense.setExpenseAmount(cursor.getString(cursor.getColumnIndex("expense_amounts")));
                latestExpense.setExpenseAverage(cursor.getDouble(cursor.getColumnIndex("expense_average")));
                latestExpense.setExpenseLastMonth(cursor.getDouble(cursor.getColumnIndex("expense_last_month")));
            }
        }

        close();
        return latestExpense;
    }

    public double getTotalExpensesAmount(){
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double totalExpensesAmount = 0.00;
        String sql = "SELECT expense_total FROM expenses ORDER BY date DESC LIMIT 1";
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor != null && cursor.moveToFirst()){
            totalExpensesAmount = cursor.getDouble(0);
        }
        close();
        return totalExpensesAmount;
    }

    public double getTotalExpensesAmount(String date) {
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double totalExpensesAmount = 0.00;
        String sql = "SELECT expense_total FROM expenses WHERE date = '"+date+"';";
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor != null && cursor.moveToFirst()){
            totalExpensesAmount = cursor.getDouble(0);
        }
        close();
        return totalExpensesAmount;
    }

    public String getLatestDate(){
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String latestDate = null;
        String sql = "SELECT date FROM expenses ORDER BY date DESC LIMIT 1";
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor!=null && cursor.moveToFirst()){
            latestDate = cursor.getString(0);
        }

        return latestDate;
    }

    public void addOrUpdateExpense(Expense expense){
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Date currentDateForComparison = Util.stringToDate(expense.getDateCreated());
        if(getLatestDate() == null){
            insertExpense(expense);
        }
        else{
            Date latestDateForComparison = Util.stringToDate(getLatestDate());
            if(currentDateForComparison.after(latestDateForComparison)){
                expense.setDateCreated(Util.getCurrentDateTime());
                insertExpense(expense);
            }
            else {
                updateExpense(expense);
            }
        }
        close();
    }

    private void updateExpense(Expense expense) {
        String strFilter = "date = '" +getLatestDate()+"'";
        ContentValues updateValues = new ContentValues();
        updateValues.put("expense_amounts", expense.getExpenseAmounts());
        updateValues.put("expense_categories", expense.getExpenseCategories());
        updateValues.put("expense_total", expense.getExpenseTotal());
        int r = database.update("expenses", updateValues, strFilter, null);
    }

    public void insertExpense(Expense expense) {
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ContentValues insertValues = new ContentValues();
        insertValues.put(MySQLiteHelper.COLUMN_EXPENSE_CATEGORY, expense.getExpenseCategories());
        insertValues.put(MySQLiteHelper.COLUMN_EXPENSE_AMOUNT, expense.getExpenseAmounts());
        insertValues.put(MySQLiteHelper.COLUMN_DATE, expense.getDateCreated());
        insertValues.put(MySQLiteHelper.COLUMN_EXPENSES_TOTAL, expense.getExpenseTotal());
        database.insert("expenses", null, insertValues);
        close();
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
        System.out.println("-----------------------------------EXPENSES------------------------------");
        System.out.println(colNames + "   |   ");

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
                System.out.println(row);
                cursor.moveToNext();
            }
        }
        cursor.close();
        close();
    }

    public ArrayList<String> getPastMonthsWithDataThisYear() {
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ArrayList<String> months = new ArrayList<String>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String sql = "SELECT DISTINCT strftime('%m', date) FROM expenses WHERE strftime('%Y', date) = '"+year+"' ORDER BY strftime('%m', date) ASC;";
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

    public String getLatestExpenseTotalsForMonth(String month) {
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String netIncome = "";
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String sql = "SELECT MAX(strftime('%d', date)), expense_total FROM expenses WHERE strftime('%Y', date) = '"+year+"' AND strftime('%m', date) = '"+month+"' ORDER BY strftime('%d', date) DESC;";
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            netIncome = cursor.getString(1);
        }

        database.close();
        return netIncome;
    }

    public String getExpensesFromLastMonth() {
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String expensesLastMonth = "";
        String lastMonth = "";
        String sql = "SELECT DISTINCT strftime('%m', date) FROM expenses WHERE strftime('%Y', date) = '"+year+"' ORDER BY strftime('%m', date) DESC";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToFirst() && cursor.getCount() >= 2) {
            cursor.moveToNext();
            lastMonth = cursor.getString(0);
        }

        if(lastMonth != null) {
            sql = "SELECT MAX(strftime('%d', date)), expense_total FROM expenses WHERE strftime('%Y', date) = '" + year + "' AND strftime('%m', date) = '" + lastMonth + "'";
            cursor = database.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    expensesLastMonth = cursor.getString(1);
                    cursor.moveToNext();
                }
            }
        }

        close();
        return expensesLastMonth;
    }
}
