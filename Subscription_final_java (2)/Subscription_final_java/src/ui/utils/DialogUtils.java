package ui.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Centralized utility class for displaying dialogs.
 * Provides consistent UI/UX across the application.
 */
public class DialogUtils {
    
    /**
     * Shows a success message dialog.
     * @param parent Parent component
     * @param message Success message
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Shows an error message dialog.
     * @param parent Parent component
     * @param message Error message
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Shows a warning message dialog.
     * @param parent Parent component
     * @param message Warning message
     */
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Warning",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    /**
     * Shows a confirmation dialog.
     * @param parent Parent component
     * @param message Confirmation message
     * @return true if user confirmed
     */
    public static boolean showConfirmation(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(
            parent,
            message,
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Shows a simple progress dialog.
     * @param parent Parent component
     * @param message Progress message
     * @return JDialog instance that can be closed later
     */
    public static JDialog showProgressDialog(Component parent, String message) {
        JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setTitle("Processing");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel(message);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        
        panel.add(label, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        
        return dialog;
    }
    
    /**
     * Shows an input dialog for text input.
     * @param parent Parent component
     * @param message Input prompt message
     * @param title Dialog title
     * @return User input or null if cancelled
     */
    public static String showInputDialog(Component parent, String message, String title) {
        return (String) JOptionPane.showInputDialog(
            parent,
            message,
            title,
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            ""
        );
    }
}
