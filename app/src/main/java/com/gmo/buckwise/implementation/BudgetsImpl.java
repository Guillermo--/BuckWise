package com.gmo.buckwise.implementation;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gmo.buckwise.database.BudgetsDAO;
import com.gmo.buckwise.model.Budget;
import com.gmo.buckwise.util.Util;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by GMO on 7/7/2015.
 */
public class BudgetsImpl {
    Context context;
    BudgetsDAO budgetsDAO;

    public BudgetsImpl(Context context){
        this.context = context;
        budgetsDAO = new BudgetsDAO(context);
    }

    public Budget getLatestBudget(){
        Budget budget = budgetsDAO.getLatestBudget();
        budget.setAmountAvailable(calculateAmountAvailable(budget));
        budget.setAmountStartedWith(calculateAmountStartedWith(budget));

        return budget;
    }

    public double calculateAmountAvailable(Budget budget){
        double amountAvailable = 0.00;

        if(!budget.getInitialAmounts().equals("") && !budget.getAmountsSpent().equals("")) {
            List<String> amountInitialList = Arrays.asList(budget.getInitialAmounts().split(","));
            List<String> amountSpentList = Arrays.asList(budget.getAmountsSpent().split(","));

            for(int i = 0; i<amountSpentList.size(); i++){
                amountAvailable += (Double.parseDouble(amountInitialList.get(i)) - Double.parseDouble(amountSpentList.get(i)));
            }
        }
        return amountAvailable;
    }

    public double calculateAmountStartedWith(Budget budget){
        double amountStartedWith = 0.0;
        if(!budget.getInitialAmounts().equals("")) {
            List<String> amountInitialList = Arrays.asList(budget.getInitialAmounts().split(","));
            amountStartedWith = 0.00;
            for (int i = 0; i < amountInitialList.size(); i++) {
                amountStartedWith += Double.parseDouble(amountInitialList.get(i));
            }
        }
        return amountStartedWith;
    }

    public Budget createBudget(Budget budget, String category, String amountToStartWith){
        String existingCategories = budget.getCategories();
        String existingAmountsToStartWith = budget.getInitialAmounts();
        String amountsSpent = budget.getAmountsSpent();
        if(existingCategories.equals("")){
            existingCategories += category;
            existingAmountsToStartWith += amountToStartWith;
            amountsSpent += "0.00";
        }
        else{
            if(!existingCategories.contains(category)){
                existingCategories += ","+category;
                existingAmountsToStartWith += ","+amountToStartWith;
                amountsSpent += ",0.00";
            }
            else{
                return null;
            }
        }

        Util util = new Util();
        budget.setDateCreated(util.getCurrentDateTime());
        budget.setCategories(existingCategories);
        budget.setInitialAmounts(existingAmountsToStartWith);
        budget.setAmountsSpent(amountsSpent);
        BudgetsDAO budgetsDAO = new BudgetsDAO(context);
        budgetsDAO.createBudgetItem(budget);

        budget.setAmountAvailable(calculateAmountAvailable(budget));
        budget.setAmountStartedWith(calculateAmountStartedWith(budget));

        return budget;
    }

    public Budget logBudgetExpense(String category, String amountSpent){
        Budget budget = getLatestBudget();
        BudgetsDAO budgetsDao = new BudgetsDAO(context);
        List<String> categories = Arrays.asList(budget.getCategories().split(","));
        List<String> amountsSpent = Arrays.asList(budget.getAmountsSpent().split(","));
        int targetIndex = categories.indexOf(category);

        double targetAmountSpent = Double.parseDouble(amountsSpent.get(targetIndex));
        double newAmountSpent = targetAmountSpent + Double.parseDouble(amountSpent);
        amountsSpent.set(targetIndex, String.valueOf(newAmountSpent));
        String amountsSpentStr = TextUtils.join(",", amountsSpent);
        budget.setAmountsSpent(amountsSpentStr);
        budget.setAmountAvailable(calculateAmountAvailable(budget));

        Date currentDateForComparison = Util.stringToDate(Util.getCurrentDateTime());
        Date latestDateForComparison = Util.stringToDate(budget.getDateCreated());

        if(currentDateForComparison.after(latestDateForComparison)){
            budget.setDateCreated(Util.getCurrentDateTime());
            budgetsDao.createBudgetItem(budget);
        }
        else {
            budgetsDao.updateBudgetAmountSpent(budget);
        }
        return budget;
    }

    public Budget editBudgetCategoryAndInitialAmount(String category, String inputCategory, String inputAmount){
        Budget budget = getLatestBudget();
        BudgetsDAO budgetsDao = new BudgetsDAO(context);
        List<String> categories = Arrays.asList(budget.getCategories().split(","));
        List<String> initialAmounts = Arrays.asList(budget.getInitialAmounts().split(","));
        int targetIndex = categories.indexOf(category);

        if(inputCategory != null && !inputCategory.isEmpty()){
            categories.set(targetIndex, inputCategory);
        }
        if(inputAmount != null && !inputAmount.isEmpty()){
            initialAmounts.set(targetIndex, inputAmount);
        }

        String categoriesStr = TextUtils.join(",", categories);
        String initialAmountsStr = TextUtils.join(",", initialAmounts);

        budget.setInitialAmounts(initialAmountsStr);
        budget.setCategories(categoriesStr);
        budget.setAmountStartedWith(calculateAmountStartedWith(budget));
        budget.setAmountAvailable(calculateAmountAvailable(budget));

        Date currentDateForComparison = Util.stringToDate(Util.getCurrentDateTime());
        Date latestDateForComparison = Util.stringToDate(budget.getDateCreated());

        if(currentDateForComparison.after(latestDateForComparison)){
            budget.setDateCreated(Util.getCurrentDateTime());
            budgetsDao.createBudgetItem(budget);
        }
        else {
            budgetsDao.updateBudgetCategoryAndInitialAmount(budget);
        }

        return budget;
    }

    public Budget deleteBudget(String categoryToDelete){
        Budget budget = getLatestBudget();
        BudgetsDAO budgetsDao = new BudgetsDAO(context);
        List<String> categoryList = (new LinkedList<>(Arrays.asList(budget.getCategories().split(","))));
        List<String> initialAmountsList = (new LinkedList<>(Arrays.asList(budget.getInitialAmounts().split(","))));
        List<String> amountsSpent = (new LinkedList<>(Arrays.asList(budget.getAmountsSpent().split(","))));
        int targetIndex = categoryList.indexOf(categoryToDelete);

        //Log.d("---Initial", amountsSpent.toString());

        categoryList.remove(categoryToDelete);
        initialAmountsList.remove(targetIndex);
        amountsSpent.remove(targetIndex);

        //Log.d("---After", amountsSpent.toString());

        String categoryStr = TextUtils.join(",", categoryList);
        String initialAmountsStr = TextUtils.join(",", initialAmountsList);
        String amountsSpentStr = TextUtils.join(",", amountsSpent);

        //Log.d("---Str", amountsSpentStr);


        budget.setCategories(categoryStr);
        budget.setInitialAmounts(initialAmountsStr);
        budget.setAmountsSpent(amountsSpentStr);
        budget.setAmountAvailable(calculateAmountAvailable(budget));
        budget.setAmountStartedWith(calculateAmountStartedWith(budget));

        Date currentDateForComparison = Util.stringToDate(Util.getCurrentDateTime());
        Date latestDateForComparison = Util.stringToDate(budget.getDateCreated());

        if(currentDateForComparison.after(latestDateForComparison)){
            //Log.d("---create", "yeah");
            budget.setDateCreated(Util.getCurrentDateTime());
            budgetsDao.createBudgetItem(budget);
        }
        else {
            //Log.d("---Update", "yeah");
            budgetsDao.updateBudgetAllLists(budget);
        }

        return budget;

    }

}
