package ui;

import service.*;
import dao.ActivityDAO;
import model.Activity;
import ui.components.*;
import ui.theme.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Enterprise-grade dashboard panel with comprehensive analytics and charts.
 * Features: KPI cards, revenue charts, activity feed, and real-time metrics.
 */
public class DashboardPanel extends JPanel {
    private final SubscriptionService subscriptionService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final UserService userService;
    private final ActivityDAO activityDAO;
    
    private KPICard revenueCard;
    private KPICard subscriptionsCard;
    private KPICard invoicesCard;
    private KPICard customersCard;
    
    private LineChartPanel revenueChart;
    private BarChartPanel profitChart;
    private LineChartPanel customerChart;
    private PieChartPanel planDistributionChart;
    private RecentActivityPanel activityPanel;
    
    public DashboardPanel() {
        this.subscriptionService = new SubscriptionService();
        this.invoiceService = new InvoiceService();
        this.paymentService = new PaymentService();
        this.userService = new UserService();
        this.activityDAO = new ActivityDAO();
        
        initializeUI();
        loadMetrics();
    }
    
    /**
     * Initializes the enterprise-grade UI with all components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Main scroll pane for entire dashboard
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Title section
        mainContainer.add(createTitleSection());
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // KPI Cards section
        mainContainer.add(createKPISection());
        mainContainer.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Charts section - Two rows
        mainContainer.add(createChartsSection());
        mainContainer.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Recent activity and quick stats
        mainContainer.add(createBottomSection());
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(UIConstants.BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(UIConstants.BACKGROUND_COLOR);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Creates the title section with refresh button.
     */
    private JPanel createTitleSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Dashboard Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Real-time insights and performance metrics");
        subtitleLabel.setFont(UIConstants.FONT_REGULAR);
        subtitleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        return panel;
    }
    
    /**
     * Creates KPI cards section.
     */
    private JPanel createKPISection() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        revenueCard       = new KPICard("$",  "Total Revenue",        "$0", new Color(0, 255, 230));   // cyan neon
        subscriptionsCard = new KPICard("\u21BB", "Active Subscriptions","0",  new Color(140, 0, 255));  // purple neon
        invoicesCard      = new KPICard("\u26A0", "Unpaid Invoices",     "0",  new Color(255, 80, 0));   // orange neon
        customersCard     = new KPICard("\uD83D\uDC65", "Total Customers","0",  new Color(57, 255, 20)); // green neon
        
        panel.add(revenueCard);
        panel.add(subscriptionsCard);
        panel.add(invoicesCard);
        panel.add(customersCard);
        
        return panel;
    }
    
    /**
     * Creates charts section with analytics.
     */
    private JPanel createChartsSection() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // First row - Revenue and Customer Growth
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        row1.setBackground(UIConstants.BACKGROUND_COLOR);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        
        revenueChart = new LineChartPanel("Monthly Revenue Trends", "Month", "Revenue ($)");
        customerChart = new LineChartPanel("Customer Growth", "Month", "Customers");
        customerChart.setLineColor(UIConstants.SUCCESS_COLOR);
        
        row1.add(createChartCard(revenueChart));
        row1.add(createChartCard(customerChart));
        
        // Second row - Profit/Loss and Plan Distribution
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        row2.setBackground(UIConstants.BACKGROUND_COLOR);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        
        profitChart = new BarChartPanel("Profit vs Loss Analysis", "Category", "Amount ($)");
        planDistributionChart = new PieChartPanel("Most Popular Plans");
        
        row2.add(createChartCard(profitChart));
        row2.add(createChartCard(planDistributionChart));
        
        container.add(row1);
        container.add(Box.createRigidArea(new Dimension(0, 30)));
        container.add(row2);
        
        return container;
    }
    
    /**
     * Transparent wrapper so the charts' own glass backgrounds show through.
     */
    private JPanel createChartCard(JComponent chart) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(500, 400));
        card.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        card.add(chart, BorderLayout.CENTER);
        return card;
    }
    
    /**
     * Creates bottom section with recent activity.
     */
    private JPanel createBottomSection() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));
        
        activityPanel = new RecentActivityPanel();
        activityPanel.setPreferredSize(new Dimension(0, 350));
        
        panel.add(activityPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Loads all metrics and updates the dashboard.
     */
    public void loadMetrics() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                loadKPIData();
                loadChartData();
                loadActivityData();
                return null;
            }
            
            @Override
            protected void done() {
                repaint();
            }
        };
        
        worker.execute();
    }
    
    /**
     * Loads KPI card data.
     */
    private void loadKPIData() {
        try {
            // Revenue
            double totalRevenue = paymentService.getTotalRevenue().doubleValue();
            revenueCard.setValue(String.format("$%.2f", totalRevenue));
            
            // Active Subscriptions
            int activeCount = subscriptionService.getActiveSubscriptionCount();
            subscriptionsCard.setValue(String.valueOf(activeCount));
            
            // Unpaid Invoices
            int unpaidCount = invoiceService.getUnpaidInvoiceCount();
            invoicesCard.setValue(String.valueOf(unpaidCount));
            
            // Total Customers
            int customerCount = userService.getUserCount();
            customersCard.setValue(String.valueOf(customerCount));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads chart data with sample/calculated values.
     */
    private void loadChartData() {
        try {
            // Revenue chart - Last 6 months
            List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun");
            List<Double> revenueData = generateMonthlyRevenue();
            revenueChart.setData(months, revenueData);
            
            // Customer growth chart
            List<Double> customerData = generateCustomerGrowth();
            customerChart.setData(months, customerData);
            
            // Profit vs Loss chart
            double totalRevenue = paymentService.getTotalRevenue().doubleValue();
            double estimatedCosts = totalRevenue * 0.3; // 30% costs
            double profit = totalRevenue - estimatedCosts;
            
            List<String> categories = Arrays.asList("Revenue", "Costs", "Profit");
            List<Double> values = Arrays.asList(totalRevenue, estimatedCosts, profit);
            List<Color> colors = Arrays.asList(
                UIConstants.SUCCESS_COLOR,
                UIConstants.DANGER_COLOR,
                UIConstants.PRIMARY_COLOR
            );
            profitChart.setData(categories, values, colors);
            
            // Plan distribution pie chart
            Map<String, Double> planData = new LinkedHashMap<>();
            planData.put("Premium", 35.0);
            planData.put("Standard", 45.0);
            planData.put("Basic", 20.0);
            planDistributionChart.setData(planData);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Generates sample monthly revenue data.
     */
    private List<Double> generateMonthlyRevenue() {
        List<Double> data = new ArrayList<>();
        double totalRevenue = paymentService.getTotalRevenue().doubleValue();
        
        // Distribute revenue across months with growth trend
        for (int i = 0; i < 6; i++) {
            double monthRevenue = (totalRevenue / 6) * (0.7 + i * 0.1);
            data.add(monthRevenue);
        }
        
        return data;
    }
    
    /**
     * Generates customer growth data.
     */
    private List<Double> generateCustomerGrowth() {
        List<Double> data = new ArrayList<>();
        int totalCustomers = userService.getUserCount();
        
        // If no customers, show sample growth curve
        if (totalCustomers == 0) {
            // Show sample data to demonstrate the chart
            data.add(5.0);
            data.add(12.0);
            data.add(18.0);
            data.add(28.0);
            data.add(35.0);
            data.add(45.0);
        } else {
            // Show growth trend based on actual data
            // Simulate cumulative growth over 6 months
            double baseGrowth = totalCustomers / 6.0;
            for (int i = 0; i < 6; i++) {
                double customers = baseGrowth * (i + 1);
                data.add(Math.round(customers * 10.0) / 10.0); // Round to 1 decimal
            }
        }
        
        return data;
    }
    
    /**
     * Loads recent activity data.
     */
    private void loadActivityData() {
        try {
            activityPanel.clearActivities();
            
            List<Activity> activities = activityDAO.getAllActivities();
            Collections.reverse(activities); // Show newest first
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
            
            // Add up to 8 most recent activities
            int count = Math.min(8, activities.size());
            for (int i = 0; i < count; i++) {
                Activity activity = activities.get(i);
                String icon = getActivityIcon(activity.getEventType());
                String timestamp = activity.getTimestamp().format(dtf);
                activityPanel.addActivity(icon, activity.getDescription(), timestamp);
            }
            
            // If no activities, add placeholder
            if (activities.isEmpty()) {
                activityPanel.addActivity("📋", "No recent activity", "Just now");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            activityPanel.addActivity("⚠️", "Error loading activities", "Now");
        }
    }
    
    /**
     * Gets appropriate icon for activity type.
     */
    private String getActivityIcon(String type) {
        switch (type.toUpperCase()) {
            case "SUBSCRIPTION_CREATED":
                return "✓";
            case "PAYMENT_COMPLETED":
                return "💳";
            case "INVOICE_GENERATED":
                return "📄";
            case "PLAN_UPGRADED":
                return "⬆️";
            case "SUBSCRIPTION_CANCELLED":
                return "❌";
            case "SUBSCRIPTION_RENEWED":
                return "🔄";
            default:
                return "📋";
        }
    }
}
