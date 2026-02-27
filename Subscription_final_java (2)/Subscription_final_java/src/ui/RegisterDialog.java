package ui;

import model.User;
import service.UserService;
import ui.components.GlassButton;
import ui.components.GlassSaveDialog;
import util.PasswordUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.regex.Pattern;

/**
 * RegisterDialog – pixel-precise dark glass Sign Up dialog.
 *
 * Layout contract
 *   • Dialog width  : 500 px fixed
 *   • Inner padding : 40 px left/right, 30 px top, 30 px bottom
 *   • Input fields  : full content width, 42 px tall, 20 px radius
 *   • Label → field : 6 px
 *   • Field → label : 20 px
 *   • Buttons       : 45 px, pill, horizontally centred, 25 px gap
 *
 * All validation / registration back-end logic is preserved verbatim.
 */
public class RegisterDialog extends JDialog {

    // ── validation constant (unchanged) ──────────────────────
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    // ── FIELD WIDTH: dialog(500) - padding(40*2) = 420 ───────
    private static final int FIELD_H   = 42;
    private static final int FIELD_W   = 420;   // fills content area

    // ── form fields (unchanged) ───────────────────────────────
    private JTextField     nameField;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField     phoneField;
    private JCheckBox      termsCheckbox;

    // ── error labels (unchanged) ──────────────────────────────
    private JLabel nameError;
    private JLabel emailError;
    private JLabel passwordError;
    private JLabel confirmPasswordError;
    private JLabel phoneError;

    // ── backend (unchanged) ───────────────────────────────────
    private UserService userService;
    private boolean     registrationSuccessful = false;
    private boolean     passwordVisible        = false;

    // ── palette ───────────────────────────────────────────────
    private static final Color C_BG     = new Color(0x0E, 0x0E, 0x12);
    private static final Color C_GLASS  = new Color(255, 255, 255, 13);   // 0.05 α
    private static final Color C_BORDER = new Color(255, 255, 255, 38);   // 0.15 α
    private static final Color C_WHITE  = Color.WHITE;
    private static final Color C_SUB    = new Color(165, 165, 190);
    private static final Color C_ERROR  = new Color(255, 85,  85);
    private static final Color C_ACCENT = new Color(150, 110, 255);

    // ─────────────────────────────────────────────────────────

    public RegisterDialog(Frame parent) {
        super(parent, "Create Account", true);
        this.userService = new UserService();
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        buildUI();
        setSize(500, computeHeight());
        setLocationRelativeTo(parent);

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /** Let the pack() equivalent happen from a fixed height instead. */
    private int computeHeight() { return 700; }

    // ═══════════════════════════════════════════════════════════
    //  ROOT PANEL
    // ═══════════════════════════════════════════════════════════

    private void buildUI() {
        // Glass card root
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,    RenderingHints.VALUE_RENDER_QUALITY);

                int w = getWidth(), h = getHeight();

                // dark fill
                g2.setColor(C_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 30, 30));

                // glass tint
                g2.setColor(C_GLASS);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 30, 30));

                // purple glow blob – top-right
                int blobR = (int)(w * 0.65);
                RadialGradientPaint glow = new RadialGradientPaint(
                        new Point2D.Float(w * 0.80f, h * 0.15f), blobR,
                        new float[]{0f, 1f},
                        new Color[]{new Color(110, 35, 200, 55), new Color(14, 14, 18, 0)});
                g2.setPaint(glow);
                g2.fillOval((int)(w * 0.80f) - blobR, (int)(h * 0.15f) - blobR, blobR * 2, blobR * 2);

                // border  rgba(255,255,255,0.15)
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, 30, 30));

                g2.dispose();
            }
        };
        root.setOpaque(false);

        root.add(buildTitleBar(),  BorderLayout.NORTH);
        root.add(buildScrollPane(), BorderLayout.CENTER);
        root.add(buildButtonRow(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ═══════════════════════════════════════════════════════════
    //  TITLE BAR  (drag + X)
    // ═══════════════════════════════════════════════════════════

    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(22, 40, 0, 30));

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 19));
        title.setForeground(C_WHITE);

        JButton closeBtn = makeCloseBtn();

        // drag-to-move
        MouseAdapter drag = new MouseAdapter() {
            Point start;
            @Override public void mousePressed (MouseEvent e) { start = e.getLocationOnScreen(); }
            @Override public void mouseDragged (MouseEvent e) {
                if (start == null) return;
                Point now = e.getLocationOnScreen();
                RegisterDialog.this.setLocation(
                        getX() + now.x - start.x,
                        getY() + now.y - start.y);
                start = now;
            }
        };
        bar.addMouseListener(drag);
        bar.addMouseMotionListener(drag);

        bar.add(title,    BorderLayout.WEST);
        bar.add(closeBtn, BorderLayout.EAST);
        return bar;
    }

    private JButton makeCloseBtn() {
        JButton btn = new JButton("\u00D7") {  // ×
            @Override protected void paintBorder(Graphics g) {}
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        btn.setForeground(new Color(160, 160, 185));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 8, 0, 0));
        btn.addActionListener(e -> dispose());
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(C_WHITE); }
            @Override public void mouseExited (MouseEvent e) { btn.setForeground(new Color(160, 160, 185)); }
        });
        return btn;
    }

    // ═══════════════════════════════════════════════════════════
    //  SCROLL PANE  (wraps the form)
    // ═══════════════════════════════════════════════════════════

    private JScrollPane buildScrollPane() {
        JPanel form = buildFormPanel();

        JScrollPane sp = new JScrollPane(form);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy  (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    // ═══════════════════════════════════════════════════════════
    //  FORM PANEL
    // ═══════════════════════════════════════════════════════════

    private JPanel buildFormPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        // top, left, bottom, right  — 30 top, 40 H, 10 bottom (button row has own padding)
        p.setBorder(new EmptyBorder(18, 40, 10, 40));

        // ── subtitle ──
        JLabel sub = label("Join SubAxis to manage your subscriptions", 13, Font.PLAIN, C_SUB);
        p.add(sub); gap(p, 22);

        // ── Full Name ──
        nameField  = glassField("Full Name");
        nameError  = errorLbl();
        p.add(fieldGroup("Full Name *", nameField, nameError));  gap(p, 20);

        // ── Email ──
        emailField = glassField("Email Address");
        emailError = errorLbl();
        p.add(fieldGroup("Email Address *", emailField, emailError)); gap(p, 20);

        // ── Password ──
        passwordField = new JPasswordField();
        JPanel pwPanel = glassPasswordPanel(passwordField, true);
        passwordError = errorLbl();
        p.add(fieldGroup("Password *", pwPanel, passwordError));

        JLabel hint = label("Must contain 8+ characters, 1 uppercase, 1 lowercase, 1 number",
                11, Font.PLAIN, new Color(130, 130, 160));
        hint.setAlignmentX(LEFT_ALIGNMENT);
        p.add(hint);  gap(p, 20);

        // ── Confirm Password ──
        confirmPasswordField = new JPasswordField();
        JPanel cpPanel = glassPasswordPanel(confirmPasswordField, false);
        confirmPasswordError = errorLbl();
        p.add(fieldGroup("Confirm Password *", cpPanel, confirmPasswordError)); gap(p, 20);

        // ── Phone ──
        phoneField = glassField("Phone (optional)");
        phoneError = errorLbl();
        p.add(fieldGroup("Phone Number (optional)", phoneField, phoneError)); gap(p, 22);

        // ── Terms ──
        termsCheckbox = buildTermsCheckbox();
        termsCheckbox.setAlignmentX(LEFT_ALIGNMENT);
        p.add(termsCheckbox);

        return p;
    }

    // ═══════════════════════════════════════════════════════════
    //  GLASS INPUT COMPONENTS
    // ═══════════════════════════════════════════════════════════

    /** Rounded glass text field with placeholder. */
    private JTextField glassField(String placeholder) {
        final boolean[] focused = {false};

        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                // glass bg
                g2.setColor(new Color(255, 255, 255, 13));
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 20, 20));
                // border
                g2.setColor(focused[0]
                        ? new Color(130, 80, 255, 200)
                        : new Color(255, 255, 255, 50));
                g2.setStroke(new BasicStroke(1.4f));
                g2.draw(new RoundRectangle2D.Float(0.7f, 0.7f, w - 1.4f, h - 1.4f, 20, 20));
                g2.dispose();
                super.paintComponent(g);
                // placeholder
                if (getText().isEmpty()) {
                    Graphics2D gp = (Graphics2D) g.create();
                    gp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    gp.setColor(new Color(255, 255, 255, 75));
                    gp.setFont(getFont());
                    FontMetrics fm = gp.getFontMetrics();
                    Insets ins = getInsets();
                    gp.drawString(placeholder, ins.left, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    gp.dispose();
                }
            }
            @Override protected void paintBorder(Graphics g) { /* none */ }
        };
        f.setOpaque(false);
        f.setForeground(C_WHITE);
        f.setCaretColor(new Color(180, 150, 255));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(new EmptyBorder(10, 16, 10, 16));
        f.setPreferredSize(new Dimension(FIELD_W, FIELD_H));
        f.setMaximumSize(new Dimension(FIELD_W, FIELD_H));
        f.setMinimumSize(new Dimension(100, FIELD_H));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { focused[0] = true;  f.repaint(); }
            @Override public void focusLost (FocusEvent e) { focused[0] = false; f.repaint(); }
        });
        return f;
    }

    /** Glass panel containing a JPasswordField + optional eye toggle. */
    private JPanel glassPasswordPanel(JPasswordField field, boolean showEye) {
        final boolean[] focused = {false};

        JPanel row = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(new Color(255, 255, 255, 13));
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 20, 20));
                g2.setColor(focused[0]
                        ? new Color(130, 80, 255, 200)
                        : new Color(255, 255, 255, 50));
                g2.setStroke(new BasicStroke(1.4f));
                g2.draw(new RoundRectangle2D.Float(0.7f, 0.7f, w - 1.4f, h - 1.4f, 20, 20));
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) { /* none */ }
        };
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(FIELD_W, FIELD_H));
        row.setMaximumSize  (new Dimension(FIELD_W, FIELD_H));
        row.setMinimumSize  (new Dimension(100, FIELD_H));

        field.setOpaque(false);
        field.setForeground(C_WHITE);
        field.setCaretColor(new Color(180, 150, 255));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new EmptyBorder(10, 16, 10, showEye ? 4 : 16));
        field.setEchoChar('\u25CF');
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { focused[0] = true;  row.repaint(); }
            @Override public void focusLost (FocusEvent e) { focused[0] = false; row.repaint(); }
        });
        row.add(field, BorderLayout.CENTER);

        if (showEye) {
            JButton eye = new JButton("\uD83D\uDC41") {
                @Override protected void paintBorder(Graphics g) {}
            };
            eye.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            eye.setForeground(new Color(170, 140, 220));
            eye.setOpaque(false);
            eye.setContentAreaFilled(false);
            eye.setBorderPainted(false);
            eye.setFocusPainted(false);
            eye.setCursor(new Cursor(Cursor.HAND_CURSOR));
            eye.setPreferredSize(new Dimension(FIELD_H, FIELD_H));
            eye.addActionListener(e -> {
                passwordVisible = !passwordVisible;
                char echo = passwordVisible ? (char) 0 : '\u25CF';
                passwordField.setEchoChar(echo);
                confirmPasswordField.setEchoChar(echo);
                eye.setText(passwordVisible ? "\uD83D\uDE48" : "\uD83D\uDC41");
            });
            row.add(eye, BorderLayout.EAST);
        }
        return row;
    }

    // ═══════════════════════════════════════════════════════════
    //  TERMS CHECKBOX
    // ═══════════════════════════════════════════════════════════

    private JCheckBox buildTermsCheckbox() {
        JCheckBox cb = new JCheckBox(
                "<html><span style='font-family:Segoe UI;font-size:12px;color:#A8A8C4;'>"
                + "I agree to the "
                + "<a style='color:#9B73FF;text-decoration:none;' href='#'>Terms of Service</a>"
                + " and "
                + "<a style='color:#9B73FF;text-decoration:none;' href='#'>Privacy Policy</a>"
                + "</span></html>");
        cb.setOpaque(false);
        cb.setFocusPainted(false);
        cb.setIcon(cbIcon(false));
        cb.setSelectedIcon(cbIcon(true));
        return cb;
    }

    private Icon cbIcon(boolean sel) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = 15;
                g2.setColor(sel ? new Color(115, 75, 250) : new Color(255, 255, 255, 35));
                g2.fillRoundRect(x, y, s, s, 5, 5);
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(x, y, s, s, 5, 5);
                if (sel) {
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x + 3, y + 8,  x + 6,  y + 12);
                    g2.drawLine(x + 6, y + 12, x + 12, y + 3);
                }
                g2.dispose();
            }
            @Override public int getIconWidth()  { return 17; }
            @Override public int getIconHeight() { return 17; }
        };
    }

    // ═══════════════════════════════════════════════════════════
    //  BUTTON ROW
    // ═══════════════════════════════════════════════════════════

    private JPanel buildButtonRow() {
        // outer wrapper keeps 30 px top/bottom padding
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(20, 40, 28, 40));

        // inner row — fixed pill buttons, 25 px gap
        JPanel row = new JPanel(new GridLayout(1, 2, 25, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(340, 45));

        GlassButton cancelBtn = new GlassButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(155, 45));
        cancelBtn.addActionListener(e -> dispose());

        GlassButton createBtn = new GlassButton("Create Account");
        createBtn.setGradientColors(new Color(0x4A, 0x6C, 0xF7), new Color(0x7B, 0x2F, 0xF7));
        createBtn.setPreferredSize(new Dimension(155, 45));
        createBtn.addActionListener(e -> handleRegistration());
        getRootPane().setDefaultButton(createBtn);

        row.add(cancelBtn);
        row.add(createBtn);
        wrapper.add(row);
        return wrapper;
    }

    // ═══════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════

    /** Label + 6 px gap + field + 2 px gap + error — all left-aligned. */
    private JPanel fieldGroup(String text, JComponent field, JLabel err) {
        JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
        g.setOpaque(false);
        g.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lbl = label(text, 12, Font.BOLD, new Color(195, 195, 218));
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(FIELD_W, FIELD_H));

        err.setAlignmentX(LEFT_ALIGNMENT);

        g.add(lbl);  g.add(Box.createRigidArea(new Dimension(0, 6)));
        g.add(field);
        g.add(Box.createRigidArea(new Dimension(0, 3)));
        g.add(err);
        return g;
    }

    private JLabel label(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(color);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JLabel errorLbl() {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(C_ERROR);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private void gap(JPanel p, int h) {
        p.add(Box.createRigidArea(new Dimension(0, h)));
    }

    // ═══════════════════════════════════════════════════════════
    //  BACKEND LOGIC  —  PRESERVED VERBATIM
    // ═══════════════════════════════════════════════════════════

    private void handleRegistration() {
        clearErrors();

        String name            = nameField.getText().trim();
        String email           = emailField.getText().trim();
        String password        = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String phone           = phoneField.getText().trim();

        boolean isValid = true;

        if (name.isEmpty()) {
            nameError.setText("Name is required"); isValid = false;
        }

        if (email.isEmpty()) {
            emailError.setText("Email is required"); isValid = false;
        } else if (!Pattern.matches(EMAIL_PATTERN, email)) {
            emailError.setText("Invalid email format"); isValid = false;
        } else if (userService.getUserByEmail(email) != null) {
            emailError.setText("Email already registered"); isValid = false;
        }

        if (password.isEmpty()) {
            passwordError.setText("Password is required"); isValid = false;
        } else if (!PasswordUtil.isValidPassword(password)) {
            passwordError.setText("Password must be 8+ chars with uppercase, lowercase, and number");
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordError.setText("Passwords do not match"); isValid = false;
        }

        if (!termsCheckbox.isSelected()) {
            JOptionPane.showMessageDialog(this,
                    "Please accept the Terms of Service and Privacy Policy",
                    "Terms Required", JOptionPane.WARNING_MESSAGE);
            isValid = false;
        }

        if (!isValid) return;

        try {
            String hashedPassword = PasswordUtil.hashPassword(password);

            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(hashedPassword);
            newUser.setPhone(phone.isEmpty() ? null : phone);
            newUser.setRole("USER");
            newUser.setStatus("ACTIVE");

            boolean success = userService.registerUser(newUser);

            if (success) {
                registrationSuccessful = true;
                showSuccessDialog();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "<html><body style='width:300px'>"
                        + "<b>Registration failed!</b><br><br>"
                        + "This might be because the database schema needs to be updated.<br><br>"
                        + "<b>Solution:</b> Run <code>migrate_database.bat</code>."
                        + "</body></html>",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            String msg = ex.getMessage();
            String html = "<html><body style='width:350px'><b>Registration Error</b><br><br>";
            if (msg != null && (msg.contains("password") || msg.contains("phone")
                    || msg.contains("Unknown column"))) {
                html += "The database is missing required columns.<br><br>"
                      + "<b>Fix:</b> Run <code>migrate_database.bat</code>.";
            } else {
                html += "Error: " + (msg != null ? msg : "Unknown error")
                      + "<br><br>Please check that MySQL is running.";
            }
            html += "</body></html>";
            JOptionPane.showMessageDialog(this, html, "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearErrors() {
        nameError.setText(" ");
        emailError.setText(" ");
        passwordError.setText(" ");
        confirmPasswordError.setText(" ");
        phoneError.setText(" ");
    }

    private void showSuccessDialog() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        GlassSaveDialog.showMessage(owner,
                "Account Created!",
                "Your account has been created successfully.",
                false);
    }

    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }
}
