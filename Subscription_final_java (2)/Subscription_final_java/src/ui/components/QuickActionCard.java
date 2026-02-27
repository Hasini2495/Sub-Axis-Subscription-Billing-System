package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Quick action card for dashboard.
 * Small card with icon, title, and click action.
 */
public class QuickActionCard extends StyledCardPanel {
    
    private JLabel iconLabel;
    private JLabel titleLabel;
    @SuppressWarnings("unused")
    private boolean isHovered = false;
    private List<ActionListener> actionListeners = new ArrayList<>();
    
    public QuickActionCard(String icon, String title, Color accentColor) {
        super();
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(180, 100));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Icon with colored background - HD QUALITY RENDERING
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // MAXIMUM QUALITY RENDERING HINTS
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                
                // Subtle colored background
                g2d.setColor(new Color(
                    accentColor.getRed(),
                    accentColor.getGreen(),
                    accentColor.getBlue(),
                    30
                ));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(50, 50));
        iconPanel.setLayout(new GridBagLayout());
        
        // Icon with HD rendering
        iconLabel = new JLabel(icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                // HD text rendering
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                super.paintComponent(g);
            }
        };
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28)); // Larger icon, 24→28px
        iconPanel.add(iconLabel);
        
        // Title - crisp and clear
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(17, 24, 39));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.add(iconPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(titleLabel);
        
        iconPanel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 2),
                    BorderFactory.createEmptyBorder(18, 18, 18, 18)
                ));
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                fireActionEvent();
            }
        });
    }
    
    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }
    
    public void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }
    
    private void fireActionEvent() {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "click");
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(event);
        }
    }
}

