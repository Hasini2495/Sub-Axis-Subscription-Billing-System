package ui;

import model.Subscription;
import service.SubscriptionService;
import ui.components.ModernTableRenderer;
import ui.components.ProfessionalDialog;
import ui.tablemodels.SubscriptionTableModel;
import ui.theme.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Modern view panel for displaying and managing subscriptions.
 * Uses professional table styling and modern UI components.
 */
public class SubscriptionViewForm extends JPanel {
    private final SubscriptionService subscriptionService;
    
    private JTable subscriptionTable;
    private SubscriptionTableModel tableModel;
    
    public SubscriptionViewForm() {
        this.subscriptionService = new SubscriptionService();
        initializeUI();
        loadSubscriptions();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 35, 20, 35));

        // ── Title ──────────────────────────────────────────────────────────────
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JLabel titleLabel = new JLabel("Subscription Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("View and manage all subscriptions");
        subtitleLabel.setFont(UIConstants.FONT_REGULAR);
        subtitleLabel.setForeground(new Color(154, 154, 203));
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(subtitleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // ── Glass table card ───────────────────────────────────────────────────
        JPanel tableCard = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 255, 255, 35));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        tableCard.setOpaque(false);

        tableModel = new SubscriptionTableModel(List.of());
        subscriptionTable = new JTable(tableModel);
        subscriptionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernTableRenderer.styleTable(subscriptionTable);
        ModernTableRenderer.setColumnWidths(subscriptionTable, 80, 150, 150, 120, 120, 120, 120);

        JScrollPane scrollPane = new JScrollPane(subscriptionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(new Color(22, 15, 56));

        tableCard.add(scrollPane, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // ── Buttons ────────────────────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        buttonPanel.setOpaque(false);

        JButton refreshBtn = makeFormButton("Refresh", new Color(55, 45, 110), new Color(40, 32, 90));
        refreshBtn.addActionListener(e -> loadSubscriptions());

        JButton cancelBtn = makeFormButton("Cancel Selected", new Color(155, 35, 35), new Color(110, 20, 20));
        cancelBtn.addActionListener(e -> handleCancel());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads subscriptions from service layer.
     */
    public void loadSubscriptions() {
        SwingWorker<List<Subscription>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Subscription> doInBackground() {
                return subscriptionService.getAllSubscriptions();
            }
            
            @Override
            protected void done() {
                try {
                    List<Subscription> subscriptions = get();
                    tableModel.setSubscriptions(subscriptions);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Handles cancelling a subscription.
     */
    private void handleCancel() {
        int selectedRow = subscriptionTable.getSelectedRow();
        
        if (selectedRow == -1) {
            ProfessionalDialog.showWarning(this, "No Selection", "Please select a subscription to cancel");
            return;
        }
        
        Subscription selectedSubscription = tableModel.getSubscriptionAt(selectedRow);
        
        if ("CANCELLED".equals(selectedSubscription.getStatus())) {
            ProfessionalDialog.showWarning(this, "Already Cancelled", "This subscription is already cancelled");
            return;
        }
        
        if ("EXPIRED".equals(selectedSubscription.getStatus())) {
            ProfessionalDialog.showWarning(this, "Already Expired", "This subscription is already expired");
            return;
        }
        
        // Confirm action
        boolean confirmed = ProfessionalDialog.showConfirmation(
            this, 
            "Cancel Subscription",
            String.format("Cancel Subscription #%d?\nThis action cannot be undone.", 
                         selectedSubscription.getSubscriptionId())
        );
        
        if (confirmed) {
            boolean success = subscriptionService.cancelSubscription(
                selectedSubscription.getSubscriptionId(),
                selectedSubscription.getUserId()
            );
            
            if (success) {
                ProfessionalDialog.showSuccess(this, "Success", "Subscription cancelled successfully!");
                loadSubscriptions(); // Refresh table
            } else {
                ProfessionalDialog.showError(this, "Error", "Failed to cancel subscription");
            }
        }
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new LinearGradientPaint(0, 0, 0, getHeight(),
                new float[]{0f, 0.5f, 1f},
                new Color[]{new Color(15, 10, 45), new Color(26, 18, 68), new Color(35, 22, 80)}));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    private JButton makeFormButton(String text, Color c1, Color c2) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setPaint(new GradientPaint(0, 0, c1, 0, h, c2));
                g2.fillRoundRect(0, 0, w, h, h, h);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRoundRect(0, 0, w, h, h, h);
                }
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRoundRect(4, 2, w - 8, h / 2, h, h);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (w - fm.stringWidth(getText())) / 2,
                        (h + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 42));
        return btn;
    }
}
