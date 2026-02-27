package ui;

import model.User;
import service.UserService;
import ui.components.StyledButton;
import ui.components.ProfessionalDialog;
import ui.theme.UIConstants;
import util.PasswordUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Professional form panel for registering new users.
 * Centered layout with max-width, modern styling.
 */
public class RegisterUserForm extends JPanel {
    private final UserService userService;
    
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleCombo;
    private JComboBox<String> statusCombo;
    
    public RegisterUserForm() {
        this.userService = new UserService();
        initializeUI();
    }
    
    // ── Rounded glass password field ────────────────────────────────────────
    private static class GlassPasswordField extends JPasswordField {
        GlassPasswordField() {
            setOpaque(false);
            setForeground(Color.WHITE);
            setCaretColor(new Color(255, 140, 66));
            setBorder(new EmptyBorder(0, 14, 0, 14));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setEchoChar('●');
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(255, 255, 255, 18));
            g2.fillRoundRect(0, 0, w, h, 16, 16);
            g2.setColor(new Color(120, 80, 255, 15));
            g2.fillRoundRect(0, 0, w, h, 16, 16);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Rounded glass text field ────────────────────────────────────────────
    private static class GlassField extends JTextField {
        GlassField() {
            setOpaque(false);
            setForeground(Color.WHITE);
            setCaretColor(new Color(255, 140, 66));
            setBorder(new EmptyBorder(0, 14, 0, 14));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            // Dark glass fill
            g2.setColor(new Color(255, 255, 255, 18));
            g2.fillRoundRect(0, 0, w, h, 16, 16);
            // Purple tint
            g2.setColor(new Color(120, 80, 255, 15));
            g2.fillRoundRect(0, 0, w, h, 16, 16);
            // Border
            g2.setColor(new Color(255, 255, 255, 60));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Glass combo box — bypasses Windows L&F, fully custom painted ─────────
    private static void styleCombo(JComboBox<String> combo) {

        // Force BasicComboBoxUI so Windows L&F cannot override our colors
        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {

            // Custom arrow button: draws a crisp down-triangle via Graphics2D
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    public Dimension getPreferredSize() { return new Dimension(30, 0); }
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                           RenderingHints.VALUE_ANTIALIAS_ON);
                        int w = getWidth(), h = getHeight();
                        // Tinted background for the arrow column
                        g2.setColor(new Color(255, 255, 255, 18));
                        g2.fillRect(0, 0, w, h);
                        // Soft left-edge separator
                        g2.setColor(new Color(255, 255, 255, 50));
                        g2.drawLine(0, 4, 0, h - 4);
                        // Down-pointing filled triangle, centred
                        int ax = w / 2, ay = h / 2;
                        int tw = 9, th = 5;
                        int[] xs = { ax - tw/2, ax + tw/2, ax };
                        int[] ys = { ay - th/2, ay - th/2, ay + th/2 };
                        g2.setColor(new Color(207, 207, 234));
                        g2.fillPolygon(xs, ys, 3);
                        g2.dispose();
                    }
                };
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setOpaque(false);
                return btn;
            }

            // Paint the glass background for the whole combo button area
            @Override
            public void paintCurrentValueBackground(Graphics g, java.awt.Rectangle bounds,
                                                    boolean hasFocus) {
                // intentionally empty — the combo's own paintComponent handles it
            }

            // Paint selected text clearly in white
            @Override
            public void paintCurrentValue(Graphics g, java.awt.Rectangle bounds,
                                          boolean hasFocus) {
                javax.swing.ListCellRenderer<Object> renderer = comboBox.getRenderer();
                Component c = renderer.getListCellRendererComponent(
                        listBox, comboBox.getSelectedItem(), -1, false, false);
                c.setFont(comboBox.getFont());
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                   RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                c.setBounds(bounds);
                c.paint(g2);
                g2.dispose();
            }
        });

        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(new Color(35, 25, 78)); // dark indigo tint
        combo.setForeground(Color.WHITE);
        combo.setOpaque(true);
        combo.setMaximumRowCount(8);

        // Glass border — matches GlassField style
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 65), 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 6)
        ));

        // ── Renderer: dark glass popup rows, clearly visible white text ───────
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focused) {
                super.getListCellRendererComponent(list, value, index, selected, focused);

                // Force popup list background to dark indigo
                list.setBackground(new Color(35, 25, 78));
                list.setForeground(Color.WHITE);
                list.setSelectionBackground(new Color(120, 80, 255));
                list.setSelectionForeground(Color.WHITE);

                setBackground(selected ? new Color(100, 65, 200) : new Color(35, 25, 78));
                setForeground(Color.WHITE);
                setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                return this;
            }
        });

        // ── Popup list: make sure the JScrollPane/border are also dark ────────
        combo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                // Grab internal popup and colour it
                javax.accessibility.Accessible popupComp = combo.getUI().getAccessibleChild(combo, 0);
                if (popupComp instanceof javax.swing.JPopupMenu) {
                    javax.swing.JPopupMenu pm = (javax.swing.JPopupMenu) popupComp;
                    pm.setBackground(new Color(35, 25, 78));
                    pm.setBorder(BorderFactory.createLineBorder(
                        new Color(255, 255, 255, 60), 1));
                    for (java.awt.Component child : pm.getComponents()) {
                        child.setBackground(new Color(35, 25, 78));
                        if (child instanceof javax.swing.JScrollPane) {
                            javax.swing.JScrollPane sp = (javax.swing.JScrollPane) child;
                            sp.setBorder(BorderFactory.createEmptyBorder());
                            sp.getViewport().setBackground(new Color(35, 25, 78));
                        }
                    }
                }
            }
            @Override public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });
    }

    /**
     * Initializes the glassmorphism UI. All business logic is unchanged.
     */
    private void initializeUI() {
        setLayout(new GridBagLayout());
        setOpaque(true); // we paint our own dark gradient

        // Main container
        JPanel container = new JPanel(new BorderLayout(20, 20));
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        container.setPreferredSize(new Dimension(520, 820));

        // ── Title section ─────────────────────────────────────────────────────
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Register New User");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Add a new user to the system");
        subtitleLabel.setFont(UIConstants.FONT_REGULAR);
        subtitleLabel.setForeground(new Color(207, 207, 234));
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        // ── Glass form card ───────────────────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w, h, 25, 25);
                // Semi-transparent white base
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fill(shape);
                // Purple tint overlay
                g2.setColor(new Color(120, 80, 255, 18));
                g2.fill(shape);
                // Glass border
                g2.setColor(new Color(255, 255, 255, 55));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(shape);
                g2.dispose();
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(UIConstants.FONT_REGULAR);
        nameLabel.setForeground(new Color(207, 207, 234));
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        nameField = new GlassField();
        nameField.setPreferredSize(new Dimension(0, 42));
        formPanel.add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(UIConstants.FONT_REGULAR);
        emailLabel.setForeground(new Color(207, 207, 234));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        emailField = new GlassField();
        emailField.setPreferredSize(new Dimension(0, 42));
        formPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(UIConstants.FONT_REGULAR);
        passwordLabel.setForeground(new Color(207, 207, 234));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        JPanel pwWrapper = new JPanel(new BorderLayout(0, 0));
        pwWrapper.setOpaque(false);
        pwWrapper.setPreferredSize(new Dimension(0, 42));
        passwordField = new GlassPasswordField();
        JButton pwToggle = makeEyeButton(passwordField);
        pwWrapper.add(passwordField, BorderLayout.CENTER);
        pwWrapper.add(pwToggle, BorderLayout.EAST);
        formPanel.add(pwWrapper, gbc);

        // Hint row
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 10, 8, 10);
        JLabel hintLabel = new JLabel("\u2139 Min 6 characters. Letters, numbers & symbols (!@#$%^&*) are all allowed.");
        hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hintLabel.setForeground(new Color(154, 154, 203));
        formPanel.add(hintLabel, gbc);
        gbc.insets = new Insets(10, 10, 10, 10); // reset

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(UIConstants.FONT_REGULAR);
        confirmLabel.setForeground(new Color(207, 207, 234));
        formPanel.add(confirmLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        JPanel cpWrapper = new JPanel(new BorderLayout(0, 0));
        cpWrapper.setOpaque(false);
        cpWrapper.setPreferredSize(new Dimension(0, 42));
        confirmPasswordField = new GlassPasswordField();
        JButton cpToggle = makeEyeButton(confirmPasswordField);
        cpWrapper.add(confirmPasswordField, BorderLayout.CENTER);
        cpWrapper.add(cpToggle, BorderLayout.EAST);
        formPanel.add(cpWrapper, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(UIConstants.FONT_REGULAR);
        roleLabel.setForeground(new Color(207, 207, 234));
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        String[] roles = {"USER", "ADMIN"};
        roleCombo = new JComboBox<>(roles);
        styleCombo(roleCombo);
        roleCombo.setPreferredSize(new Dimension(0, 42));
        formPanel.add(roleCombo, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(UIConstants.FONT_REGULAR);
        statusLabel.setForeground(new Color(207, 207, 234));
        formPanel.add(statusLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        String[] statuses = {"ACTIVE", "INACTIVE"};
        statusCombo = new JComboBox<>(statuses);
        styleCombo(statusCombo);
        statusCombo.setPreferredSize(new Dimension(0, 42));
        formPanel.add(statusCombo, gbc);

        // ── Button panel ──────────────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);

        JButton registerButton = makeFormButton("Register User",
                new Color(50, 160, 90), new Color(34, 120, 65));
        registerButton.setPreferredSize(new Dimension(150, 42));
        registerButton.addActionListener(e -> handleSubmit());

        JButton clearButton = makeFormButton("Clear",
                new Color(255, 255, 255, 18), new Color(255, 255, 255, 8));
        clearButton.setPreferredSize(new Dimension(100, 42));
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(clearButton);
        buttonPanel.add(registerButton);

        container.add(titlePanel, BorderLayout.NORTH);
        container.add(formPanel, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        GridBagConstraints containerGbc = new GridBagConstraints();
        containerGbc.fill = GridBagConstraints.VERTICAL;
        containerGbc.weightx = 0.0;
        containerGbc.weighty = 1.0;
        containerGbc.anchor = GridBagConstraints.CENTER;
        containerGbc.insets = new java.awt.Insets(20, 0, 20, 0);
        add(container, containerGbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth(), h = getHeight();
        // Deep indigo gradient: #1A1B3A → #2B1F5E → #3A2C6D
        g2d.setPaint(new LinearGradientPaint(0, 0, 0, h,
            new float[]{0f, 0.5f, 1f},
            new Color[]{new Color(26, 27, 58), new Color(43, 31, 94), new Color(58, 44, 109)}));
        g2d.fillRect(0, 0, w, h);
        g2d.dispose();
        // paint children — do NOT call super (it would fill with opaque background color)
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
            // Create user object
            User user = new User();
            user.setName(nameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setPassword(PasswordUtil.hashPassword(new String(passwordField.getPassword())));
            user.setRole((String) roleCombo.getSelectedItem());
            user.setStatus((String) statusCombo.getSelectedItem());

            // Call service layer
            boolean success = userService.createUser(user);

            if (success) {
                String roleLabel = "ADMIN".equalsIgnoreCase(user.getRole()) ? "Admin" : "User";
                ProfessionalDialog.showSuccess(this, "Success",
                    roleLabel + " '" + user.getName() + "' registered successfully!");
                clearForm();
            } else {
                ProfessionalDialog.showError(this, "Error", "Failed to register user. Please try again.");
            }

        } catch (IllegalArgumentException ex) {
            ProfessionalDialog.showError(this, "Validation Error", ex.getMessage());
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg != null && (msg.toLowerCase().contains("duplicate") ||
                                msg.toLowerCase().contains("unique"))) {
                ProfessionalDialog.showError(this, "Email Already Exists",
                    "The email '" + emailField.getText().trim() + "' is already registered.\n"
                    + "Please use a different email address.");
                emailField.requestFocus();
                emailField.selectAll();
            } else {
                ProfessionalDialog.showError(this, "Error", "An error occurred: " + msg);
            }
        }
    }
    
    /**
     * Validates form inputs.
     * @return true if all inputs are valid
     */
    private boolean validateInputs() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        
        if (name.isEmpty()) {
            ProfessionalDialog.showWarning(this, "Validation Error", "Name cannot be empty");
            nameField.requestFocus();
            return false;
        }
        
        if (email.isEmpty()) {
            ProfessionalDialog.showWarning(this, "Validation Error", "Email cannot be empty");
            emailField.requestFocus();
            return false;
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            ProfessionalDialog.showWarning(this, "Validation Error", "Invalid email address");
            emailField.requestFocus();
            return false;
        }

        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmPasswordField.getPassword());

        if (password.isEmpty()) {
            ProfessionalDialog.showWarning(this, "Validation Error", "Password cannot be empty");
            passwordField.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            ProfessionalDialog.showWarning(this, "Validation Error", "Password must be at least 6 characters");
            passwordField.requestFocus();
            return false;
        }

        if (!password.equals(confirm)) {
            ProfessionalDialog.showWarning(this, "Validation Error", "Passwords do not match");
            confirmPasswordField.requestFocus();
            return false;
        }

        return true;
    }
    
    /**
     * Creates a glass pill-style button for the form's action area.
     */
    private JButton makeFormButton(String text, Color c1, Color c2) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                // glass fill
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, h, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, h, h);
                // hover shimmer
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRoundRect(0, 0, w, h, h, h);
                }
                // glass top shine
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRoundRect(4, 2, w - 8, h / 2, h, h);
                // border
                g2.setColor(new Color(255, 255, 255, 60));
                g2.setStroke(new java.awt.BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);
                // text
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (w - fm.stringWidth(getText())) / 2,
                        (h + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Creates a small eye-toggle button that shows/hides the text in a JPasswordField.
     */
    private JButton makeEyeButton(JPasswordField field) {
        // Use a single-element array so the lambda can read/write it
        boolean[] showing = {false};

        JButton btn = new JButton() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                // translucent glass background
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                // eye icon drawing
                g2.setColor(new Color(207, 207, 234, 200));
                g2.setStroke(new java.awt.BasicStroke(1.5f));
                int cx = w / 2, cy = h / 2;
                // outer eye shape
                g2.drawOval(cx - 7, cy - 4, 14, 8);
                if (showing[0]) {
                    // open eye: filled pupil
                    g2.fillOval(cx - 3, cy - 3, 6, 6);
                } else {
                    // closed eye: pupil + diagonal slash
                    g2.drawOval(cx - 3, cy - 3, 6, 6);
                    g2.drawLine(cx - 8, cy + 5, cx + 8, cy - 5);
                }
                g2.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(38, 42));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            showing[0] = !showing[0];
            field.setEchoChar(showing[0] ? (char) 0 : '\u25CF');
            btn.repaint();
        });
        return btn;
    }

    /**
     * Clears all form fields.
     */
    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        roleCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        nameField.requestFocus();
    }
}
