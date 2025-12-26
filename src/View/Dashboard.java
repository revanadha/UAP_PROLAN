package View;

import controller.RentalController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class Dashboard extends JFrame {

    private final Color COL_HEADER = new Color(37, 99, 235);
    private final Color COL_BG = new Color(241, 245, 249);
    private final Color COL_TABLE_HEAD = new Color(30, 41, 59);
    private final Color COL_ACTIVE_TAB = new Color(37, 99, 235);
    private final Color COL_INACTIVE_TAB = Color.WHITE;

    private RentalController controller;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JButton btnTabTabel, btnTabTambah, btnTabRiwayat;
    private JTable table;
    private JLabel lblTotalPenyewa, lblTotalPendapatan;

    // Referensi ke panel order agar bisa direfresh
    private Order orderPanel;

    public Dashboard() {
        controller = new RentalController();

        setTitle("Warnet Sumber Rejeki");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COL_BG);
        setContentPane(mainPanel);

        // HEADER
        initHeader(mainPanel);

        // CONTENT
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(COL_BG);
        centerContainer.setBorder(new EmptyBorder(10, 30, 10, 30));

        // TAB NAV
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(0, 20, 0, 0));

        btnTabTabel = createTabButton("📊 Tabel Penyewa", true);
        btnTabTambah = createTabButton("➕ Tambah Pesanan", false);
        btnTabRiwayat = createTabButton("📜 Riwayat", false);

        navPanel.add(btnTabTabel);
        navPanel.add(btnTabTambah);
        navPanel.add(btnTabRiwayat);
        centerContainer.add(navPanel, BorderLayout.NORTH);

        // CARD LAYOUT
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        // View 1: Dashboard Table
        mainContentPanel.add(createDashboardPanel(), "TABEL");

        // View 2: Order Form (Simpan referensi ke variabel)
        orderPanel = new Order(controller);
        mainContentPanel.add(orderPanel, "ORDER");

        // View 3: Riwayat (Placeholder awal)
        mainContentPanel.add(new Riwayat(controller), "RIWAYAT");

        centerContainer.add(mainContentPanel, BorderLayout.CENTER);
        mainPanel.add(centerContainer, BorderLayout.CENTER);

        // FOOTER
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(230, 235, 240));
        footer.setBorder(new EmptyBorder(5, 10, 5, 10));
        footer.add(new JLabel("🟢 Status: Koneksi Realtime Aktif"), BorderLayout.WEST);
        mainPanel.add(footer, BorderLayout.SOUTH);

        // LOGIKA TAB
        btnTabTabel.addActionListener(e -> switchTab("TABEL", btnTabTabel));
        btnTabTambah.addActionListener(e -> switchTab("ORDER", btnTabTambah));
        btnTabRiwayat.addActionListener(e -> switchTab("RIWAYAT", btnTabRiwayat));

        // --- REALTIME UI UPDATER ---
        // Timer ini akan me-refresh UI setiap 2 detik
        // agar jika ada pesanan selesai otomatis, tabel langsung update
        new Timer(2000, e -> {
            if (lblTotalPenyewa != null && lblTotalPenyewa.isShowing()) {
                refreshTableData();
            }
        }).start();

        refreshTableData();
    }

    private void switchTab(String cardName, JButton activeBtn) {
        // Logic Refresh per Tab
        if (cardName.equals("RIWAYAT")) {
            // Hapus panel lama, buat baru (agar data riwayat terupdate)
            for(Component c : mainContentPanel.getComponents()) {
                if(c instanceof Riwayat) mainContentPanel.remove(c);
            }
            mainContentPanel.add(new Riwayat(controller), "RIWAYAT");
        }
        else if (cardName.equals("ORDER")) {
            // Refresh status PC (Merah/Hijau) saat buka tab tambah
            orderPanel.refreshPCStatus();
        }
        else if (cardName.equals("TABEL")) {
            refreshTableData();
        }

        cardLayout.show(mainContentPanel, cardName);

        styleTabButton(btnTabTabel, false);
        styleTabButton(btnTabTambah, false);
        styleTabButton(btnTabRiwayat, false);
        styleTabButton(activeBtn, true);
    }

    private JPanel createDashboardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("📋 DAFTAR PENYEWA AKTIF");
        lblTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        lblTitle.setBorder(new EmptyBorder(0,0,15,0));
        card.add(lblTitle, BorderLayout.NORTH);

        String[] visualColumns = {"No. PC", "Nama Penyewa", "Jam Mulai", "Jam Selesai", "Status", "Total (Rp)"};
        DefaultTableModel visualModel = new DefaultTableModel(visualColumns, 0);
        table = new JTable(visualModel);
        styleTable(table);
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        lblTotalPenyewa = new JLabel("0");
        infoPanel.add(createInfoCard("Total Penyewa Aktif", lblTotalPenyewa, new Color(219, 234, 254), new Color(30, 58, 138)));

        lblTotalPendapatan = new JLabel("Rp 0");
        infoPanel.add(createInfoCard("Est. Pendapatan Aktif", lblTotalPendapatan, new Color(220, 252, 231), new Color(20, 83, 45)));

        card.add(infoPanel, BorderLayout.SOUTH);
        p.add(card, BorderLayout.CENTER);

        // Tombol Hapus Paksa
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setOpaque(false);

        JButton btnEdit = new JButton("✏️ Edit Pesanan");
        styleButton(btnEdit, new Color(245, 158, 11), Color.WHITE); // Warna Oranye
        btnEdit.addActionListener(e -> showEditDialog());
        btnPanel.add(btnEdit);

        JButton btnHapus = new JButton("Batal / Hapus Paksa");
        styleButton(btnHapus, new Color(220, 38, 38), Color.WHITE);
        btnHapus.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row >= 0) {
                controller.hapusData(row); // Hapus dari model active
                refreshTableData();
                orderPanel.refreshPCStatus(); // Update tombol PC juga
            }
        });
        btnPanel.add(btnHapus);
        p.add(btnPanel, BorderLayout.SOUTH);

        return p;
    }

    private void refreshTableData() {
        // 1. Simpan baris yang sedang dipilih (jika ada)
        int selectedRow = table.getSelectedRow();

        DefaultTableModel vModel = (DefaultTableModel) table.getModel();
        vModel.setRowCount(0); // Ini yang menyebabkan seleksi hilang

        DefaultTableModel dataRaw = controller.getModel();
        int totalUang = 0;

        for (int i = 0; i < dataRaw.getRowCount(); i++) {
            String nama = safeStr(dataRaw.getValueAt(i, 0));
            String pc = safeStr(dataRaw.getValueAt(i, 2));
            String jamMulai = safeStr(dataRaw.getValueAt(i, 4));
            String jamSelesai = safeStr(dataRaw.getValueAt(i, 5));
            String durasiStr = safeStr(dataRaw.getValueAt(i, 3));

            int durasi = 1;
            try { durasi = Integer.parseInt(durasiStr); } catch(Exception e){}
            int total = durasi * 5000;
            totalUang += total;

            vModel.addRow(new Object[]{pc, nama, jamMulai, jamSelesai, "Aktif", "Rp " + total});
        }

        if(lblTotalPenyewa != null) lblTotalPenyewa.setText(String.valueOf(dataRaw.getRowCount()));
        if(lblTotalPendapatan != null) lblTotalPendapatan.setText("Rp " + totalUang);

        // 2. Kembalikan seleksi (Restore Selection)
        // Cek apakah baris yang tadi dipilih masih ada (valid) di data baru
        if (selectedRow >= 0 && selectedRow < vModel.getRowCount()) {
            table.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private String safeStr(Object o) { return o == null ? "-" : o.toString(); }

    private void initHeader(JPanel mainPanel) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COL_HEADER);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(new EmptyBorder(0, 30, 0, 30));

        JLabel title = new JLabel("🖥️ Warnet Sumber Rejeki");
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        styleButton(btnLogout, new Color(239, 68, 68), Color.WHITE);
        btnLogout.addActionListener(e -> { new Login().setVisible(true); dispose(); });


        header.add(title, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);
    }

    private JButton createTabButton(String text, boolean active) {
        JButton btn = new JButton(text);
        styleTabButton(btn, active);
        return btn;
    }

    private void styleTabButton(JButton btn, boolean isActive) {
        // Gunakan font Emoji agar icon tampil benar
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if(isActive) {
            btn.setContentAreaFilled(true); // Ada background warnanya
            btn.setBackground(COL_ACTIVE_TAB);
            btn.setForeground(Color.WHITE);
            // Hilangkan border garis, ganti dengan padding saja
            btn.setBorder(new EmptyBorder(10, 25, 10, 25));
        } else {
            btn.setContentAreaFilled(false); // Transparan! (Menyatu dengan background)
            btn.setForeground(new Color(100, 116, 139)); // Warna teks abu-abu (Slate)
            // Hapus LineBorder, ganti dengan padding saja agar teks sejajar
            btn.setBorder(new EmptyBorder(10, 25, 10, 25));
        }
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        // Header Tabel
        JTableHeader header = table.getTableHeader();
        header.setBackground(COL_TABLE_HEAD);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        // Tambahan: Agar Header juga Rata Tengah
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        header.setPreferredSize(new Dimension(0, 45));

        // Isi Tabel
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                // Panggil super
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // --- BAGIAN INI MEMBUAT TEKS RATA TENGAH ---
                setHorizontalAlignment(SwingConstants.CENTER);
                // -------------------------------------------

                if(!isSelected) c.setBackground(Color.WHITE);
                setBorder(new EmptyBorder(0, 10, 0, 10));

                // Khusus Kolom Status (Kolom index 4) menggunakan Panel khusus
                if (column == 4 && value.toString().equals("Aktif")) {
                    JLabel lbl = new JLabel("Aktif", SwingConstants.CENTER);
                    lbl.setForeground(new Color(22, 163, 74));
                    lbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));

                    JPanel p = new RoundedPanel(15, new Color(220, 252, 231));
                    p.setLayout(new GridBagLayout());
                    p.add(lbl);

                    // Panel pembungkus agar rounded panel tetap di tengah
                    JPanel w = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    w.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                    w.add(p);
                    return w;
                }
                return c;
            }
        });
    }

    private JPanel createInfoCard(String title, JLabel val, Color bg, Color txt) {
        RoundedPanel p = new RoundedPanel(15, bg);
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel t = new JLabel(title); t.setForeground(txt);
        val.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24)); val.setForeground(txt);
        p.add(t, BorderLayout.NORTH); p.add(val, BorderLayout.CENTER);
        return p;
    }

    class RoundedPanel extends JPanel {
        private int r; private Color c;
        public RoundedPanel(int r, Color c){this.r=r;this.c=c;setOpaque(false);}
        protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c); g2.fillRoundRect(0,0,getWidth(),getHeight(),r,r);
        }
    }
    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diedit dulu!");
            return;
        }

        // 1. Ambil Data Lama
        DefaultTableModel model = controller.getModel();
        String oldNama = model.getValueAt(row, 0).toString();
        String oldPC = model.getValueAt(row, 2).toString();
        String oldDurasi = model.getValueAt(row, 3).toString();
        String jamMulaiStr = model.getValueAt(row, 4).toString(); // Jam mulai tetap, durasi diubah -> jam selesai berubah

        // 2. Buat Dialog
        JDialog dialog = new JDialog(this, "Edit Pesanan", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        // --- INPUT FIELD (Gaya mirip order.java) ---
        JTextField tfNama = createInputForDialog(formPanel, "Nama Penyewa", oldNama);

        // Combo Box untuk PC (Supaya aman validasinya)
        JPanel pcContainer = new JPanel(new BorderLayout(0, 5));
        pcContainer.setOpaque(false);
        pcContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel lblPC = new JLabel("Pilih Komputer");
        lblPC.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String[] listPC = new String[10];
        for(int i=0; i<10; i++) listPC[i] = "PC " + (i+1);
        JComboBox<String> cbPC = new JComboBox<>(listPC);
        cbPC.setSelectedItem(oldPC); // Pilih PC lama
        cbPC.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pcContainer.add(lblPC, BorderLayout.NORTH);
        pcContainer.add(cbPC, BorderLayout.CENTER);
        formPanel.add(pcContainer);
        formPanel.add(Box.createVerticalStrut(15));

        JTextField tfDurasi = createInputForDialog(formPanel, "Durasi (Jam)", oldDurasi);
        JTextField tfHarga = createInputForDialog(formPanel, "Harga per Jam (Rp)", "5000"); // Default 5000

        // Label Total (Auto Hitung)
        JLabel lblTotal = new JLabel("Total: Rp -");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(new Color(30, 58, 138));
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(lblTotal);

        dialog.add(formPanel, BorderLayout.CENTER);

        // --- TOMBOL SAVE ---
        JButton btnSave = new JButton("Simpan Perubahan");
        styleButton(btnSave, COL_ACTIVE_TAB, Color.WHITE);
        btnSave.addActionListener(e -> {
            try {
                String newNama = tfNama.getText();
                String newPC = (String) cbPC.getSelectedItem();
                int newDurasi = Integer.parseInt(tfDurasi.getText());
                int harga = Integer.parseInt(tfHarga.getText());

                // Validasi PC: Jika ganti PC, cek apakah PC baru kosong?
                if (!newPC.equals(oldPC) && controller.isPCBooked(newPC)) {
                    JOptionPane.showMessageDialog(dialog, "PC tersebut sedang dipakai!", "Gagal", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Hitung Jam Selesai Baru
                java.time.LocalTime start = java.time.LocalTime.parse(jamMulaiStr, java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                java.time.LocalTime end = start.plusHours(newDurasi);
                String newJamSelesai = end.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

                // Simpan ke Controller
                controller.updateData(row, newNama, newPC, String.valueOf(newDurasi), newJamSelesai);

                refreshTableData();
                orderPanel.refreshPCStatus(); // Refresh warna PC di tab tambah
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Pastikan durasi & harga berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnPanelDialog = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanelDialog.setBackground(Color.WHITE);
        btnPanelDialog.add(btnSave);
        dialog.add(btnPanelDialog, BorderLayout.SOUTH);

        // Listener Hitung Otomatis
        javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calc(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calc(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calc(); }
            void calc() {
                try {
                    int d = Integer.parseInt(tfDurasi.getText());
                    int h = Integer.parseInt(tfHarga.getText());
                    lblTotal.setText("Total: Rp " + (d*h));
                } catch(Exception ex){}
            }
        };
        tfDurasi.getDocument().addDocumentListener(dl);
        tfHarga.getDocument().addDocumentListener(dl);

        // Trigger hitung awal
        try {
            int d = Integer.parseInt(tfDurasi.getText());
            int h = Integer.parseInt(tfHarga.getText());
            lblTotal.setText("Total: Rp " + (d*h));
        } catch(Exception ex){}

        dialog.setVisible(true);
    }

    // Helper UI untuk Dialog
    private JTextField createInputForDialog(JPanel p, String label, String value) {
        JPanel c = new JPanel(new BorderLayout(0, 5));
        c.setOpaque(false);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Color.GRAY);

        JTextField t = new JTextField(value);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                new EmptyBorder(5, 8, 5, 8)
        ));

        c.add(l, BorderLayout.NORTH);
        c.add(t, BorderLayout.CENTER);
        p.add(c);
        p.add(Box.createVerticalStrut(15));
        return t;
    }

}