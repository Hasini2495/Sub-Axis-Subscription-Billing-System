import ui.LoginFrame;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the SaaS Subscription Management Platform.
 */
public class Main {
    public static void main(String[] args) {
        // Set system look and feel
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch login frame on EDT
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
