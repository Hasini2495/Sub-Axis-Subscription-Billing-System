package service;

import dao.InvoiceDAO;
import dao.PaymentDAO;
import dao.SubscriptionDAO;
import model.Invoice;
import model.Payment;
import model.Subscription;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service layer for Invoice operations.
 * Handles invoice management and payment processing.
 */
public class InvoiceService {
    private final InvoiceDAO invoiceDAO;
    private final PaymentDAO paymentDAO;
    private final SubscriptionDAO subscriptionDAO;
    private ActivityService activityService;
    
    public InvoiceService() {
        this.invoiceDAO = new InvoiceDAO();
        this.paymentDAO = new PaymentDAO();
        this.subscriptionDAO = new SubscriptionDAO();
    }
    
    /**
     * Sets the activity service (to avoid circular dependency).
     */
    public void setActivityService(ActivityService activityService) {
        this.activityService = activityService;
    }
    
    /**
     * Generates a new invoice for a subscription.
     * @param subscriptionId Subscription ID
     * @param amount Invoice amount
     * @param invoiceDate Date of invoice
     * @return Invoice ID if successful, null otherwise
     */
    public Integer generateInvoice(int subscriptionId, BigDecimal amount, LocalDate invoiceDate) {
        Invoice invoice = new Invoice();
        invoice.setSubscriptionId(subscriptionId);
        invoice.setAmount(amount);
        invoice.setInvoiceDate(invoiceDate);
        invoice.setStatus("UNPAID");
        
        boolean created = invoiceDAO.createInvoice(invoice);
        
        if (created && invoice.getInvoiceId() != null) {
            // Log activity if service is available
            if (activityService != null) {
                // Get user ID from subscription
                Subscription sub = subscriptionDAO.getAllSubscriptions().stream()
                    .filter(s -> s.getSubscriptionId().equals(subscriptionId))
                    .findFirst()
                    .orElse(null);
                
                if (sub != null) {
                    activityService.logInvoiceGenerated(sub.getUserId(), 
                        invoice.getInvoiceId(), amount.doubleValue());
                }
            }
            
            return invoice.getInvoiceId();
        }
        
        return null;
    }
    
    /**
     * Creates a new invoice.
     * @param subscriptionId Subscription ID
     * @param amount Invoice amount
     * @return true if successful
     */
    public boolean createInvoice(int subscriptionId, BigDecimal amount) {
        return generateInvoice(subscriptionId, amount, LocalDate.now()) != null;
    }
    
    /**
     * Retrieves all invoices.
     * @return List of all invoices
     */
    public List<Invoice> getAllInvoices() {
        return invoiceDAO.getAllInvoices();
    }
    
    /**
     * Retrieves invoices for a specific user.
     * @param userId User ID
     * @return List of invoices for the user
     */
    public List<Invoice> getInvoicesByUserId(int userId) {
        return invoiceDAO.getInvoicesByUserId(userId);
    }
    
    /**
     * Marks an invoice as paid and creates payment record.
     * @param invoiceId Invoice ID
     * @param paymentMethod Payment method
     * @param userId User ID for activity logging
     * @return true if successful
     */
    public boolean markInvoiceAsPaid(int invoiceId, String paymentMethod, int userId) {
        // Get invoice details
        List<Invoice> invoices = invoiceDAO.getAllInvoices();
        Invoice invoice = invoices.stream()
            .filter(inv -> inv.getInvoiceId().equals(invoiceId))
            .findFirst()
            .orElse(null);
        
        if (invoice == null || "PAID".equals(invoice.getStatus())) {
            return false;
        }
        
        // Create payment record
        Payment payment = new Payment();
        payment.setInvoiceId(invoiceId);
        payment.setAmount(invoice.getAmount());
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(paymentMethod);
        
        boolean paymentCreated = paymentDAO.createPayment(payment);
        
        // Update invoice status
        if (paymentCreated && payment.getPaymentId() != null) {
            boolean updated = invoiceDAO.updateInvoiceStatus(invoiceId, "PAID");
            
            if (updated && activityService != null) {
                activityService.logPaymentCompleted(userId, payment.getPaymentId(), 
                    invoice.getAmount().doubleValue());
            }
            
            return updated;
        }
        
        return false;
    }
    
    /**
     * Gets count of unpaid invoices.
     * @return Count of unpaid invoices
     */
    public int getUnpaidInvoiceCount() {
        return invoiceDAO.getUnpaidInvoiceCount();
    }
    
    /**
     * Updates an existing invoice.
     * @param invoice Invoice object with updated data
     * @return true if successful
     */
    public boolean updateInvoice(Invoice invoice) {
        return invoiceDAO.updateInvoiceStatus(invoice.getInvoiceId(), invoice.getStatus());
    }
}
