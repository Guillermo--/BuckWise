package com.gmo.buckwise.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gmo.buckwise.model.Overview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by GMO on 5/27/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "buckwiseDB";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_OVERVIEW = "overview";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_INCOME = "income";
    public static final String COLUMN_NET_INCOME = "net_income";
    public static final String COLUMN_EXPENSES = "expenses";
    public static final String COLUMN_BANK = "bank";
    public static final String COLUMN_AVERAGE_NET_INCOME = "average_net_income";
    public static final String COLUMN_LAST_MONTH_NET_INCOME = "last_month_income";

    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_EXPENSES_TOTAL = "expense_total";
    public static final String COLUMN_EXPENSE_AMOUNT = "expense_amounts";
    public static final String COLUMN_EXPENSE_CATEGORY = "expense_categories";
    public static final String COLUMN_EXPENSE_AVERAGE = "expense_average";
    public static final String COLUMN_EXPENSE_LAST_MONTH = "expense_last_month";

    public static final String TABLE_BUDGETS = "budgets";
    public static final String COLUMN_BUDGET_CATEGORY = "budget_category";
    public static final String COLUMN_BUDGET_INITIAL_AMOUNT = "budget_initial_amount";
    public static final String COLUMN_BUDGET_AMOUNT_SPENT = "budget_amount_spent";

    public static final String TABLE_OVERVIEW_CREATE = "CREATE TABLE " + TABLE_OVERVIEW + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DATE + " DATETIME, "
            + COLUMN_INCOME + " REAL, "
            + COLUMN_NET_INCOME + " REAL, "
            + COLUMN_EXPENSES + " REAL, "
            + COLUMN_BANK + " REAL, "
            + COLUMN_AVERAGE_NET_INCOME + " REAL, "
            + COLUMN_LAST_MONTH_NET_INCOME + " REAL);";

    public static final String TABLE_EXPENSES_CREATE = "CREATE TABLE " + TABLE_EXPENSES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_EXPENSES_TOTAL + " REAL, "
            + COLUMN_DATE + " DATETIME, "
            + COLUMN_EXPENSE_CATEGORY + " TEXT, "
            + COLUMN_EXPENSE_AMOUNT + " REAL, "
            + COLUMN_EXPENSE_AVERAGE + " REAL, "
            + COLUMN_EXPENSE_LAST_MONTH + " REAL);";

    public static final String TABLE_BUDGETS_CREATE = "CREATE TABLE "+ TABLE_BUDGETS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DATE + " DATETIME, "
            + COLUMN_BUDGET_CATEGORY + " TEXT, "
            + COLUMN_BUDGET_AMOUNT_SPENT + " TEXT,"
            + COLUMN_BUDGET_INITIAL_AMOUNT + " TEXT);";


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_OVERVIEW_CREATE);
        database.execSQL(TABLE_EXPENSES_CREATE);
        database.execSQL(TABLE_BUDGETS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_OVERVIEW);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);
        onCreate(database);
    }

    public static void populateOverviewDB(){
        String file = "C:\\Users\\GMO\\AndroidStudioProjects\\BuckWise\\app\\src\\main\\overviewTestData.txt";
        BufferedReader br = null;
        String line = "";
        String delimiter = "|";

        try {
            br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                String[] object = line.split(delimiter);

                Overview overview = new Overview();
                overview.setDateCreated(object[0]);
                overview.setNetIncome(Double.valueOf(object[1]));
                overview.setExpenses(Double.valueOf(object[2]));
                overview.setLastMonthNetIncome(Double.valueOf(object[3]));

                OverviewDAO overviewDAO = new OverviewDAO(null);
                overviewDAO.insertOverview(overview);

                System.out.println("--------Data populated: Overview -----------");
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
