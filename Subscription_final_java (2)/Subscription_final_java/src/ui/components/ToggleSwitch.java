package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern toggle switch component (ON/OFF).
 * Smooth animation, professional appearance.
 * Used for Auto-Renew feature.
 */
public class ToggleSwitch extends JComponent implements ItemSelectable {
    
    private boolean isOn = false;
    private float thumbPosition = 0.0f; // 0.0 (left) to 1.0 (right)
    private List<ItemListener> itemListeners;
    
    private static final int WIDTH = 50;
    private static final int HEIGHT = 26;
    private static final Color ON_COLOR = new Color(16, 185, 129); // Success green
    private static final Color OFF_COLOR = new Color(203, 213, 225); // Gray
    private static final Color THUMB_COLOR = Color.WHITE;
    
    private Timer animationTimer;
    private static final int ANIMATION_DURATION = 200; // ms
    private static final int ANIMATION_FPS = 60;
    private float animationStep;
    
    public ToggleSwitch() {
        this(false);
    }
    
    public ToggleSwitch(boolean initialState) {
        this.isOn = initialState;
        this.thumbPosition = initialState ? 1.0f : 0.0f;
        
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setMaximumSize(new Dimension(WIDTH, HEIGHT));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Calculate animation step
        int totalFrames = (ANIMATION_DURATION / 1000) * ANIMATION_FPS;
        animationStep = 1.0f / totalFrames;
        
        // Click to toggle
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggle();
            }
        });
    }
    
    public void toggle() {
        setOn(!isOn);
    }
    
    public void setOn(boolean on) {
        if (this.isOn != on) {
            boolean oldState = this.isOn;
            this.isOn = on;
            animateToggle();
            firePropertyChange("state", oldState, on);
            fireItemStateChanged();
        }
    }
    
    public boolean isOn() {
        return isOn;
    }
    
    // Standard Swing-compatible methods
    public void setSelected(boolean selected) {
        setOn(selected);
    }
    
    public boolean isSelected() {
        return isOn();
    }
    
    public void addItemListener(ItemListener listener) {
        if (itemListeners == null) {
           itemListeners = new ArrayList<>();
        }
        itemListeners.add(listener);
    }
    
    public void removeItemListener(ItemListener listener) {
        if (itemListeners != null) {
            itemListeners.remove(listener);
        }
    }
    
    private void fireItemStateChanged() {
        if (itemListeners != null && !itemListeners.isEmpty()) {
            ItemEvent event = new ItemEvent(
                this,
                ItemEvent.ITEM_STATE_CHANGED,
                this,
                isOn ? ItemEvent.SELECTED : ItemEvent.DESELECTED
            );
            for (ItemListener listener : new ArrayList<>(itemListeners)) {
                listener.itemStateChanged(event);
            }
        }
    }
    
    private void animateToggle() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        
        final float targetPosition = isOn ? 1.0f : 0.0f;
        final float startPosition = thumbPosition;
        @SuppressWarnings("unused")
        final float totalDistance = Math.abs(targetPosition - startPosition);
        
        animationTimer = new Timer(1000 / ANIMATION_FPS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isOn) {
                    thumbPosition += animationStep;
                    if (thumbPosition >= 1.0f) {
                        thumbPosition = 1.0f;
                        animationTimer.stop();
                    }
                } else {
                    thumbPosition -= animationStep;
                    if (thumbPosition <= 0.0f) {
                        thumbPosition = 0.0f;
                        animationTimer.stop();
                    }
                }
                repaint();
            }
        });
        animationTimer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Interpolate background color
        Color backgroundColor;
        if (thumbPosition < 0.5f) {
            // Closer to OFF
            backgroundColor = OFF_COLOR;
        } else {
            // Closer to ON
            backgroundColor = ON_COLOR;
        }
        
        // Draw track (rounded rectangle)
        g2d.setColor(backgroundColor);
        g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, height, height));
        
        // Calculate thumb position
        int thumbSize = height - 4;
        int thumbX = (int) (2 + thumbPosition * (width - thumbSize - 4));
        int thumbY = 2;
        
        // Draw thumb (circle)
        g2d.setColor(THUMB_COLOR);
        g2d.fill(new Ellipse2D.Float(thumbX, thumbY, thumbSize, thumbSize));
        
        // Add subtle shadow on thumb
        g2d.setColor(new Color(0, 0, 0, 20));
        g2d.draw(new Ellipse2D.Float(thumbX, thumbY, thumbSize, thumbSize));
    }
    
    /**
     * Adds a change listener for toggle state changes.
     */
    public void addChangeListener(java.beans.PropertyChangeListener listener) {
        addPropertyChangeListener("state", listener);
    }
    
    // ItemSelectable interface implementation
    @Override
    public Object[] getSelectedObjects() {
        return isOn ? new Object[]{this} : null;
    }
}
