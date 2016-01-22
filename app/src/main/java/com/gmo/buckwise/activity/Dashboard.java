package com.gmo.buckwise.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.gmo.buckwise.R;
import com.gmo.buckwise.database.BudgetsDAO;
import com.gmo.buckwise.database.ExpensesDAO;
import com.gmo.buckwise.database.MySQLiteHelper;
import com.gmo.buckwise.database.OverviewDAO;
import com.gmo.buckwise.implementation.ExpensesImpl;
import com.gmo.buckwise.implementation.OverviewImpl;
import com.gmo.buckwise.model.NavigationDrawerArrayAdapter;
import com.gmo.buckwise.model.Overview;
import com.gmo.buckwise.model.NavigationDrawerItemClickListener;
import com.gmo.buckwise.util.Util;

public class Dashboard extends AppCompatActivity {
    Util util;
    public static Context context;
    Overview overview;
    TextView dashboardTitle;
    TextView dateDay;
    TextView dateMonth;
    TextView dateDayName;
    TextView netIncome;
    TextView income;
    TextView expenses;
    TextView bank;
    TextView averageNetIncome;
    TextView lastMonthNetIncome;
    Button buttonAddToIncome;
    Button buttonOpenExpenses;
    TextView netIncomeTitle;
    TextView averageNetIncomeTitle;
    TextView lastMonthNetIncomeTitle;
    TextView incomeTitle;
    TextView expensesTitle;
    TextView bankTitle;
    TextView navigationDrawerTitle;
    ListView navigationDrawerItems;
    ListView mDrawerList;
    public static DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        context = this;
        initializeViews();
        setCurrentDate();
        getOverviewData();
        setActivityData(overview);
        handleAddIncomeButton();
        handleViewExpensesButton();
        setUpNavigationDrawer();
        setTypefaces();

        printDatabase();

        //MySQLiteHelper.populateOverviewDataFromFile(context);
        //MySQLiteHelper.populateExpenseDataFromFile(context);
        //MySQLiteHelper.populateBudgetDataFromFile(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        getOverviewData();
        setActivityData(overview);
    }

    private void setUpNavigationDrawer() {
        String[] values = new String[] {"Overview", "Expenses", "Budgets", "Analytics", "Settings"};
        NavigationDrawerArrayAdapter adapter = new NavigationDrawerArrayAdapter(this, values);
        navigationDrawerItems = (ListView)findViewById(R.id.navigationDrawer_items);
        navigationDrawerItems.setAdapter(adapter);
        setHamburgerIconClick();
        mDrawerList.setOnItemClickListener(new NavigationDrawerItemClickListener(mDrawerLayout, context));

    }

    private void setHamburgerIconClick() {
        mDrawerList = (ListView)findViewById(R.id.navigationDrawer_items);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.DrawerLayout);
        ImageButton drawerButton = (ImageButton) findViewById(R.id.expense_iconHamburger);
        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    public void initializeViews(){
        util = new Util();
        dashboardTitle = (TextView)findViewById(R.id.dashboard_title);


        dateDay = (TextView) findViewById(R.id.dashboard_dateDay);
        dateMonth = (TextView) findViewById(R.id.dashboard_dateMonth);
        dateDayName= (TextView) findViewById(R.id.dashboard_dateDayName);

        netIncome = (TextView) findViewById(R.id.dashboard_cardView_netIncomeAmount);
        averageNetIncome = (TextView) findViewById(R.id.dashboard_cardView_averageAmount);
        lastMonthNetIncome = (TextView) findViewById(R.id.dashboard_cardView_lastMonthAmount);
        netIncomeTitle = (TextView)findViewById(R.id.dashboard_cardView_netIncomeTitle);
        averageNetIncomeTitle = (TextView)findViewById(R.id.dashboard_cardView_averageTitle);
        lastMonthNetIncomeTitle = (TextView)findViewById(R.id.dashboard_cardView_lastMonthTitle);

        incomeTitle = (TextView)findViewById(R.id.dashboard_list_incomeTitle);
        expensesTitle = (TextView)findViewById(R.id.dashboard_list_expensesTitle);
        bankTitle = (TextView)findViewById(R.id.dashboard_list_bankTitle);
        income = (TextView) findViewById(R.id.dashboard_list_incomeAmount);
        expenses = (TextView) findViewById(R.id.dashboard_list_expensesAmount);
        bank = (TextView) findViewById(R.id.dashboard_list_bankAmount);

        navigationDrawerTitle = (TextView)findViewById(R.id.navigationDrawer_title);

        buttonAddToIncome = (Button) findViewById(R.id.dashboard_iconPlus);
        buttonOpenExpenses = (Button) findViewById(R.id.dashboard_iconEdit);

    }

    private void setTypefaces(){

        dashboardTitle.setTypeface(Util.typefaceRobotoRegular);

        dateDay.setTypeface(Util.typefaceRobotoThin);
        dateMonth.setTypeface(Util.typefaceRobotoThin);
        dateDayName.setTypeface(Util.typefaceRobotoThin);

        netIncomeTitle.setTypeface(Util.typefaceRobotoLight);
        averageNetIncomeTitle.setTypeface(Util.typefaceRobotoLight);
        lastMonthNetIncomeTitle.setTypeface(Util.typefaceRobotoLight);

        netIncome.setTypeface(Util.typefaceRobotoLight);
        averageNetIncome.setTypeface(Util.typefaceRobotoLight);
        lastMonthNetIncome.setTypeface(Util.typefaceRobotoLight);

        incomeTitle.setTypeface(Util.typefaceRobotoLight);
        expensesTitle.setTypeface(Util.typefaceRobotoLight);
        bankTitle.setTypeface(Util.typefaceRobotoLight);

        income.setTypeface(Util.typefaceRobotoLight);
        expenses.setTypeface(Util.typefaceRobotoLight);
        bank.setTypeface(Util.typefaceRobotoLight);

        navigationDrawerTitle.setTypeface(Util.typefaceBadScript, Typeface.BOLD);
    }

    public void setCurrentDate() {
        dateMonth.setText(util.getCurrentMonth());
        dateDay.setText(util.getDayOfMonth());
        dateDayName.setText(util.getDayOfWeek());
    }

    public void getOverviewData() {
        OverviewImpl overviewImpl = new OverviewImpl(context);
        overview = overviewImpl.getLatestOverview();
    }

    public void setActivityData(Overview overview) {

        income.setText(Util.doubleToCurrency(overview.getIncome()));
        netIncome.setText(Util.doubleToCurrency(overview.getNetIncome()));
        expenses.setText(Util.doubleToCurrency(overview.getExpenses()));

        OverviewImpl overviewImpl = new OverviewImpl(context);
        averageNetIncome.setText(Util.doubleToCurrency(Double.parseDouble(overviewImpl.calculateAverageNetIncome())));
        lastMonthNetIncome.setText(Util.doubleToCurrency(Double.parseDouble(overviewImpl.calculateNetIncomeLastMonth())));
        bank.setText(Util.doubleToCurrency(Double.parseDouble(overviewImpl.calculateInBank())));

    }

    public void handleAddIncomeButton() {
        buttonAddToIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                final View inputDialog = layoutInflater.inflate(R.layout.inputdialog_addamount, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(inputDialog);

                final AlertDialog alertDialog = alertDialogBuilder.create();
                TextView dialogTitle = (TextView) inputDialog.findViewById(R.id.inputDialog_Add_Title);
                dialogTitle.setText("Add Income");
                dialogTitle.setTypeface(Util.typefaceRobotoMedium);
                Button add = (Button) inputDialog.findViewById(R.id.inputDialog_add_buttonAdd);
                Button cancel = (Button) inputDialog.findViewById(R.id.inputDialog_add_buttonCancel);
                final EditText input = (EditText) inputDialog.findViewById(R.id.inputDialog_Add_InputAmount);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processIncomeAddition(input);
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }

    public void handleViewExpensesButton(){
        buttonOpenExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Expenses.class);
                startActivity(intent);
            }
        });
    }

    private void processIncomeAddition(EditText input) {
        String inputString = input.getText().toString();
        if(!inputString.isEmpty()) {
            calculateAndUpdateIncome(inputString);
            overview = createUpdatedOverview();
            setActivityData(overview);
        }
    }

    private Overview createUpdatedOverview() {
        OverviewImpl overviewImpl = new OverviewImpl(context);
        Overview updatedOverview = overviewImpl.createOverview(overview);
        return updatedOverview;
    }

    private void calculateAndUpdateIncome(String inputString) {
        double addToIncome = Double.parseDouble(inputString);
        double newIncomeAmount = addToIncome + overview.getIncome();
        overview.setIncome(newIncomeAmount);
    }

    private void printDatabase() {
        ExpensesDAO expensesDAO = new ExpensesDAO(context);
        expensesDAO.printDatabase(MySQLiteHelper.TABLE_EXPENSES);

        OverviewDAO overviewDAO = new OverviewDAO(context);
        overviewDAO.printDatabase(MySQLiteHelper.TABLE_OVERVIEW);

        BudgetsDAO budgetsDAO = new BudgetsDAO(context);
        budgetsDAO.printDatabase(MySQLiteHelper.TABLE_BUDGETS);
    }


}

