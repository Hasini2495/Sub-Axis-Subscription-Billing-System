package ui;

import model.Invoice;
import service.InvoiceService;
import ui.components.ModernTableRenderer;
import ui.components.ProfessionalDialog;
import ui.tablemodels.InvoiceTableModel;
import ui.theme.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Modern view panel for displaying and managing invoices.
 * Uses professional table styling with modern UI components.
 */
public class InvoiceViewForm extends JPanel {
    private final InvoiceService invoiceService;
    
    private JTable invoiceTable;
    private InvoiceTableModel tableModel;
    
    public InvoiceViewForm() {
        this.invoiceService = new InvoiceService();
        initializeUI();
        loadInvoices();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 35, 20, 35));

        // ── Title ──────────────────────────────────────────────────────────────
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JLabel titleLabel = new JLabel("Invoice Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("View and process invoices");
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

        tableModel = new InvoiceTableModel(List.of());
        invoiceTable = new JTable(tableModel);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernTableRenderer.styleTable(invoiceTable);
        ModernTableRenderer.setColumnWidths(invoiceTable, 80, 120, 120, 120, 100, 120);

        JScrollPane scrollPane = new JScrollPane(invoiceTable);
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
        refreshBtn.addActionListener(e -> loadInvoices());

        JButton markPaidBtn = makeFormButton("Mark as Paid", new Color(22, 120, 65), new Color(15, 88, 48));
        markPaidBtn.addActionListener(e -> handleMarkPaid());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(markPaidBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Loads invoices from service layer.
     */
    public void loadInvoices() {
        SwingWorker<List<Invoice>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Invoice> doInBackground() {
                return invoiceService.getAllInvoices();
            }
            
            @Override
            protected void done() {
                try {
                    List<Invoice> invoices = get();
                    tableModel.setInvoices(invoices);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Handles marking an invoice as paid.
     */
    private void handleMarkPaid() {
        int selectedRow = invoiceTable.getSelectedRow();
        
        if (selectedRow == -1) {
            ProfessionalDialog.showWarning(this, "No Selection", "Please select an invoice to mark as paid");
            return;
        }
        
        Invoice selectedInvoice = tableModel.getInvoiceAt(selectedRow);
        
        if ("PAID".equals(selectedInvoice.getStatus())) {
            ProfessionalDialog.showWarning(this, "Already Paid", "This invoice is already paid");
            return;
        }
        
        // Ask for payment method
        String[] paymentMethods = {"CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "BANK_TRANSFER"};
        String paymentMethod = (String) JOptionPane.showInputDialog(
            this,
            "Select payment method:",
            "Payment Method",
            JOptionPane.QUESTION_MESSAGE,
            null,
            paymentMethods,
            paymentMethods[0]
        );
        
        if (paymentMethod == null) {
            return; // User cancelled
        }
        
        // Confirm action
        boolean confirmed = ProfessionalDialog.showConfirmation(
            this, 
            "Mark as Paid",
            String.format("Mark Invoice #%d as PAID using %s?", 
                         selectedInvoice.getInvoiceId(), 
                         paymentMethod)
        );
        
        if (confirmed) {
            // Get userId from invoice's subscription
            int userId = 1; // Default admin user
            try {
                dao.SubscriptionDAO subscriptionDAO = new dao.SubscriptionDAO();
                model.Subscription sub = subscriptionDAO.getAllSubscriptions().stream()
                    .filter(s -> s.getSubscriptionId().equals(selectedInvoice.getSubscriptionId()))
                    .findFirst()
                    .orElse(null);
                if (sub != null) {
                    userId = sub.getUserId();
                }
            } catch (Exception e) {
                // Use default if subscription not found
            }
            
            boolean success = invoiceService.markInvoiceAsPaid(
                selectedInvoice.getInvoiceId(), 
                paymentMethod,
                userId
            );
            
            if (success) {
                ProfessionalDialog.showSuccess(this, "Success", "Invoice marked as paid successfully!");
                loadInvoices(); // Refresh table
            } else {
                ProfessionalDialog.showError(this, "Error", "Failed to mark invoice as paid");
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
