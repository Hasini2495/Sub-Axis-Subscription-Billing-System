package dao;

import model.Payment;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Payment entity.
 * Handles all database operations for payments.
 */
public class PaymentDAO {
    
    /**
     * Creates a new payment in the database.
     * @param payment Payment object to create
     * @return true if successful
     */
    public boolean createPayment(Payment payment) {
        String sql = "INSERT INTO payments (invoice_id, amount, payment_date, payment_method) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, payment.getInvoiceId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setDate(3, Date.valueOf(payment.getPaymentDate()));
            stmt.setString(4, payment.getPaymentMethod());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        payment.setPaymentId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves all payments from the database.
     * @return List of all payments
     */
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY payment_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("payment_id"));
                payment.setInvoiceId(rs.getInt("invoice_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                payment.setPaymentMethod(rs.getString("payment_method"));
                payments.add(payment);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return payments;
    }
    
    /**
     * Retrieves payments for a specific user.
     * @param userId User ID
     * @return List of payments for the user
     */
    public List<Payment> getPaymentsByUserId(int userId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.* FROM payments p " +
                     "INNER JOIN invoices i ON p.invoice_id = i.invoice_id " +
                     "INNER JOIN subscriptions s ON i.subscription_id = s.subscription_id " +
                     "WHERE s.user_id = ? " +
                     "ORDER BY p.payment_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("payment_id"));
                payment.setInvoiceId(rs.getInt("invoice_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                payment.setPaymentMethod(rs.getString("payment_method"));
                payments.add(payment);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return payments;
    }
    
    /**
     * Gets total revenue collected.
     * @return Total revenue as BigDecimal
     */
    public BigDecimal getTotalRevenue() {
        String sql = "SELECT SUM(amount) FROM payments";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal(1);
                return total != null ? total : BigDecimal.ZERO;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }
}
