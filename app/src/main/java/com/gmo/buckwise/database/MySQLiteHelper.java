package com.gmo.buckwise.database;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gmo.buckwise.model.Budget;
import com.gmo.buckwise.model.Expense;
import com.gmo.buckwise.model.Overview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    public static void populateOverviewDataFromFile(Context context){
        String line = "";
        BufferedReader br = null;

        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open("overviewTestData.txt");
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                String[] object = line.split(";");

                System.out.println(line);
                System.out.println(object.length);
                System.out.println(object[0]);
                System.out.println(object[1]);
                System.out.println(object[2]);
                System.out.println(object[3]);

                Overview overview = new Overview();
                overview.setDateCreated(object[0]);
                overview.setIncome(Double.valueOf(object[1]));
                overview.setNetIncome(Double.valueOf(object[2]));
                overview.setExpenses(Double.valueOf(object[3]));
                overview.setLastMonthNetIncome(Double.valueOf(object[4]));

                OverviewDAO overviewDAO = new OverviewDAO(context);
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

    public static void populateExpenseDataFromFile(Context context){
        String line = "";
        BufferedReader br = null;

        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open("expensesTestData.txt");
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                String[] object = line.split(";");

                System.out.println(line);
                System.out.println(object.length);
                System.out.println(object[0]);
                System.out.println(object[1]);
                System.out.println(object[2]);
                System.out.println(object[3]);

                Expense expense = new Expense();
                expense.setDateCreated(object[0]);
                expense.setExpenseTotal(Double.valueOf(object[1]));
                expense.setExpenseCategory(object[2]);
                expense.setExpenseAmount(object[3]);

                ExpensesDAO expensesDAO = new ExpensesDAO(context);
                expensesDAO.insertExpense(expense);

                System.out.println("--------Data populated: Expense -----------");
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

    public static void populateBudgetDataFromFile(Context context){
        String line = "";
        BufferedReader br = null;

        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open("budgetTestData.txt");
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                String[] object = line.split(";");

                System.out.println(line);
                System.out.println(object.length);
                System.out.println(object[0]);
                System.out.println(object[1]);
                System.out.println(object[2]);
                System.out.println(object[3]);

                Budget budget = new Budget();
                budget.setDateCreated(object[0]);
                budget.setCategories(object[1]);
                budget.setAmountsSpent(object[2]);
                budget.setInitialAmounts(object[3]);

                BudgetsDAO budgetsDAO = new BudgetsDAO(context);
                budgetsDAO.createBudgetItem(budget);

                System.out.println("--------Data populated: Budget -----------");
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
