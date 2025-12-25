package main;

import javax.swing.*;
import java.awt.*;

public class main extends JFrame {

    public main() {
        setTitle("Login Rental Warnet");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Menggunakan GridBagLayout untuk posisi center yang presisi
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Memberi jarak antar elemen

        // Panel Pembungkus agar terlihat seperti kartu
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createTitledBorder("Silakan Login"));
        cardPanel.setBackground(Color.WHITE);

        // Komponen
        JLabel lblUser = new JLabel("Username:");
        JTextField tfUser = new JTextField(15);
        JLabel lblPass = new JLabel("Password:");
        JPasswordField pfPass = new JPasswordField(15);
        JButton btnLogin = new JButton("Login Masuk");

        // Styling Tombol
        btnLogin.setBackground(new Color(33, 150, 243));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);

        // Menyusun Komponen
        gbc.gridx = 0; gbc.gridy = 0;
        cardPanel.add(lblUser, gbc);

        gbc.gridx = 1;
        cardPanel.add(tfUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        cardPanel.add(lblPass, gbc);

        gbc.gridx = 1;
        cardPanel.add(pfPass, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        cardPanel.add(btnLogin, gbc);

        add(cardPanel); // Tambahkan panel kartu ke frame utama

        // Logika Login
        btnLogin.addActionListener(e -> {
            String user = tfUser.getText();
            String pass = new String(pfPass.getPassword());

            if (user.equals("admin") && pass.equals("123")) {
                JOptionPane.showMessageDialog(this, "Login Berhasil!");
                new tabel().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username/Password Salah!", "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        // MENGUBAH TEMA KE NIMBUS (Tampilan Modern Bawaan Java)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new main().setVisible(true));
    }
}