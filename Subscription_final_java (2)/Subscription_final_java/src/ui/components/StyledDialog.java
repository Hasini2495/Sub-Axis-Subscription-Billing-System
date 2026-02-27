package ui.components;

import ui.theme.UIConstants;
import javax.swing.*;
import java.awt.*;

/**
 * Custom styled dialog for professional message displays.
 * Provides consistent appearance for all dialogs.
 */
public class StyledDialog {
    
    public enum DialogType {
        SUCCESS, ERROR, WARNING, INFO, QUESTION
    }
    
    /**
     * Shows a styled success message.
     */
    public static void showSuccess(Component parent, String title, String message) {
        showDialog(parent, title, message, DialogType.SUCCESS);
    }
    
    /**
     * Shows a styled error message.
     */
    public static void showError(Component parent, String title, String message) {
        showDialog(parent, title, message, DialogType.ERROR);
    }
    
    /**
     * Shows a styled warning message.
     */
    public static void showWarning(Component parent, String title, String message) {
        showDialog(parent, title, message, DialogType.WARNING);
    }
    
    /**
     * Shows a styled information message.
     */
    public static void showInfo(Component parent, String title, String message) {
        showDialog(parent, title, message, DialogType.INFO);
    }
    
    /**
     * Shows a styled confirmation dialog.
     */
    public static boolean showConfirmation(Component parent, String title, String message) {
        JPanel panel = createStyledPanel(message, DialogType.QUESTION);
        
        int result = JOptionPane.showConfirmDialog(
            parent,
            panel,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Shows a styled input dialog.
     */
    public static String showInput(Component parent, String title, String message, String defaultValue) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel(message);
        label.setFont(UIConstants.FONT_REGULAR);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        
        JTextField textField = new JTextField(defaultValue, 20);
        textField.setFont(UIConstants.FONT_REGULAR);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(
            parent,
            panel,
            title,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            return textField.getText();
        }
        return null;
    }
    
    private static void showDialog(Component parent, String title, String message, DialogType type) {
        JPanel panel = createStyledPanel(message, type);
        JOptionPane.showMessageDialog(parent, panel, title, JOptionPane.PLAIN_MESSAGE);
    }
    
    private static JPanel createStyledPanel(String message, DialogType type) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Icon panel
        JPanel iconPanel = new JPanel();
        iconPanel.setBackground(Color.WHITE);
        JLabel iconLabel = new JLabel(getIcon(type));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconPanel.add(iconLabel);
        
        // Message panel
        JPanel messagePanel = new JPanel(new GridLayout(0, 1, 5, 5));
        messagePanel.setBackground(Color.WHITE);
        
        // Split message by newlines
        String[] lines = message.split("\n");
        for (String line : lines) {
            JLabel label = new JLabel(line);
            label.setFont(UIConstants.FONT_REGULAR);
            label.setForeground(UIConstants.TEXT_PRIMARY);
            messagePanel.add(label);
        }
        
        panel.add(iconPanel, BorderLayout.WEST);
        panel.add(messagePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static String getIcon(DialogType type) {
        switch (type) {
            case SUCCESS: return "✅";
            case ERROR: return "❌";
            case WARNING: return "⚠️";
            case INFO: return "ℹ️";
            case QUESTION: return "❓";
            default: return "ℹ️";
        }
    }
}
