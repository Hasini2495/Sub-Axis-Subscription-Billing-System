package model;

import java.time.LocalDate;

/**
 * Model class representing a subscription.
 * Links a user to a plan with start and end dates.
 */
public class Subscription {
    private Integer subscriptionId;
    private Integer userId;
    private Integer planId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextBillingDate;
    private String status; // ACTIVE, EXPIRED, CANCELLED
    private Boolean autoRenew; // Auto-renewal flag

    // Constructors
    public Subscription() {
    }

    public Subscription(Integer subscriptionId, Integer userId, Integer planId, 
                       LocalDate startDate, LocalDate endDate, String status) {
        this.subscriptionId = subscriptionId;
        this.userId = userId;
        this.planId = planId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters and Setters
    public Integer getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Integer subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getNextBillingDate() {
        return nextBillingDate;
    }

    public void setNextBillingDate(LocalDate nextBillingDate) {
        this.nextBillingDate = nextBillingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public Boolean getAutoRenew() {
        return autoRenew;
    }
    
    public void setAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    @Override
    public String toString() {
        return "Subscription #" + subscriptionId + " - " + status;
    }
}
