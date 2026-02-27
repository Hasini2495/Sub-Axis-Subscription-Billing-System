package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Clean sidebar navigation button - NO ICONS, NO SQUARE BOXES.
 * Text-only design with smooth hover transition.
 */
public class ModernSidebarButton extends JButton {
    
    private boolean isActive = false;
    private boolean isHovered = false;
    
    private static final Color HOVER_BG = new Color(255, 255, 255, 25);
    private static final Color ACTIVE_BG = new Color(255, 255, 255, 40);
    private static final int HEIGHT = 48;
    
    public ModernSidebarButton(String text) {
        super(text);
        initButton();
    }
    
    private void initButton() {
        // Fixed size - prevents shifting
        setPreferredSize(new Dimension(240, HEIGHT));
        setMinimumSize(new Dimension(240, HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
        
        // Style
        setFont(new Font("Segoe UI", Font.PLAIN, 15));
        setForeground(Color.WHITE);
        setHorizontalAlignment(SwingConstants.LEFT);
        setVerticalAlignment(SwingConstants.CENTER);
        
        // Remove default styling
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        
        // PRECISE PADDING: 18px left for proper centering
        setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        
        // Cursor
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover listeners
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isActive) {
                    isHovered = true;
                    repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Background
        if (isActive) {
            g2d.setColor(ACTIVE_BG);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        } else if (isHovered) {
            g2d.setColor(HOVER_BG);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        }
        
        super.paintComponent(g);
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
        setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 15));
        repaint();
    }
    
    public boolean isActive() {
        return isActive;
    }
}
