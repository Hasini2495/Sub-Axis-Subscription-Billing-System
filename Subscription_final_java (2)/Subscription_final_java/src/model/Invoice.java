package model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model class representing an invoice.
 * Generated for each billing cycle of a subscription.
 */
public class Invoice {
    private Integer invoiceId;
    private Integer subscriptionId;
    private BigDecimal amount;
    private LocalDate invoiceDate;
    private String status; // UNPAID, PAID, OVERDUE

    // Constructors
    public Invoice() {
    }

    public Invoice(Integer invoiceId, Integer subscriptionId, BigDecimal amount, 
                  LocalDate invoiceDate, String status) {
        this.invoiceId = invoiceId;
        this.subscriptionId = subscriptionId;
        this.amount = amount;
        this.invoiceDate = invoiceDate;
        this.status = status;
    }

    // Getters and Setters
    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Integer subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Invoice #" + invoiceId + " - $" + amount + " - " + status;
    }
}
