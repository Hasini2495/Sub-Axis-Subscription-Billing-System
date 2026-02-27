package ui;

import service.BillingService;
import ui.components.*;
import ui.theme.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Enterprise-Grade Admin Dashboard with modern SaaS-style UI.
 * Complete redesign with professional analytics, charts, and polished interface.
 */
public class AdminDashboard extends JFrame {
    private final BillingService billingService;
    
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JLabel statusLabel;
    
    // View panels
    private DashboardPanel dashboardPanel;
    private CreatePlanForm createPlanForm;
    private RegisterUserForm registerUserForm;
    private AssignSubscriptionForm assignSubscriptionForm;
    private SubscriptionViewForm subscriptionViewForm;
    private InvoiceViewForm invoiceViewForm;
    private PaymentHistoryForm paymentHistoryForm;
    
    public AdminDashboard() {
        this.billingService = new BillingService();
        initializeUI();
    }
    
    /**
     * Initializes the modern professional UI.
     */
    private void initializeUI() {
        setTitle("SubAxis - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 950);
        setLocationRelativeTo(null);
        
        // Main layout
        setLayout(new BorderLayout());
        
        // Professional sidebar with gradient
        add(createEnhancedSidebar(), BorderLayout.WEST);
        
        // Main content panel with CardLayout — dark gradient background
        cardLayout = new CardLayout();
        mainContentPanel = new ui.components.DarkGradientPanel(cardLayout);
        
        // Initialize all view panels
        dashboardPanel = new DashboardPanel();
        createPlanForm = new CreatePlanForm();
        registerUserForm = new RegisterUserForm();
        assignSubscriptionForm = new AssignSubscriptionForm();
        subscriptionViewForm = new SubscriptionViewForm();
        invoiceViewForm = new InvoiceViewForm();
        paymentHistoryForm = new PaymentHistoryForm();
        
        // Add panels to CardLayout
        mainContentPanel.add(dashboardPanel, "DASHBOARD");
        mainContentPanel.add(createPlanForm, "PLANS");
        mainContentPanel.add(registerUserForm, "USERS");
        mainContentPanel.add(assignSubscriptionForm, "SUBSCRIPTIONS");
        mainContentPanel.add(subscriptionViewForm, "SUBSCRIPTIONS_VIEW");
        mainContentPanel.add(invoiceViewForm, "INVOICES");
        mainContentPanel.add(paymentHistoryForm, "PAYMENTS");
        
        // Wrap content panel with enhanced header
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(new Color(26, 27, 58)); // deep indigo
        contentWrapper.add(createEnhancedHeader(), BorderLayout.NORTH);
        contentWrapper.add(mainContentPanel, BorderLayout.CENTER);
        contentWrapper.add(createEnhancedStatusBar(), BorderLayout.SOUTH);
        
        add(contentWrapper, BorderLayout.CENTER);
        
        // Show dashboard by default
        cardLayout.show(mainContentPanel, "DASHBOARD");
        setStatus("Welcome to SubAxis Admin Dashboard");
    }
    
    /**
     * Creates enhanced sidebar with deep gradient and modern buttons.
     */
    private JPanel createEnhancedSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Deep indigo gradient: #1A1B3A → #3A2C6D
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(18, 18, 45),
                    0, getHeight(), new Color(58, 44, 109)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 18, 25, 18));
        
        // Logo section
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoPanel.setAlignmentX(LEFT_ALIGNMENT);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel logoIcon = new JLabel("⚡");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JLabel logoText = new JLabel("SubAxis");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoText.setForeground(Color.WHITE);
        
        logoPanel.add(logoIcon);
        logoPanel.add(logoText);
        
        sidebar.add(logoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 35)));
        
        // Navigation buttons
        addEnhancedSidebarButton(sidebar, "Dashboard", "DASHBOARD");
        addEnhancedSidebarButton(sidebar, "Users", "USERS");
        addEnhancedSidebarButton(sidebar, "Plans", "PLANS");
        addEnhancedSidebarButton(sidebar, "Subscriptions", "SUBSCRIPTIONS");
        addEnhancedSidebarButton(sidebar, "View All", "SUBSCRIPTIONS_VIEW");
        addEnhancedSidebarButton(sidebar, "Invoices", "INVOICES");
        addEnhancedSidebarButton(sidebar, "Payments", "PAYMENTS");
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(255, 255, 255, 30));
        separator.setBackground(new Color(255, 255, 255, 30));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(separator);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Actions
        addEnhancedSidebarButton(sidebar, "Run Billing", "BILLING");
        sidebar.add(Box.createVerticalGlue());
        
        return sidebar;
    }
    
    /**
     * Adds an enhanced sidebar button with modern glassy styling.
     */
    private void addEnhancedSidebarButton(JPanel sidebar, String text, String command) {
        JButton button = new JButton() {
            private boolean hovered = false;
            private boolean isActive = false;
            
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Glassy background with hover effect
                if (isActive) {
                    // Active section: gradient with glow
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(59, 130, 246),
                        getWidth(), 0, new Color(37, 99, 235)
                    );
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    
                    // Subtle glow effect
                    g2.setColor(new Color(59, 130, 246, 40));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                } else if (hovered) {
                    // Hover: semi-transparent white
                    g2.setColor(new Color(255, 255, 255, 25));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setText(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 17));
        button.setForeground(Color.WHITE);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setActionCommand(command);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.addActionListener(this::handleNavigation);
        
        // Track text rendering with letter spacing
        button.putClientProperty("letterSpacing", 0.5f);
        
        sidebar.add(button);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
    }
    
    /**
     * Creates enhanced header with gradient, admin info, and logout button.
     */
    private JPanel createEnhancedHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Soft gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(37, 99, 235),
                    getWidth(), 0, new Color(139, 92, 246)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 75));
        header.setBorder(BorderFactory.createEmptyBorder(18, 30, 18, 30));
        header.setOpaque(true);
        
        // Left section - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        leftPanel.add(titleLabel);
        
        // Right section - Admin info and Logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightPanel.setOpaque(false);
        
        // Admin profile circle and name
        JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        adminPanel.setOpaque(false);
        
        // Profile circle
        JPanel profileCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(255, 255, 255, 180));
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                g2.setColor(new Color(37, 99, 235));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                String initial = "A";
                int x = (getWidth() - fm.stringWidth(initial)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initial, x, y);
            }
        };
        profileCircle.setPreferredSize(new Dimension(38, 38));
        profileCircle.setOpaque(false);
        
        JLabel adminLabel = new JLabel("Administrator");
        adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        adminLabel.setForeground(Color.WHITE);
        
        adminPanel.add(profileCircle);
        adminPanel.add(adminLabel);
        
        // Logout button
        JButton logoutButton = new JButton() {
            private boolean hovered = false;
            
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                Color color1 = hovered ? new Color(239, 68, 68) : new Color(220, 38, 38);
                Color color2 = hovered ? new Color(220, 38, 38) : new Color(185, 28, 28);
                GradientPaint gradient = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Shadow effect
                if (hovered) {
                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.fillRoundRect(2, 2, getWidth(), getHeight(), 12, 12);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        logoutButton.setText("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setPreferredSize(new Dimension(120, 38));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> handleLogout());
        
        rightPanel.add(adminPanel);
        rightPanel.add(logoutButton);
        
        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    /**
     * Creates enhanced status bar with modern styling.
     */
    private JPanel createEnhancedStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(18, 18, 45)); // darkest indigo
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 30)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(UIConstants.FONT_SMALL);
        statusLabel.setForeground(new Color(154, 154, 203)); // muted lavender

        JLabel versionLabel = new JLabel("SubAxis v2.0 © 2026");
        versionLabel.setFont(UIConstants.FONT_SMALL);
        versionLabel.setForeground(new Color(154, 154, 203));

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(versionLabel, BorderLayout.EAST);

        return statusBar;
    }
    
    /**
     * Handles navigation between views with status updates.
     */
    private void handleNavigation(ActionEvent e) {
        String command = e.getActionCommand();
        
        switch (command) {
            case "DASHBOARD":
                cardLayout.show(mainContentPanel, "DASHBOARD");
                dashboardPanel.loadMetrics();
                setStatus("📊 Dashboard - System overview and analytics");
                break;
                
            case "PLANS":
                cardLayout.show(mainContentPanel, "PLANS");
                setStatus("📦 Plans - Create and manage subscription plans");
                break;
                
            case "USERS":
                cardLayout.show(mainContentPanel, "USERS");
                setStatus("👥 Users - Register and manage users");
                break;
                
            case "SUBSCRIPTIONS":
                cardLayout.show(mainContentPanel, "SUBSCRIPTIONS");
                setStatus("🔄 Subscriptions - Assign subscriptions to users");
                break;
                
            case "SUBSCRIPTIONS_VIEW":
                cardLayout.show(mainContentPanel, "SUBSCRIPTIONS_VIEW");
                subscriptionViewForm.loadSubscriptions();
                setStatus("📋 View All - All active and expired subscriptions");
                break;
                
            case "INVOICES":
                cardLayout.show(mainContentPanel, "INVOICES");
                invoiceViewForm.loadInvoices();
                setStatus("📄 Invoices - Manage and process invoices");
                break;
                
            case "PAYMENTS":
                cardLayout.show(mainContentPanel, "PAYMENTS");
                paymentHistoryForm.loadPayments();
                setStatus("💳 Payments - Payment history and transactions");
                break;
                
            case "BILLING":
                handleRunBilling();
                break;
                
            default:
                setStatus("⚠️ Unknown action: " + command);
        }
    }
    
    /**
     * Handles running the automated billing process.
     */
    private void handleRunBilling() {
        boolean confirmed = ProfessionalDialog.showConfirmation(
            this,
            "Run Billing Process",
            "Run automated billing for all active subscriptions?\nThis will generate invoices and update statuses."
        );
        
        if (!confirmed) {
            return;
        }
        
        setStatus("⚡ Running billing process...");
        
        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() {
                return billingService.runBillingProcess();
            }
            
            @Override
            protected void done() {
                try {
                    int invoicesGenerated = get();
                    
                    ProfessionalDialog.showSuccess(
                        AdminDashboard.this,
                        "Billing Complete",
                        String.format("Billing process completed successfully!\n\n✓ %d invoices generated\n✓ Subscriptions updated\n✓ Ready for payments", 
                                    invoicesGenerated)
                    );
                    
                    dashboardPanel.loadMetrics();
                    setStatus("✓ Billing completed - " + invoicesGenerated + " invoices generated");
                    
                } catch (Exception ex) {
                    ProfessionalDialog.showError(
                        AdminDashboard.this,
                        "Billing Error",
                        "Error running billing process:\n" + ex.getMessage()
                    );
                    setStatus("❌ Billing process failed");
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Handles logout action.
     */
    private void handleLogout() {
        boolean confirm = ProfessionalDialog.showConfirmation(
            this, 
            "Logout Confirmation", 
            "Are you sure you want to logout from Admin Dashboard?"
        );
        
        if (confirm) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
    
    /**
     * Updates the status bar message.
     */
    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    /**
     * Main entry point - Starts with LoginFrame.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Start with login screen
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
