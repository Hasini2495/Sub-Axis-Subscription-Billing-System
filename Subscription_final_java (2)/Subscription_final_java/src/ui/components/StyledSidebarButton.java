package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Professional sidebar button - NO ICONS, NO SQUARE BOXES.
 * Clean text-only design with gradient hover effect.
 */
public class StyledSidebarButton extends JButton {
    
    private Color normalBackground = new Color(255, 255, 255, 0);
    private Color hoverBackground = new Color(255, 255, 255, 30);
    private Color activeBackground = new Color(255, 255, 255, 50);
    private boolean isHovered = false;
    private boolean isActive = false;
    
    private static final int BUTTON_HEIGHT = 50;
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    
    public StyledSidebarButton(String text, String icon) {
        // IGNORE icon parameter - causes square boxes on Windows
        super(text);
        initializeButton();
    }
    
    public StyledSidebarButton(String text) {
        super(text);
        initializeButton();
    }
    
    private void initializeButton() {
        // Fixed sizing - CRITICAL for no shifting
        setFont(BUTTON_FONT);
        setPreferredSize(new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT));
        setMinimumSize(new Dimension(200, BUTTON_HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT));
        
        // Left-aligned text
        setHorizontalAlignment(SwingConstants.LEFT);
        setVerticalAlignment(SwingConstants.CENTER);
        
        // Styling - NO SQUARE BOXES
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false);
        
        // Remove any icon that could cause square boxes
        setIcon(null);
        setDisabledIcon(null);
        setPressedIcon(null);
        setRolloverIcon(null);
        
        // Fixed border - never changes
        setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        
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
            
            @Override
            public void mousePressed(MouseEvent e) {
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background based on state
        if (isActive) {
            g2d.setColor(activeBackground);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        } else if (isHovered) {
            g2d.setColor(hoverBackground);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        } else {
            g2d.setColor(normalBackground);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // Draw text - NO FONT CHANGES
        super.paintComponent(g);
    }
    
    /**
     * Sets this button as active/selected.
     */
    public void setActive(boolean active) {
        this.isActive = active;
        if (active) {
            setFont(BUTTON_FONT.deriveFont(Font.BOLD));
        } else {
            setFont(BUTTON_FONT);
        }
        repaint();
    }
    
    /**
     * Gets active state.
     */
    public boolean isActive() {
        return isActive;
    }
}
