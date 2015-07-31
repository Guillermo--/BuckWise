package com.gmo.buckwise.model;

/**
 * Created by GMO on 5/29/2015.
 */
public class Overview {

    private int id;
    private double netIncome;
    private double income;
    private double expenses;
    private double bank;
    private String dateCreated;
    private double averageNetIncome;
    private double lastMonthNetIncome;

    public double getLastMonthNetIncome() {
        return lastMonthNetIncome;
    }

    public void setLastMonthNetIncome(double lastMonthNetIncome) {
        this.lastMonthNetIncome = lastMonthNetIncome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(double netIncome) {
        this.netIncome = netIncome;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getExpenses() {
        return expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }

    public double getBank() {
        return bank;
    }

    public void setBank(double bank) {
        this.bank = bank;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public double getAverageNetIncome() {
        return averageNetIncome;
    }

    public void setAverageNetIncome(double averageNetIncome) {
        this.averageNetIncome = averageNetIncome;
    }
}
