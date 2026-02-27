package ui.tablemodels;

import model.Payment;
import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Custom TableModel for displaying payments.
 * Provides proper column names and data formatting.
 */
public class PaymentTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Payment ID", "Invoice ID", "Amount", "Date", "Method"};
    private List<Payment> payments;
    
    public PaymentTableModel(List<Payment> payments) {
        this.payments = payments;
    }
    
    @Override
    public int getRowCount() {
        return payments.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Payment payment = payments.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return payment.getPaymentId();
            case 1: return payment.getInvoiceId();
            case 2: return "$" + payment.getAmount();
            case 3: return payment.getPaymentDate();
            case 4: return payment.getPaymentMethod();
            default: return null;
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0 || columnIndex == 1) {
            return Integer.class;
        }
        return String.class;
    }
    
    /**
     * Updates the payment list and refreshes the table.
     * @param payments New list of payments
     */
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
        fireTableDataChanged();
    }
    
    /**
     * Gets the payment at the specified row.
     * @param row Row index
     * @return Payment object
     */
    public Payment getPaymentAt(int row) {
        return payments.get(row);
    }
}
