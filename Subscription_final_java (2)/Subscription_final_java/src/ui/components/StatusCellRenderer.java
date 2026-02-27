package ui.components;

import ui.theme.UIConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Custom table cell renderer with professional styling.
 * Provides color-coded status indicators.
 */
public class StatusCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (!isSelected && value != null) {
            String status = value.toString().toUpperCase();
            
            switch (status) {
                case "ACTIVE":
                case "PAID":
                    setBackground(new Color(220, 252, 231)); // Light green
                    setForeground(new Color(22, 163, 74));   // Dark green
                    break;
                    
                case "EXPIRED":
                    setBackground(new Color(244, 244, 245)); // Light gray
                    setForeground(new Color(113, 113, 122)); // Dark gray
                    break;
                    
                case "UNPAID":
                case "PENDING":
                    setBackground(new Color(254, 243, 199)); // Light orange
                    setForeground(new Color(234, 88, 12));   // Dark orange
                    break;
                    
                case "CANCELLED":
                case "OVERDUE":
                    setBackground(new Color(254, 226, 226)); // Light red
                    setForeground(new Color(220, 38, 38));   // Dark red
                    break;
                    
                case "INACTIVE":
                    setBackground(new Color(241, 245, 249)); // Very light gray
                    setForeground(new Color(100, 116, 139)); // Gray
                    break;
                    
                default:
                    setBackground(Color.WHITE);
                    setForeground(UIConstants.TEXT_PRIMARY);
            }
            
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setHorizontalAlignment(CENTER);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }
        
        return c;
    }
}
