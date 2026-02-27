package ui;

import model.Plan;
import service.PlanService;
import ui.components.ProfessionalDialog;
import ui.theme.UIConstants;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Professional form panel for creating new subscription plans.
 * Centered layout with max-width, modern styling.
 */
public class CreatePlanForm extends JPanel {
    private final PlanService planService;
    
    private JTextField planNameField;
    private JTextField priceField;
    private JComboBox<String> billingCycleCombo;
    private JTextField durationField;
    
    public CreatePlanForm() {
        this.planService = new PlanService();
        initializeUI();
    }
    
    /**
     * Initializes the professional UI with centered form.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth(), h = getHeight();
        LinearGradientPaint gp = new LinearGradientPaint(0, 0, 0, h,
                new float[]{0f, 0.5f, 1f},
                new Color[]{new Color(26, 27, 58), new Color(43, 31, 94), new Color(58, 44, 109)});
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setOpaque(true);

        // Glass card container
        JPanel container = new JPanel(new BorderLayout(0, 16)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), r = 18;
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillRoundRect(0, 0, w, h, r, r);
                g2.setColor(new Color(120, 80, 255, 20));
                g2.fillRoundRect(0, 0, w, h, r, r);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, r, r);
                g2.dispose();
            }
        };
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(28, 35, 22, 35));
        container.setPreferredSize(new Dimension(520, 430));

        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Create New Plan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitleLabel = new JLabel("Define a new subscription plan");
        subtitleLabel.setFont(UIConstants.FONT_REGULAR);
        subtitleLabel.setForeground(new Color(190, 185, 230));
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(subtitleLabel);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Plan Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        JLabel planNameLabel = new JLabel("Plan Name:");
        planNameLabel.setFont(UIConstants.FONT_REGULAR);
        planNameLabel.setForeground(new Color(207, 207, 234));
        formPanel.add(planNameLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        planNameField = new GlassField();
        planNameField.setPreferredSize(new Dimension(0, 42));
        formPanel.add(planNameField, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel priceLabel = new JLabel("Price ($):");
        priceLabel.setFont(UIConstants.FONT_REGULAR);
        priceLabel.setForeground(new Color(207, 207, 234));
        formPanel.add(priceLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        priceField = new GlassField();
        priceField.setPreferredSize(new Dimension(0, 42));
        formPanel.add(priceField, gbc);

        // Billing Cycle
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel billingLabel = new JLabel("Billing Cycle:");
        billingLabel.setFont(UIConstants.FONT_REGULAR);
        billingLabel.setForeground(new Color(207, 207, 234));
        formPanel.add(billingLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        String[] cycles = {"MONTHLY", "QUARTERLY", "YEARLY"};
        billingCycleCombo = new JComboBox<>(cycles);
        styleCombo(billingCycleCombo);
        billingCycleCombo.setPreferredSize(new Dimension(0, 42));
        formPanel.add(billingCycleCombo, gbc);

        // Duration
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel durationLabel = new JLabel("Duration (days):");
        durationLabel.setFont(UIConstants.FONT_REGULAR);
        durationLabel.setForeground(new Color(207, 207, 234));
        formPanel.add(durationLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        durationField = new GlassField();
        durationField.setPreferredSize(new Dimension(0, 42));
        formPanel.add(durationField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        JButton createButton = makeFormButton("Create Plan", new Color(50, 160, 90), new Color(34, 120, 65));
        createButton.setPreferredSize(new Dimension(140, 42));
        createButton.addActionListener(e -> handleSubmit());
        JButton clearButton = makeFormButton("Clear", new Color(255, 255, 255, 18), new Color(255, 255, 255, 8));
        clearButton.setPreferredSize(new Dimension(100, 42));
        clearButton.addActionListener(e -> clearForm());
        buttonPanel.add(clearButton);
        buttonPanel.add(createButton);

        container.add(titlePanel, BorderLayout.NORTH);
        container.add(formPanel, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        GridBagConstraints containerGbc = new GridBagConstraints();
        containerGbc.fill = GridBagConstraints.VERTICAL;
        containerGbc.weightx = 0.0;
        containerGbc.weighty = 1.0;
        containerGbc.anchor = GridBagConstraints.CENTER;
        containerGbc.insets = new Insets(20, 0, 20, 0);
        add(container, containerGbc);
    }

    // ── Glass field ───────────────────────────────────────────────────────────
    private static class GlassField extends JTextField {
        GlassField() {
            setOpaque(false);
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setFont(UIConstants.FONT_REGULAR);
            setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), r = 16;
            g2.setColor(new Color(255, 255, 255, 18));
            g2.fillRoundRect(0, 0, w, h, r, r);
            g2.setColor(new Color(120, 80, 255, 15));
            g2.fillRoundRect(0, 0, w, h, r, r);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, r, r);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Styled combo ──────────────────────────────────────────────────────────
    private static <T> void styleCombo(JComboBox<T> combo) {
        combo.setOpaque(true);
        combo.setBackground(new Color(35, 25, 100));
        combo.setForeground(Color.WHITE);
        combo.setFont(UIConstants.FONT_REGULAR);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 65), 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 6)
        ));
        combo.setUI(new BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(255, 255, 255, 0));
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        int cx = getWidth() / 2, cy = getHeight() / 2;
                        int[] xs = {cx - 5, cx + 5, cx};
                        int[] ys = {cy - 2, cy - 2, cy + 3};
                        g2.setColor(new Color(207, 207, 234));
                        g2.fillPolygon(xs, ys, 3);
                        g2.dispose();
                    }
                    @Override public Dimension getPreferredSize() { return new Dimension(30, 0); }
                };
                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                return btn;
            }
            @Override public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(combo.getBackground());
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });
        combo.setMaximumRowCount(8);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v,
                    int i, boolean sel, boolean focus) {
                super.getListCellRendererComponent(l, v, i, sel, focus);
                setForeground(Color.WHITE);
                setBackground(sel ? new Color(60, 45, 120) : new Color(35, 25, 100));
                setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
                return this;
            }
        });
        combo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                Object popup = combo.getUI().getAccessibleChild(combo, 0);
                if (popup instanceof javax.swing.JPopupMenu) {
                    javax.swing.JPopupMenu pm = (javax.swing.JPopupMenu) popup;
                    if (pm.getComponent(0) instanceof JScrollPane) {
                        ((JScrollPane) pm.getComponent(0)).getViewport()
                                .setBackground(new Color(35, 25, 100));
                    }
                }
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });
    }

    // ── Glass form button ─────────────────────────────────────────────────────
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    /**
     * Handles form submission.
     */
    private void handleSubmit() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }
        
        try {
            // Create plan object
            Plan plan = new Plan();
            plan.setPlanName(planNameField.getText().trim());
            plan.setPrice(new BigDecimal(priceField.getText().trim()));
            plan.setBillingCycle((String) billingCycleCombo.getSelectedItem());
            plan.setDuration(Integer.parseInt(durationField.getText().trim()));
            
            // Call service layer
            boolean success = planService.createPlan(plan);
            
            if (success) {
                ProfessionalDialog.showSuccess(this, "Success", "Plan created successfully!");
                clearForm();
            } else {
                ProfessionalDialog.showError(this, "Error", "Failed to create plan. Please try again.");
            }
            
        } catch (IllegalArgumentException ex) {
            ProfessionalDialog.showError(this, "Validation Error", ex.getMessage());
        } catch (Exception ex) {
            ProfessionalDialog.showError(this, "Error", "An error occurred: " + ex.getMessage());
        }
    }
    
    /**
     * Validates form inputs.
     * @return true if all inputs are valid
     */
    private boolean validateInputs() {
        String planName = planNameField.getText().trim();
        String priceText = priceField.getText().trim();
        String durationText = durationField.getText().trim();
        
        if (planName.isEmpty()) {
            ProfessionalDialog.showWarning(this, "Validation Error", "Plan name cannot be empty");
            planNameField.requestFocus();
            return false;
        }
        
        if (priceText.isEmpty()) {
            ProfessionalDialog.showWarning(this, "Validation Error", "Price cannot be empty");
            priceField.requestFocus();
            return false;
        }
        
        try {
            BigDecimal price = new BigDecimal(priceText);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                ProfessionalDialog.showWarning(this, "Validation Error", "Price must be greater than 0");
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException ex) {
            ProfessionalDialog.showWarning(this, "Validation Error", "Price must be a valid number");
            priceField.requestFocus();
            return false;
        }
        
        if (durationText.isEmpty()) {
            ProfessionalDialog.showWarning(this, "Validation Error", "Duration cannot be empty");
            durationField.requestFocus();
            return false;
        }
        
        try {
            int duration = Integer.parseInt(durationText);
            if (duration <= 0) {
                ProfessionalDialog.showWarning(this, "Validation Error", "Duration must be greater than 0");
                durationField.requestFocus();
                return false;
            }
        } catch (NumberFormatException ex) {
            ProfessionalDialog.showWarning(this, "Validation Error", "Duration must be a valid integer");
            durationField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Clears all form fields.
     */
    private void clearForm() {
        planNameField.setText("");
        priceField.setText("");
        durationField.setText("");
        billingCycleCombo.setSelectedIndex(0);
        planNameField.requestFocus();
    }
}
