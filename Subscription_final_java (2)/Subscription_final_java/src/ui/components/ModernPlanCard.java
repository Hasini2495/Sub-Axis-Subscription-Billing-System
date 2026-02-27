package ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Modern pricing plan card with gradient header.
 * Clean design with high-contrast subscribe button.
 */
public class ModernPlanCard extends JPanel {
    
    private JButton subscribeBtn;
    private Color gradientStart;
    private Color gradientEnd;
    private boolean isPopular;
    
    public ModernPlanCard(String planName, double price, String[] features, 
                          Color gradientStart, Color gradientEnd, boolean isPopular) {
        this.gradientStart = gradientStart;
        this.gradientEnd = gradientEnd;
        this.isPopular = isPopular;
        
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(280, 420));
        setMaximumSize(new Dimension(280, 420));
        
        // Add shadow and rounded border
        setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(16, new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        initComponents(planName, price, features);
    }
    
    private void initComponents(String planName, double price, String[] features) {
        // Header with gradient
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, gradientStart,
                    getWidth(), getHeight(), gradientEnd
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(25, 20, 25, 20));
        header.setOpaque(false);
        
        if (isPopular) {
            JLabel popularLabel = new JLabel("POPULAR");
            popularLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            popularLabel.setForeground(new Color(255, 255, 255, 200));
            popularLabel.setAlignmentX(CENTER_ALIGNMENT);
            header.add(popularLabel);
            header.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        
        JLabel nameLabel = new JLabel(planName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(CENTER_ALIGNMENT);
        header.add(nameLabel);
        header.add(Box.createRigidArea(new Dimension(0, 12)));
        
        JLabel priceLabel = new JLabel(String.format("$%.2f", price));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setAlignmentX(CENTER_ALIGNMENT);
        header.add(priceLabel);
        
        JLabel periodLabel = new JLabel("/month");
        periodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        periodLabel.setForeground(new Color(255, 255, 255, 180));
        periodLabel.setAlignmentX(CENTER_ALIGNMENT);
        header.add(periodLabel);
        
        add(header, BorderLayout.NORTH);
        
        // Features
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setBackground(Color.WHITE);
        featuresPanel.setBorder(new EmptyBorder(25, 20, 20, 20));
        
        for (String feature : features) {
            JLabel featureLabel = new JLabel("• " + feature);
            featureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            featureLabel.setForeground(new Color(55, 65, 81));
            featureLabel.setAlignmentX(LEFT_ALIGNMENT);
            featuresPanel.add(featureLabel);
            featuresPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        add(featuresPanel, BorderLayout.CENTER);
        
        // Subscribe button - MAXIMUM CONTRAST
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(0, 20, 25, 20));
        
        subscribeBtn = new JButton("Subscribe Now");
        subscribeBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        subscribeBtn.setPreferredSize(new Dimension(220, 48));
        subscribeBtn.setBackground(new Color(17, 24, 39)); // Dark background
        subscribeBtn.setForeground(Color.WHITE); // White text
        subscribeBtn.setFocusPainted(false);
        subscribeBtn.setBorderPainted(false);
        subscribeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        subscribeBtn.setOpaque(true);
        
        subscribeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                subscribeBtn.setBackground(new Color(31, 41, 55));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                subscribeBtn.setBackground(new Color(17, 24, 39));
            }
        });
        
        buttonPanel.add(subscribeBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public JButton getSubscribeButton() {
        return subscribeBtn;
    }
    
    // Custom rounded border
    private static class RoundedBorder implements javax.swing.border.Border {
        private int radius;
        private Color color;
        
        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}
