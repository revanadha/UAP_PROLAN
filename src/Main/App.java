package Main;

import View.Login; // Wajib import karena Login ada di package View
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Panggil class Login, bukan main
            new Login().setVisible(true);
        });
    }
}