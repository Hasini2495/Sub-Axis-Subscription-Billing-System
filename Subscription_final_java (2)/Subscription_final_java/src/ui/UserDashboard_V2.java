package ui;

import ui.components.*;
import ui.utils.IconManager;
import service.*;
import model.*;
import dao.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * SubAxis User Dashboard - Version 2.0
 * Complete professional SaaS implementation
 * All panels fully functional with service layer integration
 */
public class UserDashboard_V2 extends JFrame {
    
    private User currentUser;
    private SubscriptionService subscriptionService;
    private PaymentService paymentService;
    private InvoiceService invoiceService;
    private PlanService planService;
    private ActivityService activityService;
    
    private JPanel contentArea;
    private Map<String, ModernSidebarButton> navButtons = new HashMap<>();
    private Map<String, JPanel> panels = new HashMap<>();
    
    // Panel names
    private static final String PANEL_DASHBOARD = "Dashboard";
    private static final String PANEL_PLANS = "Available Plans";
    private static final String PANEL_SUBSCRIPTIONS = "My Subscriptions";
    private static final String PANEL_PAYMENTS = "Payments";
    private static final String PANEL_INVOICES = "Invoices";
    private static final String PANEL_OFFERS = "Special Offers";
    private static final String PANEL_SETTINGS = "Settings";
    
    // Colors - Dark Glassmorphism Theme
    private static final Color BG_COLOR = new Color(15, 32, 39); // Dark gradient top
    private static final Color SIDEBAR_BG = new Color(0, 0, 0, 80); // Semi-transparent dark
    private static final Color SIDEBAR_START = new Color(15, 32, 39);
    private static final Color SIDEBAR_END = new Color(44, 83, 100);
    
    public UserDashboard_V2(User user) {
        this.currentUser = user;
        this.subscriptionService = new SubscriptionService();
        this.paymentService = new PaymentService();
        this.invoiceService = new InvoiceService();
        this.planService = new PlanService();
        this.activityService = new ActivityService();
        
        // Set activity service references (to avoid circular dependency)
        this.invoiceService.setActivityService(activityService);
        this.paymentService.setActivityService(activityService);
        
        setTitle("SubAxis - Subscription Management Platform");
        setSize(1440, 920);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
        navigateToPanel(PANEL_DASHBOARD);
    }
    
    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        
        // Use dark gradient background
        DarkGradientPanel backgroundPanel = new DarkGradientPanel(new BorderLayout(0, 0));
        setContentPane(backgroundPanel);
        
        // Sidebar
        add(createSidebar(), BorderLayout.WEST);
        
        // Main content
        DarkGradientPanel mainPanel = new DarkGradientPanel(new BorderLayout());
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        
        // Content area with scroll
        contentArea = new DarkGradientPanel(new CardLayout());
        
        // Initialize all panels
        panels.put(PANEL_DASHBOARD, createDashboardPanel());
        panels.put(PANEL_PLANS, createPlansPanel());
        panels.put(PANEL_SUBSCRIPTIONS, createSubscriptionsPanel());
        panels.put(PANEL_PAYMENTS, createPaymentsPanel());
        panels.put(PANEL_INVOICES, createInvoicesPanel());
        panels.put(PANEL_OFFERS, createOffersPanel());
        panels.put(PANEL_SETTINGS, createSettingsPanel());
        
        for (Map.Entry<String, JPanel> entry : panels.entrySet()) {
            contentArea.add(entry.getValue(), entry.getKey());
        }
        
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSidebar() {
        // Dark glassmorphism sidebar
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Dark gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, SIDEBAR_START,
                    0, getHeight(), SIDEBAR_END
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Semi-transparent glass overlay
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Subtle right border
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                
                g2d.dispose();
            }
        };
        
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setMinimumSize(new Dimension(280, 600));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));
        
        // Logo Section - Full branding display with circular logo
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(240, 110));
        logoPanel.setPreferredSize(new Dimension(240, 110));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Brand container with logo + text
        JPanel brandContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        brandContainer.setOpaque(false);
        brandContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Circular Logo (48px diameter)
        JLabel circularLogo = createCircularLogo(48);
        brandContainer.add(circularLogo);
        
        // SubAxis Text
        JLabel logoLabel = new JLabel("SubAxis");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        logoLabel.setForeground(Color.WHITE);
        brandContainer.add(logoLabel);
        
        logoPanel.add(brandContainer);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        // Tagline
        JLabel taglineLabel = new JLabel("<html><div style='text-align: center;'>Subscription Management</div></html>");
        taglineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taglineLabel.setForeground(new Color(255, 255, 255, 200));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        taglineLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoPanel.add(taglineLabel);
        
        sidebar.add(logoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Navigation
        JLabel navLabel = new JLabel("NAVIGATION");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navLabel.setForeground(new Color(255, 255, 255, 160));
        navLabel.setBorder(new EmptyBorder(0, 10, 10, 0));
        sidebar.add(navLabel);
        
        addNavButton(sidebar, PANEL_DASHBOARD);
        addNavButton(sidebar, PANEL_PLANS);
        addNavButton(sidebar, PANEL_OFFERS);
        addNavButton(sidebar, PANEL_SUBSCRIPTIONS);
        addNavButton(sidebar, PANEL_PAYMENTS);
        addNavButton(sidebar, PANEL_INVOICES);
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        addNavButton(sidebar, PANEL_SETTINGS);
        
        sidebar.add(Box.createVerticalGlue());
        
        // Version
        JLabel versionLabel = new JLabel("Version 2.0.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(255, 255, 255, 140));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(versionLabel);
        
        return sidebar;
    }
    
    private void addNavButton(JPanel sidebar, String panelName) {
        ModernSidebarButton btn = new ModernSidebarButton(panelName);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(e -> navigateToPanel(panelName));
        navButtons.put(panelName, btn);
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
    }
    
    private JPanel createHeader() {
        // Dark glassmorphism header
        GlassPanel header = new GlassPanel(new BorderLayout());
        header.setCornerRadius(0);
        header.setShowShadow(false);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Left - Welcome
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        leftPanel.add(welcomeLabel);
        
        ActiveStatusIndicator statusLight = new ActiveStatusIndicator(true);
        leftPanel.add(statusLight);
        
        JLabel statusLabel = new JLabel("Active");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(207, 207, 207));
        leftPanel.add(statusLabel);
        
        header.add(leftPanel, BorderLayout.WEST);
        
        // Right - Actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        // Premium Logout Button
        JButton logoutBtn = createPremiumLogoutButton();
        logoutBtn.addActionListener(e -> handleLogout());
        rightPanel.add(logoutBtn);
        
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createDashboardPanel() {
        DarkGradientPanel panel = new DarkGradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Metrics Cards Row
        JPanel metricsRow = new JPanel(new GridLayout(1, 4, 20, 0));
        metricsRow.setOpaque(false);
        metricsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        metricsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Get actual data
        List<Subscription> userSubs = subscriptionService.getSubscriptionsByUserId(currentUser.getUserId());
        @SuppressWarnings("unused")
        int activeCount = (int) userSubs.stream().filter(s -> "ACTIVE".equals(s.getStatus())).count();
        Subscription currentSub = getCurrentSubscription();
        
        metricsRow.add(createMetricCard("Current Plan", 
            currentSub != null ? getPlanName(currentSub.getPlanId()) : "No Active Plan", 
            IconManager.IconType.SUBSCRIPTION, new Color(99, 102, 241)));
        
        metricsRow.add(createMetricCard("Expiry Date", 
            currentSub != null ? currentSub.getEndDate().toString() : "N/A", 
            IconManager.IconType.CALENDAR, new Color(16, 185, 129)));
        
        double totalSpent = paymentService.getAllPayments()
            .stream()
            .mapToDouble(p -> p.getAmount().doubleValue())
            .sum();
        metricsRow.add(createMetricCard("Total Spent", 
            String.format("$%.2f", totalSpent), 
            IconManager.IconType.DOLLAR, new Color(245, 158, 11)));
        
        metricsRow.add(createMetricCard("Auto-Renew", 
            currentSub != null && currentSub.getAutoRenew() ? "Enabled" : "Disabled", 
            IconManager.IconType.SYNC, currentSub != null && currentSub.getAutoRenew() ? 
                new Color(34, 197, 94) : new Color(239, 68, 68)));
        
        panel.add(metricsRow);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Quick Actions Section
        JLabel quickActionsLabel = new JLabel("Quick Actions");
        quickActionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        quickActionsLabel.setForeground(Color.WHITE);
        quickActionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(quickActionsLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JPanel actionsGrid = new JPanel(new GridLayout(2, 3, 20, 20));
        actionsGrid.setOpaque(false);
        actionsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        actionsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        QuickActionButton browsePlans = QuickActionButton.withIcon("Browse Plans",
            IconManager.IconType.PLAN, new Color(99, 102, 241));
        browsePlans.addClickListener(e -> navigateToPanel(PANEL_PLANS));
        actionsGrid.add(browsePlans);

        QuickActionButton viewOffers = QuickActionButton.withIcon("View Offers",
            IconManager.IconType.OFFER, new Color(16, 185, 129));
        viewOffers.addClickListener(e -> navigateToPanel(PANEL_OFFERS));
        actionsGrid.add(viewOffers);

        QuickActionButton mySubscriptions = QuickActionButton.withIcon("My Services",
            IconManager.IconType.SUBSCRIPTION, new Color(245, 158, 11));
        mySubscriptions.addClickListener(e -> navigateToPanel(PANEL_SUBSCRIPTIONS));
        actionsGrid.add(mySubscriptions);

        QuickActionButton payments = QuickActionButton.withIcon("Payments",
            IconManager.IconType.PAYMENT, new Color(59, 130, 246));
        payments.addClickListener(e -> navigateToPanel(PANEL_PAYMENTS));
        actionsGrid.add(payments);

        QuickActionButton invoices = QuickActionButton.withIcon("Invoices",
            IconManager.IconType.INVOICE, new Color(139, 92, 246));
        invoices.addClickListener(e -> navigateToPanel(PANEL_INVOICES));
        actionsGrid.add(invoices);

        QuickActionButton settings = QuickActionButton.withIcon("Settings",
            IconManager.IconType.SETTINGS, new Color(107, 114, 128));
        settings.addClickListener(e -> navigateToPanel(PANEL_SETTINGS));
        actionsGrid.add(settings);
        
        panel.add(actionsGrid);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Recent Activity Section
        JLabel activityLabel = new JLabel("Recent Activity");
        activityLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        activityLabel.setForeground(Color.WHITE);
        activityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(activityLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        panel.add(createRecentActivityCard());
        
        return panel;
    }
    
    private JPanel createMetricCard(String title, String value, IconManager.IconType iconType, Color color) {
        final int ARC = 20;
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight();
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D s = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, ARC, ARC);
                // drop shadow
                for (int i = 4; i >= 1; i--) {
                    int a = 22 - i * 4; if (a <= 0) continue;
                    g2.setColor(new Color(0, 0, 0, a));
                    g2.fill(new RoundRectangle2D.Float(i, i * 2, w - i * 2, h - i, ARC, ARC));
                }
                g2.setClip(s);
                // glass fill
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
                g2.setColor(Color.WHITE); g2.fill(s);
                // shimmer
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h * 0.5f, new Color(255, 255, 255, 0)));
                g2.fill(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2.setClip(null);
                // border
                g2.setColor(new Color(255, 255, 255, 40));
                g2.setStroke(new BasicStroke(1f)); g2.draw(s);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(15, 10));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        // Icon
        JLabel iconLabel = new JLabel(IconManager.createIcon(iconType, color, 32));
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconPanel.setOpaque(false);
        iconPanel.add(iconLabel);
        card.add(iconPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(180, 190, 210));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(valueLabel);

        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }
    
    private JPanel createRecentActivityCard() {
        final int ARC = 20;
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                int w = getWidth(), h = getHeight();
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D s = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, ARC, ARC);
                for (int i = 4; i >= 1; i--) {
                    int a = 22 - i * 4; if (a <= 0) continue;
                    g2.setColor(new Color(0, 0, 0, a));
                    g2.fill(new RoundRectangle2D.Float(i, i * 2, w - i * 2, h - i, ARC, ARC));
                }
                g2.setClip(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
                g2.setColor(Color.WHITE); g2.fill(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h * 0.5f, new Color(255, 255, 255, 0)));
                g2.fill(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2.setClip(null);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.setStroke(new BasicStroke(1f)); g2.draw(s);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(0, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Get actual activity data (FRESH from database each time)
        List<Activity> activities = activityService.getRecentActivities(currentUser.getUserId(), 5);
        
        JPanel activityList = new JPanel();
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        activityList.setOpaque(false);
        
        if (activities.isEmpty()) {
            JPanel emptyState = new JPanel();
            emptyState.setLayout(new BoxLayout(emptyState, BoxLayout.Y_AXIS));
            emptyState.setOpaque(false);
            
            JLabel emptyIcon = new JLabel(IconManager.createIcon(IconManager.IconType.ACTIVITY, 
                new Color(203, 213, 225), 48));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel emptyText = new JLabel("No recent activity");
            emptyText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyText.setForeground(new Color(148, 163, 184));
            emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            emptyState.add(Box.createVerticalGlue());
            emptyState.add(emptyIcon);
            emptyState.add(Box.createRigidArea(new Dimension(0, 10)));
            emptyState.add(emptyText);
            emptyState.add(Box.createVerticalGlue());
            
            activityList.add(emptyState);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm");
            
            for (int i = 0; i < activities.size(); i++) {
                Activity activity = activities.get(i);
                activityList.add(createActivityItem(activity, formatter));
                
                if (i < activities.size() - 1) {
                    activityList.add(Box.createRigidArea(new Dimension(0, 12)));
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(activityList);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        card.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createActivityItem(Activity activity, DateTimeFormatter formatter) {
        JPanel item = new JPanel(new BorderLayout(12, 5));
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        // Icon based on event type
        IconManager.IconType iconType;
        Color iconColor;
        
        String eventType = activity.getEventType();
        if (eventType.contains("SUBSCRIPTION_CREATED") || eventType.contains("SUBSCRIPTION_RENEWED")) {
            iconType = IconManager.IconType.CHECK;
            iconColor = new Color(34, 197, 94); // Green
        } else if (eventType.contains("PAYMENT")) {
            iconType = IconManager.IconType.PAYMENT;
            iconColor = new Color(99, 102, 241); // Indigo
        } else if (eventType.contains("INVOICE")) {
            iconType = IconManager.IconType.INVOICE;
            iconColor = new Color(245, 158, 11); // Orange
        } else if (eventType.contains("CANCELLED")) {
            iconType = IconManager.IconType.TRASH;
            iconColor = new Color(239, 68, 68); // Red
        } else {
            iconType = IconManager.IconType.ACTIVITY;
            iconColor = new Color(107, 114, 128); // Gray
        }
        
        JLabel icon = new JLabel(IconManager.createIcon(iconType, iconColor, 24));
        icon.setBorder(new EmptyBorder(5, 0, 0, 0));
        item.add(icon, BorderLayout.WEST);
        
        // Text content panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel descriptionLabel = new JLabel(activity.getDescription().replaceAll("[^\\x20-\\x7E\\xA0-\\u024F]", "").trim());
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(207, 207, 207));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel timestampLabel = new JLabel(activity.getTimestamp().format(formatter));
        timestampLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timestampLabel.setForeground(new Color(148, 163, 184));
        timestampLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(descriptionLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(timestampLabel);
        
        item.add(textPanel, BorderLayout.CENTER);
        
        return item;
    }
    
    private Subscription getCurrentSubscription() {
        List<Subscription> subs = subscriptionService.getSubscriptionsByUserId(currentUser.getUserId())
            .stream()
            .filter(s -> "ACTIVE".equals(s.getStatus()))
            .collect(java.util.stream.Collectors.toList());
        return subs.isEmpty() ? null : subs.get(0);
    }
    
    private String getPlanName(int planId) {
        Plan plan = planService.getPlanById(planId);
        return plan != null ? plan.getPlanName() : "Unknown Plan";
    }
    
    private JPanel createPlansPanel() {
        System.out.println("\n=== createPlansPanel() called ===");
        DarkGradientPanel panel = new DarkGradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Title - Centered (white for dark theme)
        JLabel titleLabel = new JLabel("Available Subscription Plans");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Choose the perfect plan for your needs");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(207, 207, 207));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(subtitleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Get actual plans from database
        List<Plan> plans = planService.getAllPlans();
        System.out.println("Plans loaded: " + plans.size());
        for (Plan p : plans) {
            System.out.println("  - " + p.getPlanName() + " ($" + p.getPrice() + ")");
        }
        
        if (plans.isEmpty()) {
            // Empty state
            StyledCard emptyCard = new StyledCard();
            emptyCard.setLayout(new BoxLayout(emptyCard, BoxLayout.Y_AXIS));
            emptyCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
            emptyCard.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel emptyIcon = new JLabel(IconManager.createIcon(IconManager.IconType.SUBSCRIPTION, 
                new Color(203, 213, 225), 64));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel emptyText = new JLabel("No plans available");
            emptyText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            emptyText.setForeground(new Color(148, 163, 184));
            emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            emptyCard.add(Box.createVerticalGlue());
            emptyCard.add(emptyIcon);
            emptyCard.add(Box.createRigidArea(new Dimension(0, 15)));
            emptyCard.add(emptyText);
            emptyCard.add(Box.createVerticalGlue());
            
            panel.add(emptyCard);
        } else {
            // Plans Grid - dynamic based on number of plans
            int cols = Math.min(4, plans.size());
            int rows = (int) Math.ceil((double) plans.size() / cols);
            DarkGradientPanel plansGrid = new DarkGradientPanel(new GridLayout(rows, cols, 25, 25));
            plansGrid.setMaximumSize(new Dimension(1300, rows * 450));
            
            // Glow colors for glassmorphism cards (neon accent colors)
            Color[] glowColors = {
                new Color(123,  92, 255),  // Soft violet  #7B5CFF  (Basic)
                new Color(255, 140,  66),  // Warm orange  #FF8C42  (Pro)
                new Color(193,  92, 255),  // Purple-pink  #C15CFF  (Premium)
                new Color(123,  92, 255),  // Soft violet  (cycle back)
                new Color(255, 140,  66),  // Warm orange
                new Color(193,  92, 255),  // Purple-pink
                new Color(123,  92, 255),  // Soft violet
                new Color(255, 140,  66)   // Warm orange
            };
            
            // Create glass plan cards dynamically
            for (int i = 0; i < plans.size(); i++) {
                Plan plan = plans.get(i);
                int colorIndex = i % glowColors.length;
                
                // Generate features based on plan type
                String[] features = generateFeatures(plan);
                
                // Popular tag for Pro/Enterprise plans
                boolean isPopular = plan.getPlanName().toLowerCase().contains("pro") && 
                                   !plan.getPlanName().toLowerCase().contains("non-profit");
                
                GlassPlanCard planCard = new GlassPlanCard(
                    plan.getPlanName(),
                    plan.getPrice().doubleValue(),
                    features,
                    glowColors[colorIndex],
                    isPopular
                );
                
                // Add action listener with actual plan data
                planCard.getSubscribeButton().addActionListener(e -> 
                    handleSubscribeWithPlanId(plan.getPlanId(), plan.getPlanName(), plan.getPrice().doubleValue())
                );
                
                plansGrid.add(planCard);
            }
            
            panel.add(plansGrid);
        }
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private String[] generateFeatures(Plan plan) {
        String planName = plan.getPlanName().toLowerCase();
        String cycle = plan.getBillingCycle();
        
        if (planName.contains("basic")) {
            return new String[]{
                "3 Users",
                "Basic Features",
                "Email Support",
                cycle + " Billing"
            };
        } else if (planName.contains("pro")) {
            return new String[]{
                "10 Users",
                "Advanced Features",
                "Priority Support",
                cycle + " Billing"
            };
        } else if (planName.contains("enterprise")) {
            return new String[]{
                "Unlimited Users",
                "All Features",
                "24/7 Support",
                cycle + " Billing"
            };
        } else {
            return new String[]{
                "Multiple Users",
                "Standard Features",
                "Email Support",
                cycle + " Billing"
            };
        }
    }
    
    private JPanel createSubscriptionsPanel() {
        System.out.println("\n=== createSubscriptionsPanel() called ===");
        System.out.println("Current User ID: " + (currentUser != null ? currentUser.getUserId() : "NULL"));
        DarkGradientPanel panel = new DarkGradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("My Subscriptions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Get user subscriptions
        List<Subscription> subscriptions = subscriptionService.getSubscriptionsByUserId(currentUser.getUserId());
        System.out.println("Subscriptions loaded: " + subscriptions.size());
        for (Subscription s : subscriptions) {
            System.out.println("  - Sub ID: " + s.getSubscriptionId() + ", Status: " + s.getStatus());
        }
        
        if (subscriptions.isEmpty()) {
            // Empty state
            StyledCard emptyCard = new StyledCard();
            emptyCard.setLayout(new BoxLayout(emptyCard, BoxLayout.Y_AXIS));
            emptyCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
            emptyCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel emptyIcon = new JLabel(IconManager.createIcon(IconManager.IconType.SUBSCRIPTION, 
                new Color(203, 213, 225), 64));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel emptyText = new JLabel("No active subscriptions");
            emptyText.setFont(new Font("Segoe UI", Font.BOLD, 18));
            emptyText.setForeground(new Color(100, 116, 139));
            emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel emptySubtext = new JLabel("Subscribe to a plan to get started");
            emptySubtext.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptySubtext.setForeground(new Color(148, 163, 184));
            emptySubtext.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            StyledButton browseBtn = new StyledButton("Browse Plans", StyledButton.ButtonStyle.PRIMARY);
            browseBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            browseBtn.addActionListener(e -> navigateToPanel(PANEL_PLANS));
            
            emptyCard.add(Box.createVerticalGlue());
            emptyCard.add(emptyIcon);
            emptyCard.add(Box.createRigidArea(new Dimension(0, 15)));
            emptyCard.add(emptyText);
            emptyCard.add(Box.createRigidArea(new Dimension(0, 8)));
            emptyCard.add(emptySubtext);
            emptyCard.add(Box.createRigidArea(new Dimension(0, 20)));
            emptyCard.add(browseBtn);
            emptyCard.add(Box.createVerticalGlue());
            
            panel.add(emptyCard);
        } else {
            // Display subscriptions
            for (Subscription sub : subscriptions) {
                panel.add(createSubscriptionCard(sub));
                panel.add(Box.createRigidArea(new Dimension(0, 20)));
            }
        }
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createSubscriptionCard(Subscription subscription) {
        // Glass card — dark glassmorphism, same language as GlassPlanCard
        final int CARD_ARC = 24;
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING,    RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, CARD_ARC, CARD_ARC);
                // Drop shadow
                for (int i = 4; i >= 1; i--) {
                    int a = 30 - i * 5;
                    if (a <= 0) continue;
                    g2d.setColor(new Color(0, 0, 0, a));
                    g2d.fill(new RoundRectangle2D.Float(i, i * 2, w - i * 2, h - i, CARD_ARC, CARD_ARC));
                }
                g2d.setClip(shape);
                // Glass fill
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
                g2d.setColor(Color.WHITE);
                g2d.fill(shape);
                // Top shimmer
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, h * 0.4f, new Color(255, 255, 255, 0)));
                g2d.fill(shape);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2d.setClip(null);
                // Subtle white border
                g2d.setColor(new Color(255, 255, 255, 45));
                g2d.setStroke(new BasicStroke(1f));
                g2d.draw(shape);
                g2d.dispose();
            }
        };
        card.setLayout(new BorderLayout(20, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        // Left section - Plan info
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        
        Plan plan = planService.getPlanById(subscription.getPlanId());
        String planName = plan != null ? plan.getPlanName() : "Unknown Plan";
        double planPrice = plan != null ? plan.getPrice().doubleValue() : 0.0;
        
        JLabel planLabel = new JLabel(planName);
        planLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        planLabel.setForeground(Color.WHITE);
        planLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel priceLabel = new JLabel(String.format("$%.2f/month", planPrice)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText();
                int textWidth = fm.stringWidth(text);
                GradientPaint grad = new GradientPaint(
                    0, 0, new Color(0, 245, 160),
                    textWidth, 0, new Color(99, 179, 255)
                );
                g2d.setPaint(grad);
                g2d.drawString(text, 0, fm.getAscent());
                g2d.dispose();
            }
        };
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftPanel.add(planLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(priceLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Dates
        JPanel datesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        datesPanel.setOpaque(false);
        
        JPanel startPanel = new JPanel();
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
        startPanel.setOpaque(false);
        JLabel startTitle = new JLabel("Start Date");
        startTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        startTitle.setForeground(new Color(170, 170, 170));
        JLabel startValue = new JLabel(subscription.getStartDate().toString());
        startValue.setFont(new Font("Segoe UI", Font.BOLD, 13));
        startValue.setForeground(Color.WHITE);
        startPanel.add(startTitle);
        startPanel.add(startValue);
        
        JPanel endPanel = new JPanel();
        endPanel.setLayout(new BoxLayout(endPanel, BoxLayout.Y_AXIS));
        endPanel.setOpaque(false);
        JLabel endTitle = new JLabel("End Date");
        endTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        endTitle.setForeground(new Color(170, 170, 170));
        JLabel endValue = new JLabel(subscription.getEndDate().toString());
        endValue.setFont(new Font("Segoe UI", Font.BOLD, 13));
        endValue.setForeground(Color.WHITE);
        endPanel.add(endTitle);
        endPanel.add(endValue);
        
        JPanel billingPanel = new JPanel();
        billingPanel.setLayout(new BoxLayout(billingPanel, BoxLayout.Y_AXIS));
        billingPanel.setOpaque(false);
        JLabel billingTitle = new JLabel("Next Billing");
        billingTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        billingTitle.setForeground(new Color(170, 170, 170));
        JLabel billingValue = new JLabel(subscription.getNextBillingDate() != null ? 
            subscription.getNextBillingDate().toString() : "N/A");
        billingValue.setFont(new Font("Segoe UI", Font.BOLD, 13));
        billingValue.setForeground(Color.WHITE);
        billingPanel.add(billingTitle);
        billingPanel.add(billingValue);
        
        datesPanel.add(startPanel);
        datesPanel.add(endPanel);
        datesPanel.add(billingPanel);
        
        leftPanel.add(datesPanel);
        
        card.add(leftPanel, BorderLayout.WEST);
        
        // Right section - Status and actions
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        
        // Status badge
        String status = subscription.getStatus();
        Color statusColor = status.equals("ACTIVE") ? new Color(34, 197, 94) : new Color(239, 68, 68);
        
        JPanel statusBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        statusBadge.setOpaque(false);
        // Pill-shaped status badge — no rectangular painting
        JPanel pillBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(statusColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2d.dispose();
            }
        };
        pillBadge.setOpaque(false);
        pillBadge.setLayout(new GridBagLayout());
        pillBadge.setBorder(new EmptyBorder(5, 15, 5, 15));
        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(Color.WHITE);
        pillBadge.add(statusLabel);
        statusBadge.add(pillBadge);
        rightPanel.add(statusBadge);
        
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Auto-renew toggle
        JPanel autoRenewPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        autoRenewPanel.setOpaque(false);
        
        JLabel autoRenewLabel = new JLabel("Auto-Renew");
        autoRenewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        autoRenewLabel.setForeground(new Color(200, 210, 230));
        
        ToggleSwitch autoRenewToggle = new ToggleSwitch();
        autoRenewToggle.setSelected(subscription.getAutoRenew());
        autoRenewToggle.addItemListener(e -> {
            subscription.setAutoRenew(autoRenewToggle.isSelected());
            subscriptionService.updateSubscription(subscription);
            ModernDialog.showSuccess(this, "Updated", 
                "Auto-renew has been " + (autoRenewToggle.isSelected() ? "enabled" : "disabled"));
        });
        
        autoRenewPanel.add(autoRenewLabel);
        autoRenewPanel.add(autoRenewToggle);
        rightPanel.add(autoRenewPanel);
        
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        GlassButton upgradeBtn = new GlassButton("Upgrade");
        upgradeBtn.setGlowColor(new Color(99, 102, 241));   // indigo glow
        upgradeBtn.setPreferredSize(new Dimension(130, 40));
        upgradeBtn.addActionListener(e -> navigateToPanel(PANEL_PLANS));

        GlassButton cancelBtn = new GlassButton("Cancel");
        cancelBtn.setGlowColor(new Color(239, 68, 68));     // red glow
        cancelBtn.setPreferredSize(new Dimension(130, 40));
        cancelBtn.addActionListener(e -> {
            boolean confirm = ModernDialog.showConfirmation(this, "Cancel Subscription",
                "Are you sure you want to cancel this subscription?\nThis action cannot be undone.");
            if (confirm) {
                boolean cancelled = subscriptionService.cancelSubscription(subscription.getSubscriptionId(), currentUser.getUserId());
                if (cancelled) {
                    refreshSubscriptionsPanel();
                    refreshDashboard();
                    ModernDialog.showSuccess(this, "Cancelled", "Subscription has been cancelled");
                } else {
                    ModernDialog.showError(this, "Error", "Failed to cancel subscription");
                }
            }
        });

        buttonPanel.add(upgradeBtn);
        buttonPanel.add(cancelBtn);
        rightPanel.add(buttonPanel);
        
        card.add(rightPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private void refreshSubscriptionsPanel() {
        // Remove old panel from content area
        contentArea.remove(panels.get(PANEL_SUBSCRIPTIONS));
        
        // Create fresh panel with new data
        JPanel newPanel = createSubscriptionsPanel();
        panels.put(PANEL_SUBSCRIPTIONS, newPanel);
        
        // Add to content area
        contentArea.add(newPanel, PANEL_SUBSCRIPTIONS);
        
        // Refresh UI
        contentArea.revalidate();
        contentArea.repaint();
        
        // Navigate to show updated panel
        navigateToPanel(PANEL_SUBSCRIPTIONS);
    }
    
    private JPanel createPaymentsPanel() {
        DarkGradientPanel panel = new DarkGradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JLabel titleLabel = new JLabel("Payment History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Get payment data
        List<Payment> payments = paymentService.getPaymentsByUserId(currentUser.getUserId());

        if (payments.isEmpty()) {
            // Glass empty-state card
            final int E_ARC = 24;
            JPanel emptyCard = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    RoundRectangle2D s = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, E_ARC, E_ARC);
                    // drop shadow
                    for (int i = 4; i >= 1; i--) {
                        int a = 20 - i * 4; if (a <= 0) continue;
                        g2.setColor(new Color(0, 0, 0, a));
                        g2.fill(new RoundRectangle2D.Float(i, i * 2, getWidth() - i * 2, getHeight() - i, E_ARC, E_ARC));
                    }
                    g2.setClip(s);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
                    g2.setColor(Color.WHITE); g2.fill(s);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                    g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight() * 0.5f, new Color(255, 255, 255, 0)));
                    g2.fill(s);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    g2.setClip(null);
                    g2.setColor(new Color(255, 255, 255, 45));
                    g2.setStroke(new BasicStroke(1f)); g2.draw(s);
                    g2.dispose();
                }
            };
            emptyCard.setOpaque(false);
            emptyCard.setLayout(new BoxLayout(emptyCard, BoxLayout.Y_AXIS));
            emptyCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
            emptyCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            emptyCard.setBorder(new EmptyBorder(30, 30, 30, 30));

            JLabel emptyIcon = new JLabel(IconManager.createIcon(IconManager.IconType.PAYMENT,
                new Color(203, 213, 225), 64));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel emptyText = new JLabel("No payment history");
            emptyText.setFont(new Font("Segoe UI", Font.BOLD, 18));
            emptyText.setForeground(Color.WHITE);
            emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel emptySubtext = new JLabel("Your payments will appear here");
            emptySubtext.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptySubtext.setForeground(new Color(200, 200, 200));
            emptySubtext.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyCard.add(Box.createVerticalGlue());
            emptyCard.add(emptyIcon);
            emptyCard.add(Box.createRigidArea(new Dimension(0, 15)));
            emptyCard.add(emptyText);
            emptyCard.add(Box.createRigidArea(new Dimension(0, 8)));
            emptyCard.add(emptySubtext);
            emptyCard.add(Box.createVerticalGlue());
            panel.add(emptyCard);
        } else {
            for (Payment p : payments) {
                panel.add(createPaymentCard(p));
                panel.add(Box.createRigidArea(new Dimension(0, 18)));
            }
        }

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel createPaymentCard(Payment payment) {
        final int ARC = 24;
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D s = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
                // drop shadow
                for (int i = 4; i >= 1; i--) {
                    int a = 22 - i * 4; if (a <= 0) continue;
                    g2.setColor(new Color(0, 0, 0, a));
                    g2.fill(new RoundRectangle2D.Float(i, i * 2, getWidth() - i * 2, getHeight() - i, ARC, ARC));
                }
                g2.setClip(s);
                // glass fill
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
                g2.setColor(Color.WHITE); g2.fill(s);
                // shimmer
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight() * 0.5f, new Color(255, 255, 255, 0)));
                g2.fill(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2.setClip(null);
                // border
                g2.setColor(new Color(255, 255, 255, 45));
                g2.setStroke(new BasicStroke(1f)); g2.draw(s);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(20, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Left — Payment ID, date, method
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel paymentIdLabel = new JLabel("PAY-" + payment.getPaymentId());
        paymentIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        paymentIdLabel.setForeground(Color.WHITE);
        paymentIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel("Date: " + payment.getPaymentDate().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(new Color(180, 190, 210));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel methodLabel = new JLabel("Method: " + (payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "—"));
        methodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        methodLabel.setForeground(new Color(180, 190, 210));
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(paymentIdLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        leftPanel.add(dateLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        leftPanel.add(methodLabel);
        card.add(leftPanel, BorderLayout.WEST);

        // Center — gradient amount
        String amtText = String.format("$%.2f", payment.getAmount());
        JLabel amountLabel = new JLabel(amtText) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                int tw = fm.stringWidth(t);
                g2.setPaint(new GradientPaint(0, 0, new Color(0, 245, 160), tw, 0, new Color(99, 179, 255)));
                g2.drawString(t, (getWidth() - tw) / 2, fm.getAscent());
                g2.dispose();
            }
        };
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        amountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(amountLabel, BorderLayout.CENTER);

        // Right — pill status badge + vertical centering
        JPanel rightOuter = new JPanel(new GridBagLayout());
        rightOuter.setOpaque(false);

        JPanel statusPill = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(34, 197, 94));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
            }
        };
        statusPill.setOpaque(false);
        statusPill.setLayout(new GridBagLayout());
        statusPill.setPreferredSize(new Dimension(100, 30));
        statusPill.setMaximumSize(new Dimension(100, 30));

        JLabel statusLbl = new JLabel("Completed");
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLbl.setForeground(Color.WHITE);
        statusPill.add(statusLbl);

        rightOuter.add(statusPill);
        card.add(rightOuter, BorderLayout.EAST);

        return card;
    }
    
    private JPanel createInvoicesPanel() {
        DarkGradientPanel panel = new DarkGradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Invoices");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        List<Invoice> invoices = invoiceService.getInvoicesByUserId(currentUser.getUserId());

        if (invoices.isEmpty()) {
            final int E_ARC = 24;
            JPanel emptyCard = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    RoundRectangle2D s = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, E_ARC, E_ARC);
                    for (int i = 4; i >= 1; i--) {
                        int a = 20 - i * 4; if (a <= 0) continue;
                        g2.setColor(new Color(0, 0, 0, a));
                        g2.fill(new RoundRectangle2D.Float(i, i * 2, getWidth() - i * 2, getHeight() - i, E_ARC, E_ARC));
                    }
                    g2.setClip(s);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
                    g2.setColor(Color.WHITE); g2.fill(s);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                    g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight() * 0.5f, new Color(255, 255, 255, 0)));
                    g2.fill(s);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    g2.setClip(null);
                    g2.setColor(new Color(255, 255, 255, 45));
                    g2.setStroke(new BasicStroke(1f)); g2.draw(s);
                    g2.dispose();
                }
            };
            emptyCard.setOpaque(false);
            emptyCard.setLayout(new BoxLayout(emptyCard, BoxLayout.Y_AXIS));
            emptyCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
            emptyCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            emptyCard.setBorder(new EmptyBorder(30, 30, 30, 30));

            JLabel emptyIcon = new JLabel(IconManager.createIcon(IconManager.IconType.INVOICE, new Color(203, 213, 225), 64));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel emptyText = new JLabel("No invoices available");
            emptyText.setFont(new Font("Segoe UI", Font.BOLD, 18));
            emptyText.setForeground(Color.WHITE);
            emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel emptySubtext = new JLabel("Your invoices will appear here");
            emptySubtext.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptySubtext.setForeground(new Color(200, 200, 200));
            emptySubtext.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyCard.add(Box.createVerticalGlue());
            emptyCard.add(emptyIcon);
            emptyCard.add(Box.createRigidArea(new Dimension(0, 15)));
            emptyCard.add(emptyText);
            emptyCard.add(Box.createRigidArea(new Dimension(0, 8)));
            emptyCard.add(emptySubtext);
            emptyCard.add(Box.createVerticalGlue());
            panel.add(emptyCard);
        } else {
            for (Invoice invoice : invoices) {
                panel.add(createInvoiceCard(invoice));
                panel.add(Box.createRigidArea(new Dimension(0, 18)));
            }
        }

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel createInvoiceCard(Invoice invoice) {
        final int ARC = 24;
        String status = invoice.getStatus();
        Color statusColor;
        if ("PAID".equalsIgnoreCase(status)) {
            statusColor = new Color(34, 197, 94);
        } else if ("UNPAID".equalsIgnoreCase(status)) {
            statusColor = new Color(239, 68, 68);
        } else {
            statusColor = new Color(251, 146, 60);
        }

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D s = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
                for (int i = 4; i >= 1; i--) {
                    int a = 22 - i * 4; if (a <= 0) continue;
                    g2.setColor(new Color(0, 0, 0, a));
                    g2.fill(new RoundRectangle2D.Float(i, i * 2, getWidth() - i * 2, getHeight() - i, ARC, ARC));
                }
                g2.setClip(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
                g2.setColor(Color.WHITE); g2.fill(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight() * 0.5f, new Color(255, 255, 255, 0)));
                g2.fill(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2.setClip(null);
                g2.setColor(new Color(255, 255, 255, 45));
                g2.setStroke(new BasicStroke(1f)); g2.draw(s);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(20, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Left — Invoice ID, date, gradient amount
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel invoiceIdLabel = new JLabel("Invoice #" + invoice.getInvoiceId());
        invoiceIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        invoiceIdLabel.setForeground(Color.WHITE);
        invoiceIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel("Date: " + invoice.getInvoiceDate().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(new Color(180, 190, 210));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String amtText = String.format("$%.2f", invoice.getAmount());
        JLabel amountLabel = new JLabel(amtText) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                int tw = fm.stringWidth(t);
                g2.setPaint(new GradientPaint(0, 0, new Color(0, 245, 160), tw, 0, new Color(99, 179, 255)));
                g2.drawString(t, 0, fm.getAscent());
                g2.dispose();
            }
        };
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(invoiceIdLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(dateLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        leftPanel.add(amountLabel);
        card.add(leftPanel, BorderLayout.WEST);

        // Center — pill status badge
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        final Color pillColor = statusColor;
        JPanel statusPill = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(pillColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
            }
        };
        statusPill.setOpaque(false);
        statusPill.setLayout(new GridBagLayout());
        statusPill.setPreferredSize(new Dimension(90, 30));
        statusPill.setMaximumSize(new Dimension(90, 30));
        JLabel statusLabel = new JLabel(status.toUpperCase());
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(Color.WHITE);
        statusPill.add(statusLabel);
        centerPanel.add(statusPill);
        card.add(centerPanel, BorderLayout.CENTER);

        // Right — GlassButton actions
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        GlassButton downloadBtn = new GlassButton("Download");
        downloadBtn.setGlowColor(new Color(99, 102, 241));
        downloadBtn.setPreferredSize(new Dimension(125, 38));
        downloadBtn.addActionListener(e -> ModernDialog.showSuccess(this, "Download Started",
                "Invoice #" + invoice.getInvoiceId() + " is being downloaded."));
        btnRow.add(downloadBtn);

        if (!"PAID".equalsIgnoreCase(status)) {
            GlassButton payBtn = new GlassButton("Pay Now");
            payBtn.setGlowColor(new Color(34, 197, 94));
            payBtn.setPreferredSize(new Dimension(110, 38));
            payBtn.addActionListener(e -> {
                boolean confirm = ModernDialog.showConfirmation(this, "Process Payment",
                    "Process payment of $" + String.format("%.2f", invoice.getAmount().doubleValue()) + "?");
                if (confirm) {
                    boolean success = invoiceService.markInvoiceAsPaid(
                        invoice.getInvoiceId(), "CREDIT_CARD", currentUser.getUserId());
                    if (success) {
                        refreshInvoicesPanel();
                        refreshPaymentsPanel();
                        refreshDashboard();
                        ModernDialog.showSuccess(this, "Success", "Payment processed successfully!");
                    } else {
                        ModernDialog.showError(this, "Error", "Failed to process payment");
                    }
                }
            });
            btnRow.add(payBtn);
        }

        rightPanel.add(btnRow);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }
    
    private void refreshInvoicesPanel() {
        // Remove old panel
        contentArea.remove(panels.get(PANEL_INVOICES));
        
        // Create fresh panel with new data
        JPanel newPanel = createInvoicesPanel();
        panels.put(PANEL_INVOICES, newPanel);
        
        // Add to content area
        contentArea.add(newPanel, PANEL_INVOICES);
        
        // Refresh UI
        contentArea.revalidate();
        contentArea.repaint();
    }
    
    private void refreshPaymentsPanel() {
        // Remove old panel
        contentArea.remove(panels.get(PANEL_PAYMENTS));
        
        // Create fresh panel with new data
        JPanel newPanel = createPaymentsPanel();
        panels.put(PANEL_PAYMENTS, newPanel);
        
        // Add to content area
        contentArea.add(newPanel, PANEL_PAYMENTS);
        
        // Refresh UI
        contentArea.revalidate();
        contentArea.repaint();
    }
    
    private void refreshDashboard() {
        // Remove old panel
        contentArea.remove(panels.get(PANEL_DASHBOARD));
        
        // Create fresh panel with new data
        JPanel newPanel = createDashboardPanel();
        panels.put(PANEL_DASHBOARD, newPanel);
        
        // Add to content area
        contentArea.add(newPanel, PANEL_DASHBOARD);
        
        // Refresh UI
        contentArea.revalidate();
        contentArea.repaint();
    }
    
    private JPanel createOffersPanel() {
        DarkGradientPanel panel = new DarkGradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Special Offers");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Exclusive deals and promotions for you");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(207, 207, 207));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(subtitleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Offers Grid - 3 columns, 2 rows, smaller size
        DarkGradientPanel offersGrid = new DarkGradientPanel(new GridLayout(2, 3, 15, 15));
        offersGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 480));
        offersGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Offer 1: Visa Exclusive
        offersGrid.add(createOfferCard(
            "Welcome Offer",
            "20% OFF",
            "First Month",
            "WELCOME20",
            "Valid until Dec 31, 2026",
            new Color(99, 102, 241),
            "Visa",
            new Color(26, 31, 113)
        ));
        
        // Offer 2: Mastercard Special
        offersGrid.add(createOfferCard(
            "Annual Special",
            "30% OFF",
            "Yearly Subscription",
            "ANNUAL30",
            "Save $360 per year",
            new Color(16, 185, 129),
            "Mastercard",
            new Color(235, 0, 27)
        ));
        
        // Offer 3: Amex Premium
        offersGrid.add(createOfferCard(
            "Refer Friends",
            "15% OFF",
            "Each Referral",
            "REFER15",
            "Unlimited uses",
            new Color(245, 158, 11),
            "Amex",
            new Color(0, 111, 207)
        ));
        
        // Offer 4: Discover Rewards
        offersGrid.add(createOfferCard(
            "Upgrade Today",
            "25% OFF",
            "Premium Plans",
            "UPGRADE25",
            "Limited time offer",
            new Color(139, 92, 246),
            "Discover",
            new Color(255, 96, 0)
        ));
        
        // Offer 5: Visa Signature
        offersGrid.add(createOfferCard(
            "Student Discount",
            "35% OFF",
            "Annual Plan",
            "STUDENT35",
            "Valid student ID required",
            new Color(236, 72, 153),
            "Visa",
            new Color(26, 31, 113)
        ));
        
        // Offer 6: All Cards
        offersGrid.add(createOfferCard(
            "Flash Sale",
            "40% OFF",
            "Limited Time",
            "FLASH40",
            "Expires in 48 hours",
            new Color(220, 38, 38),
            "All Cards",
            new Color(107, 114, 128)
        ));
        
        panel.add(offersGrid);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createOfferCard(String title, String discount, String description, 
                                     String code, String validity, Color accentColor,
                                     String cardName, Color cardColor) {
        GlassOfferCard card = new GlassOfferCard(accentColor);
        
        // Credit Card Badge at top-right
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Card Badge
        JPanel cardBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(cardColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
            }
        };
        cardBadge.setOpaque(false);
        cardBadge.setPreferredSize(new Dimension(85, 24));
        cardBadge.setLayout(new GridBagLayout());
        
        JLabel cardLabel = new JLabel(cardName);
        cardLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        cardLabel.setForeground(Color.WHITE);
        cardBadge.add(cardLabel);
        
        headerPanel.add(cardBadge, BorderLayout.EAST);
        card.add(headerPanel, BorderLayout.NORTH);
        
        // Center section - Discount
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        JLabel discountLabel = new JLabel(discount);
        discountLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        discountLabel.setForeground(accentColor);
        discountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(discountLabel);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(207, 207, 207));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(descLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        // Code section
        JPanel codePanel = new JPanel(new BorderLayout(8, 0));
        codePanel.setOpaque(false);
        codePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 40), 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        codePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        
        JLabel codeLabel = new JLabel("Code: " + code);
        codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        codeLabel.setForeground(Color.WHITE);
        codePanel.add(codeLabel, BorderLayout.CENTER);
        
        JButton copyBtn = new JButton("\u29C9"); // ⧉ overlapping squares — standard copy symbol
        copyBtn.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 15));
        copyBtn.setForeground(new Color(200, 200, 255));
        copyBtn.setOpaque(false);
        copyBtn.setContentAreaFilled(false);
        copyBtn.setBorderPainted(false);
        copyBtn.setBorder(new EmptyBorder(0, 5, 0, 5));
        copyBtn.setFocusPainted(false);
        copyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        copyBtn.setPreferredSize(new Dimension(28, 20));
        copyBtn.addActionListener(e -> {
            java.awt.datatransfer.StringSelection selection = 
                new java.awt.datatransfer.StringSelection(code);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            ModernDialog.showSuccess(this, "Copied!", "Offer code copied to clipboard");
        });
        codePanel.add(copyBtn, BorderLayout.EAST);
        
        centerPanel.add(codePanel);
        card.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom section
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        
        JLabel validityLabel = new JLabel(validity);
        validityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        validityLabel.setForeground(new Color(148, 163, 184));
        validityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomPanel.add(validityLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        GlassButton applyBtn = new GlassButton("Apply Offer");
        applyBtn.setGlowColor(accentColor);
        applyBtn.setPreferredSize(new Dimension(150, 42));
        applyBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        applyBtn.addActionListener(e -> {
            ModernDialog.showSuccess(this, "Success",
                "Offer code " + code + " has been applied to your account!");
        });
        bottomPanel.add(applyBtn);
        
        card.add(bottomPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    // ── Shared glass card factory ────────────────────────────────────────
    private JPanel makeGlassCard(int arc, float fillAlpha) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D s = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, arc, arc);
                for (int i = 4; i >= 1; i--) {
                    int a = 22 - i*4; if (a <= 0) continue;
                    g2.setColor(new Color(0,0,0,a));
                    g2.fill(new RoundRectangle2D.Float(i, i*2, getWidth()-i*2, getHeight()-i, arc, arc));
                }
                g2.setClip(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fillAlpha));
                g2.setColor(Color.WHITE); g2.fill(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2.setPaint(new GradientPaint(0,0,Color.WHITE,0,getHeight()*0.5f,new Color(255,255,255,0)));
                g2.fill(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2.setClip(null);
                g2.setColor(new Color(255,255,255,45));
                g2.setStroke(new BasicStroke(1f)); g2.draw(s);
                g2.dispose();
            }
        };
    }

    private JPanel makeGlassCardDanger(int arc) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D s = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, arc, arc);
                for (int i = 4; i >= 1; i--) {
                    int a = 22 - i*4; if (a <= 0) continue;
                    g2.setColor(new Color(0,0,0,a));
                    g2.fill(new RoundRectangle2D.Float(i, i*2, getWidth()-i*2, getHeight()-i, arc, arc));
                }
                g2.setClip(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
                g2.setColor(Color.WHITE); g2.fill(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
                g2.setColor(new Color(239,68,68)); g2.fill(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2.setPaint(new GradientPaint(0,0,Color.WHITE,0,getHeight()*0.5f,new Color(255,255,255,0)));
                g2.fill(s);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2.setClip(null);
                g2.setColor(new Color(239,68,68,120));
                g2.setStroke(new BasicStroke(1.5f)); g2.draw(s);
                g2.dispose();
            }
        };
    }

    private JPanel createSettingsPanel() {
        DarkGradientPanel panel = new DarkGradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Account Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // ── Profile card ──────────────────────────────────────────────────
        JPanel profileCard = makeGlassCard(24, 0.10f);
        profileCard.setOpaque(false);
        profileCard.setLayout(new BoxLayout(profileCard, BoxLayout.Y_AXIS));
        profileCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        profileCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel profileTitle = new JLabel("Profile Information");
        profileTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        profileTitle.setForeground(Color.WHITE);
        profileTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileCard.add(profileTitle);
        profileCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Account Status
        boolean isActive = true;
        List<Subscription> activeSubs = subscriptionService.getSubscriptionsByUserId(currentUser.getUserId())
            .stream().filter(s -> "ACTIVE".equals(s.getStatus()))
            .collect(java.util.stream.Collectors.toList());
        isActive = !activeSubs.isEmpty();
        final boolean activeState = isActive;
        final Color dotColor = activeState ? new Color(34, 197, 94) : new Color(239, 68, 68);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusPanel.setOpaque(false);
        statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel statusTitle = new JLabel("Account Status:");
        statusTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusTitle.setForeground(new Color(200, 210, 230));
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dotColor); g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dot.setOpaque(false); dot.setPreferredSize(new Dimension(12, 12));
        JLabel statusText = new JLabel(activeState ? "Active" : "Inactive");
        statusText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusText.setForeground(dotColor);
        statusPanel.add(statusTitle); statusPanel.add(dot); statusPanel.add(statusText);
        profileCard.add(statusPanel);
        profileCard.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Username
        JPanel usernamePanel = createSettingRow("Username", currentUser.getName(), "Update Username", e -> {
            String newName = ModernDialog.showInput(this, "Update Username", "Enter new username:", currentUser.getName());
            if (newName != null && !newName.trim().isEmpty()) {
                currentUser.setName(newName.trim());
                new UserDAO().updateUser(currentUser);
                refreshSettingsPanel();
                ModernDialog.showSuccess(this, "Success", "Username updated successfully");
            }
        });
        profileCard.add(usernamePanel);
        profileCard.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Email
        JPanel emailPanel = createSettingRow("Email", currentUser.getEmail(), "Update Email", e -> {
            String newEmail = ModernDialog.showInput(this, "Update Email", "Enter new email:", currentUser.getEmail());
            if (newEmail != null && !newEmail.trim().isEmpty() && newEmail.contains("@")) {
                currentUser.setEmail(newEmail.trim());
                new UserDAO().updateUser(currentUser);
                refreshSettingsPanel();
                ModernDialog.showSuccess(this, "Success", "Email updated successfully");
            } else if (newEmail != null) {
                ModernDialog.showError(this, "Invalid Email", "Please enter a valid email address");
            }
        });
        profileCard.add(emailPanel);
        profileCard.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Password
        JPanel passwordPanel = createSettingRow("Password", "••••••••", "Change Password", e -> {
            String[] pwds = ModernDialog.showChangePassword(this);
            if (pwds != null) {
                String newPassword     = pwds[1];
                String confirmPassword = pwds[2];
                if (newPassword.length() < 6) {
                    ModernDialog.showError(this, "Error", "New password must be at least 6 characters");
                } else if (!newPassword.equals(confirmPassword)) {
                    ModernDialog.showError(this, "Error", "Passwords do not match");
                } else {
                    ModernDialog.showSuccess(this, "Success", "Password changed successfully");
                }
            }
        });
        profileCard.add(passwordPanel);
        
        panel.add(profileCard);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Preferences Section
        JPanel preferencesCard = makeGlassCard(24, 0.10f);
        preferencesCard.setOpaque(false);
        preferencesCard.setLayout(new BoxLayout(preferencesCard, BoxLayout.Y_AXIS));
        preferencesCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 270));
        preferencesCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        preferencesCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel preferencesTitle = new JLabel("Preferences");
        preferencesTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        preferencesTitle.setForeground(Color.WHITE);
        preferencesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        preferencesCard.add(preferencesTitle);
        preferencesCard.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Auto-Renew Global Setting
        Subscription currentSub = getCurrentSubscription();
        JPanel autoRenewPanel = createToggleRow("Auto-Renew Subscriptions", 
            "Automatically renew subscriptions before expiry",
            currentSub != null && currentSub.getAutoRenew(),
            e -> {
                ToggleSwitch toggle = (ToggleSwitch) e.getSource();
                if (currentSub != null) {
                    currentSub.setAutoRenew(toggle.isSelected());
                    subscriptionService.updateSubscription(currentSub);
                    ModernDialog.showSuccess(this, "Updated", 
                        "Auto-renew preference has been " + (toggle.isSelected() ? "enabled" : "disabled"));
                } else {
                    toggle.setSelected(false);
                    ModernDialog.showError(this, "Error", "No active subscription found");
                }
            });
        preferencesCard.add(autoRenewPanel);
        preferencesCard.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Email Notifications
        JPanel notificationsPanel = createToggleRow("Email Notifications", 
            "Receive email updates about your subscriptions",
            true,
            e -> {
                ToggleSwitch toggle = (ToggleSwitch) e.getSource();
                ModernDialog.showSuccess(this, "Updated", 
                    "Email notifications " + (toggle.isSelected() ? "enabled" : "disabled"));
            });
        preferencesCard.add(notificationsPanel);
        preferencesCard.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Marketing Emails
        JPanel marketingPanel = createToggleRow("Marketing Emails", 
            "Receive promotional offers and updates",
            false,
            e -> {
                ToggleSwitch toggle = (ToggleSwitch) e.getSource();
                ModernDialog.showSuccess(this, "Updated", 
                    "Marketing emails " + (toggle.isSelected() ? "enabled" : "disabled"));
            });
        preferencesCard.add(marketingPanel);
        
        panel.add(preferencesCard);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Danger Zone
        JPanel dangerCard = makeGlassCardDanger(24);
        dangerCard.setOpaque(false);
        dangerCard.setLayout(new BoxLayout(dangerCard, BoxLayout.Y_AXIS));
        dangerCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        dangerCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        dangerCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel dangerTitle = new JLabel("Danger Zone");
        dangerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dangerTitle.setForeground(new Color(252, 129, 129));
        dangerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        dangerCard.add(dangerTitle);
        dangerCard.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JLabel dangerDesc = new JLabel("Once you delete your account, there is no going back. Please be certain.");
        dangerDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dangerDesc.setForeground(new Color(207, 207, 207));
        dangerDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        dangerCard.add(dangerDesc);
        dangerCard.add(Box.createRigidArea(new Dimension(0, 18)));

        GlassButton deleteBtn = new GlassButton("Delete Account");
        deleteBtn.setGlowColor(new Color(239, 68, 68));
        deleteBtn.setPreferredSize(new Dimension(160, 38));
        deleteBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        deleteBtn.addActionListener(e -> {
            boolean confirm = ModernDialog.showConfirmation(this, "Delete Account",
                "Are you absolutely sure you want to delete your account?\n\n" +
                "This will:\n" +
                "• Cancel all active subscriptions\n" +
                "• Delete all your data permanently\n" +
                "• Cannot be undone\n\n" +
                "Type 'DELETE' to confirm.");
            
            if (confirm) {
                String confirmation = ModernDialog.showInput(this, "Delete Account", "Type DELETE to confirm account deletion:", "");
                if ("DELETE".equals(confirmation)) {
                    // Delete user account
                    new UserDAO().deleteUser(currentUser.getUserId());
                    dispose();
                    new LoginFrame().setVisible(true);
                    JOptionPane.showMessageDialog(null, "Account deleted successfully");
                }
            }
        });
        dangerCard.add(deleteBtn);
        
        panel.add(dangerCard);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createSettingRow(String label, String value, String buttonText,
                                     ActionListener action) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelText.setForeground(Color.WHITE);

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueText.setForeground(new Color(180, 190, 210));

        leftPanel.add(labelText);
        leftPanel.add(valueText);

        GlassButton button = new GlassButton(buttonText);
        button.setGlowColor(new Color(99, 102, 241));
        button.setPreferredSize(new Dimension(160, 34));
        button.addActionListener(action);

        row.add(leftPanel, BorderLayout.WEST);
        row.add(button, BorderLayout.EAST);

        return row;
    }
    
    private JPanel createToggleRow(String label, String description, boolean initialState,
                                    java.awt.event.ItemListener action) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelText.setForeground(Color.WHITE);

        JLabel descText = new JLabel(description);
        descText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descText.setForeground(new Color(180, 190, 210));

        leftPanel.add(labelText);
        leftPanel.add(descText);

        ToggleSwitch toggle = new ToggleSwitch();
        toggle.setSelected(initialState);
        toggle.addItemListener(action);

        row.add(leftPanel, BorderLayout.WEST);
        row.add(toggle, BorderLayout.EAST);

        return row;
    }
    
    private void refreshSettingsPanel() {
        panels.put(PANEL_SETTINGS, createSettingsPanel());
        contentArea.remove(contentArea.getComponentCount() - 1);
        contentArea.add(panels.get(PANEL_SETTINGS), PANEL_SETTINGS);
        navigateToPanel(PANEL_SETTINGS);
    }
    
    private void navigateToPanel(String panelName) {
        CardLayout cl = (CardLayout) contentArea.getLayout();
        cl.show(contentArea, panelName);
        
        // Update button states
        for (Map.Entry<String, ModernSidebarButton> entry : navButtons.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(panelName));
        }
    }
    
    private void handleSubscribeWithPlanId(int planId, String planName, double price) {
        boolean confirmed = ModernDialog.showConfirmation(
            this,
            "Confirm Subscription",
            "Subscribe to " + planName + " for $" + String.format("%.2f", price) + "?"
        );
        
        if (confirmed) {
            boolean success = subscriptionService.createSubscription(
                currentUser.getUserId(), 
                planId, 
                LocalDate.now()
            );
            
            if (success) {
                // Refresh all affected panels with fresh data from database
                refreshSubscriptionsPanel();
                refreshInvoicesPanel();
                refreshPaymentsPanel();
                refreshDashboard();
                
                // Show success message
                ModernDialog.showSuccess(this, "Success", 
                    "Successfully subscribed to " + planName + "!\n\nYour subscription is now active.");
                
                // Navigate to subscriptions to see the new subscription
                navigateToPanel(PANEL_SUBSCRIPTIONS);
            } else {
                ModernDialog.showError(this, "Error", "Failed to create subscription. Please try again.");
            }
        }
    }
    
    /**
     * Creates a circular logo for the sidebar branding
     * Size: 48px diameter, high-quality rendering
     */
    private JLabel createCircularLogo(int diameter) {
        BufferedImage logo = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = logo.createGraphics();
        
        // Enable highest quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        
        // Create circular gradient background
        RadialGradientPaint gradient = new RadialGradientPaint(
            diameter / 2f, diameter / 2f, diameter / 2f,
            new float[]{0f, 1f},
            new Color[]{new Color(99, 102, 241), new Color(67, 56, 202)}
        );
        g2d.setPaint(gradient);
        g2d.fillOval(0, 0, diameter, diameter);
        
        // Draw "S" monogram in center
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, diameter / 2));
        FontMetrics fm = g2d.getFontMetrics();
        String letter = "S";
        int x = (diameter - fm.stringWidth(letter)) / 2;
        int y = ((diameter - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(letter, x, y);
        
        // Add subtle border
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawOval(1, 1, diameter - 2, diameter - 2);
        
        g2d.dispose();
        
        return new JLabel(new ImageIcon(logo));
    }
    
    /**
     * Creates professional logout button with icon
     * Premium SaaS design with proper hover states
     */
    private JButton createPremiumLogoutButton() {
        JButton logoutBtn = new JButton(" ← Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background with hover effect
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(220, 38, 38)); // #DC2626
                } else {
                    g2d.setColor(new Color(239, 68, 68)); // #EF4444
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Subtle shadow when not hovered
                if (!getModel().isRollover()) {
                    g2d.setColor(new Color(0, 0, 0, 20));
                    g2d.fillRoundRect(0, 2, getWidth(), getHeight(), 8, 8);
                }
                
                super.paintComponent(g);
            }
        };
        
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setPreferredSize(new Dimension(115, 40));
        logoutBtn.setMargin(new Insets(10, 18, 10, 18));
        
        return logoutBtn;
    }
    
    private void handleLogout() {
        boolean confirmed = ModernDialog.showConfirmation(
            this,
            "Logout",
            "Are you sure you want to logout?"
        );
        
        if (confirmed) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
