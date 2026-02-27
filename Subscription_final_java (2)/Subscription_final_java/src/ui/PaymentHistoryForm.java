package ui;

import model.Payment;
import service.PaymentService;
import ui.components.ModernTableRenderer;

import ui.tablemodels.PaymentTableModel;
import ui.theme.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Modern view panel for displaying payment history.
 * Uses professional table styling and KPI card for revenue.
 */
public class PaymentHistoryForm extends JPanel {
    private final PaymentService paymentService;
    
    private JTable paymentTable;
    private PaymentTableModel tableModel;
    private JLabel totalRevenueLabel;
    
    public PaymentHistoryForm() {
        this.paymentService = new PaymentService();
        initializeUI();
        loadPayments();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 35, 20, 35));

        // ── Title with revenue ────────────────────────────────────────────
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JPanel titleTextBlock = new JPanel();
        titleTextBlock.setLayout(new BoxLayout(titleTextBlock, BoxLayout.Y_AXIS));
        titleTextBlock.setOpaque(false);

        JLabel titleLabel = new JLabel("Payment History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("View all payment transactions");
        subtitleLabel.setFont(UIConstants.FONT_REGULAR);
        subtitleLabel.setForeground(new Color(154, 154, 203));
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);

        titleTextBlock.add(titleLabel);
        titleTextBlock.add(Box.createRigidArea(new Dimension(0, 4)));
        titleTextBlock.add(subtitleLabel);

        totalRevenueLabel = new JLabel("Total Revenue: $0.00");
        totalRevenueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalRevenueLabel.setForeground(new Color(34, 197, 94));

        titlePanel.add(titleTextBlock, BorderLayout.WEST);
        titlePanel.add(totalRevenueLabel, BorderLayout.EAST);
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

        tableModel = new PaymentTableModel(List.of());
        paymentTable = new JTable(tableModel);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernTableRenderer.styleTable(paymentTable);
        ModernTableRenderer.setColumnWidths(paymentTable, 80, 120, 120, 120, 150);

        JScrollPane scrollPane = new JScrollPane(paymentTable);
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
        refreshBtn.addActionListener(e -> loadPayments());

        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Loads payments from service layer.
     */
    public void loadPayments() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                List<Payment> payments = paymentService.getAllPayments();
                SwingUtilities.invokeLater(() -> {
                    tableModel.setPayments(payments);
                    
                    // Update total revenue
                    String revenue = "$" + paymentService.getTotalRevenue().toString();
                    totalRevenueLabel.setText("Total Revenue: " + revenue);
                });
                return null;
            }
        };
        worker.execute();
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
