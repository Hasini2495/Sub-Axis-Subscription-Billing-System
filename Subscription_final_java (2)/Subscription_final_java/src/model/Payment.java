package model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model class representing a payment.
 * Records payment transactions for invoices.
 */
public class Payment {
    private Integer paymentId;
    private Integer invoiceId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER

    // Constructors
    public Payment() {
    }

    public Payment(Integer paymentId, Integer invoiceId, BigDecimal amount, 
                  LocalDate paymentDate, String paymentMethod) {
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "Payment #" + paymentId + " - $" + amount + " via " + paymentMethod;
    }
}
