package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class Login extends JFrame {

    // Warna Palet sesuai Screenshot
    private final Color COLOR_BG = new Color(235, 240, 245); // Background layar
    private final Color COLOR_PRIMARY = new Color(37, 99, 235); // Biru Utama
    private final Color COLOR_TEXT_HEADER = new Color(30, 41, 59); // Teks Gelap
    private final Color COLOR_TEXT_SUB = new Color(100, 116, 139); // Teks Abu
    private final Color COLOR_CANCEL = new Color(226, 232, 240); // Abu cancel

    private JTextField tfUser;
    private JPasswordField pfPass;

    public Login() {
        setTitle("Warnet penghuni surga");
        setSize(900, 600); // Ukuran window
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Background Utama Window
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(COLOR_BG);
        setContentPane(mainPanel);

        // --- KARTU LOGIN (Panel Putih di Tengah) ---
        JPanel cardPanel = new RoundedPanel(20, Color.WHITE);
        cardPanel.setLayout(null); // Absolute layout untuk kontrol posisi presisi di dalam kartu
        cardPanel.setPreferredSize(new Dimension(400, 500));

        // Bayangan (Opsional, simulasi border halus)
        cardPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        // 1. Header Biru (Bagian atas kartu)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setBounds(0, 0, 400, 60);
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 15));

        JLabel lblHeaderTitle = new JLabel("              Warnet Sumber Rejeki");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeaderTitle.setForeground(Color.WHITE);
        headerPanel.add(lblHeaderTitle);

        // 2. Icon Komputer (Simulasi dengan Teks Emoji atau Font Besar)
        JLabel lblIcon = new JLabel("🖥️", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        lblIcon.setBounds(0, 80, 400, 80);

        // 3. Judul "WARNET ADMIN"
        JLabel lblTitle = new JLabel("ADMIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_TEXT_HEADER);
        lblTitle.setBounds(0, 150, 400, 30);

        // 4. Subjudul "Silakan login..."
        JLabel lblSubtitle = new JLabel("Selain admin dilarang login", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(COLOR_TEXT_SUB);
        lblSubtitle.setBounds(0, 180, 400, 20);

        // 5. Form Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(COLOR_TEXT_HEADER);
        lblUser.setBounds(40, 220, 320, 20);

        tfUser = new JTextField();
        styleTextField(tfUser, "Masukkan username");
        tfUser.setBounds(40, 245, 320, 40);

        // 6. Form Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(COLOR_TEXT_HEADER);
        lblPass.setBounds(40, 295, 320, 20);

        pfPass = new JPasswordField();
        styleTextField(pfPass, "Masukkan password"); // Perlu handling khusus untuk password
        pfPass.setEchoChar((char) 0); // Awalnya terlihat teks placeholder
        pfPass.setBounds(40, 320, 320, 40);

        // 7. Tombol Login (Biru)
        JButton btnLogin = new JButton("Login");
        styleButton(btnLogin, COLOR_PRIMARY, Color.WHITE);
        btnLogin.setBounds(40, 390, 220, 40);

        // 8. Tombol Cancel (Abu)
        JButton btnCancel = new JButton("Cancel");
        styleButton(btnCancel, COLOR_CANCEL, COLOR_TEXT_HEADER);
        btnCancel.setBounds(270, 390, 90, 40);

        // --- MENYUSUN KOMPONEN KE KARTU ---
        cardPanel.add(headerPanel);
        cardPanel.add(lblIcon);
        cardPanel.add(lblTitle);
        cardPanel.add(lblSubtitle);
        cardPanel.add(lblUser);
        cardPanel.add(tfUser);
        cardPanel.add(lblPass);
        cardPanel.add(pfPass);
        cardPanel.add(btnLogin);
        cardPanel.add(btnCancel);

        // Menambahkan Kartu ke Main Panel (Center)
        mainPanel.add(cardPanel);

        // --- LOGIKA TOMBOL ---
        btnLogin.addActionListener(e -> prosesLogin());
        btnCancel.addActionListener(e -> System.exit(0));
    }

    // --- LOGIKA LOGIN TERPISAH ---
    private void prosesLogin() {
        String user = tfUser.getText();
        String pass = new String(pfPass.getPassword());

        // Hapus placeholder jika user tidak sengaja mengirim placeholder
        if(user.equals("Masukkan username")) user = "";
        if(pass.equals("Masukkan password")) pass = "";

        if (user.equals("admin") && pass.equals("123")) {
            // Animasi atau Feedback sukses bisa ditaruh disini
            JOptionPane.showMessageDialog(this, "Login Berhasil! Selamat Datang Admin.");

            // Buka Dashboard
            new Dashboard().setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password Salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- HELPER STYLING ---

    private void styleTextField(JTextField field, String placeholder) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true), // Rounded border halus
                new EmptyBorder(5, 10, 5, 10) // Padding text
        ));

        // Logic Placeholder
        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('•'); // Ubah ke titik saat ngetik
                    }
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0); // Ubah ke teks biasa
                    }
                }
            }
        });
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder()); // Flat look
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efek Hover sederhana
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
    }

    // --- INNER CLASS: ROUNDED PANEL ---
    // Kelas ini membuat JPanel dengan sudut melengkung
    class RoundedPanel extends JPanel {
        private int radius;
        private Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false); // Transparan agar sudut tidak kotak hitam
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Anti-aliasing agar lengkungan halus
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Gambar Kotak Putih Melengkung
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            // Gambar Header Biru (Hanya melengkung di atas)
            g2.setColor(COLOR_PRIMARY);
            // Kita gambar kotak biru di atas, lalu kita clip agar bawahnya lurus
            // Cara manual sederhana: Gambar rect round penuh, lalu gambar rect kotak di bawah untuk nutupin lengkungan bawah
            // Tapi karena header ada di dalam panel anak, kita biarkan logic header di atas.
            // Di sini kita gambar border luar saja jika perlu.
        }
    }
}