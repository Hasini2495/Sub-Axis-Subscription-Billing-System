package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Status bar panel for the Admin Dashboard.
 * Displays system status and information.
 */
public class StatusBarPanel extends JPanel {
    private JLabel statusLabel;
    
    public StatusBarPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241)); // Light gray
        setPreferredSize(new Dimension(0, 30));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 20, 5, 20)
        ));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel versionLabel = new JLabel("v1.0.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(Color.GRAY);
        
        add(statusLabel, BorderLayout.WEST);
        add(versionLabel, BorderLayout.EAST);
    }
    
    /**
     * Updates the status message.
     * @param message Status message to display
     */
    public void setStatus(String message) {
        statusLabel.setText(message);
    }
}
