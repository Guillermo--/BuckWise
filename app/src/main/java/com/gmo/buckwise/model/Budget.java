package com.gmo.buckwise.model;

/**
 * Created by GMO on 7/7/2015.
 */
public class Budget {
    private int id;
    private String dateCreated;
    private String categories = "";
    private String initialAmounts = "";
    private String amountsSpent = "";
    private double amountAvailable;
    private double amountStartedWith;

    public double getAmountAvailable() {
        return amountAvailable;
    }

    public void setAmountAvailable(double amountAvailable) {
        this.amountAvailable = amountAvailable;
    }

    public double getAmountStartedWith() {
        return amountStartedWith;
    }

    public void setAmountStartedWith(double amountStartedWith) {
        this.amountStartedWith = amountStartedWith;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getInitialAmounts() {
        return initialAmounts;
    }

    public void setInitialAmounts(String initialAmounts) {
        this.initialAmounts = initialAmounts;
    }

    public String getAmountsSpent() {
        return amountsSpent;
    }

    public void setAmountsSpent(String amountsSpent) {
        this.amountsSpent = amountsSpent;
    }


}
