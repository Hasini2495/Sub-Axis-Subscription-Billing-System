package service;

import dao.PaymentDAO;
import dao.InvoiceDAO;
import dao.SubscriptionDAO;
import model.Payment;
import model.Invoice;
import model.Subscription;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service layer for Payment operations.
 * Handles payment history and revenue calculations.
 */
public class PaymentService {
    private final PaymentDAO paymentDAO;
    private final InvoiceDAO invoiceDAO;
    private final SubscriptionDAO subscriptionDAO;
    private ActivityService activityService;
    
    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
        this.invoiceDAO = new InvoiceDAO();
        this.subscriptionDAO = new SubscriptionDAO();
    }
    
    /**
     * Sets the activity service (to avoid circular dependency).
     */
    public void setActivityService(ActivityService activityService) {
        this.activityService = activityService;
    }
    
    /**
     * Processes a payment for an invoice.
     * Updates invoice status, subscription billing date, and logs activity.
     * @param invoiceId Invoice ID
     * @param amount Payment amount
     * @param paymentMethod Payment method
     * @return true if successful
     */
    public boolean makePayment(int invoiceId, BigDecimal amount, String paymentMethod) {
        // Create payment record
        Payment payment = new Payment();
        payment.setInvoiceId(invoiceId);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(paymentMethod);
        
        boolean created = paymentDAO.createPayment(payment);
        
        if (created && payment.getPaymentId() != null) {
            // Update invoice status to PAID
            invoiceDAO.updateInvoiceStatus(invoiceId, "PAID");
            
            // Get invoice to find subscription and user
            Invoice invoice = invoiceDAO.getAllInvoices().stream()
                .filter(inv -> inv.getInvoiceId().equals(invoiceId))
                .findFirst()
                .orElse(null);
            
            if (invoice != null) {
                // Get subscription to find user and update next billing date
                Subscription subscription = subscriptionDAO.getAllSubscriptions().stream()
                    .filter(sub -> sub.getSubscriptionId().equals(invoice.getSubscriptionId()))
                    .findFirst()
                    .orElse(null);
                
                if (subscription != null) {
                    // Log activity
                    if (activityService != null) {
                        activityService.logPaymentCompleted(subscription.getUserId(), 
                            payment.getPaymentId(), amount.doubleValue());
                    }
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Retrieves all payments.
     * @return List of all payments
     */
    public List<Payment> getAllPayments() {
        return paymentDAO.getAllPayments();
    }
    
    /**
     * Retrieves payments for a specific user.
     * @param userId User ID
     * @return List of payments for the user
     */
    public List<Payment> getPaymentsByUserId(int userId) {
        return paymentDAO.getPaymentsByUserId(userId);
    }
    
    /**
     * Gets total revenue collected.
     * @return Total revenue
     */
    public BigDecimal getTotalRevenue() {
        return paymentDAO.getTotalRevenue();
    }
}
