package ui.components;

import ui.theme.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom styled button with rounded corners and hover effects.
 * Provides consistent professional appearance across the application.
 */
public class StyledButton extends JButton {
    
    public enum ButtonStyle {
        PRIMARY, SUCCESS, DANGER, WARNING, SECONDARY
    }
    
    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private ButtonStyle style;
    private boolean isHovered = false;
    private boolean isPressed = false;
    
    public StyledButton(String text) {
        this(text, ButtonStyle.PRIMARY);
    }
    
    public StyledButton(String text, ButtonStyle style) {
        super(text);
        this.style = style;
        initializeButton();
    }
    
    private void initializeButton() {
        setColors();
        
        setFont(UIConstants.FONT_BUTTON);
        setForeground(UIConstants.TEXT_LIGHT);
        setPreferredSize(UIConstants.BUTTON_SIZE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }
    
    private void setColors() {
        switch (style) {
            case PRIMARY:
                normalColor = UIConstants.PRIMARY_COLOR;
                hoverColor = UIConstants.PRIMARY_DARK;
                pressedColor = new Color(24, 62, 173);
                break;
            case SUCCESS:
                normalColor = UIConstants.SUCCESS_COLOR;
                hoverColor = new Color(22, 163, 74);
                pressedColor = new Color(21, 128, 61);
                break;
            case DANGER:
                normalColor = UIConstants.DANGER_COLOR;
                hoverColor = new Color(220, 38, 38);
                pressedColor = new Color(185, 28, 28);
                break;
            case WARNING:
                normalColor = UIConstants.WARNING_COLOR;
                hoverColor = new Color(234, 88, 12);
                pressedColor = new Color(194, 65, 12);
                break;
            case SECONDARY:
                normalColor = new Color(100, 116, 139);
                hoverColor = new Color(71, 85, 105);
                pressedColor = new Color(51, 65, 85);
                break;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Choose color based on state
        Color bgColor = normalColor;
        if (!isEnabled()) {
            bgColor = new Color(203, 213, 225);
        } else if (isPressed) {
            bgColor = pressedColor;
        } else if (isHovered) {
            bgColor = hoverColor;
        }
        
        // Draw rounded rectangle background
        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 
                UIConstants.BUTTON_RADIUS, UIConstants.BUTTON_RADIUS));
        
        g2.dispose();
        
        // Draw text
        super.paintComponent(g);
    }
}
