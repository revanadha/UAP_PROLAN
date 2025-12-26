package View;

import controller.RentalController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Order extends JPanel {

    private RentalController controller;
    private JTextField tfNoPC, tfNama, tfJamMulai, tfDurasi, tfHarga;
    private JLabel lblTotal;
    private JPanel pcGridPanel;
    private Timer timerJam; // Timer untuk jam digital

    // Warna
    private final Color COL_BG = new Color(241, 245, 249);
    private final Color COL_BLUE = new Color(37, 99, 235);
    private final Color COL_RED_BG = new Color(254, 226, 226);
    private final Color COL_RED_TXT = new Color(153, 27, 27);
    private final Color COL_GREEN_BG = new Color(220, 252, 231);
    private final Color COL_GREEN_TXT = new Color(22, 101, 52);
    private final Color COL_YELLOW_BG = new Color(254, 249, 195);
    private final Color COL_YELLOW_BORDER = new Color(250, 204, 21);

    public Order(RentalController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(COL_BG);
        setBorder(new EmptyBorder(10, 15, 10, 15));

        // --- KARTU UTAMA ---
        JPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout(0, 20));
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        // HEADER
        JLabel lblTitle = new JLabel("➕ FORM TAMBAH PESANAN");
        lblTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        lblTitle.setForeground(new Color(51, 65, 85));
        card.add(lblTitle, BorderLayout.NORTH);

        // --- KONTEN TENGAH (2 KOLOM) ---
        JPanel centerContent = new JPanel(new GridLayout(1, 2, 40, 0));
        centerContent.setOpaque(false);

        // === KIRI (DATA) ===
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        leftPanel.add(createSectionHeader("Data Pesanan"));
        leftPanel.add(Box.createVerticalStrut(15));

        tfNoPC = createLabeledInput(leftPanel, "No. Komputer", "Pilih dari status di bawah...");
        tfNoPC.setEditable(false);
        tfNoPC.setBackground(new Color(248, 250, 252));

        leftPanel.add(Box.createVerticalStrut(15));
        tfNama = createLabeledInput(leftPanel, "Nama Penyewa", "Masukkan nama penyewa");

        leftPanel.add(Box.createVerticalStrut(15));

        // --- JAM MULAI OTOMATIS ---
        tfJamMulai = createLabeledInput(leftPanel, "Jam Mulai (Otomatis)", "Jam saat ini...");
        tfJamMulai.setEditable(false); // Tidak bisa diedit user
        tfJamMulai.setBackground(new Color(240, 253, 244)); // Warna agak hijau menandakan aktif/otomatis
        tfJamMulai.setForeground(new Color(22, 101, 52));
        tfJamMulai.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Timer agar jam berjalan real-time
        timerJam = new Timer(1000, e -> updateJamOtomatis());
        timerJam.start();
        updateJamOtomatis(); // Panggil sekali di awal

        leftPanel.add(Box.createVerticalGlue());

        // === KANAN (HITUNGAN) ===
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        rightPanel.add(createSectionHeader("Perhitungan"));
        rightPanel.add(Box.createVerticalStrut(15));

        tfDurasi = createLabeledInput(rightPanel, "Durasi (Jam)", "Berapa jam?");
        rightPanel.add(Box.createVerticalStrut(15));

        tfHarga = createLabeledInput(rightPanel, "Harga per Jam (Rp)", "5000");
        tfHarga.setText("5000");
        rightPanel.add(Box.createVerticalStrut(15));

        // Box Total
        JPanel containerTotal = new JPanel(new BorderLayout(0, 5));
        containerTotal.setOpaque(false);
        containerTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel lblTotalTitle = new JLabel("Total Pembayaran");
        lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalTitle.setForeground(new Color(133, 77, 14));

        lblTotal = new JLabel("Rp 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotal.setForeground(new Color(133, 77, 14));

        JPanel yellowBox = new RoundedPanel(10, COL_YELLOW_BG);
        yellowBox.setBorder(BorderFactory.createLineBorder(COL_YELLOW_BORDER, 1));
        yellowBox.setLayout(new BorderLayout());
        yellowBox.setBorder(new EmptyBorder(10, 15, 10, 15));
        yellowBox.add(lblTotal, BorderLayout.CENTER);

        containerTotal.add(lblTotalTitle, BorderLayout.NORTH);
        containerTotal.add(yellowBox, BorderLayout.CENTER);

        rightPanel.add(containerTotal);
        rightPanel.add(Box.createVerticalGlue());

        centerContent.add(leftPanel);
        centerContent.add(rightPanel);
        card.add(centerContent, BorderLayout.CENTER);

        // --- BAWAH (STATUS PC & TOMBOL) ---
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setOpaque(false);

        JPanel headerStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        headerStatus.setOpaque(false);
        headerStatus.add(createSectionHeader("Status Komputer"));
        bottomContainer.add(headerStatus);

        pcGridPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        pcGridPanel.setOpaque(false);
        initPCGrid();
        bottomContainer.add(pcGridPanel);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        JButton btnSimpan = new JButton("Simpan Pesanan");
        styleButton(btnSimpan, COL_BLUE, Color.WHITE);
        JButton btnReset = new JButton("Reset");
        styleButton(btnReset, new Color(51, 65, 85), Color.WHITE);

        actionPanel.add(btnSimpan);
        actionPanel.add(Box.createHorizontalStrut(15));
        actionPanel.add(btnReset);
        bottomContainer.add(actionPanel);

        card.add(bottomContainer, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

        // --- LISTENERS ---
        DocumentListener calcListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { hitungTotal(); }
            public void removeUpdate(DocumentEvent e) { hitungTotal(); }
            public void changedUpdate(DocumentEvent e) { hitungTotal(); }
        };
        tfDurasi.getDocument().addDocumentListener(calcListener);
        tfHarga.getDocument().addDocumentListener(calcListener);

        btnSimpan.addActionListener(e -> prosesSimpan());
        btnReset.addActionListener(e -> resetForm());
    }

    // --- LOGIKA JAM OTOMATIS ---
    private void updateJamOtomatis() {
        if (tfJamMulai != null) {
            String jamSekarang = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            tfJamMulai.setText(jamSekarang);
        }
    }

    // --- UI HELPERS ---
    private JTextField createLabeledInput(JPanel parentPanel, String labelText, String placeholder) {
        JPanel container = new JPanel(new BorderLayout(0, 5));
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(100, 116, 139));
        container.add(lbl, BorderLayout.NORTH);

        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(0, 30));

        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(0, 8, 0, 8)
        ));
        tf.setToolTipText(placeholder);

        container.add(tf, BorderLayout.CENTER);
        parentPanel.add(container);
        return tf;
    }

    private void initPCGrid() {
        pcGridPanel.removeAll();
        for (int i = 1; i <= 10; i++) {
            String pcName = "PC " + i;
            boolean isBooked = controller.isPCBooked(pcName);
            JButton btnPC = new JButton();
            btnPC.setLayout(new BorderLayout());

            JLabel lblName = new JLabel(pcName, SwingConstants.CENTER);
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JLabel lblStatus = new JLabel(isBooked ? "● Terisi" : "● Kosong", SwingConstants.CENTER);
            lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 10));

            if (isBooked) {
                btnPC.setBackground(COL_RED_BG);
                btnPC.setBorder(BorderFactory.createLineBorder(new Color(239, 68, 68), 1));
                lblName.setForeground(COL_RED_TXT);
                lblStatus.setForeground(COL_RED_TXT);
                btnPC.setEnabled(false);
            } else {
                btnPC.setBackground(COL_GREEN_BG);
                btnPC.setBorder(BorderFactory.createLineBorder(new Color(34, 197, 94), 1));
                lblName.setForeground(COL_GREEN_TXT);
                lblStatus.setForeground(COL_GREEN_TXT);
                btnPC.setCursor(new Cursor(Cursor.HAND_CURSOR));
                int finalI = i;
                btnPC.addActionListener(e -> tfNoPC.setText("PC " + finalI)); // Simpan "PC X"
            }
            btnPC.add(lblName, BorderLayout.CENTER);
            btnPC.add(lblStatus, BorderLayout.SOUTH);
            btnPC.setPreferredSize(new Dimension(0, 60));

            pcGridPanel.add(btnPC);
        }
        pcGridPanel.revalidate();
        pcGridPanel.repaint();
    }

    public void refreshPCStatus() { initPCGrid(); }

    private void hitungTotal() {
        try {
            int durasi = Integer.parseInt(tfDurasi.getText());
            int harga = Integer.parseInt(tfHarga.getText());
            int total = durasi * harga;
            NumberFormat indo = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            lblTotal.setText(indo.format(total).replace("Rp", "Rp "));
        } catch (NumberFormatException e) {
            lblTotal.setText("Rp 0");
        }
    }

    // --- PROSES SIMPAN DENGAN WAKTU OTOMATIS ---
    private void prosesSimpan() {
        // Cek validasi dasar
        if(tfNoPC.getText().isEmpty() || tfNama.getText().isEmpty() || tfDurasi.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi Nama, PC, dan Durasi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int durasi = Integer.parseInt(tfDurasi.getText());

            // 1. Ambil Waktu Sekarang (Realtime)
            LocalTime startTime = LocalTime.now();

            // 2. Hitung Waktu Selesai (Otomatis handle lintas hari)
            LocalTime endTime = startTime.plusHours(durasi);

            // 3. Format ke String "HH:mm" untuk disimpan
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
            String jamMulaiStr = startTime.format(fmt);
            String jamSelesaiStr = endTime.format(fmt);

            // 4. Kirim ke Controller
            controller.tambahData(new Object[]{
                    tfNama.getText(),
                    "-",
                    tfNoPC.getText(),
                    String.valueOf(durasi),
                    jamMulaiStr,
                    jamSelesaiStr
            });

            JOptionPane.showMessageDialog(this, "Pesanan Berhasil Disimpan!\nWaktu Mulai: " + jamMulaiStr);
            resetForm();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Durasi harus angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        tfNoPC.setText("");
        tfNama.setText("");
        tfDurasi.setText("");
        lblTotal.setText("Rp 0");
        // Jam tidak perlu di-reset karena otomatis update
        refreshPCStatus();
    }

    private JLabel createSectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(COL_BLUE);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return lbl;
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 25, 12, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    class RoundedPanel extends JPanel {
        private int r; private Color c;
        public RoundedPanel(int r, Color c) { this.r=r; this.c=c; setOpaque(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),r,r);
        }
    }

}