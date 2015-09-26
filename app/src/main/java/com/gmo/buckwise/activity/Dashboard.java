package com.gmo.buckwise.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Dashboard extends AppCompatActivity {
    Util util;
    public static Context context;
    Overview overview;
    TextView dashboardTitle;
    TextView dateDays;
    TextView dateMonth;
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
    ImageButton calendarIcon;
    int year_x, month_x, day_x;
    int CALENDAR_DIALOG_ID = 0;
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
        handleCalendarIcon();
        printDatabase();


//        MySQLiteHelper.populateOverviewDataFromFile(context);
//        MySQLiteHelper.populateExpenseDataFromFile(context);
//        MySQLiteHelper.populateBudgetDataFromFile(context);
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
        Calendar calendar = Calendar.getInstance();
        dashboardTitle = (TextView)findViewById(R.id.dashboard_title);

        dateDays = (TextView) findViewById(R.id.dashboard_dateDays);
        dateMonth = (TextView) findViewById(R.id.dashboard_dateMonth);

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

        calendarIcon = (ImageButton)findViewById(R.id.dashboard_iconCalendar);
        year_x = calendar.get(Calendar.YEAR);
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void setTypefaces(){

        dashboardTitle.setTypeface(util.typefaceRobotoRegular);

        dateDays.setTypeface(util.typefaceRobotoLight);
        dateMonth.setTypeface(util.typefaceRobotoLight);

        netIncomeTitle.setTypeface(util.typefaceRobotoLight);
        averageNetIncomeTitle.setTypeface(util.typefaceRobotoLight);
        lastMonthNetIncomeTitle.setTypeface(util.typefaceRobotoLight);

        netIncome.setTypeface(util.typefaceRobotoLight);
        averageNetIncome.setTypeface(util.typefaceRobotoLight);
        lastMonthNetIncome.setTypeface(util.typefaceRobotoLight);

        incomeTitle.setTypeface(util.typefaceRobotoLight);
        expensesTitle.setTypeface(util.typefaceRobotoLight);
        bankTitle.setTypeface(util.typefaceRobotoLight);

        income.setTypeface(util.typefaceRobotoLight);
        expenses.setTypeface(util.typefaceRobotoLight);
        bank.setTypeface(util.typefaceRobotoLight);

        navigationDrawerTitle.setTypeface(util.typefaceBadScript, Typeface.BOLD);
    }

    public void setCurrentDate() {
        dateMonth.setText(util.getCurrentMonth());
        dateDays.setText(util.getDaysString());
    }

    public void setAlternateDate(int selectedDay, int selectedMonth, int selectedYear){
        Calendar cal = Calendar.getInstance();
        cal.set(selectedYear, selectedMonth, selectedDay);
        String monthStr = new DateFormatSymbols().getMonths()[selectedMonth];
        String dayStr = new SimpleDateFormat("EEEE").format(cal.getTime()) + ", "+selectedDay;
        //String daysStr = new DateFormatSymbols().getShortWeekdays()[selectedDay];
        dateMonth.setText(monthStr);
        dateDays.setText(dayStr);
    }

    public void getOverviewData() {
        OverviewImpl overviewImpl = new OverviewImpl(context);
        overview = overviewImpl.getLatestOverview();
    }

    public void getSpecificOverviewData(String date) {
        OverviewImpl overviewImpl = new OverviewImpl(context);
        overview = overviewImpl.getSpecificOverview(date);
    }

    public void setActivityData(Overview overview) {
        Util util = new Util();
        income.setText(util.doubleToCurrency(overview.getIncome()));
        netIncome.setText(util.doubleToCurrency(overview.getNetIncome()));
        expenses.setText(util.doubleToCurrency(overview.getExpenses()));
        bank.setText(util.doubleToCurrency(overview.getBank()));
        averageNetIncome.setText(util.doubleToCurrency(overview.getAverageNetIncome()));
        lastMonthNetIncome.setText(util.doubleToCurrency(overview.getLastMonthNetIncome()));

        ExpensesImpl expensesImpl = new ExpensesImpl(context);
        expenses.setText(util.doubleToCurrency(expensesImpl.getTotalExpensesAmount()));
    }

    public void setSpecificActivityData(Overview overview, String date) {
        Util util = new Util();
        income.setText(util.doubleToCurrency(overview.getIncome()));
        netIncome.setText(util.doubleToCurrency(overview.getNetIncome()));
        expenses.setText(util.doubleToCurrency(overview.getExpenses()));
        bank.setText(util.doubleToCurrency(overview.getBank()));
        averageNetIncome.setText(util.doubleToCurrency(overview.getAverageNetIncome()));
        lastMonthNetIncome.setText(util.doubleToCurrency(overview.getLastMonthNetIncome()));

        ExpensesImpl expensesImpl = new ExpensesImpl(context);
        expenses.setText(util.doubleToCurrency(expensesImpl.getTotalExpensesAmount(date)));
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
                dialogTitle.setTypeface(util.typefaceRobotoMedium);
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

    public void handleCalendarIcon(){
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(CALENDAR_DIALOG_ID);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(id == CALENDAR_DIALOG_ID){
            DatePickerDialog datePicker = new DatePickerDialog(this,datePickerListener, year_x, month_x, day_x);
            return datePicker;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
            year_x = selectedYear;
            month_x = selectedMonth + 1;
            day_x = selectedDay;
            String twoDigitMonth = "";
            String twoDigitDay = "";

            if(month_x < 10){
                twoDigitMonth = 0+String.valueOf(month_x);
            }
            if(day_x < 10) {
                twoDigitDay = 0+String.valueOf(day_x);
            }

            String date = year_x + "/" + twoDigitMonth + "/" + twoDigitDay;
            setAlternateDate(selectedDay, selectedMonth, selectedYear);
            getSpecificOverviewData(date);
            setSpecificActivityData(overview, date);
            Toast.makeText(getBaseContext(), "Displaying data for "+date, Toast.LENGTH_LONG).show();
        }
    };

    private void printDatabase() {
        ExpensesDAO expensesDAO = new ExpensesDAO(context);
        expensesDAO.printDatabase(MySQLiteHelper.TABLE_EXPENSES);

        OverviewDAO overviewDAO = new OverviewDAO(context);
        overviewDAO.printDatabase(MySQLiteHelper.TABLE_OVERVIEW);

        BudgetsDAO budgetsDAO = new BudgetsDAO(context);
        budgetsDAO.printDatabase(MySQLiteHelper.TABLE_BUDGETS);
    }
}

