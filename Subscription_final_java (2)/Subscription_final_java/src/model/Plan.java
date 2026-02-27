package model;

import java.math.BigDecimal;

/**
 * Model class representing a subscription plan.
 * This class follows JavaBean conventions for enterprise applications.
 */
public class Plan {
    private Integer planId;
    private String planName;
    private String description;
    private BigDecimal price;
    private String billingCycle; // MONTHLY, QUARTERLY, YEARLY
    private Integer duration; // Duration in days

    // Constructors
    public Plan() {
    }

    public Plan(Integer planId, String planName, BigDecimal price, String billingCycle, Integer duration) {
        this.planId = planId;
        this.planName = planName;
        this.price = price;
        this.billingCycle = billingCycle;
        this.duration = duration;
    }

    // Getters and Setters
    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return planName + " - $" + price + "/" + billingCycle;
    }
}
