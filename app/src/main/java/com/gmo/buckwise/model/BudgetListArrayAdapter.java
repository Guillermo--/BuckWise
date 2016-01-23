package com.gmo.buckwise.model;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.gmo.buckwise.R;
import com.gmo.buckwise.activity.Budgets;
import com.gmo.buckwise.implementation.BudgetsImpl;
import com.gmo.buckwise.implementation.ExpensesImpl;
import com.gmo.buckwise.util.Util;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by GMO on 7/10/2015.
 */
public class BudgetListArrayAdapter extends BaseAdapter{
    private ArrayList<Map.Entry<String, Pair<String, String>>> data;
    Context context;
    Util util = new Util();

    public BudgetListArrayAdapter(Map<String, Pair<String, String>> mapOfListValues, Context context){
        data = new ArrayList<>();
        data.addAll(mapOfListValues.entrySet());
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Map.Entry<String, Pair<String, String>> getItem(int position) {
        return (Map.Entry)data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View row;

        if(convertView == null){
            row = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_list_row_layout, parent, false);
        }else{
            row = convertView;
        }

        Map.Entry<String, Pair<String, String>> item = getItem(position);
        TextView category = (TextView)row.findViewById(R.id.budget_list_row_category);
        TextView actualAmountAvailable = (TextView)row.findViewById(R.id.budget_list_row_actualAmount);
        TextView initialAmountAvailable = (TextView)row.findViewById(R.id.budget_list_row_initialAmount);
        final ImageView editButton = (ImageView)row.findViewById(R.id.budget_list_row_iconEdit);
        category.setText(item.getKey());
        actualAmountAvailable.setText(util.doubleToCurrency(Double.parseDouble(item.getValue().first)));
        initialAmountAvailable.setText(util.doubleToCurrency(Double.parseDouble(item.getValue().second)));
        setOptionsButtonOnClickListener(editButton, row);
        category.setTypeface(Util.typefaceRobotoRegular);
        actualAmountAvailable.setTypeface(Util.typefaceRobotoLight);
        initialAmountAvailable.setTypeface(Util.typefaceRobotoLight);
        ((TextView)row.findViewById(R.id.budget_list_row_outOf)).setTypeface(Util.typefaceRobotoLight);

        return row;
    }

    private void setOptionsButtonOnClickListener(ImageView editButton, final View row) {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = inflatePopupMenu(view);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String rowName = ((TextView) row.findViewById(R.id.budget_list_row_category)).getText().toString();
                        String rowCurrentAmountSpent = ((TextView)row.findViewById(R.id.budget_list_row_actualAmount)).getText().toString();

                        if (menuItem.getTitle().equals("Log Expense")) {
                            handleAddExpense(rowName, rowCurrentAmountSpent);
                        } else if (menuItem.getTitle().equals("Edit Budget")){
                            handleEditBudget(rowName, rowCurrentAmountSpent);
                        } else if (menuItem.getTitle().equals("Delete")){
                            handleDeleteBudget(rowName);
                        } else if (menuItem.getTitle().equals("Undo")) {
                            Toast.makeText(context, "Feature not yet available.", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    private void handleAddExpense(final String rowName, final String rowCurrentAmountSpent) {
        final View inputDialog = inflateAddDialogLayout();
        final AlertDialog alertDialog = createAlertDialog(inputDialog);
        String dialogTitle = "Log Expense";
        TextView dialogTitleView = (TextView) inputDialog.findViewById(R.id.inputDialog_Add_Title);
        dialogTitleView.setTypeface(util.typefaceRobotoMedium);
        dialogTitleView.setText(dialogTitle);
        Button add = (Button) inputDialog.findViewById(R.id.inputDialog_add_buttonAdd);
        Button cancel = (Button) inputDialog.findViewById(R.id.inputDialog_add_buttonCancel);
        final EditText input = (EditText) inputDialog.findViewById(R.id.inputDialog_Add_InputAmount);
        final Button addButton = (Button) inputDialog.findViewById(R.id.inputDialog_add_buttonAdd);
        addButton.setText("Ok");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BudgetsImpl budgetsImpl = new BudgetsImpl(context);
                Budgets budgetsActivity = new Budgets();
                Budget budget = budgetsImpl.logBudgetExpense(rowName, input.getText().toString());
                refreshListData(budgetsActivity.getMapForBudgetListAdapter(budget.getCategories(), budget.getAmountsSpent(), budget.getInitialAmounts()));
                ((BaseAdapter)Budgets.budgetList.getAdapter()).notifyDataSetChanged();
                Budgets.progressBarAmountAvailable.setText(Util.doubleToCurrency(budget.getAmountAvailable()));
                Budgets.animateProgressBar(Budgets.mProgress.getProgress() - 200, (int) budget.getAmountAvailable());

                //reflect changes on Expenses activity
                ExpensesImpl expensesImpl = new ExpensesImpl(context);
                Expense latestExpense = expensesImpl.getLatestExpenses();
                expensesImpl.addCategoryAndAmount(latestExpense, rowName, input.getText().toString());


                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void handleEditBudget(final String rowName, final String rowCurrentAmountSpent){
        final View inputDialog = inflateEditDialogLayout();
        final AlertDialog alertDialog = createAlertDialog(inputDialog);
        String dialogTitle = "Edit " + rowName + " - " + rowCurrentAmountSpent;
        TextView dialogTitleView = (TextView) inputDialog.findViewById(R.id.inputDialog_edit_title);
        dialogTitleView.setText(dialogTitle);
        Button edit = (Button) inputDialog.findViewById(R.id.inputDialog_edit_buttonAdd);
        edit.setText("Edit");
        Button cancel = (Button) inputDialog.findViewById(R.id.inputDialog_edit_buttonCancel);
        final EditText inputAmount = (EditText) inputDialog.findViewById(R.id.inputDialog_edit_inputAmount);
        final TextView inputAmountTitle = (TextView)inputDialog.findViewById(R.id.inputDialog_edit_message);
        inputAmountTitle.setText("New initial amount");
        dialogTitleView.setTypeface(util.typefaceRobotoMedium);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BudgetsImpl budgetsImpl = new BudgetsImpl(context);
                Budgets budgetsActivity = new Budgets();
                Budget budget = budgetsImpl.editBudgetCategoryAndInitialAmount(rowName, inputAmount.getText().toString());
                refreshListData(budgetsActivity.getMapForBudgetListAdapter(budget.getCategories(), budget.getAmountsSpent(), budget.getInitialAmounts()));
                ((BaseAdapter)Budgets.budgetList.getAdapter()).notifyDataSetChanged();

                Budgets.progressBarAmountStartedWith.setText(Util.doubleToCurrency(budget.getAmountStartedWith()));
                Budgets.progressBarAmountAvailable.setText(Util.doubleToCurrency(budget.getAmountAvailable()));
                Budgets.mProgress.setMax((int) budget.getAmountStartedWith());
                Budgets.progressBarAmountAvailable.setText(Util.doubleToCurrency(budget.getAmountAvailable()));
                Budgets.animateProgressBar((int)budget.getAmountStartedWith()-200, (int)budget.getAmountAvailable());
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void handleDeleteBudget(final String rowName){
        final View inputDialog = inflateDeleteDialogLayout();
        final AlertDialog alertDialog = createAlertDialog(inputDialog);
        String dialogTitle = "Delete " + rowName;
        TextView dialogTitleView = (TextView) inputDialog.findViewById(R.id.alertDialog_delete_title);
        dialogTitleView.setText(dialogTitle);
        Button add = (Button) inputDialog.findViewById(R.id.alertDialog_delete_okButton);
        Button cancel = (Button) inputDialog.findViewById(R.id.alertDialog_delete_cancelButton);
        dialogTitleView.setTypeface(util.typefaceRobotoMedium);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BudgetsImpl budgetsImpl = new BudgetsImpl(context);
                Budgets budgetsActivity = new Budgets();
                Budget budget = budgetsImpl.deleteBudget(rowName);
                refreshListData(budgetsActivity.getMapForBudgetListAdapter(budget.getCategories(), budget.getAmountsSpent(), budget.getInitialAmounts()));
                ((BaseAdapter)Budgets.budgetList.getAdapter()).notifyDataSetChanged();

                //Budgets.progressBarAmountStartedWith.setText(Util.doubleToCurrency(budget.getAmountStartedWith()));
                Budgets.progressBarAmountAvailable.setText(Util.doubleToCurrency(budget.getAmountAvailable()));
                Budgets.progressBarAmountStartedWith.setText(Util.doubleToCurrency(budget.getAmountStartedWith()));
                Budgets.mProgress.setMax((int) budget.getAmountStartedWith());
                Budgets.animateProgressBar((int) budget.getAmountStartedWith() - 200, (int) budget.getAmountAvailable());

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private PopupMenu inflatePopupMenu(View view) {
        final PopupMenu popup = new PopupMenu(context, view);
        popup.getMenuInflater().inflate(R.menu.edit_budgets_popup, popup.getMenu());
        return popup;
    }

    public void refreshListData(Map<String, Pair<String,String>> map){
        data.clear();
        data.addAll(map.entrySet());
    }

    private AlertDialog createAlertDialog(View inputDialog) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(inputDialog);
        return alertDialogBuilder.create();
    }

    private View inflateAddDialogLayout() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return layoutInflater.inflate(R.layout.inputdialog_addamount, null);
    }

    private View inflateEditDialogLayout() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return layoutInflater.inflate(R.layout.inputdialog_edit_expense, null);
    }

    private View inflateDeleteDialogLayout(){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return layoutInflater.inflate(R.layout.alertdialog_delete_expense, null);
    }
}
