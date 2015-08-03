package com.gmo.buckwise.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gmo.buckwise.R;
import com.gmo.buckwise.implementation.BudgetsImpl;
import com.gmo.buckwise.implementation.ExpensesImpl;
import com.gmo.buckwise.model.Budget;
import com.gmo.buckwise.model.BudgetListArrayAdapter;
import com.gmo.buckwise.model.Expense;
import com.gmo.buckwise.model.NavigationDrawerArrayAdapter;
import com.gmo.buckwise.model.NavigationDrawerItemClickListener;
import com.gmo.buckwise.util.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Budgets extends ActionBarActivity {

    public static Context context;
    private Budget budget;
    TextView progressBarTitle;
    public static TextView progressBarAmountAvailable;
    public static TextView progressBarAmountStartedWith;
    ImageButton addBudgetButton;
    Util util;
    BudgetListArrayAdapter adapter;
    public static ListView budgetList;
    ListView navigationDrawerItems;
    TextView navigationDrawerTitle;
    ListView mDrawerList;
    public static DrawerLayout mDrawerLayout;
    public static ProgressBar mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgets);
        context = this;

        BudgetsImpl budgetsImpl = new BudgetsImpl(context);
        budget = budgetsImpl.getLatestBudget();

        initializeViews();
        setViews();
        setTypeFaces();
        setUpProgressBar();
        setUpBudgetList();
        handleAddBudgetButton();
        setUpNavigationDrawer();

    }

    public void initializeViews(){
        progressBarTitle = (TextView)findViewById(R.id.budgets_progressBar_containerTitle);
        progressBarAmountAvailable = (TextView)findViewById(R.id.budgets_progressBar_containerAmount);
        progressBarAmountStartedWith = (TextView) findViewById(R.id.budgets_progressBar_containerInitialTotal);
        addBudgetButton = (ImageButton) findViewById(R.id.budgets_addBudgetButton);
        util = new Util();
        budgetList = (ListView)findViewById(R.id.budget_list);
        navigationDrawerTitle = (TextView)findViewById(R.id.navigationDrawer_title);
        mProgress = (ProgressBar) findViewById(R.id.budgets_progressBar);
    }

    public void setViews(){
        progressBarAmountAvailable.setText(Util.doubleToCurrency(budget.getAmountAvailable()));
        progressBarAmountStartedWith.setText(Util.doubleToCurrency(budget.getAmountStartedWith()));
    }

    public void setTypeFaces(){
        progressBarTitle.setTypeface(Util.typefaceRobotoRegular);
        progressBarAmountAvailable.setTypeface(Util.typefaceRobotoLight);
        progressBarAmountStartedWith.setTypeface(Util.typefaceRobotoLight);
    }

    private void setUpProgressBar() {
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.progressbar_background);
        mProgress.setMax((int) budget.getAmountStartedWith());
        //mProgress.setProgress((int) budget.getAmountAvailable());
        animateProgressBar((int) budget.getAmountStartedWith(), (int) budget.getAmountAvailable());
        mProgress.setProgressDrawable(drawable);
    }

    public static void animateProgressBar(int initialAmount, int amountAvailable) {
        ObjectAnimator animation = ObjectAnimator.ofInt(mProgress, "progress", initialAmount + 200, amountAvailable);
        animation.setDuration(2000);
        animation.setInterpolator(new LinearInterpolator());
        animation.start();
    }

    private void handleAddBudgetButton(){
        addBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                final View inputDialog = layoutInflater.inflate(R.layout.inputdialog_create_budget, null);
                ((TextView) inputDialog.findViewById(R.id.budgetInputDialog_title)).setTypeface(util.typefaceRobotoMedium);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(inputDialog);

                final AlertDialog alertDialog = alertDialogBuilder.create();
                Button add = (Button) inputDialog.findViewById(R.id.budgetInputDialog_buttonAdd);
                Button cancel = (Button) inputDialog.findViewById(R.id.budgetInputDialog_buttonCancel);
                final EditText inputCategory = (EditText) inputDialog.findViewById(R.id.budgetInputDialog_inputCategory);
                final EditText inputAmount = (EditText) inputDialog.findViewById(R.id.budgetInputDialog_inputAmount);
                final Button createButton = (Button) inputDialog.findViewById(R.id.budgetInputDialog_buttonAdd);
                createButton.setText("Ok");

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String inputCategoryStr = inputCategory.getText().toString();
                        String inputAmountStr = inputAmount.getText().toString();
                        if (inputCategoryStr.isEmpty()) {
                            Toast.makeText(context, "Enter a category.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (inputAmountStr.isEmpty()) {
                                inputAmountStr = "$0.00";
                            }
                            BudgetsImpl budgetsImpl = new BudgetsImpl(context);
                            Budget resultBudget = budgetsImpl.createBudget(budget, inputCategoryStr, inputAmountStr);
                            if (resultBudget == null) {
                                Toast.makeText(context, "Category already exists. No action taken.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            } else {
                                budget = resultBudget;
                                setViews();
                                adapter.refreshListData(getMapForBudgetListAdapter(budget.getCategories(), budget.getAmountsSpent(), budget.getInitialAmounts()));
                                ((BaseAdapter) budgetList.getAdapter()).notifyDataSetChanged();
                                setUpProgressBar();

                                ExpensesImpl expensesImpl = new ExpensesImpl(context);
                                Expense latestExpense = expensesImpl.getLatestExpenses();
                                latestExpense.setDateCreated(Util.getCurrentDateTime());
                                expensesImpl.addCategoryAndAmount(latestExpense, inputCategoryStr, "0.00");
                            }

                            alertDialog.dismiss();
                        }
                    }
                });
                alertDialog.show();
            }
        });
    }

    private void setUpBudgetList(){
        Map<String, Pair<String, String>> mapOfListValues = new HashMap<>();
        if(!budget.getCategories().isEmpty()) {
            mapOfListValues = getMapForBudgetListAdapter(budget.getCategories(), budget.getAmountsSpent(), budget.getInitialAmounts());
        }
        adapter = new BudgetListArrayAdapter(mapOfListValues, context);
        budgetList.setAdapter(adapter);

        budgetList.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLastFirstVisibleItem < firstVisibleItem) {
                    addBudgetButton.animate().translationY(addBudgetButton.getHeight() * 2);
                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    addBudgetButton.animate().translationY(0);
                }
                mLastFirstVisibleItem = firstVisibleItem;
            }
        });
    }

    public Map<String, Pair<String, String>> getMapForBudgetListAdapter(String categories, String amountsSpent, String initialAmountsAvailable){
        List<String> categoriesList = Arrays.asList(categories.split(","));
        List<String> amountsSpentList = Arrays.asList(amountsSpent.split(","));
        List<String> initialAmountAvailableList = Arrays.asList(initialAmountsAvailable.split(","));
        Map<String, Pair<String, String>> mapOfListValues = new HashMap<>();

        if (!categoriesList.contains("")) {
            for (int i = 0; i < categoriesList.size(); i++) {
                String initialAmount = initialAmountAvailableList.get(i);
                String amountSpent = amountsSpentList.get(i);
                Pair<String, String> amountsPair = new Pair<>(calculateActualAmountAvailable(initialAmount, amountSpent), initialAmount);
                mapOfListValues.put(categoriesList.get(i), amountsPair);
            }
        }
        return mapOfListValues;
    }

    private String calculateActualAmountAvailable(String initialAmount, String amountSpent){

        double result = Double.valueOf(initialAmount) - Double.valueOf(amountSpent);
        return String.valueOf(result);
    }

    private void setUpNavigationDrawer() {
        String[] values = new String[] {"Overview", "Expenses", "Budgets", "Settings"};
        NavigationDrawerArrayAdapter adapter = new NavigationDrawerArrayAdapter(this, values);
        navigationDrawerItems = (ListView)findViewById(R.id.navigationDrawer_items);
        navigationDrawerItems.setAdapter(adapter);
        handleHamburgerIcon();
        mDrawerList.setOnItemClickListener(new NavigationDrawerItemClickListener(mDrawerLayout, context));
    }

    private void handleHamburgerIcon() {
        mDrawerList = (ListView)findViewById(R.id.navigationDrawer_items);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.DrawerLayout);
        ImageButton drawerButton = (ImageButton) findViewById(R.id.budgets_iconHamburger);
        drawerButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                    }
                });
    }

    private void createExpenseFromBudget(String inputCategory, String inputAmount){
        ExpensesImpl expensesImpl = new ExpensesImpl(context);
        Expense latestExpense = expensesImpl.getLatestExpenses();
        latestExpense.setDateCreated(Util.getCurrentDateTime());
        expensesImpl.addCategoryAndAmount(latestExpense, inputCategory, inputAmount);
    }

    public Budget getBudget(){
        return budget;
    }

    public void setBudget(Budget budget){
        this.budget = budget;
    }
}
