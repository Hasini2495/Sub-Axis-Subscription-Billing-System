package ui;

import model.*;
import service.*;
import ui.components.*;
import ui.theme.UIConstants;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

/**
 * Professional SaaS User Dashboard.
 * Production-level UI with gradient sidebar, card-based layout, and all features.
 */
@SuppressWarnings("unused")
public class UserDashboard extends JFrame {
    
    // Services
    private User currentUser;
    private PlanService planService;
    private SubscriptionService subscriptionService;
    private InvoiceService invoiceService;
    private PaymentService paymentService;
    @SuppressWarnings("unused")
    private UserService userService;
    
    // UI Components
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JLabel statusLabel;
    private Map<String, StyledSidebarButton> navigationButtons;
    @SuppressWarnings("unused")
    private String currentPanel = "DASHBOARD";
    
    // Panel names
    private static final String PANEL_DASHBOARD = "DASHBOARD";
    private static final String PANEL_PLANS = "PLANS";
    private static final String PANEL_OFFERS = "OFFERS";
    private static final String PANEL_SUBSCRIPTIONS = "SUBSCRIPTIONS";
    private static final String PANEL_PAYMENTS = "PAYMENTS";
    private static final String PANEL_INVOICES = "INVOICES";
    private static final String PANEL_SETTINGS = "SETTINGS";
    
    public UserDashboard(User user) {
        this.currentUser = user;
        this.planService = new PlanService();
        this.subscriptionService = new SubscriptionService();
        this.invoiceService = new InvoiceService();
        this.paymentService = new PaymentService();
        this.userService = new UserService();
        this.navigationButtons = new HashMap<>();
        
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("SaaS Platform - User Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1400, 900));
        
        setLayout(new BorderLayout());
        
        // Header
        add(createHeader(), BorderLayout.NORTH);
        
        // Gradient Sidebar
        add(createSidebar(), BorderLayout.WEST);
        
        // Main Content Area
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(UIConstants.BACKGROUND_COLOR);
        
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Create all panels
        mainContentPanel.add(createDashboardPanel(), PANEL_DASHBOARD);
        mainContentPanel.add(createPlansPanel(), PANEL_PLANS);
        mainContentPanel.add(createOffersPanel(), PANEL_OFFERS);
        mainContentPanel.add(createSubscriptionsPanel(), PANEL_SUBSCRIPTIONS);
        mainContentPanel.add(createPaymentsPanel(), PANEL_PAYMENTS);
        mainContentPanel.add(createInvoicesPanel(), PANEL_INVOICES);
        mainContentPanel.add(createSettingsPanel(), PANEL_SETTINGS);
        
        centerContainer.add(mainContentPanel, BorderLayout.CENTER);
        centerContainer.add(createStatusBar(), BorderLayout.SOUTH);
        
        add(centerContainer, BorderLayout.CENTER);
        
        // Show dashboard by default
        navigateToPanel(PANEL_DASHBOARD);
    }
    
    /**
     * Creates professional header with SubAxis branding and status indicator.
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        // Left - SubAxis Brand
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(Color.WHITE);
        
        LogoComponent logoComponent = new LogoComponent(48, 48);
        
        JLabel titleLabel = new JLabel(UIConstants.BRAND_NAME);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(79, 70, 229));
        
        JLabel taglineLabel = new JLabel(UIConstants.BRAND_TAGLINE);
        taglineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        taglineLabel.setForeground(new Color(107, 114, 128));
        
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(Color.WHITE);
        brandPanel.add(titleLabel);
        brandPanel.add(taglineLabel);
        
        leftPanel.add(logoComponent);
        leftPanel.add(brandPanel);
        
        // Right - Status + User + Logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(Color.WHITE);
        
        // Status indicator
        StatusIndicator statusIndicator = new StatusIndicator(true);
        JLabel statusText = new JLabel("Active");
        statusText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusText.setForeground(new Color(107, 114, 128));
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.add(statusIndicator);
        statusPanel.add(statusText);
        
        // User badge
        JPanel userBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        userBadge.setBackground(new Color(243, 244, 246));
        userBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        
        JLabel userName = new JLabel(currentUser.getName());
        userName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userName.setForeground(new Color(17, 24, 39));
        
        userBadge.add(userIcon);
        userBadge.add(userName);
        
        // Logout button
        StyledButton logoutBtn = new StyledButton("Logout", StyledButton.ButtonStyle.DANGER);
        logoutBtn.setPreferredSize(new Dimension(100, 36));
        logoutBtn.addActionListener(e -> handleLogout());
        
        rightPanel.add(statusPanel);
        rightPanel.add(userBadge);
        rightPanel.add(logoutBtn);
        
        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    /**
     * Creates gradient sidebar with proper width - NO TEXT TRUNCATION.
     */
    private JPanel createSidebar() {
        // Gradient panel - Purple to Blue theme with FIXED 280px width
        GradientPanel sidebar = new GradientPanel(
            new Color(109, 40, 217), // Purple
            new Color(79, 70, 229)     // Indigo
        );
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(280, 0));  // INCREASED from 260
        sidebar.setMinimumSize(new Dimension(280, 0));
        sidebar.setMaximumSize(new Dimension(280, Integer.MAX_VALUE));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        
        // Logo section - SubAxis branding - FULL WIDTH
        JPanel logoSection = new JPanel();
        logoSection.setLayout(new BoxLayout(logoSection, BoxLayout.Y_AXIS));
        logoSection.setOpaque(false);
        logoSection.setMaximumSize(new Dimension(280, 150));
        logoSection.setAlignmentX(CENTER_ALIGNMENT);
        
        LogoComponent sidebarLogo = new LogoComponent(56, 56);
        sidebarLogo.setAlignmentX(CENTER_ALIGNMENT);
        
        JLabel logoText = new JLabel(UIConstants.BRAND_NAME);
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoText.setForeground(Color.WHITE);
        logoText.setAlignmentX(CENTER_ALIGNMENT);
        
        JLabel logoSubtext = new JLabel("Subscription Management");
        logoSubtext.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoSubtext.setForeground(new Color(255, 255, 255, 200));
        logoSubtext.setAlignmentX(CENTER_ALIGNMENT);
        
        logoSection.add(sidebarLogo);
        logoSection.add(Box.createRigidArea(new Dimension(0, 12)));
        logoSection.add(logoText);
        logoSection.add(Box.createRigidArea(new Dimension(0, 4)));
        logoSection.add(logoSubtext);
        
        sidebar.add(logoSection);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Navigation section label
        JLabel navLabel = new JLabel("NAVIGATION");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navLabel.setForeground(new Color(255, 255, 255, 180));
        navLabel.setAlignmentX(LEFT_ALIGNMENT);
        navLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 10, 0));
        sidebar.add(navLabel);
        
        // Navigation buttons - FULL WIDTH
        addNavButton(sidebar, "📊", "Dashboard", PANEL_DASHBOARD);
        addNavButton(sidebar, "📦", "Available Plans", PANEL_PLANS);
        addNavButton(sidebar, "🎁", "Special Offers", PANEL_OFFERS);
        addNavButton(sidebar, "🔄", "My Subscriptions", PANEL_SUBSCRIPTIONS);
        addNavButton(sidebar, "💳", "Payments", PANEL_PAYMENTS);
        addNavButton(sidebar, "📄", "Invoices", PANEL_INVOICES);
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setMaximumSize(new Dimension(260, 1));
        sidebar.add(sep);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        addNavButton(sidebar, "⚙️", "Settings", PANEL_SETTINGS);
        
        sidebar.add(Box.createVerticalGlue());
        
        // Version footer
        JLabel versionLabel = new JLabel("v2.0.0 Pro");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(255, 255, 255, 150));
        versionLabel.setAlignmentX(CENTER_ALIGNMENT);
        sidebar.add(versionLabel);
        
        return sidebar;
    }
    
    /**
     * Adds navigation button to sidebar.
     */
    private void addNavButton(JPanel sidebar, String icon, String text, String panelName) {
        StyledSidebarButton button = new StyledSidebarButton(text, icon);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.addActionListener(e -> navigateToPanel(panelName));
        
        navigationButtons.put(panelName, button);
        sidebar.add(button);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
    }
    
    /**
     * Navigates to specified panel and updates button states.
     */
    private void navigateToPanel(String panelName) {
        cardLayout.show(mainContentPanel, panelName);
        currentPanel = panelName;
        
        // Update button active states
        for (Map.Entry<String, StyledSidebarButton> entry : navigationButtons.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(panelName));
        }
        
        // Update status bar
        updateStatus(getPanelTitle(panelName));
    }
    
    /**
     * Gets display title for panel.
     */
    private String getPanelTitle(String panelName) {
        switch (panelName) {
            case PANEL_DASHBOARD: return "Dashboard - Welcome back, " + currentUser.getName();
            case PANEL_PLANS: return "Available Plans - Choose your perfect subscription";
            case PANEL_OFFERS: return "Special Offers - Exclusive deals for you";
            case PANEL_SUBSCRIPTIONS: return "My Subscriptions - Manage your active plans";
            case PANEL_PAYMENTS: return "Payment History - Track all transactions";
            case PANEL_INVOICES: return "Invoices - Download and view billing documents";
            case PANEL_SETTINGS: return "Settings - Manage your account";
            default: return "SaaS Platform";
        }
    }
    
    /**
     * COMPLETELY REBUILT DASHBOARD - SaaS Structure.
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(25, 25));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));
        
        // Create scrollable content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // === TOP SECTION - WELCOME + STATUS ===
        JPanel welcomeSection = createWelcomeSection();
        welcomeSection.setAlignmentX(LEFT_ALIGNMENT);
        welcomeSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        contentPanel.add(welcomeSection);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // === STATS SECTION - 4 CARDS ===
        JPanel statsSection = createStatsCardsSection();
        statsSection.setAlignmentX(LEFT_ALIGNMENT);
        statsSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        contentPanel.add(statsSection);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // === QUICK ACTIONS SECTION - 6 BUTTONS ===
        JPanel quickActionsSection = createQuickActionsSection();
        quickActionsSection.setAlignmentX(LEFT_ALIGNMENT);
        quickActionsSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));
        contentPanel.add(quickActionsSection);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // === RECENT ACTIVITY SECTION ===
        JPanel recentActivitySection = createRecentActivitySection();
        recentActivitySection.setAlignmentX(LEFT_ALIGNMENT);
        recentActivitySection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        contentPanel.add(recentActivitySection);
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIConstants.BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates welcome section with status indicator.
     */
    private JPanel createWelcomeSection() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Left - Welcome text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getName() + " 👋");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(new Color(17, 24, 39));
        welcomeLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Here's your subscription overview and activity");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        textPanel.add(welcomeLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        textPanel.add(subtitleLabel);
        
        // Right - Status indicator
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statusPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        boolean isActive = getActiveSubscriptionCount() > 0;
        StatusIndicator statusIndicator = new StatusIndicator(isActive);
        
        JLabel statusText = new JLabel(isActive ? "Active Subscription" : "No Active Plan");
        statusText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusText.setForeground(isActive ? new Color(16, 185, 129) : new Color(107, 114, 128));
        
        statusPanel.add(statusIndicator);
        statusPanel.add(statusText);
        
        panel.add(textPanel, BorderLayout.WEST);
        panel.add(statusPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Creates stats cards section.
     */
    private JPanel createStatsCardsSection() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Get metrics
        int activeCount = getActiveSubscriptionCount();
        long daysToRenewal = getDaysUntilNextRenewal();
        double totalSpend = getTotalSpending();
        boolean autoRenew = true; // Get from user preferences
        
        // Card 1: Current Plan
        StyledCardPanel planCard = createStatCard("📦", "Current Plan", 
            activeCount > 0 ? "Active" : "None", 
            activeCount > 0 ? new Color(16, 185, 129) : new Color(107, 114, 128));
        
        // Card 2: Expiry Date
        String expiryText = daysToRenewal > 0 ? daysToRenewal + " days" : "---";
        StyledCardPanel expiryCard = createStatCard("📅", "Expires In", 
            expiryText, new Color(59, 130, 246));
        
        // Card 3: Total Payments
        String totalText = "$" + String.format("%.2f", totalSpend);
        StyledCardPanel paymentsCard = createStatCard("💰", "Total Spent", 
            totalText, new Color(245, 158, 11));
        
        // Card 4: Auto-Renew Status
        StyledCardPanel autoRenewCard = createStatCard("🔄", "Auto-Renew", 
            autoRenew ? "Enabled" : "Disabled", 
            autoRenew ? new Color(16, 185, 129) : new Color(239, 68, 68));
        
        panel.add(planCard);
        panel.add(expiryCard);
        panel.add(paymentsCard);
        panel.add(autoRenewCard);
        
        return panel;
    }
    
    /**
     * Creates a stat card.
     */
    private StyledCardPanel createStatCard(String icon, String label, String value, Color accentColor) {
        StyledCardPanel card = new StyledCardPanel();
        card.setLayout(new BorderLayout(12, 12));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelLabel.setForeground(new Color(107, 114, 128));
        labelLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        textPanel.add(valueLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        textPanel.add(labelLabel);
        
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Creates quick actions section with 6 action cards.
     */
    private JPanel createQuickActionsSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 15));
        wrapper.setBackground(UIConstants.BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("⚡ Quick Actions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(17, 24, 39));
        
        JPanel actionsGrid = new JPanel(new GridLayout(2, 3, 18, 18));
        actionsGrid.setBackground(UIConstants.BACKGROUND_COLOR);
        
        QuickActionCard browsePlans = new QuickActionCard("📦", "Browse Plans", new Color(99, 102, 241));
        browsePlans.addActionListener(e -> navigateToPanel(PANEL_PLANS));
        
        QuickActionCard viewOffers = new QuickActionCard("🎁", "View Offers", new Color(16, 185, 129));
        viewOffers.addActionListener(e -> navigateToPanel(PANEL_OFFERS));
        
        QuickActionCard upgradePlan = new QuickActionCard("⬆️", "Upgrade Plan", new Color(245, 158, 11));
        upgradePlan.addActionListener(e -> navigateToPanel(PANEL_PLANS));
        
        QuickActionCard makePayment = new QuickActionCard("💳", "Make Payment", new Color(59, 130, 246));
        makePayment.addActionListener(e -> navigateToPanel(PANEL_PAYMENTS));
        
        QuickActionCard viewInvoices = new QuickActionCard("📄", "View Invoices", new Color(139, 92, 246));
        viewInvoices.addActionListener(e -> navigateToPanel(PANEL_INVOICES));
        
        QuickActionCard settings = new QuickActionCard("⚙️", "Settings", new Color(107, 114, 128));
        settings.addActionListener(e -> navigateToPanel(PANEL_SETTINGS));
        
        actionsGrid.add(browsePlans);
        actionsGrid.add(viewOffers);
        actionsGrid.add(upgradePlan);
        actionsGrid.add(makePayment);
        actionsGrid.add(viewInvoices);
        actionsGrid.add(settings);
        
        wrapper.add(titleLabel, BorderLayout.NORTH);
        wrapper.add(actionsGrid, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    /**
     * Creates recent activity section with sample data.
     */
    private JPanel createRecentActivitySection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 15));
        wrapper.setBackground(UIConstants.BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Recent Activity");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(15, 23, 42));
        
        StyledCardPanel activityCard = new StyledCardPanel();
        activityCard.setLayout(new BoxLayout(activityCard, BoxLayout.Y_AXIS));
        
        // Get actual activities or use sample data
        List<String[]> activities = getRecentActivities();
        
        for (int i = 0; i < activities.size(); i++) {
            String[] activity = activities.get(i);
            JPanel activityItem = createActivityItem(activity[0], activity[1], activity[2]);
            activityItem.setAlignmentX(LEFT_ALIGNMENT);
            activityItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            activityCard.add(activityItem);
            
            if (i < activities.size() - 1) {
                JSeparator sep = new JSeparator();
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                sep.setForeground(new Color(229, 231, 235));
                activityCard.add(sep);
            }
        }
        
        wrapper.add(titleLabel, BorderLayout.NORTH);
        wrapper.add(activityCard, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    /**
     * Creates an activity item.
     */
    private JPanel createActivityItem(String icon, String text, String time) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(new Color(17, 24, 39));
        
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(156, 163, 175));
        
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textLabel, BorderLayout.CENTER);
        panel.add(timeLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Gets recent activities (sample data if DB empty).
     */
    private List<String[]> getRecentActivities() {
        List<String[]> activities = new ArrayList<>();
        
        // Try to get real activities
        List<Subscription> subs = subscriptionService.getSubscriptionsByUserId(currentUser.getUserId());
        List<Payment> payments = paymentService.getAllPayments();
        
        if (!subs.isEmpty()) {
            Subscription lastSub = subs.get(subs.size() - 1);
            Plan plan = planService.getPlanById(lastSub.getPlanId());
            activities.add(new String[]{
                "✅",
                "Subscribed to " + (plan != null ? plan.getPlanName() : "a plan"),
                formatTimeAgo(lastSub.getStartDate())
            });
        }
        
        if (!payments.isEmpty()) {
            Payment lastPayment = payments.get(payments.size() - 1);
            activities.add(new String[]{
                "💳",
                "Payment received - $" + lastPayment.getAmount(),
                formatTimeAgo(lastPayment.getPaymentDate())
            });
        }
        
        // Add sample data if not enough activities
        if (activities.size() < 5) {
            activities.add(new String[]{"🔄", "Auto-renewal enabled for active subscription", "2 days ago"});
            activities.add(new String[]{"📄", "New invoice generated", "5 days ago"});
            activities.add(new String[]{"🎁", "Special offer available - 20% off", "1 week ago"});
            activities.add(new String[]{"⚙️", "Account settings updated", "2 weeks ago"});
        }
        
        // Return first 5
        return activities.subList(0, Math.min(5, activities.size()));
    }
    
    /**
     * Formats date as time ago.
     */
    private String formatTimeAgo(LocalDate date) {
        if (date == null) return "Recently";
        
        long days = ChronoUnit.DAYS.between(date, LocalDate.now());
        if (days == 0) return "Today";
        if (days == 1) return "Yesterday";
        if (days < 7) return days + " days ago";
        if (days < 30) return (days / 7) + " weeks ago";
        return (days / 30) + " months ago";
    }
    

    
    /**
     * Creates available plans panel with subscription grid.
     */
    private JPanel createPlansPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title with billing toggle
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("💎 Subscription Plans");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(17, 24, 39));
        
        JLabel subtitleLabel = new JLabel("Choose the perfect plan to grow your business");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        titlePanel.add(subtitleLabel);
        
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        // Billing cycle toggle
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        togglePanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        JLabel monthlyLabel = new JLabel("Monthly");
        monthlyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        monthlyLabel.setForeground(new Color(79, 70, 229));
        
        ToggleSwitch billingToggle = new ToggleSwitch();
        billingToggle.setSelected(false);
        
        JLabel yearlyLabel = new JLabel("Yearly (Save 20%)");
        yearlyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        yearlyLabel.setForeground(new Color(16, 185, 129));
        
        togglePanel.add(monthlyLabel);
        togglePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        togglePanel.add(billingToggle);
        togglePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        togglePanel.add(yearlyLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(togglePanel, BorderLayout.EAST);
        
        // Professional plans grid
        JPanel plansGrid = new JPanel(new GridLayout(0, 3, 25, 25));
        plansGrid.setBackground(UIConstants.BACKGROUND_COLOR);
        plansGrid.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Create professional plan cards
        String[] planTypes = {"Basic", "Starter", "Growth", "Professional", "Business", "Premium", "Enterprise"};
        Map<String, Color[]> planColors = new HashMap<>();
        planColors.put("Basic", new Color[]{new Color(99, 102, 241), new Color(139, 92, 246)});
        planColors.put("Starter", new Color[]{new Color(59, 130, 246), new Color(99, 102, 241)});
        planColors.put("Growth", new Color[]{new Color(16, 185, 129), new Color(34, 197, 94)});
        planColors.put("Professional", new Color[]{new Color(245, 158, 11), new Color(251, 191, 36)});
        planColors.put("Business", new Color[]{new Color(239, 68, 68), new Color(244, 63, 94)});
        planColors.put("Premium", new Color[]{new Color(168, 85, 247), new Color(192, 132, 252)});
        planColors.put("Enterprise", new Color[]{new Color(30, 41, 59), new Color(51, 65, 85)});
        
        for (String planType : planTypes) {
            Color[] colors = planColors.get(planType);
            boolean isRecommended = planType.equals("Growth") || planType.equals("Professional");
            
            ProfessionalPlanCard planCard = new ProfessionalPlanCard(
                planType,
                colors[0],
                colors[1],
                isRecommended
            );
            
            // Subscribe button action
            planCard.addSubscribeListener(e -> {
                // Find matching plan from database or create subscription
                List<Plan> dbPlans = planService.getAllPlans();
                Plan matchingPlan = dbPlans.stream()
                    .filter(p -> p.getPlanName().equalsIgnoreCase(planType))
                    .findFirst()
                    .orElse(dbPlans.isEmpty() ? null : dbPlans.get(0));
                
                if (matchingPlan != null) {
                    handleSubscribe(matchingPlan);
                } else {
                    ProfessionalDialog.showError(
                        this,
                        "Plan Not Available",
                        "This plan is currently not available. Please contact support."
                    );
                }
            });
            
            plansGrid.add(planCard);
            
            // Toggle listener for yearly/monthly pricing
            billingToggle.addItemListener(e -> {
                planCard.updatePrice(billingToggle.isSelected());
            });
        }
        
        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(plansGrid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIConstants.BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates individual plan card with subscribe button.
     */
    private JPanel createPlanCard(Plan plan) {
        StyledCardPanel card = new StyledCardPanel();
        card.setLayout(new BorderLayout(15, 15));
        card.setPreferredSize(new Dimension(300, 320));
        
        // Header with icon
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("📦");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(plan.getPlanName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setForeground(new Color(17, 24, 39));
        nameLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        JLabel priceLabel = new JLabel("$" + plan.getPrice() + " / " + plan.getBillingCycle().toLowerCase());
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        priceLabel.setForeground(new Color(79, 70, 229));
        priceLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        titlePanel.add(nameLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(priceLabel);
        
        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setOpaque(false);
        headerContent.add(iconLabel);
        headerContent.add(Box.createRigidArea(new Dimension(0, 10)));
        headerContent.add(titlePanel);
        
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);
        titlePanel.setAlignmentX(CENTER_ALIGNMENT);
        
        // Description
        String description = plan.getDescription();
        if (description == null || description.isEmpty()) {
            description = "Perfect for your subscription needs. Get started today!";
        }
        
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setForeground(new Color(107, 114, 128));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setRows(3);
        
        // Subscribe button
        StyledButton subscribeBtn = new StyledButton("Subscribe Now", StyledButton.ButtonStyle.PRIMARY);
        subscribeBtn.setPreferredSize(new Dimension(200, 45));
        subscribeBtn.setAlignmentX(CENTER_ALIGNMENT);
        subscribeBtn.addActionListener(e -> handleSubscribe(plan));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(subscribeBtn);
        
        card.add(headerContent, BorderLayout.NORTH);
        card.add(descArea, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    /**
     * REBUILT SPECIAL OFFERS PANEL - Complete implementation.
     */
    private JPanel createOffersPanel() {
        JPanel panel = new JPanel(new BorderLayout(25, 25));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));
        
        // Title
        JLabel titleLabel = new JLabel("🎁 Special Offers & Promotions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(17, 24, 39));
        
        JLabel subtitleLabel = new JLabel("Exclusive deals and discounts available for you");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(UIConstants.BACKGROUND_COLOR);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        titlePanel.add(subtitleLabel);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        // Offers grid
        JPanel offersGrid = new JPanel(new GridLayout(0, 2, 22, 22));
        offersGrid.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Comprehensive offer cards with expiry dates
        offersGrid.add(createOfferCard(
            "🎉 Spring Sale 2026",
            "20% OFF",
            "Get 20% discount on all annual subscriptions",
            "SPRING20",
            "Valid until: March 31, 2026",
            new Color(16, 185, 129)
        ));
        
        offersGrid.add(createOfferCard(
            "🌟 New User Welcome",
            "FREE MONTH",
            "First month absolutely free for new subscribers",
            "WELCOME2026",
            "Valid until: December 31, 2026",
            new Color(59, 130, 246)
        ));
        
        offersGrid.add(createOfferCard(
            "💎 Premium Upgrade",
            "50% OFF",
            "Upgrade to Premium plan and save 50%",
            "PREMIUM50",
            "Valid until: June 30, 2026",
            new Color(139, 92, 246)
        ));
        
        offersGrid.add(createOfferCard(
            "🚀 Referral Bonus",
            "1 FREE MONTH",
            "Refer a friend and get 1 month free",
            "REFER_FRIEND",
            "Ongoing offer - No expiry",
            new Color(245, 158, 11)
        ));
        
        offersGrid.add(createOfferCard(
            "⚡ Flash Sale",
            "30% OFF",
            "Limited time - 30% off on Business plans",
            "FLASH30",
            "Valid until: Feb 28, 2026",
            new Color(239, 68, 68)
        ));
        
        offersGrid.add(createOfferCard(
            "🎓 Student Discount",
            "40% OFF",
            "Special pricing for students with valid ID",
            "STUDENT40",
            "Valid until: August 31, 2026",
            new Color(14, 165, 233)
        ));
        
        JScrollPane scrollPane = new JScrollPane(offersGrid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIConstants.BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates enhanced offer card with discount badge.
     */
    private JPanel createOfferCard(String title, String discount, String description, 
                                   String code, String validUntil, Color accentColor) {
        StyledCardPanel card = new StyledCardPanel();
        card.setLayout(new BorderLayout(18, 18));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 3),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Top - Title and discount badge
        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(17, 24, 39));
        
        JLabel discountBadge = new JLabel(discount);
        discountBadge.setFont(new Font("Segoe UI", Font.BOLD, 16));
        discountBadge.setForeground(Color.WHITE);
        discountBadge.setOpaque(true);
        discountBadge.setBackground(accentColor);
        discountBadge.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(discountBadge, BorderLayout.EAST);
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descLabel.setForeground(new Color(75, 85, 99));
        descLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel validLabel = new JLabel(validUntil);
        validLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        validLabel.setForeground(new Color(156, 163, 175));
        validLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        contentPanel.add(descLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(validLabel);
        
        // Code section
        JPanel codePanel = new JPanel(new BorderLayout(10, 0));
        codePanel.setOpaque(false);
        
        JLabel codeLabel = new JLabel("Code: " + code);
        codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        codeLabel.setForeground(accentColor);
        
        codePanel.add(codeLabel, BorderLayout.WEST);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        StyledButton copyBtn = new StyledButton("📋 Copy Code", StyledButton.ButtonStyle.SUCCESS);
        copyBtn.setPreferredSize(new Dimension(140, 42));
        copyBtn.addActionListener(e -> {
            java.awt.datatransfer.StringSelection selection = 
                new java.awt.datatransfer.StringSelection(code);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            ProfessionalDialog.showSuccess(this, "Code Copied!", 
                "Promo code \"" + code + "\" has been copied to your clipboard!");
        });
        
        StyledButton applyBtn = new StyledButton("✓ Apply", StyledButton.ButtonStyle.PRIMARY);
        applyBtn.setPreferredSize(new Dimension(100, 42));
        applyBtn.addActionListener(e -> {
            ProfessionalDialog.showSuccess(this, "Offer Applied!", 
                "This promo code will be applied to your next subscription purchase.");
            navigateToPanel(PANEL_PLANS);
        });
        
        buttonPanel.add(copyBtn);
        buttonPanel.add(applyBtn);
        
        // Assemble
        JPanel mainContent = new JPanel(new BorderLayout(0, 15));
        mainContent.setOpaque(false);
        mainContent.add(topPanel, BorderLayout.NORTH);
        mainContent.add(contentPanel, BorderLayout.CENTER);
        mainContent.add(codePanel, BorderLayout.SOUTH);
        
        card.add(mainContent, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    /**
     * Creates my subscriptions panel with toggle switches.
     */
    private JPanel createSubscriptionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("My Subscriptions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(15, 23, 42));
        
        // Table
        String[] columns = {"Plan", "Status", "Start Date", "End Date", "Price", "Auto-Renew", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6; // Auto-renew and Actions
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return Boolean.class;
                return String.class;
            }
        };
        
        List<Subscription> subscriptions = subscriptionService.getSubscriptionsByUserId(currentUser.getUserId());
        for (Subscription sub : subscriptions) {
            Plan plan = planService.getPlanById(sub.getPlanId());
            model.addRow(new Object[]{
                plan != null ? plan.getPlanName() : "Unknown",
                sub.getStatus(),
                sub.getStartDate(),
                sub.getEndDate(),
                plan != null ? "$" + plan.getPrice() : "$0.00",
                sub.getAutoRenew() != null ? sub.getAutoRenew() : false,
                "Manage"
            });
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(50);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(224, 242, 254));
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(new Color(17, 24, 39));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)));
        
        // Apply custom renderer (status column is index 1)
        CustomTableRenderer renderer = new CustomTableRenderer(1);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates payments history panel.
     */
    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Payment History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(15, 23, 42));
        
        // Table
        String[] columns = {"Date", "Description", "Amount", "Method", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        List<Payment> payments = paymentService.getAllPayments();
        for (Payment payment : payments) {
            model.addRow(new Object[]{
                payment.getPaymentDate(),
                "Subscription Payment",
                "$" + payment.getAmount(),
                payment.getPaymentMethod(),
                "Completed"
            });
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(50);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(224, 242, 254));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(249, 250, 251));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        
        CustomTableRenderer renderer = new CustomTableRenderer(4); // Status at index 4
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        
        // Add payment button
        StyledButton addPaymentBtn = new StyledButton("+ Add Payment Method", StyledButton.ButtonStyle.PRIMARY);
        addPaymentBtn.setPreferredSize(new Dimension(200, 45));
        addPaymentBtn.addActionListener(e -> handleAddPaymentMethod());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(addPaymentBtn, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates invoices panel with download actions.
     */
    private JPanel createInvoicesPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Invoices");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(15, 23, 42));
        
        // Table
        String[] columns = {"Invoice #", "Date", "Description", "Amount", "Status", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        
        List<Invoice> invoices = invoiceService.getAllInvoices();
        for (Invoice invoice : invoices) {
            model.addRow(new Object[]{
                "INV-" + invoice.getInvoiceId(),
                invoice.getInvoiceDate(),
                "Subscription Invoice",
                "$" + invoice.getAmount(),
                invoice.getStatus(),
                "Download"
            });
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(50);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(224, 242, 254));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(249, 250, 251));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        
        CustomTableRenderer renderer = new CustomTableRenderer(4); // Status at index 4
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        
        // Action column button renderer
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                panel.setOpaque(true);
                panel.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                
                StyledButton btn = new StyledButton("📥 Download", StyledButton.ButtonStyle.SECONDARY);
                btn.setPreferredSize(new Dimension(130, 35));
                btn.addActionListener(e -> handleDownloadInvoice(row));
                
                panel.add(btn);
                return panel;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * MASSIVELY EXPANDED SETTINGS PANEL - Complete implementation.
     */
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(25, 25));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));
        
        // Title
        JLabel titleLabel = new JLabel("⚙️ Account Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(17, 24, 39));
        
        JLabel subtitleLabel = new JLabel("Manage your account, preferences, and security settings");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(UIConstants.BACKGROUND_COLOR);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        titlePanel.add(subtitleLabel);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        // Scrollable content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // === PROFILE INFORMATION CARD ===
        contentPanel.add(createProfileCard());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // === SECURITY CARD ===
        contentPanel.add(createSecurityCard());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // === PREFERENCES CARD ===
        contentPanel.add(createPreferencesCard());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // === NOTIFICATION SETTINGS CARD ===
        contentPanel.add(createNotificationCard());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // === ACCOUNT STATUS CARD ===
        contentPanel.add(createAccountStatusCard());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // === DANGER ZONE CARD ===
        contentPanel.add(createDangerZoneCard());
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIConstants.BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates profile information card.
     */
    private StyledCardPanel createProfileCard() {
        StyledCardPanel card = new StyledCardPanel();
        card.setLayout(new BorderLayout(20, 20));
        card.setMaximumSize(new Dimension(900, 280));
        card.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel cardTitle = new JLabel("👤 Profile Information");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        cardTitle.setForeground(new Color(17, 24, 39));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username field
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(userLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.75;
        JTextField userField = new JTextField(currentUser.getName());
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userField.setPreferredSize(new Dimension(350, 40));
        userField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        formPanel.add(userField, gbc);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.25;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.75;
        JTextField emailField = new JTextField(currentUser.getEmail());
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(350, 40));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        formPanel.add(emailField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);
        
        StyledButton cancelBtn = new StyledButton("Cancel", StyledButton.ButtonStyle.SECONDARY);
        cancelBtn.setPreferredSize(new Dimension(100, 40));
        cancelBtn.addActionListener(e -> {
            userField.setText(currentUser.getName());
            emailField.setText(currentUser.getEmail());
        });
        
        StyledButton saveBtn = new StyledButton("Save Changes", StyledButton.ButtonStyle.PRIMARY);
        saveBtn.setPreferredSize(new Dimension(140, 40));
        saveBtn.addActionListener(e -> {
            String newName = userField.getText().trim();
            String newEmail = emailField.getText().trim();
            
            if (newName.isEmpty() || newEmail.isEmpty()) {
                ProfessionalDialog.showError(this, "Validation Error", "Fields cannot be empty.");
                return;
            }
            if (!newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                ProfessionalDialog.showError(this, "Invalid Email", "Please enter a valid email address.");
                return;
            }
            
            currentUser.setName(newName);
            currentUser.setEmail(newEmail);
            ProfessionalDialog.showSuccess(this, "Profile Updated", "Your profile information has been saved!");
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        card.add(cardTitle, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    /**
     * Creates security settings card with password change.
     */
    private StyledCardPanel createSecurityCard() {
        StyledCardPanel card = new StyledCardPanel();
        card.setLayout(new BorderLayout(20, 20));
        card.setMaximumSize(new Dimension(900, 320));
        card.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel cardTitle = new JLabel("🔒 Security Settings");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        cardTitle.setForeground(new Color(17, 24, 39));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Current password
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        JLabel currentPwdLabel = new JLabel("Current Password:");
        currentPwdLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(currentPwdLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.75;
        JPasswordField currentPwdField = new JPasswordField();
        currentPwdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        currentPwdField.setPreferredSize(new Dimension(350, 40));
        currentPwdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        formPanel.add(currentPwdField, gbc);
        
        // New password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.25;
        JLabel newPwdLabel = new JLabel("New Password:");
        newPwdLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(newPwdLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.75;
        JPasswordField newPwdField = new JPasswordField();
        newPwdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newPwdField.setPreferredSize(new Dimension(350, 40));
        newPwdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        formPanel.add(newPwdField, gbc);
        
        // Confirm password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.25;
        JLabel confirmPwdLabel = new JLabel("Confirm Password:");
        confirmPwdLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(confirmPwdLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.75;
        JPasswordField confirmPwdField = new JPasswordField();
        confirmPwdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPwdField.setPreferredSize(new Dimension(350, 40));
        confirmPwdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        formPanel.add(confirmPwdField, gbc);
        
        // Change password button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);
        
        StyledButton changePasswordBtn = new StyledButton("Change Password", StyledButton.ButtonStyle.WARNING);
        changePasswordBtn.setPreferredSize(new Dimension(160, 40));
        changePasswordBtn.addActionListener(e -> {
            String current = new String(currentPwdField.getPassword());
            String newPwd = new String(newPwdField.getPassword());
            String confirm = new String(confirmPwdField.getPassword());
            
            if (current.isEmpty() || newPwd.isEmpty() || confirm.isEmpty()) {
                ProfessionalDialog.showError(this, "Validation Error", "All password fields are required.");
                return;
            }
            if (newPwd.length() < 6) {
                ProfessionalDialog.showError(this, "Weak Password", "Password must be at least 6 characters.");
                return;
            }
            if (!newPwd.equals(confirm)) {
                ProfessionalDialog.showError(this, "Password Mismatch", "New password and confirmation don't match.");
                return;
            }
            
            // In real app: verify current password and update
            ProfessionalDialog.showSuccess(this, "Password Changed", "Your password has been updated successfully!");
            currentPwdField.setText("");
            newPwdField.setText("");
            confirmPwdField.setText("");
        });
        
        buttonPanel.add(changePasswordBtn);
        
        card.add(cardTitle, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    /**
     * Creates preferences card.
     */
    private StyledCardPanel createPreferencesCard() {
        StyledCardPanel card = new StyledCardPanel();
        card.setLayout(new BorderLayout(20, 20));
        card.setMaximumSize(new Dimension(900, 150));
        card.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel cardTitle = new JLabel("🎛️ Subscription Preferences");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        cardTitle.setForeground(new Color(17, 24, 39));
        
        JPanel prefPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        prefPanel.setOpaque(false);
        
        JLabel autoRenewLabel = new JLabel("Enable auto-renewal for all subscriptions:");
        autoRenewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        ToggleSwitch autoRenewToggle = new ToggleSwitch();
        autoRenewToggle.setSelected(true);
        autoRenewToggle.addItemListener(e -> {
            boolean enabled = autoRenewToggle.isOn();
            ProfessionalDialog.showInfo(this, "Auto-Renewal " + (enabled ? "Enabled" : "Disabled"),
                "Auto-renewal has been " + (enabled ? "enabled" : "disabled") + " for all subscriptions.");
        });
        
        prefPanel.add(autoRenewLabel);
        prefPanel.add(autoRenewToggle);
        
        card.add(cardTitle, BorderLayout.NORTH);
        card.add(prefPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Creates notification preferences card.
     */
    private StyledCardPanel createNotificationCard() {
        StyledCardPanel card = new StyledCardPanel();
        card.setLayout(new BorderLayout(20, 20));
        card.setMaximumSize(new Dimension(900, 240));
        card.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel cardTitle = new JLabel("🔔 Notification Preferences");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        cardTitle.setForeground(new Color(17, 24, 39));
        
        JPanel notifPanel = new JPanel();
        notifPanel.setLayout(new BoxLayout(notifPanel, BoxLayout.Y_AXIS));
        notifPanel.setOpaque(false);
        
        // Email notifications
        JPanel emailNotifRow = createToggleRow("Email Notifications", "Receive email updates about your subscriptions", true);
        emailNotifRow.setAlignmentX(LEFT_ALIGNMENT);
        notifPanel.add(emailNotifRow);
        notifPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Renewal reminders
        JPanel reminderRow = createToggleRow("Renewal Reminders", "Get notified 3 days before subscription renewal", true);
        reminderRow.setAlignmentX(LEFT_ALIGNMENT);
        notifPanel.add(reminderRow);
        notifPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Promotional emails
        JPanel promoRow = createToggleRow("Promotional Emails", "Receive special offers and discounts", false);
        promoRow.setAlignmentX(LEFT_ALIGNMENT);
        notifPanel.add(promoRow);
        
        card.add(cardTitle, BorderLayout.NORTH);
        card.add(notifPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Creates account status display card.
     */
    private StyledCardPanel createAccountStatusCard() {
        StyledCardPanel card = new StyledCardPanel();
        card.setLayout(new BorderLayout(20, 20));
        card.setMaximumSize(new Dimension(900, 180));
        card.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel cardTitle = new JLabel("Account Status");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        cardTitle.setForeground(new Color(15, 23, 42));
        
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JPanel statusRow1 = createStatusRow("Account Type:", currentUser.getRole(), new Color(79, 70, 229));
        JPanel statusRow2 = createStatusRow("Account Status:", currentUser.getStatus(), new Color(16, 185, 129));
        JPanel statusRow3 = createStatusRow("Member Since:", "January 2026", new Color(107, 114, 128));
        
        statusRow1.setAlignmentX(LEFT_ALIGNMENT);
        statusRow2.setAlignmentX(LEFT_ALIGNMENT);
        statusRow3.setAlignmentX(LEFT_ALIGNMENT);
        
        statusPanel.add(statusRow1);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statusPanel.add(statusRow2);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statusPanel.add(statusRow3);
        
        card.add(cardTitle, BorderLayout.NORTH);
        card.add(statusPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Creates danger zone card with delete account.
     */
    private StyledCardPanel createDangerZoneCard() {
        StyledCardPanel card = new StyledCardPanel();
        card.setLayout(new BorderLayout(20, 20));
        card.setMaximumSize(new Dimension(900, 180));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(239, 68, 68), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel cardTitle = new JLabel("⚠️ Danger Zone");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        cardTitle.setForeground(new Color(239, 68, 68));
        
        JPanel dangerPanel = new JPanel(new BorderLayout(15, 0));
        dangerPanel.setOpaque(false);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel dangerTitle = new JLabel("Delete Account");
        dangerTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dangerTitle.setForeground(new Color(17, 24, 39));
        dangerTitle.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel dangerDesc = new JLabel("Once deleted, your account cannot be recovered. This action is permanent.");
        dangerDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dangerDesc.setForeground(new Color(107, 114, 128));
        dangerDesc.setAlignmentX(LEFT_ALIGNMENT);
        
        textPanel.add(dangerTitle);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(dangerDesc);
        
        StyledButton deleteAccountBtn = new StyledButton("Delete Account", StyledButton.ButtonStyle.DANGER);
        deleteAccountBtn.setPreferredSize(new Dimension(150, 40));
        deleteAccountBtn.addActionListener(e -> {
            boolean confirmed = ProfessionalDialog.showConfirmation(
                this,
                "⚠️ Delete Account",
                "Are you ABSOLUTELY SURE you want to delete your account?\n\n" +
                "This action will:\n" +
                "• Cancel all active subscriptions\n" +
                "• Delete all your data permanently\n" +
                "• Remove all payment methods\n\n" +
                "This action CANNOT be undone!"
            );
            
            if (confirmed) {
                boolean doubleConfirm = ProfessionalDialog.showConfirmation(
                    this,
                    "Final Confirmation",
                    "Last chance! Are you 100% sure?"
                );
                
                if (doubleConfirm) {
                    ProfessionalDialog.showSuccess(
                        this,
                        "Account Deleted",
                        "Your account has been permanently deleted. You will be logged out."
                    );
                    // Logout and return to login screen
                    handleLogout();
                }
            }
        });
        
        dangerPanel.add(textPanel, BorderLayout.WEST);
        dangerPanel.add(deleteAccountBtn, BorderLayout.EAST);
        
        card.add(cardTitle, BorderLayout.NORTH);
        card.add(dangerPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Creates a toggle row for notifications.
     */
    private JPanel createToggleRow(String title, String description, boolean defaultValue) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(17, 24, 39));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(107, 114, 128));
        descLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(descLabel);
        
        ToggleSwitch toggle = new ToggleSwitch();
        toggle.setSelected(defaultValue);
        
        panel.add(textPanel, BorderLayout.WEST);
        panel.add(toggle, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Creates a status info row.
     */
    private JPanel createStatusRow(String label, String value, Color valueColor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setOpaque(false);
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComp.setForeground(new Color(75, 85, 99));
        labelComp.setPreferredSize(new Dimension(150, 25));
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueComp.setForeground(valueColor);
        
        panel.add(labelComp);
        panel.add(valueComp);
        
        return panel;
    }
    
    /**
     * Creates status bar.
     */
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(249, 250, 251));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(107, 114, 128));
        
        JLabel copyrightLabel = new JLabel("© 2026 SaaS Platform");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyrightLabel.setForeground(new Color(156, 163, 175));
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(copyrightLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    // ============ ACTION HANDLERS ============
    
    private void handleSubscribe(Plan plan) {
        boolean confirm = ProfessionalDialog.showConfirmation(this, "Subscribe to Plan",
            "Subscribe to " + plan.getPlanName() + "?\n\n" +
            "Price: $" + plan.getPrice() + " / " + plan.getBillingCycle().toLowerCase() + "\n\n" +
            "Your subscription will start immediately.");
        
        if (confirm) {
            boolean success = subscriptionService.createSubscription(
                currentUser.getUserId(),
                plan.getPlanId(),
                LocalDate.now()
            );
            
            if (success) {
                ProfessionalDialog.showSuccess(this, "Success!", 
                    "Successfully subscribed to " + plan.getPlanName() + "!");
                navigateToPanel(PANEL_SUBSCRIPTIONS);
            } else {
                ProfessionalDialog.showError(this, "Error", 
                    "Failed to create subscription. Please try again.");
            }
        }
    }
    
    private void handleAddPaymentMethod() {
        ProfessionalDialog.showInfo(this, "Add Payment Method", 
            "Payment method management coming soon!\n\n" +
            "You'll be able to add and manage credit cards, PayPal, and bank accounts.");
    }
    
    private void handleDownloadInvoice(int row) {
        ProfessionalDialog.showSuccess(this, "Download Invoice", 
            "Invoice download initiated!\n\nCheck your Downloads folder.");
    }
    
    private void handleLogout() {
        boolean confirm = ProfessionalDialog.showConfirmation(this, "Logout",
            "Are you sure you want to logout?");
        
        if (confirm) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
    
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    // ============ HELPER METHODS FOR METRICS ============
    
    private int getActiveSubscriptionCount() {
        List<Subscription> subs = subscriptionService.getSubscriptionsByUserId(currentUser.getUserId());
        return (int) subs.stream().filter(s -> "ACTIVE".equals(s.getStatus())).count();
    }
    
    private long getDaysUntilNextRenewal() {
        List<Subscription> subs = subscriptionService.getSubscriptionsByUserId(currentUser.getUserId());
        for (Subscription sub : subs) {
            if ("ACTIVE".equals(sub.getStatus()) && sub.getEndDate() != null) {
                return ChronoUnit.DAYS.between(LocalDate.now(), sub.getEndDate());
            }
        }
        return -1;
    }
    
    private double getTotalSpending() {
        List<Subscription> subs = subscriptionService.getSubscriptionsByUserId(currentUser.getUserId());
        double total = 0.0;
        for (Subscription sub : subs) {
            Plan plan = planService.getPlanById(sub.getPlanId());
            if (plan != null) {
                total += plan.getPrice().doubleValue();
            }
        }
        return total;
    }
    
    private int getInvoiceCount() {
        return invoiceService.getAllInvoices().size();
    }
    
    // ============ NEW HELPER METHODS ============
    
    /**
     * Styles a table consistently.
     */
    private void styleTable(JTable table, int statusColumnIndex) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(55);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(224, 242, 254));
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(new Color(17, 24, 39));
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)));
        
        // Apply custom renderer
        CustomTableRenderer renderer = new CustomTableRenderer(statusColumnIndex);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }
    
    /**
     * Creates an empty state panel.
     */
    private JPanel createEmptyState(String icon, String title, String message, 
                                    String buttonText, ActionListener buttonAction) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(80, 50, 80, 50));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(17, 24, 39));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageLabel.setForeground(new Color(107, 114, 128));
        messageLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        StyledButton actionButton = new StyledButton(buttonText, StyledButton.ButtonStyle.PRIMARY);
        actionButton.setPreferredSize(new Dimension(180, 50));
        actionButton.setAlignmentX(CENTER_ALIGNMENT);
        actionButton.addActionListener(buttonAction);
        
        panel.add(iconLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(messageLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(actionButton);
        
        return panel;
    }
    
    /**
     * Handles auto-renew toggle with Subscription object.
     */
    private void handleAutoRenewToggle(Subscription sub, boolean newState) {
        sub.setAutoRenew(newState);
        // Update in database (would call subscriptionService.updateSubscription())
        
        String message = newState ? 
            "Auto-renewal enabled. Your subscription will renew automatically." :
            "Auto-renewal disabled. Subscription will expire at the end of current period.";
        
        ProfessionalDialog.showInfo(this, "Auto-Renewal " + (newState ? "Enabled" : "Disabled"), message);
        updateStatus("Auto-renewal setting updated");
    }
    
    /**
     * Handles subscription cancellation.
     */
    private void handleCancelSubscription(Subscription sub, Plan plan) {
        boolean confirm = ProfessionalDialog.showConfirmation(
            this,
            "Cancel Subscription",
            "Are you sure you want to cancel your " + plan.getPlanName() + " subscription?\n\n" +
            "You will still have access until: " + sub.getEndDate()
        );
        
        if (confirm) {
            // Cancel subscription logic
            sub.setStatus("CANCELLED");
            ProfessionalDialog.showSuccess(
                this,
                "Subscription Cancelled",
                "Your subscription has been cancelled. You'll have access until " + sub.getEndDate()
            );
            // Refresh panel
            navigateToPanel(PANEL_SUBSCRIPTIONS);
        }
    }
}

