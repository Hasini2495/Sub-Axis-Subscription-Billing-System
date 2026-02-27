package ui.components;

import model.Plan;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Professional pricing plan card with gradient, features list, and subscribe button.
 * Production-level design matching modern SaaS pricing pages.
 */
public class ProfessionalPlanCard extends JPanel {
    
    private Plan plan;
    private Color gradientStart;
    private Color gradientEnd;
    private boolean isRecommended;
    private JLabel priceLabel;
    @SuppressWarnings("unused")
    private boolean isYearly = false;
    private StyledButton subscribeBtn;
    
    public ProfessionalPlanCard(Plan plan, Color gradientStart, Color gradientEnd, boolean isRecommended) {
        this.plan = plan;
        this.gradientStart = gradientStart;
        this.gradientEnd = gradientEnd;
        this.isRecommended = isRecommended;
        
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(320, 480));
        setOpaque(false);
        
        initializeCard();
    }
    
    /**
     * Alternative constructor for quick plan creation with just a name.
     */
    public ProfessionalPlanCard(String planType, Color gradientStart, Color gradientEnd, boolean isRecommended) {
        // Create a mock Plan object with default values
        this.plan = createDefaultPlan(planType);
        this.gradientStart = gradientStart;
        this.gradientEnd = gradientEnd;
        this.isRecommended = isRecommended;
        
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(320, 480));
        setOpaque(false);
        
        initializeCard();
    }
    
    private Plan createDefaultPlan(String planType) {
        Plan mockPlan = new Plan();
        mockPlan.setPlanName(planType);
        mockPlan.setBillingCycle("Monthly");
        
        // Set default prices based on plan type
        switch (planType.toUpperCase()) {
            case "BASIC":
                mockPlan.setPrice(new java.math.BigDecimal("9.99"));
                break;
            case "STARTER":
                mockPlan.setPrice(new java.math.BigDecimal("19.99"));
                break;
            case "GROWTH":
                mockPlan.setPrice(new java.math.BigDecimal("39.99"));
                break;
            case "PROFESSIONAL":
                mockPlan.setPrice(new java.math.BigDecimal("79.99"));
                break;
            case "BUSINESS":
                mockPlan.setPrice(new java.math.BigDecimal("149.99"));
                break;
            case "PREMIUM":
                mockPlan.setPrice(new java.math.BigDecimal("249.99"));
                break;
            case "ENTERPRISE":
                mockPlan.setPrice(new java.math.BigDecimal("499.99"));
                break;
            default:
                mockPlan.setPrice(new java.math.BigDecimal("29.99"));
        }
        
        return mockPlan;
    }
    
    private void initializeCard() {
        // Main card panel with gradient
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, 20, 20);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, gradientStart,
                    0, getHeight(), gradientEnd
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);
                
                // Recommended badge
                if (isRecommended) {
                    g2d.setColor(new Color(251, 191, 36));
                    g2d.fillRoundRect(getWidth() / 2 - 60, 15, 120, 30, 15, 15);
                    
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    FontMetrics fm = g2d.getFontMetrics();
                    String text = "RECOMMENDED";
                    int textWidth = fm.stringWidth(text);
                    g2d.drawString(text, (getWidth() - textWidth) / 2, 34);
                }
            }
        };
        
        cardPanel.setLayout(new BorderLayout(0, 20));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(
            isRecommended ? 60 : 30,
            30, 30, 30
        ));
        cardPanel.setOpaque(false);
        
        // Header section
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        
        JLabel planName = new JLabel(plan.getPlanName());
        planName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        planName.setForeground(Color.WHITE);
        planName.setAlignmentX(CENTER_ALIGNMENT);
        
        // Price
        priceLabel = new JLabel("$" + plan.getPrice());
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        JLabel billingLabel = new JLabel("per " + plan.getBillingCycle().toLowerCase());
        billingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        billingLabel.setForeground(new Color(255, 255, 255, 200));
        billingLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        headerPanel.add(planName);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        headerPanel.add(priceLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(billingLabel);
        
        // Features list
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setOpaque(false);
        featuresPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        List<String> features = getPlanFeatures(plan.getPlanName());
        for (String feature : features) {
            JPanel featureRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            featureRow.setOpaque(false);
            featureRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            
            JLabel checkIcon = new JLabel("✓");
            checkIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));
            checkIcon.setForeground(new Color(16, 185, 129));
            
            JLabel featureLabel = new JLabel(feature);
            featureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            featureLabel.setForeground(new Color(255, 255, 255, 230));
            
            featureRow.add(checkIcon);
            featureRow.add(featureLabel);
            featuresPanel.add(featureRow);
        }
        
        // Subscribe button - MAXIMUM CONTRAST
        subscribeBtn = new StyledButton("Subscribe Now", StyledButton.ButtonStyle.PRIMARY);
        subscribeBtn.setPreferredSize(new Dimension(220, 50));
        subscribeBtn.setMaximumSize(new Dimension(220, 50));
        subscribeBtn.setMinimumSize(new Dimension(220, 50));
        subscribeBtn.setAlignmentX(CENTER_ALIGNMENT);
        subscribeBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        // CRITICAL: Pure white background with pure black text
        subscribeBtn.setBackground(Color.WHITE);
        subscribeBtn.setForeground(Color.BLACK);
        subscribeBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(subscribeBtn);
        
        cardPanel.add(headerPanel, BorderLayout.NORTH);
        cardPanel.add(featuresPanel, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(cardPanel, BorderLayout.CENTER);
    }
    
    /**
     * Gets feature list based on plan name.
     */
    private List<String> getPlanFeatures(String planName) {
        switch (planName.toUpperCase()) {
            case "BASIC":
                return Arrays.asList(
                    "1 User Account",
                    "Basic Features",
                    "Email Support",
                    "5GB Storage"
                );
            case "STARTER":
                return Arrays.asList(
                    "3 User Accounts",
                    "Standard Features",
                    "Priority Email Support",
                    "20GB Storage",
                    "Basic Analytics"
                );
            case "GROWTH":
                return Arrays.asList(
                    "10 User Accounts",
                    "Advanced Features",
                    "24/7 Support",
                    "100GB Storage",
                    "Advanced Analytics",
                    "API Access"
                );
            case "PROFESSIONAL":
                return Arrays.asList(
                    "25 User Accounts",
                    "Professional Features",
                    "Dedicated Support",
                    "500GB Storage",
                    "Custom Analytics",
                    "Full API Access",
                    "Priority Processing"
                );
            case "BUSINESS":
                return Arrays.asList(
                    "50 User Accounts",
                    "Business Features",
                    "Account Manager",
                    "1TB Storage",
                    "Business Analytics",
                    "Unlimited API Calls",
                    "SLA Guarantee"
                );
            case "PREMIUM":
                return Arrays.asList(
                    "100 User Accounts",
                    "Premium Features",
                    "VIP Support",
                    "5TB Storage",
                    "Real-time Analytics",
                    "White-label Options",
                    "Custom Integrations"
                );
            case "ENTERPRISE":
                return Arrays.asList(
                    "Unlimited Users",
                    "Enterprise Features",
                    "Dedicated Team",
                    "Unlimited Storage",
                    "Custom Dashboards",
                    "Single Sign-On (SSO)",
                    "Advanced Security",
                    "Custom SLA"
                );
            default:
                return Arrays.asList(
                    "Multiple Users",
                    "Core Features",
                    "Email Support",
                    "Cloud Storage"
                );
        }
    }
    
    public void updatePrice(boolean yearly) {
        this.isYearly = yearly;
        if (yearly) {
            double yearlyPrice = plan.getPrice().doubleValue() * 10; // 2 months free
            priceLabel.setText("$" + String.format("%.0f", yearlyPrice));
        } else {
            priceLabel.setText("$" + plan.getPrice());
        }
    }
    
    public JButton getSubscribeButton() {
        // Find and return subscribe button
        return findSubscribeButton(this);
    }
    
    private JButton findSubscribeButton(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                return (JButton) comp;
            } else if (comp instanceof Container) {
                JButton btn = findSubscribeButton((Container) comp);
                if (btn != null) return btn;
            }
        }
        return null;
    }
    
    /**
     * Adds an action listener to the subscribe button.
     */
    public void addSubscribeListener(java.awt.event.ActionListener listener) {
        if (subscribeBtn != null) {
            subscribeBtn.addActionListener(listener);
        }
    }
    
    /**
     * Gets the Plan object associated with this card.
     */
    public Plan getPlan() {
        return plan;
    }
}

