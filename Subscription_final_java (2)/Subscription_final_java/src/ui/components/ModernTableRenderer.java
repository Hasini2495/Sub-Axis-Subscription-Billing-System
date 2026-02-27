package ui.components;

import ui.theme.UIConstants;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * Modern table cell renderer with professional styling.
 * Provides alternating row colors, status badges, and proper alignment.
 */
public class ModernTableRenderer extends DefaultTableCellRenderer {
    
    private static final Color HEADER_BG = new Color(12, 8, 38);
    private static final Color HEADER_FG = Color.WHITE;
    private static final Color ROW_EVEN = new Color(22, 15, 56);
    private static final Color ROW_ODD = new Color(28, 20, 66);
    
    /**
     * Configure table with modern styling.
     */
    public static void styleTable(JTable table) {
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(HEADER_FG);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createEmptyBorder());
        
        // Set custom header renderer to ensure colors are applied
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                         boolean isSelected, boolean hasFocus,
                                                         int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(HEADER_BG);
                c.setForeground(HEADER_FG);
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                ((JLabel) c).setHorizontalAlignment(JLabel.LEFT);
                return c;
            }
        };
        
        // Apply header renderer to all columns
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        
        // Table styling
        table.setFont(UIConstants.FONT_REGULAR);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(60, 45, 120));
        table.setSelectionForeground(Color.WHITE);
        table.setBorder(BorderFactory.createEmptyBorder());
        
        // Set renderer for all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new ModernTableRenderer());
        }
    }
    
    /**
     * Configure column widths to prevent collapsing.
     */
    public static void setColumnWidths(JTable table, int... widths) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < widths.length && i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(widths[i]);
            columnModel.getColumn(i).setMinWidth(widths[i]);
        }
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // Set alternating row colors
        if (!isSelected) {
            if (row % 2 == 0) {
                c.setBackground(ROW_EVEN);
            } else {
                c.setBackground(ROW_ODD);
            }
            c.setForeground(UIConstants.TEXT_PRIMARY);
        }
        
        // Handle status badges
        String columnName = table.getColumnName(column).toUpperCase();
        if (columnName.contains("STATUS") && value != null) {
            return createStatusBadge(value.toString(), isSelected, row);
        }
        
        // Handle amount alignment
        if (columnName.contains("AMOUNT") || columnName.contains("PRICE") || columnName.contains("REVENUE")) {
            setHorizontalAlignment(RIGHT);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        } else if (columnName.contains("STATUS")) {
            setHorizontalAlignment(CENTER);
        } else {
            setHorizontalAlignment(LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));
        }
        
        return c;
    }
    
    /**
     * Creates a styled status badge.
     */
    private JPanel createStatusBadge(String status, boolean isSelected, int row) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setOpaque(true);
        
        if (!isSelected) {
            panel.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
        } else {
            panel.setBackground(new Color(60, 45, 120));
        }
        
        JLabel badge = new JLabel(status);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        
        // Status-specific colors
        String statusUpper = status.toUpperCase();
        Color bgColor;
        Color fgColor = Color.WHITE;
        
        switch (statusUpper) {
            case "ACTIVE":
            case "PAID":
            case "SUCCESS":
            case "COMPLETED":
                bgColor = new Color(34, 197, 94);
                break;
            case "UNPAID":
            case "PENDING":
            case "WARNING":
                bgColor = new Color(251, 146, 60);
                break;
            case "EXPIRED":
            case "INACTIVE":
                bgColor = new Color(161, 161, 170);
                break;
            case "FAILED":
            case "CANCELLED":
            case "ERROR":
                bgColor = new Color(239, 68, 68);
                break;
            default:
                bgColor = new Color(100, 116, 139);
        }
        
        badge.setBackground(bgColor);
        badge.setForeground(fgColor);
        
        panel.add(badge);
        return panel;
    }
}
