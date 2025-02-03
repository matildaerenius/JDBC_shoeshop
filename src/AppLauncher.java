
import gui.ViewManager;

import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ViewManager viewManager = new ViewManager();
                viewManager.setVisible(true);

            }
        });
    }
}
