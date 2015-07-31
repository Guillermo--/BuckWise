package com.gmo.buckwise.activity;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.gmo.buckwise.model.Expense;
import com.gmo.buckwise.R;
import com.gmo.buckwise.implementation.ExpensesImpl;
import com.gmo.buckwise.model.ExpenseListArrayAdapter;
import com.gmo.buckwise.model.NavigationDrawerArrayAdapter;
import com.gmo.buckwise.model.NavigationDrawerItemClickListener;
import com.gmo.buckwise.util.Util;

import java.util.ArrayList;
import java.util.Map;


public class Expenses extends AppCompatActivity {

    ListView navigationDrawerItems;
    ListView mDrawerList;
    public static DrawerLayout mDrawerLayout;
    TextView navigationDrawerTitle;
    TextView expensesTotalTitle;
    public static TextView expensesTotalAmount;
    TextView expensesAverageTitle;
    TextView expensesAverageAmount;
    TextView expensesLastMonthTitle;
    TextView expensesLastMonthAmount;
    Expense expense;
    public static Context context;
    TextView expenseHelperMessage;
    ImageButton addExpenseButton;
    public static ListView expensesList;
    ExpenseListArrayAdapter adapter;
    Map<String, Double> listValues;
    PieChart pieChart;
    ImageButton pieChartButton;
    RelativeLayout pieChartContainer;
    Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        context = this;
        initializeViews();
        setUpNavigationDrawer();
        getExpenseData();
        setExpenseData();
        handleAddExpenseButton();
        handleHelperMessage();
        handleExpenseList();
        handleHamburgerIcon();
        setTypefaces();
        handlePieChart();
        handlePieChartButton();
    }

    private void handleExpenseList() {
        adapter = new ExpenseListArrayAdapter(expense.getExpenseCategoryAndAmount(), context);
        expensesList.setAdapter(adapter);

        expensesList.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (mLastFirstVisibleItem < firstVisibleItem) {
                    addExpenseButton.animate().translationY(addExpenseButton.getHeight() * 2);
                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    addExpenseButton.animate().translationY(0);
                }
                mLastFirstVisibleItem = firstVisibleItem;
            }
        });
    }

    private void handleHelperMessage() {
        listValues = expense.getExpenseCategoryAndAmount();
        if(listValues.keySet().isEmpty()){
            expenseHelperMessage.setVisibility(View.VISIBLE);
            expensesList.setVisibility(View.GONE);
        }
        else{
            expenseHelperMessage.setVisibility(View.GONE);
            expensesList.setVisibility(View.VISIBLE);
        }
    }

    private void initializeViews(){
        navigationDrawerTitle = (TextView)findViewById(R.id.navigationDrawer_title);
        expensesTotalTitle = (TextView)findViewById(R.id.expenses_total_title);
        expensesTotalAmount = (TextView)findViewById(R.id.expenses_total_amount);
        expensesAverageTitle = (TextView)findViewById(R.id.expenses_average_title);
        expensesAverageAmount = (TextView)findViewById(R.id.expenses_average_amount);
        expensesLastMonthTitle = (TextView)findViewById(R.id.expenses_lastMonth_title);
        expensesLastMonthAmount = (TextView)findViewById(R.id.expenses_lastMonth_amount);
        expenseHelperMessage = (TextView)findViewById(R.id.expenses_helper_message);
        addExpenseButton = (ImageButton)findViewById(R.id.expenses_addExpenseButton);
        expensesList = (ListView)findViewById(R.id.expenses_list);
        pieChartButton = (ImageButton)findViewById(R.id.expenses_iconPieChart);
        pieChartContainer = (RelativeLayout)findViewById(R.id.expenses_pieChart_container);

        util = new Util();
    }

    private void setExpenseData(){
        expensesTotalAmount.setText(util.doubleToCurrency(expense.getExpenseTotal()));
        expensesAverageAmount.setText(util.doubleToCurrency(expense.getExpenseAverage()));
        expensesLastMonthAmount.setText(util.doubleToCurrency(expense.getExpenseLastMonth()));
    }

    private void getExpenseData(){
        ExpensesImpl expenseImpl = new ExpensesImpl(context);
        expense = expenseImpl.getLatestExpenses();
    }

    private void setTypefaces(){
        navigationDrawerTitle.setTypeface(util.typefaceRobotoLight);
        expensesTotalTitle.setTypeface(util.typefaceRobotoLight);
        expensesTotalAmount.setTypeface(util.typefaceRobotoLight);
        expensesAverageTitle.setTypeface(util.typefaceRobotoLight);
        expensesAverageAmount.setTypeface(util.typefaceRobotoLight);
        expensesLastMonthTitle.setTypeface(util.typefaceRobotoLight);
        expensesLastMonthAmount.setTypeface(util.typefaceRobotoLight);
        expenseHelperMessage.setTypeface(util.typefaceRobotoMedium);
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
        ImageButton drawerButton = (ImageButton) findViewById(R.id.expense_iconHamburger);
        drawerButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                    }
                });
    }

    private void handleAddExpenseButton(){
        addExpenseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                final View inputDialog = layoutInflater.inflate(R.layout.inputdialog_add_expenses, null);
                ((TextView)inputDialog.findViewById(R.id.expenseInputDialog_title)).setTypeface(util.typefaceRobotoMedium);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(inputDialog);

                final AlertDialog alertDialog = alertDialogBuilder.create();
                Button add = (Button) inputDialog.findViewById(R.id.expenseInputDialog_buttonAdd);
                Button cancel = (Button) inputDialog.findViewById(R.id.expenseInputDialog_buttonCancel);
                final EditText inputCategory = (EditText) inputDialog.findViewById(R.id.expenseInputDialog_inputCategory);
                final EditText inputAmount = (EditText) inputDialog.findViewById(R.id.expenseInputDialog_inputAmount);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ExpensesImpl expenseImpl = new ExpensesImpl(context);
                        expense = expenseImpl.addCategoryAndAmount(expense, inputCategory.getText().toString(), inputAmount.getText().toString());
                        alertDialog.dismiss();
                        handleHelperMessage();
                        adapter.refreshListData(listValues);
                        ((BaseAdapter) expensesList.getAdapter()).notifyDataSetChanged();
                        expensesTotalAmount.setText(util.doubleToCurrency(expense.getExpenseTotal()));
                    }
                });
                alertDialog.show();
            }
        });
    }

    private void handlePieChartButton(){
        pieChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float offset = (float) expensesList.getMeasuredWidth() * -2;
                if(expensesList.getVisibility() == View.VISIBLE) {
                    pieChartButton.setImageResource(R.drawable.ic_format_list_bulleted_grey600_24dp);
                    expensesList.animate().setDuration(500).translationX(offset).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            addExpenseButton.animate().translationY(addExpenseButton.getHeight() * 2);
                        }
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            handlePieChart();
                            pieChartContainer.setVisibility(View.VISIBLE);
                            expensesList.setVisibility(View.GONE);
                        }
                        @Override
                        public void onAnimationCancel(Animator animator) {}
                        @Override
                        public void onAnimationRepeat(Animator animator) {}
                    });
                }
                else {
                    pieChartButton.setImageResource(R.drawable.ic_chart_arc_grey600_24dp);
                    expensesList.animate().setDuration(500).translationX(0.0f).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            addExpenseButton.animate().translationY(0);
                            //pieChart.animateX(1500, Easing.EasingOption.EaseInCirc);
                            pieChartContainer.setVisibility(View.GONE);
                            expensesList.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onAnimationEnd(Animator animator) {}
                        @Override
                        public void onAnimationCancel(Animator animator) {}
                        @Override
                        public void onAnimationRepeat(Animator animator) {}
                    });
                }
            }
        });
    }

    private void handlePieChart(){
        pieChart = (PieChart)findViewById(R.id.expenses_pieChart);
        pieChart.highlightValues(null);
        pieChart.setDescription("");
        pieChart.animateX(1500, Easing.EasingOption.EaseOutCirc);
        setPieData();
    }

    private void setPieData(){
        ExpensesImpl expenseImpl = new ExpensesImpl(context);
        expense = expenseImpl.getLatestExpenses();
        ArrayList<Entry> yValues = new ArrayList<Entry>();//amount
        ArrayList<String> xValues = new ArrayList<String>();//category

        Map<String, Double> map = expense.getExpenseCategoryAndAmount();
        int index = 0;
        for(String category : map.keySet()){
            xValues.add(category);
            yValues.add(new Entry((float) map.get(category).doubleValue(), index));
            index++;
        }

        PieDataSet dataSet = preparePieChartDataSet(yValues);
        PieData data = preparePieChartData(xValues, dataSet);
        pieChart.setData(data);
    }

    private PieData preparePieChartData(ArrayList<String> xValues, PieDataSet dataSet) {
        PieData data = new PieData(xValues, dataSet);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(util.typefaceRobotoLight);
        return data;
    }

    private PieDataSet preparePieChartDataSet(ArrayList<Entry> yValues) {
        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        ArrayList<Integer> colors = setPieChartColors();
        dataSet.setColors(colors);
        return dataSet;
    }

    private ArrayList<Integer> setPieChartColors() {
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.parseColor("#26A69A"));
        colors.add(Color.parseColor("#26C6DA"));
        colors.add(Color.parseColor("#29B6F6"));
        colors.add(Color.parseColor("#D4E157"));
        colors.add(Color.parseColor("#9CCC65"));
        colors.add(Color.parseColor("#66BB6A"));
        colors.add(Color.parseColor("#FFA726"));
        colors.add(Color.parseColor("#FF7043"));
        return colors;
    }

}
