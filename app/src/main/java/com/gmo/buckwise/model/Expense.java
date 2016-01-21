package com.gmo.buckwise.model;

import java.util.Map;

/**
 * Created by GMO on 6/6/2015.
 */
public class Expense {

    private double expenseTotal;
    private String expenseCategories ="";
    private String expenseAmounts ="";
    private double expenseAverage;
    private double expenseLastMonth;
    private int id;
    private String dateCreated;
    private Map<String, Double> expenseCategoryAndAmount;

    public double getExpenseTotal() {
        return expenseTotal;
    }

    public void setExpenseTotal(double expenseTotal) {
        this.expenseTotal = expenseTotal;
    }

    public Map<String, Double> getExpenseCategoryAndAmount() {
        return expenseCategoryAndAmount;
    }

    public void setExpenseCategoryAndAmount(Map<String, Double> expenseCategoryAndAmount) {
        this.expenseCategoryAndAmount = expenseCategoryAndAmount;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getExpenseCategories() {
        return expenseCategories;
    }

    public void setExpenseCategory(String expenseCategory) {
        this.expenseCategories = expenseCategory;
    }

    public String getExpenseAmounts() {
        return expenseAmounts;
    }

    public void setExpenseAmount(String expenseAmount) {
        this.expenseAmounts = expenseAmount;
    }

    public double getExpenseAverage() {
        return expenseAverage;
    }

    public void setExpenseAverage(double expenseAverage) {
        this.expenseAverage = expenseAverage;
    }

    public double getExpenseLastMonth() {
        return expenseLastMonth;
    }

    public void setExpenseLastMonth(double expenseLastMonth) {
        this.expenseLastMonth = expenseLastMonth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
