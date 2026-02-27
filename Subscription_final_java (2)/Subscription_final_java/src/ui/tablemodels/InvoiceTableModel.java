package ui.tablemodels;

import model.Invoice;
import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Custom TableModel for displaying invoices.
 * Provides proper column names and data formatting.
 */
public class InvoiceTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Invoice ID", "Subscription ID", "Amount", "Date", "Status"};
    private List<Invoice> invoices;
    
    public InvoiceTableModel(List<Invoice> invoices) {
        this.invoices = invoices;
    }
    
    @Override
    public int getRowCount() {
        return invoices.size();
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
        Invoice invoice = invoices.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return invoice.getInvoiceId();
            case 1: return invoice.getSubscriptionId();
            case 2: return "$" + invoice.getAmount();
            case 3: return invoice.getInvoiceDate();
            case 4: return invoice.getStatus();
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
     * Updates the invoice list and refreshes the table.
     * @param invoices New list of invoices
     */
    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
        fireTableDataChanged();
    }
    
    /**
     * Gets the invoice at the specified row.
     * @param row Row index
     * @return Invoice object
     */
    public Invoice getInvoiceAt(int row) {
        return invoices.get(row);
    }
}
