package com.gmo.buckwise.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gmo.buckwise.model.Budget;

import java.sql.SQLException;

/**
 * Created by GMO on 7/7/2015.
 */
public class BudgetsDAO {

    private SQLiteDatabase database;
    private MySQLiteHelper databaseHelper;

    public BudgetsDAO(Context context){
        databaseHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close(){
        databaseHelper.close();
    }

    public Budget getLatestBudget(){
        try{
            open();
        }catch(SQLException e){
            e.printStackTrace();
        }
        Budget budget = new Budget();
        String sql = "SELECT * FROM budgets ORDER BY date DESC LIMIT 1";
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor != null && cursor.moveToFirst()){
            budget.setCategories(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_BUDGET_CATEGORY)));
            budget.setAmountsSpent(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_BUDGET_AMOUNT_SPENT)));
            budget.setInitialAmounts(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_BUDGET_INITIAL_AMOUNT)));
            budget.setDateCreated(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_DATE)));
        }
        close();
        return budget;
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
                System.out.println(row);
                cursor.moveToNext();
            }
        }
        cursor.close();
        close();
    }

    public void createBudgetItem(Budget budget){
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(isThereAnEntryForThisDate(budget.getDateCreated())){
            deleteOldEntryInDate(budget.getDateCreated());
        }

        //Log.d("---Test", budget.getAmountsSpent().toString());

        ContentValues insertValues = new ContentValues();
        insertValues.put(MySQLiteHelper.COLUMN_BUDGET_CATEGORY, budget.getCategories());
        insertValues.put(MySQLiteHelper.COLUMN_BUDGET_INITIAL_AMOUNT, budget.getInitialAmounts());
        insertValues.put(MySQLiteHelper.COLUMN_BUDGET_AMOUNT_SPENT, budget.getAmountsSpent());
        insertValues.put(MySQLiteHelper.COLUMN_DATE, budget.getDateCreated());
        database.insert("budgets", null, insertValues);
        close();
    }

    public boolean isThereAnEntryForThisDate(String date){
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "SELECT * FROM budgets WHERE date ='"+date+"';";
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor!=null){
            close();
            return true;
        }
        close();
        return false;
    }

    public void deleteOldEntryInDate(String date){
        try{
            open();
        }catch(SQLException e){
            e.printStackTrace();
        }

        String whereClause = "date='"+date+"';";
        database.delete(MySQLiteHelper.TABLE_BUDGETS, whereClause, null);
    }

    public void updateBudgetAmountSpent(Budget budget){
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String strFilter = "date = '" +budget.getDateCreated()+"'";
        ContentValues updateValues = new ContentValues();
        updateValues.put(MySQLiteHelper.COLUMN_BUDGET_AMOUNT_SPENT, budget.getAmountsSpent());

        int r = database.update("budgets", updateValues, strFilter, null);
    }

    public void updateBudgetCategoryAndInitialAmount(Budget budget){
        try{
            open();
        }catch(SQLException e){
            e.printStackTrace();
        }

        String strFilter = "date = '"+budget.getDateCreated()+"';";
        ContentValues updateValues = new ContentValues();
        updateValues.put(MySQLiteHelper.COLUMN_BUDGET_CATEGORY, budget.getCategories());
        updateValues.put(MySQLiteHelper.COLUMN_BUDGET_INITIAL_AMOUNT, budget.getInitialAmounts());
        int r = database.update("budgets", updateValues, strFilter, null);
    }

    public void updateBudgetAllLists(Budget budget){
        try{
            open();
        }catch(SQLException e){
            e.printStackTrace();
        }

        String strFilter = "date = '"+budget.getDateCreated()+"';";
        ContentValues updateValues = new ContentValues();
        updateValues.put(MySQLiteHelper.COLUMN_BUDGET_CATEGORY, budget.getCategories());
        updateValues.put(MySQLiteHelper.COLUMN_BUDGET_INITIAL_AMOUNT, budget.getInitialAmounts());
        updateValues.put(MySQLiteHelper.COLUMN_BUDGET_AMOUNT_SPENT, budget.getAmountsSpent());
        int r = database.update("budgets", updateValues, strFilter, null);
        Log.d("---Deleted?", String.valueOf(r));
    }
}
