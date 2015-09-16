package com.gmo.buckwise.implementation;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gmo.buckwise.activity.Expenses;
import com.gmo.buckwise.database.ExpensesDAO;
import com.gmo.buckwise.database.OverviewDAO;
import com.gmo.buckwise.model.Budget;
import com.gmo.buckwise.model.Expense;
import com.gmo.buckwise.util.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by GMO on 6/6/2015.
 */
public class ExpensesImpl {
    Context context;
    ExpensesDAO expensesDAO;

    public ExpensesImpl(Context context){
        this.context = context;
        expensesDAO = new ExpensesDAO(context);
    }

    public Expense getLatestExpenses(){
        Expense expense = expensesDAO.getLatestExpenses();
        String categories = expense.getExpenseCategories();
        String amounts = expense.getExpenseAmounts();
        Map<String, Double> categoriesAndAmounts= new HashMap<String, Double>();
        String[] categoriesSplit = categories.split(",");
        String[] amountsSplit = amounts.split(",");

        if(!categories.equals("")) {
            for (int i = 0; i < categoriesSplit.length; i++) {
                if (amountsSplit[i].equals("")) {
                    categoriesAndAmounts.put(categoriesSplit[i], 0.00);
                } else {
                    categoriesAndAmounts.put(categoriesSplit[i], Double.parseDouble(amountsSplit[i]));
                }
            }
        }

        expense.setExpenseCategoryAndAmount(categoriesAndAmounts);
        return expense;
    }

    public double getTotalExpensesAmount(){
        return expensesDAO.getTotalExpensesAmount();
    }

    public double getTotalExpensesAmount(String date) {
        return expensesDAO.getTotalExpensesAmount(date);
    }

    public Expense editExpenseAmount(Expense expense, String category, String newAmount){
        Map<String, Double> categoriesAndAmounts = expense.getExpenseCategoryAndAmount();
        double existingAmount;
        double existingTotalExpenses = expense.getExpenseTotal();
        double newTotalExpenses = 0.0;
        String newCategoriesString = expense.getExpenseCategories();
        String newAmountsString;

        if (categoriesAndAmounts.containsKey(category)) {
            existingAmount = categoriesAndAmounts.get(category);
            categoriesAndAmounts.put(category, Double.parseDouble(newAmount));

            //Do strings
            List<String> amountsList = Arrays.asList(expense.getExpenseAmounts().split(","));
            List<String> categoriesList = Arrays.asList(expense.getExpenseCategories().split(","));
            if (categoriesList.contains(category)) {
                int index = categoriesList.indexOf(category);
                amountsList.set(index, String.valueOf(newAmount));

                for(int i = 0; i<amountsList.size(); i++){
                    newTotalExpenses += Double.parseDouble(amountsList.get(i));
                }

                newAmountsString = TextUtils.join(",", amountsList);
                String datetime = Util.getCurrentDateTime();

                expense.setExpenseTotal(newTotalExpenses);
                expense.setExpenseCategoryAndAmount(categoriesAndAmounts);
                expense.setExpenseCategory(newCategoriesString);
                expense.setExpenseAmount(newAmountsString);
                expense.setDateCreated(datetime);

                OverviewDAO overviewDAO = new OverviewDAO(Expenses.context);
                ExpensesDAO expensesDAO = new ExpensesDAO(Expenses.context);
                expensesDAO.addOrUpdateExpense(expense);
                overviewDAO.updateNetIncome(getTotalExpensesAmount());

                return expense;
            }
        }
        return null;
    }

    public Expense deleteExpense(String category){
        ExpensesImpl expensesImpl = new ExpensesImpl(context);
        Expense expense = expensesImpl.getLatestExpenses();
        Map<String, Double> categoriesAndAmounts = expense.getExpenseCategoryAndAmount();
        String newCategoriesString;
        String newAmountsString;
        double newTotalExpenses = expense.getExpenseTotal();
        List<String> amountsList = new LinkedList<String>(Arrays.asList(expense.getExpenseAmounts().split(",")));
        List<String> categoriesList = new LinkedList<String>(Arrays.asList(expense.getExpenseCategories().split(",")));

        categoriesAndAmounts.remove(category);
        int index = categoriesList.indexOf(category);
        newTotalExpenses -= Double.parseDouble(amountsList.get(index));
        categoriesList.remove(index);
        amountsList.remove(index);
        newAmountsString = TextUtils.join(",", amountsList);
        newCategoriesString = TextUtils.join(",", categoriesList);

        String datetime = Util.getCurrentDateTime();
        expense.setExpenseTotal(newTotalExpenses);
        expense.setExpenseCategoryAndAmount(categoriesAndAmounts);
        expense.setExpenseCategory(newCategoriesString);
        expense.setExpenseAmount(newAmountsString);
        expense.setDateCreated(datetime);

        OverviewDAO overviewDAO = new OverviewDAO(Expenses.context);
        ExpensesDAO expensesDAO = new ExpensesDAO(Expenses.context);
        expensesDAO.addOrUpdateExpense(expense);
        overviewDAO.updateNetIncome(getTotalExpensesAmount());

        return expense;
    }

    public Expense addCategoryAndAmount(Expense expense, String category, String amount){
        Map<String, Double> categoriesAndAmounts = expense.getExpenseCategoryAndAmount();
        double existingAmount;
        double newAmount;
        double existingTotalExpenses = expense.getExpenseTotal();
        double newTotalExpenses = 0.0;
        String newCategoriesString = expense.getExpenseCategories();
        String newAmountsString = expense.getExpenseAmounts();

        if(expense.getExpenseCategories().isEmpty()){
            newCategoriesString = category;
            newAmountsString = amount;
            newTotalExpenses = Double.parseDouble(amount);
            categoriesAndAmounts.put(category, Double.parseDouble(amount));
        }
        else {
            if (categoriesAndAmounts.containsKey(category)) {
                existingAmount = categoriesAndAmounts.get(category);
                newAmount = existingAmount + Double.parseDouble(amount);
                categoriesAndAmounts.put(category, newAmount);
                newTotalExpenses = existingTotalExpenses + Double.parseDouble(amount);
                //Do strings
                List<String> amountsList = Arrays.asList(expense.getExpenseAmounts().split(","));
                List<String> categoriesList = Arrays.asList(expense.getExpenseCategories().split(","));
                if (categoriesList.contains(category)) {
                    int index = categoriesList.indexOf(category);
                    amountsList.set(index, String.valueOf(newAmount));
                    newAmountsString = TextUtils.join(",", amountsList);
                }
            }
            else {
                categoriesAndAmounts.put(category, Double.parseDouble(amount));
                newTotalExpenses = existingTotalExpenses + Double.parseDouble(amount);
                newCategoriesString += ","+category;
                newAmountsString += ","+amount;
            }
        }

        String datetime = Util.getCurrentDateTime();
        expense.setExpenseTotal(newTotalExpenses);
        expense.setExpenseCategoryAndAmount(categoriesAndAmounts);
        expense.setExpenseCategory(newCategoriesString);
        expense.setExpenseAmount(newAmountsString);
        expense.setDateCreated(datetime);

        OverviewDAO overviewDAO = new OverviewDAO(context);
        ExpensesDAO expensesDAO = new ExpensesDAO(context);
        expensesDAO.addOrUpdateExpense(expense);
        overviewDAO.updateNetIncome(getTotalExpensesAmount());

        return expense;
    }


}
