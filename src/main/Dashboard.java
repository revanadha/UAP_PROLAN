package main;

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
    private order orderPanel;

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
        mainContentPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // View 1: Dashboard Table
        mainContentPanel.add(createDashboardPanel(), "TABEL");

        // View 2: Order Form (Simpan referensi ke variabel)
        orderPanel = new order(controller);
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

        JLabel lblTitle = new JLabel("📋 DAFTAR PENYEWA AKTIF (REALTIME)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
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
        DefaultTableModel vModel = (DefaultTableModel) table.getModel();
        vModel.setRowCount(0);

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
    }

    private String safeStr(Object o) { return o == null ? "-" : o.toString(); }

    private void initHeader(JPanel mainPanel) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COL_HEADER);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(new EmptyBorder(0, 30, 0, 30));

        JLabel title = new JLabel("🖥️ Warnet Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        styleButton(btnLogout, new Color(239, 68, 68), Color.WHITE);
        btnLogout.addActionListener(e -> { new main().setVisible(true); dispose(); });

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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        if(isActive) {
            btn.setBackground(COL_ACTIVE_TAB);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        } else {
            btn.setBackground(COL_INACTIVE_TAB);
            btn.setForeground(Color.GRAY);
            btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        }
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(COL_TABLE_HEAD);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(!isSelected) c.setBackground(Color.WHITE);
                setBorder(new EmptyBorder(0, 10, 0, 10));

                if (column == 4 && value.toString().equals("Aktif")) { // Status
                    JLabel lbl = new JLabel("Aktif", SwingConstants.CENTER);
                    lbl.setForeground(new Color(22, 163, 74));
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    JPanel p = new RoundedPanel(15, new Color(220, 252, 231));
                    p.setLayout(new GridBagLayout()); p.add(lbl);
                    JPanel w = new JPanel(new FlowLayout(FlowLayout.CENTER)); w.setBackground(isSelected?table.getSelectionBackground():Color.WHITE); w.add(p);
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
        val.setFont(new Font("Segoe UI", Font.BOLD, 24)); val.setForeground(txt);
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
}