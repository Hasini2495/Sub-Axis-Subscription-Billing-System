package ui;

import service.UserService;
import ui.components.GlassButton;
import ui.components.GlassPanel;
import ui.components.GlassTextField;
import ui.components.GlassSaveDialog;
import ui.components.ModernDialog;
import model.User;
import util.CredentialManager;
import util.CredentialManager.SavedCredentials;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * LoginFrame – dark glass redesign.
 *
 * Left panel  : "Welcome Back !!" hero text.
 * Right panel : glassmorphism login card.
 *
 * ALL backend / authentication logic is preserved verbatim from the
 * original implementation.  Only the UI presentation layer is changed.
 */
public class LoginFrame extends JFrame {

    // ── backend fields (unchanged) ────────────────────────────
    private UserService    userService;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JButton        eyeButton;
    private boolean        passwordVisible = false;
    private GlassButton    loginButton;
    private JLabel         autoFillIndicator;

    // ─────────────────────────────────────────────────────────

    public LoginFrame() {
        this.userService = new UserService();
        initializeUI();
        loadSavedCredentials();
    }

    // ═══════════════════════════════════════════════════════════
    //  UI CONSTRUCTION
    // ═══════════════════════════════════════════════════════════

    private void initializeUI() {
        setTitle("SaaS Subscription Platform \u2013 Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(920, 620));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // ── Background panel with purple / indigo radial blobs ──
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);

                // Solid dark base  (#0E0E0E)
                g2.setColor(new Color(0x0E, 0x0E, 0x0E));
                g2.fillRect(0, 0, getWidth(), getHeight());

                int blobR = (int) (Math.min(getWidth(), getHeight()) * 0.60);

                // Top-left purple blob
                RadialGradientPaint blob1 = new RadialGradientPaint(
                        new Point2D.Float(getWidth() * 0.20f, getHeight() * 0.25f),
                        blobR,
                        new float[]{0f, 1f},
                        new Color[]{new Color(100, 28, 190, 85),
                                    new Color(14, 14, 14, 0)}
                );
                g2.setPaint(blob1);
                g2.fillOval(
                        (int)(getWidth()  * 0.20f) - blobR,
                        (int)(getHeight() * 0.25f) - blobR,
                        blobR * 2, blobR * 2);

                // Bottom-right indigo blob
                int blobR2 = (int)(blobR * 0.68);
                RadialGradientPaint blob2 = new RadialGradientPaint(
                        new Point2D.Float(getWidth() * 0.81f, getHeight() * 0.80f),
                        blobR2,
                        new float[]{0f, 1f},
                        new Color[]{new Color(74, 108, 247, 65),
                                    new Color(14, 14, 14, 0)}
                );
                g2.setPaint(blob2);
                g2.fillOval(
                        (int)(getWidth()  * 0.81f) - blobR2,
                        (int)(getHeight() * 0.80f) - blobR2,
                        blobR2 * 2, blobR2 * 2);

                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);

        // ── Grid: left 55 % hero / right 45 % card ──
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy   = 0;
        gbc.weighty = 1.0;
        gbc.fill    = GridBagConstraints.BOTH;

        gbc.gridx   = 0;
        gbc.weightx = 0.55;
        mainPanel.add(buildLeftPanel(), gbc);

        gbc.gridx   = 1;
        gbc.weightx = 0.45;
        mainPanel.add(buildRightPanel(), gbc);

        setContentPane(mainPanel);
    }

    // ── Left hero panel ───────────────────────────────────────

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        JLabel hero = new JLabel("<html><div>"
                + "Welcome<br>Back !!"
                + "</div></html>");
        hero.setFont(new Font("Segoe UI", Font.BOLD, 72));
        hero.setForeground(Color.WHITE);

        panel.add(hero);
        return panel;
    }

    // ── Right panel (centres the glass card vertically) ───────

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(buildLoginCard());
        return panel;
    }

    // ── Glass login card ──────────────────────────────────────

    private GlassPanel buildLoginCard() {
        GlassPanel card = new GlassPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(44, 44, 36, 44));
        card.setPreferredSize(new Dimension(420, 630));
        card.setMaximumSize (new Dimension(420, 630));
        card.setCornerRadius(30);

        // headings
        JLabel titleLbl = makeLabel("Login", 28, Font.BOLD, Color.WHITE);
        JLabel subLbl   = makeLabel("Glad you\u2019re back.!", 14, Font.PLAIN,
                new Color(175, 175, 200));

        // auto-fill indicator (hidden until credentials are loaded)
        autoFillIndicator = makeLabel("Credentials auto-filled", 11,
                Font.ITALIC, new Color(100, 210, 130));
        autoFillIndicator.setVisible(false);

        // input fields
        emailField = new GlassTextField("Username");
        emailField.setAlignmentX(LEFT_ALIGNMENT);
        emailField.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));

        JPanel pwRow = buildPasswordRow();
        pwRow.setAlignmentX(LEFT_ALIGNMENT);
        pwRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));

        // remember me
        JCheckBox remCb = buildRememberMeCheckbox();
        remCb.setAlignmentX(LEFT_ALIGNMENT);

        // login button  (blue #4A6CF7 → purple #7B2FF7)
        loginButton = new GlassButton("Login");
        loginButton.setGradientColors(new Color(0x4A, 0x6C, 0xF7),
                                      new Color(0x7B, 0x2F, 0xF7));
        loginButton.setAlignmentX(LEFT_ALIGNMENT);
        loginButton.setMaximumSize  (new Dimension(Short.MAX_VALUE, 48));
        loginButton.setPreferredSize(new Dimension(332, 48));
        loginButton.addActionListener(e -> handleLogin());
        getRootPane().setDefaultButton(loginButton);

        // signup row ("Don't have an account? Signup")
        JPanel signupRow = buildSignupRow();
        signupRow.setAlignmentX(LEFT_ALIGNMENT);

        // bottom links
        JPanel bottomLinks = buildBottomLinks();
        bottomLinks.setAlignmentX(LEFT_ALIGNMENT);

        // Enter-key support
        KeyAdapter enter = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        };
        emailField.addKeyListener(enter);
        passwordField.addKeyListener(enter);

        // assemble
        card.add(titleLbl);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(subLbl);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(autoFillIndicator);
        card.add(Box.createRigidArea(new Dimension(0, 24)));
        card.add(emailField);
        card.add(Box.createRigidArea(new Dimension(0, 14)));
        card.add(pwRow);
        card.add(Box.createRigidArea(new Dimension(0, 14)));
        card.add(remCb);
        card.add(Box.createRigidArea(new Dimension(0, 22)));
        card.add(loginButton);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(signupRow);
        card.add(Box.createVerticalGlue());
        card.add(bottomLinks);

        return card;
    }

    // ── Password row (glass container + eye toggle) ───────────

    private JPanel buildPasswordRow() {
        // The row paints the glass frame; the inner JPasswordField is
        // transparent so there is no double-border artefact.
        final boolean[] focused = {false};

        JPanel row = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                // glass background
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 20, 20));
                // border (purple glow on focus)
                g2.setColor(focused[0]
                        ? new Color(130, 80, 255, 200)
                        : new Color(255, 255, 255,  50));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0.75f, 0.75f, w - 1.5f, h - 1.5f, 20, 20));
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) { /* none */ }
        };
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(332, 42));
        row.setMaximumSize  (new Dimension(Short.MAX_VALUE, 42));

        // Plain JPasswordField — transparent, no Swing border
        passwordField = new JPasswordField() {
            @Override protected void paintBorder(Graphics g) { /* none */ }
        };
        passwordField.setOpaque(false);
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(new Color(180, 150, 255));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(new EmptyBorder(10, 18, 10, 4));
        passwordField.setEchoChar('\u25CF');   // ●
        passwordField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { focused[0] = true;  row.repaint(); }
            @Override public void focusLost (FocusEvent e) { focused[0] = false; row.repaint(); }
        });

        // Eye toggle button
        eyeButton = new JButton("\uD83D\uDC41") {
            @Override protected void paintBorder(Graphics g) { /* none */ }
        };
        eyeButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        eyeButton.setForeground(new Color(180, 155, 225));
        eyeButton.setOpaque(false);
        eyeButton.setContentAreaFilled(false);
        eyeButton.setBorderPainted(false);
        eyeButton.setFocusPainted(false);
        eyeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eyeButton.setPreferredSize(new Dimension(42, 42));
        eyeButton.addActionListener(e -> togglePasswordVisibility());

        row.add(passwordField, BorderLayout.CENTER);
        row.add(eyeButton,     BorderLayout.EAST);
        return row;
    }

    // ── Remember-me checkbox ──────────────────────────────────

    private JCheckBox buildRememberMeCheckbox() {
        JCheckBox cb = new JCheckBox("Remember me");
        cb.setOpaque(false);
        cb.setForeground(new Color(195, 195, 215));
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setFocusPainted(false);
        cb.setSelected(true);
        cb.setIcon(cbIcon(false));
        cb.setSelectedIcon(cbIcon(true));
        return cb;
    }

    /** Draws a small rounded checkbox icon with a purple accent when selected. */
    private Icon cbIcon(final boolean selected) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int s = 14;
                g2.setColor(selected ? new Color(115, 75, 250) : new Color(255, 255, 255, 38));
                g2.fillRoundRect(x, y, s, s, 4, 4);
                g2.setColor(new Color(255, 255, 255, 90));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(x, y, s, s, 4, 4);
                if (selected) {
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x + 3, y + 7,  x + 6,  y + 11);
                    g2.drawLine(x + 6, y + 11, x + 11, y + 3);
                }
                g2.dispose();
            }
            @Override public int getIconWidth()  { return 16; }
            @Override public int getIconHeight() { return 16; }
        };
    }

    // ── Signup row ────────────────────────────────────────────

    private JPanel buildSignupRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        row.setOpaque(false);

        JLabel text = makeLabel("Don\u2019t have an account?", 13, Font.PLAIN,
                new Color(175, 175, 200));

        JButton signupBtn = new JButton("Signup");
        signupBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signupBtn.setForeground(new Color(155, 115, 255));
        signupBtn.setOpaque(false);
        signupBtn.setContentAreaFilled(false);
        signupBtn.setBorderPainted(false);
        signupBtn.setFocusPainted(false);
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupBtn.setBorder(new EmptyBorder(0, 2, 0, 2));
        signupBtn.addActionListener(e -> handleRegister());
        signupBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                signupBtn.setText("<html><u>Signup</u></html>");
            }
            @Override public void mouseExited(MouseEvent e) {
                signupBtn.setText("Signup");
            }
        });

        row.add(text);
        row.add(signupBtn);
        return row;
    }

    // ── Bottom links ──────────────────────────────────────────

    private JPanel buildBottomLinks() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        row.setOpaque(false);
        for (String label : new String[]{"Terms \u0026 Conditions", "Support", "Customer Care"}) {
            row.add(makeLinkBtn(label));
        }
        return row;
    }

    private JButton makeLinkBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setForeground(new Color(145, 145, 175));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(2, 4, 2, 4));
        final String plain = text;
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setText("<html><u>" + plain + "</u></html>");
                btn.setForeground(new Color(175, 145, 255));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setText(plain);
                btn.setForeground(new Color(145, 145, 175));
            }
        });
        return btn;
    }

    // ── helper ────────────────────────────────────────────────

    private JLabel makeLabel(String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", style, size));
        lbl.setForeground(color);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    // ═══════════════════════════════════════════════════════════
    //  BACKEND LOGIC  —  PRESERVED VERBATIM
    // ═══════════════════════════════════════════════════════════

    /** Toggles password visibility with eye icon. */
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            passwordField.setEchoChar((char) 0);    // show
            eyeButton.setText("\uD83D\uDE48");       // 🙈
        } else {
            passwordField.setEchoChar('\u25CF');     // ● hide
            eyeButton.setText("\uD83D\uDC41");       // 👁
        }
    }

    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            ModernDialog.showWarning(this, "Validation Error",
                    "Please enter both email and password.");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");

        // Demo authentication with hardcoded credentials
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {

            @Override
            protected User doInBackground() throws Exception {
                // Admin authentication
                if (email.equals("admin@company.com") && password.equals("admin123")) {
                    User admin = new User();
                    admin.setUserId(999);
                    admin.setName("Administrator");
                    admin.setEmail(email);
                    admin.setRole("ADMIN");
                    admin.setStatus("ACTIVE");
                    return admin;
                }

                // Regular user – try database first, then demo fallback
                if (email.equals("john.doe@example.com") && password.equals("user123")) {
                    try {
                        User dbUser = userService.getUserByEmail(email);
                        if (dbUser != null && "ACTIVE".equals(dbUser.getStatus())) {
                            return dbUser;
                        }
                    } catch (Exception e) {
                        System.out.println("Database lookup failed, using demo user: " + e.getMessage());
                    }
                    User demoUser = new User();
                    demoUser.setUserId(1);
                    demoUser.setName("John Doe");
                    demoUser.setEmail(email);
                    demoUser.setRole("USER");
                    demoUser.setStatus("ACTIVE");
                    return demoUser;
                }

                // Additional demo users
                if (email.equals("jane.smith@example.com") && password.equals("user123")) {
                    User user = new User();
                    user.setUserId(2);
                    user.setName("Jane Smith");
                    user.setEmail(email);
                    user.setRole("USER");
                    user.setStatus("ACTIVE");
                    return user;
                }

                return null;
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null && "ACTIVE".equals(user.getStatus())) {
                        // Prompt to save credentials
                        promptSaveCredentials(email, password);

                        if (user.getUserId() == 999 || "ADMIN".equals(user.getRole())) {
                            openAdminDashboard(user);
                        } else {
                            openUserDashboard(user);
                        }
                        dispose();
                    } else {
                        ModernDialog.showError(LoginFrame.this, "Login Failed",
                                "Invalid email or password.\nPlease try again.");
                        passwordField.setText("");
                    }
                } catch (Exception ex) {
                    ModernDialog.showError(LoginFrame.this, "Error",
                            "An error occurred during login:\n" + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            }
        };
        worker.execute();
    }

    private void handleRegister() {
        RegisterDialog registerDialog = new RegisterDialog(this);
        registerDialog.setVisible(true);

        if (registerDialog.isRegistrationSuccessful()) {
            emailField.setText("");
            passwordField.setText("");
            JOptionPane.showMessageDialog(this,
                    "Registration successful! Please login with your credentials.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openAdminDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            AdminDashboard dashboard = new AdminDashboard();
            dashboard.setVisible(true);
        });
    }

    private void openUserDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            UserDashboard_V2 dashboard = new UserDashboard_V2(user);
            dashboard.setVisible(true);
        });
    }

    /**
     * Loads saved credentials and auto-fills the login form.
     */
    private void loadSavedCredentials() {
        SavedCredentials credentials = CredentialManager.loadCredentials();

        if (credentials != null) {
            emailField.setText(credentials.getEmail());
            passwordField.setText(credentials.getPassword());

            if (autoFillIndicator != null) {
                autoFillIndicator.setVisible(true);
            }
        }
    }

    /**
     * Prompts user to save credentials after successful login.
     * Uses GlassSaveDialog for the confirmation; all credential-storage
     * logic is unchanged.
     */
    private void promptSaveCredentials(String email, String password) {
        // If credentials match what is already saved, skip the prompt
        SavedCredentials existing = CredentialManager.loadCredentials();
        if (existing != null
                && existing.getEmail().equals(email)
                && existing.getPassword().equals(password)) {
            return;
        }

        // Glass-themed dialog (replaces the old JOptionPane.showConfirmDialog)
        GlassSaveDialog dlg = new GlassSaveDialog(this);
        dlg.setVisible(true);   // modal — blocks until closed

        if (dlg.isConfirmed()) {
            boolean saved = CredentialManager.saveCredentials(email, password);

            if (saved) {
                GlassSaveDialog.showMessage(this, "Credentials saved!",
                        "They will be auto-filled next time.", false);
            } else {
                GlassSaveDialog.showMessage(this, "Save Failed",
                        "Could not save credentials. Please try again later.", true);
            }
        }
    }

    // ── stand-alone entry-point ───────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
