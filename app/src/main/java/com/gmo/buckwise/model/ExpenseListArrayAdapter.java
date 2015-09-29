package com.gmo.buckwise.model;

import android.content.Context;
import android.support.v7.app.AlertDialog;
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

import com.gmo.buckwise.R;
import com.gmo.buckwise.activity.Dashboard;
import com.gmo.buckwise.activity.Expenses;
import com.gmo.buckwise.implementation.BudgetsImpl;
import com.gmo.buckwise.implementation.ExpensesImpl;
import com.gmo.buckwise.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by GMO on 6/9/2015.
 */
public class ExpenseListArrayAdapter extends BaseAdapter {
    private final ArrayList<Map.Entry<String, Double>> mData;
    Context context;
    Util util = new Util();

    public ExpenseListArrayAdapter (Map<String, Double> map, Context context) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
        this.context = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        final View row;

        if (convertView == null) {
            row = LayoutInflater.from(parent.getContext()).inflate(R.layout.expenses_list_row_layout, parent, false);
        } else {
            row = convertView;
        }

        Map.Entry<String, String> item = getItem(position);
        TextView category = (TextView) row.findViewById(R.id.expenseList_itemName);
        TextView amount = (TextView) row.findViewById(R.id.expenseList_itemAmount);
        final ImageView editButton = (ImageView)row.findViewById(R.id.expenseList_iconEdit);
        category.setText(item.getKey());
        amount.setText(util.doubleToCurrency(Double.parseDouble(String.valueOf(item.getValue()))));
        category.setTypeface(util.typefaceRobotoLight);
        amount.setTypeface(util.typefaceRobotoLight);
        setOptionsButtonOnClickListener(editButton, row);

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
                        String rowName = ((TextView) row.findViewById(R.id.expenseList_itemName)).getText().toString();
                        String rowAmount = ((TextView) row.findViewById(R.id.expenseList_itemAmount)).getText().toString();

                        if (menuItem.getTitle().equals("Add")) {
                            handleAddToExpense(rowName, rowAmount);
                        } else if (menuItem.getTitle().equals("Edit")) {
                            handleEditExpense(rowName, rowAmount);
                        } else if (menuItem.getTitle().equals("Delete")) {
                            handleDeleteExpense(rowName, rowAmount);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    private void handleDeleteExpense(final String rowName, String rowAmount){
        final View inputDialog = inflateDeleteDialogLayout();
        final AlertDialog alertDialog = createAlertDialog(inputDialog);
        String dialogTitle = "Delete " + rowName + " (" + rowAmount + ")";
        TextView dialogTitleView = (TextView) inputDialog.findViewById(R.id.alertDialog_delete_title);
        dialogTitleView.setTypeface(util.typefaceRobotoMedium);
        dialogTitleView.setText(dialogTitle);
        Button ok = (Button) inputDialog.findViewById(R.id.alertDialog_delete_okButton);
        Button cancel = (Button) inputDialog.findViewById(R.id.alertDialog_delete_cancelButton);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpensesImpl expenseImpl = new ExpensesImpl(context);
                Expenses expenseActivity = new Expenses();
                Expense updatedExpense = expenseImpl.deleteExpense(rowName);
                refreshListData(updatedExpense.getExpenseCategoryAndAmount());
                ((BaseAdapter)Expenses.expensesList.getAdapter()).notifyDataSetChanged();
                Expenses.expensesTotalAmount.setText(Util.doubleToCurrency(updatedExpense.getExpenseTotal()));

                BudgetsImpl budgetsImpl = new BudgetsImpl(context);
                Budget latestBudget = budgetsImpl.getLatestBudget();
                String latestCategories = latestBudget.getCategories();
                if(latestCategories.contains(rowName)) {
                    budgetsImpl.deleteBudget(rowName);
                }

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void handleEditExpense(final String rowName, final String rowAmount){
        final View inputDialog = inflateEditDialogLayout();
        final AlertDialog alertDialog = createAlertDialog(inputDialog);
        String dialogTitle = "Edit " + rowName + " - " + rowAmount;
        TextView dialogTitleView = (TextView) inputDialog.findViewById(R.id.inputDialog_edit_title);
        dialogTitleView.setTypeface(util.typefaceRobotoMedium);
        dialogTitleView.setText(dialogTitle);
        Button edit = (Button) inputDialog.findViewById(R.id.inputDialog_edit_buttonAdd);
        edit.setText("Edit");
        Button cancel = (Button) inputDialog.findViewById(R.id.inputDialog_edit_buttonCancel);
        final EditText input = (EditText) inputDialog.findViewById(R.id.inputDialog_edit_inputAmount);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!input.getText().toString().isEmpty()) {
                    ExpensesImpl expenseImpl = new ExpensesImpl(context);
                    Expense updatedExpense = expenseImpl.editExpenseAmount(expenseImpl.getLatestExpenses(), rowName, input.getText().toString());
                    refreshListData(updatedExpense.getExpenseCategoryAndAmount());
                    ((BaseAdapter) Expenses.expensesList.getAdapter()).notifyDataSetChanged();
                    Expenses.expensesTotalAmount.setText(Util.doubleToCurrency(updatedExpense.getExpenseTotal()));
                    alertDialog.dismiss();
                }
                else {
                    alertDialog.dismiss();
                }
            }
        });

        alertDialog.show();
    }

    private void handleAddToExpense(final String rowName, final String rowAmount) {
        final View inputDialog = inflateAddDialogLayout();
        final AlertDialog alertDialog = createAlertDialog(inputDialog);
        String dialogTitle = "Add To " + rowName;
        TextView dialogTitleView = (TextView) inputDialog.findViewById(R.id.inputDialog_Add_Title);
        dialogTitleView.setTypeface(util.typefaceRobotoMedium);
        dialogTitleView.setText(dialogTitle);
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
                if(!input.getText().toString().isEmpty()) {
                    ExpensesImpl expenseImpl = new ExpensesImpl(context);
                    Expense expense = expenseImpl.addCategoryAndAmount(expenseImpl.getLatestExpenses(), rowName, input.getText().toString());
                    refreshListData(expense.getExpenseCategoryAndAmount());
                    ((BaseAdapter) Expenses.expensesList.getAdapter()).notifyDataSetChanged();
                    Expenses.expensesTotalAmount.setText(Util.doubleToCurrency(expense.getExpenseTotal()));

                    //reflect changes on Budget activity if applicable
                    BudgetsImpl budgetsImpl = new BudgetsImpl(context);
                    Budget budget = budgetsImpl.getLatestBudget();
                    String budgetCategories = budget.getCategories();
                    if (budgetCategories.contains(rowName)) {
                        budgetsImpl.logBudgetExpense(rowName, input.getText().toString());
                    }

                    alertDialog.dismiss();
                }
                else {
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    private PopupMenu inflatePopupMenu(View view) {
        final PopupMenu popup = new PopupMenu(context, view);
        popup.getMenuInflater().inflate(R.menu.edit_expenses_popup, popup.getMenu());
        return popup;
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

    public void refreshListData(Map<String, Double> map){
        mData.clear();
        mData.addAll(map.entrySet());
    }
}
