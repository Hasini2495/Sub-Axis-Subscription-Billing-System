package dao;

import model.Invoice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Invoice entity.
 * Handles all database operations for invoices.
 */
public class InvoiceDAO {
    
    /**
     * Creates a new invoice in the database.
     * @param invoice Invoice object to create
     * @return true if successful
     */
    public boolean createInvoice(Invoice invoice) {
        String sql = "INSERT INTO invoices (subscription_id, amount, invoice_date, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, invoice.getSubscriptionId());
            stmt.setBigDecimal(2, invoice.getAmount());
            stmt.setDate(3, Date.valueOf(invoice.getInvoiceDate()));
            stmt.setString(4, invoice.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        invoice.setInvoiceId(generatedKeys.getInt(1));
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
     * Retrieves all invoices from the database.
     * @return List of all invoices
     */
    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY invoice_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setSubscriptionId(rs.getInt("subscription_id"));
                invoice.setAmount(rs.getBigDecimal("amount"));
                invoice.setInvoiceDate(rs.getDate("invoice_date").toLocalDate());
                invoice.setStatus(rs.getString("status"));
                invoices.add(invoice);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return invoices;
    }
    
    /**
     * Retrieves invoices for a specific user.
     * @param userId User ID
     * @return List of invoices for the user
     */
    public List<Invoice> getInvoicesByUserId(int userId) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT i.* FROM invoices i " +
                     "INNER JOIN subscriptions s ON i.subscription_id = s.subscription_id " +
                     "WHERE s.user_id = ? " +
                     "ORDER BY i.invoice_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setSubscriptionId(rs.getInt("subscription_id"));
                invoice.setAmount(rs.getBigDecimal("amount"));
                invoice.setInvoiceDate(rs.getDate("invoice_date").toLocalDate());
                invoice.setStatus(rs.getString("status"));
                invoices.add(invoice);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return invoices;
    }
    
    /**
     * Updates invoice status.
     * @param invoiceId Invoice ID
     * @param status New status
     * @return true if successful
     */
    public boolean updateInvoiceStatus(int invoiceId, String status) {
        String sql = "UPDATE invoices SET status = ? WHERE invoice_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, invoiceId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets count of unpaid invoices.
     * @return Count of unpaid invoices
     */
    public int getUnpaidInvoiceCount() {
        String sql = "SELECT COUNT(*) FROM invoices WHERE status = 'UNPAID'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
}
