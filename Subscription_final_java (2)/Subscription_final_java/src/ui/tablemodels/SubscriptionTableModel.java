package ui.tablemodels;

import model.Subscription;
import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Custom TableModel for displaying subscriptions.
 * Provides proper column names and data formatting.
 */
public class SubscriptionTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Sub ID", "User ID", "Plan ID", "Start Date", "End Date", "Status"};
    private List<Subscription> subscriptions;
    
    public SubscriptionTableModel(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
    
    @Override
    public int getRowCount() {
        return subscriptions.size();
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
        Subscription subscription = subscriptions.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return subscription.getSubscriptionId();
            case 1: return subscription.getUserId();
            case 2: return subscription.getPlanId();
            case 3: return subscription.getStartDate();
            case 4: return subscription.getEndDate();
            case 5: return subscription.getStatus();
            default: return null;
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex <= 2) {
            return Integer.class;
        }
        return String.class;
    }
    
    /**
     * Updates the subscription list and refreshes the table.
     * @param subscriptions New list of subscriptions
     */
    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
        fireTableDataChanged();
    }
    
    /**
     * Gets the subscription at the specified row.
     * @param row Row index
     * @return Subscription object
     */
    public Subscription getSubscriptionAt(int row) {
        return subscriptions.get(row);
    }
}
