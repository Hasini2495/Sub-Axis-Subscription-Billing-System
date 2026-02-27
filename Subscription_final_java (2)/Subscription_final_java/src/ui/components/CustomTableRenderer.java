package ui.components;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * Professional table cell renderer with color-coded status and alternating rows.
 */
public class CustomTableRenderer extends DefaultTableCellRenderer {
    
    private static final Color EVEN_ROW = Color.WHITE;
    private static final Color ODD_ROW = new Color(249, 250, 251);
    private static final Color SELECTED_BG = new Color(224, 242, 254);
    
    private static final Color STATUS_ACTIVE = new Color(16, 185, 129);
    private static final Color STATUS_EXPIRED = new Color(156, 163, 175);
    private static final Color STATUS_CANCELLED = new Color(239, 68, 68);
    private static final Color STATUS_UNPAID = new Color(245, 158, 11);
    private static final Color STATUS_PAID = new Color(16, 185, 129);
    
    private int statusColumnIndex = -1;
    
    public CustomTableRenderer() {
        setOpaque(true);
    }
    
    public CustomTableRenderer(int statusColumnIndex) {
        this();
        this.statusColumnIndex = statusColumnIndex;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // Font and padding
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Background color - alternating rows
        if (isSelected) {
            setBackground(SELECTED_BG);
            setForeground(new Color(17, 24, 39));
        } else {
            setBackground(row % 2 == 0 ? EVEN_ROW : ODD_ROW);
            setForeground(new Color(55, 65, 81));
        }
        
        // Status column color-coding
        if (column == statusColumnIndex && value != null) {
            String status = value.toString().toUpperCase();
            Font boldFont = getFont().deriveFont(Font.BOLD);
            setFont(boldFont);
            
            switch (status) {
                case "ACTIVE":
                    setForeground(STATUS_ACTIVE);
                    break;
                case "EXPIRED":
                    setForeground(STATUS_EXPIRED);
                    break;
                case "CANCELLED":
                case "CANCELED":
                    setForeground(STATUS_CANCELLED);
                    break;
                case "UNPAID":
                case "OVERDUE":
                    setForeground(STATUS_UNPAID);
                    break;
                case "PAID":
                case "COMPLETED":
                    setForeground(STATUS_PAID);
                    break;
            }
        }
        
        return c;
    }
}
