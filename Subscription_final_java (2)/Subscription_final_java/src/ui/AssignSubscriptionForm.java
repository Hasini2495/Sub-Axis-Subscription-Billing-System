package ui;

import model.User;
import model.Plan;
import service.UserService;
import service.PlanService;
import service.SubscriptionService;
import ui.components.ProfessionalDialog;
import ui.theme.UIConstants;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Professional form panel for assigning subscriptions to users.
 * Centered layout with max-width, modern styling.
 */
public class AssignSubscriptionForm extends JPanel {
    private final UserService userService;
    private final PlanService planService;
    private final SubscriptionService subscriptionService;
    
    private JComboBox<User> userCombo;
    private JComboBox<Plan> planCombo;
    private JTextField startDateField;
    private JLabel endDateLabel;
    
    public AssignSubscriptionForm() {
        this.userService = new UserService();
        this.planService = new PlanService();
        this.subscriptionService = new SubscriptionService();
        initializeUI();
        loadData();
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
        JLabel titleLabel = new JLabel("Assign Subscription");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        JLabel subtitleLabel = new JLabel("Assign a subscription plan to a user");
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

        // User
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        JLabel userLabel = new JLabel("Select User:");
        userLabel.setFont(UIConstants.FONT_REGULAR);
        userLabel.setForeground(new Color(207, 207, 234));
        formPanel.add(userLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        userCombo = new JComboBox<>();
        userCombo.setPreferredSize(new Dimension(0, 42));
        styleCombo(userCombo);
        userCombo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setForeground(value == null ? new Color(154, 154, 203) : Color.WHITE);
                setBackground(isSelected ? new Color(60, 45, 120) : new Color(35, 25, 100));
                setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
                if (value == null) setText("No users available");
                else if (value instanceof User) setText(((User)value).getName() + " (" + ((User)value).getEmail() + ")");
                return this;
            }
        });
        formPanel.add(userCombo, gbc);

        // Plan
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel planLabel = new JLabel("Select Plan:");
        planLabel.setFont(UIConstants.FONT_REGULAR);
        planLabel.setForeground(new Color(207, 207, 234));
        formPanel.add(planLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        planCombo = new JComboBox<>();
        planCombo.setPreferredSize(new Dimension(0, 42));
        styleCombo(planCombo);
        planCombo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setForeground(Color.WHITE);
                setBackground(isSelected ? new Color(60, 45, 120) : new Color(35, 25, 100));
                setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
                if (value instanceof Plan) {
                    Plan p = (Plan) value;
                    setText(p.getPlanName() + " — $" + p.getPrice() + " / " + p.getBillingCycle());
                }
                return this;
            }
        });
        formPanel.add(planCombo, gbc);

        // Start Date
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel startDateLabel = new JLabel("Start Date:");
        startDateLabel.setFont(UIConstants.FONT_REGULAR);
        startDateLabel.setForeground(new Color(207, 207, 234));
        formPanel.add(startDateLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        startDateField = new GlassField();
        startDateField.setText(LocalDate.now().toString());
        startDateField.setPreferredSize(new Dimension(0, 42));
        startDateField.setToolTipText("Format: YYYY-MM-DD");
        formPanel.add(startDateField, gbc);

        // End Date
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel endLabel = new JLabel("End Date:");
        endLabel.setFont(UIConstants.FONT_REGULAR);
        endLabel.setForeground(new Color(207, 207, 234));
        formPanel.add(endLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        endDateLabel = new JLabel("Calculated automatically from plan duration");
        endDateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        endDateLabel.setForeground(new Color(154, 154, 203));
        formPanel.add(endDateLabel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        JButton assignButton = makeFormButton("Assign Subscription", new Color(100, 70, 220), new Color(70, 40, 180));
        assignButton.setPreferredSize(new Dimension(180, 42));
        assignButton.addActionListener(e -> handleSubmit());
        JButton refreshButton = makeFormButton("Refresh", new Color(255, 255, 255, 18), new Color(255, 255, 255, 8));
        refreshButton.setPreferredSize(new Dimension(110, 42));
        refreshButton.addActionListener(e -> loadData());
        buttonPanel.add(refreshButton);
        buttonPanel.add(assignButton);

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
                btn.setOpaque(false); btn.setContentAreaFilled(false); btn.setBorderPainted(false);
                return btn;
            }
            @Override public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(combo.getBackground());
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });
        combo.setMaximumRowCount(8);
        if (combo.getRenderer() instanceof DefaultListCellRenderer) {
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
        }
        combo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                Object popup = combo.getUI().getAccessibleChild(combo, 0);
                if (popup instanceof javax.swing.JPopupMenu) {
                    javax.swing.JPopupMenu pm = (javax.swing.JPopupMenu) popup;
                    if (pm.getComponent(0) instanceof JScrollPane)
                        ((JScrollPane) pm.getComponent(0)).getViewport().setBackground(new Color(35, 25, 100));
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
                if (getModel().isRollover()) { g2.setColor(new Color(255,255,255,30)); g2.fillRoundRect(0,0,w,h,h,h); }
                g2.setColor(new Color(255,255,255,35)); g2.fillRoundRect(4,2,w-8,h/2,h,h);
                g2.setColor(new Color(255,255,255,60)); g2.setStroke(new BasicStroke(1f)); g2.drawRoundRect(0,0,w-1,h-1,h,h);
                g2.setFont(getFont()); g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),(w-fm.stringWidth(getText()))/2,(h+fm.getAscent()-fm.getDescent())/2);
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
     * Loads users and plans from service layer.
     */
    private void loadData() {
        // Load users (only USER role, not ADMIN)
        userCombo.removeAllItems();
        List<User> allUsers = userService.getAllUsers();
        
        // Filter to show only users with USER role
        for (User user : allUsers) {
            if (user.getRole() != null && user.getRole().equalsIgnoreCase("USER")) {
                userCombo.addItem(user);
            }
        }
        
        // If no users found, show message
        if (userCombo.getItemCount() == 0) {
            // Add a placeholder (will be skipped in validation)
            userCombo.addItem(null);
            userCombo.setEnabled(false);
        } else {
            userCombo.setEnabled(true);
        }
        
        // Load plans
        planCombo.removeAllItems();
        List<Plan> plans = planService.getAllPlans();
        for (Plan plan : plans) {
            planCombo.addItem(plan);
        }
        
        // If no plans found, disable
        if (planCombo.getItemCount() == 0) {
            planCombo.setEnabled(false);
        } else {
            planCombo.setEnabled(true);
        }
    }
    
    /**
     * Handles form submission.
     */
    private void handleSubmit() {
        // Validate selections
        if (userCombo.getSelectedItem() == null) {
            ProfessionalDialog.showWarning(this, "Validation Error", "Please select a user");
            return;
        }
        
        if (planCombo.getSelectedItem() == null) {
            ProfessionalDialog.showWarning(this, "Validation Error", "Please select a plan");
            return;
        }
        
        try {
            // Parse start date
            LocalDate startDate = LocalDate.parse(startDateField.getText().trim());
            
            // Get selected user and plan
            User selectedUser = (User) userCombo.getSelectedItem();
            Plan selectedPlan = (Plan) planCombo.getSelectedItem();
            
            // Call service layer (end date calculated automatically)
            boolean success = subscriptionService.createSubscription(
                selectedUser.getUserId(),
                selectedPlan.getPlanId(),
                startDate
            );
            
            if (success) {
                ProfessionalDialog.showSuccess(this, "Success", "Subscription assigned successfully!");
                startDateField.setText(LocalDate.now().toString());
            } else {
                ProfessionalDialog.showError(this, "Error", "Failed to assign subscription. Please try again.");
            }
            
        } catch (DateTimeParseException ex) {
            ProfessionalDialog.showError(this, "Invalid Date", "Invalid date format. Please use YYYY-MM-DD");
        } catch (Exception ex) {
            ProfessionalDialog.showError(this, "Error", "An error occurred: " + ex.getMessage());
        }
    }
}
