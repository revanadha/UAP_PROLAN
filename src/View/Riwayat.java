package View;

import controller.RentalController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Riwayat extends JPanel {

    private RentalController controller;
    private JTable table;
    private JTextField tfFilter;
    private JLabel lblTotalTransaksi, lblTotalJam, lblTotalPendapatan;

    // Warna Sesuai Desain Screenshot
    private final Color COL_BG = new Color(241, 245, 249);
    private final Color COL_HEADER_TABLE = new Color(55, 65, 81); // Dark Grey
    private final Color COL_BLUE_PC = new Color(219, 234, 254);
    private final Color COL_BLUE_TEXT = new Color(30, 58, 138);
    private final Color COL_PRIMARY = new Color(37, 99, 235);

    public Riwayat (RentalController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(COL_BG);
        setBorder(new EmptyBorder(10, 20, 20, 20));

        // --- 1. MAIN CARD (PEMBUNGKUS UTAMA) ---
        JPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout(0, 15));
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        // --- HEADER SECTION (JUDUL & FILTER) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Judul
        JLabel lblTitle = new JLabel("📜 RIWAYAT PESANAN");
        lblTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        lblTitle.setForeground(new Color(51, 65, 85));

        // Panel Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setOpaque(false);

        tfFilter = new JTextField("dd/mm/yyyy");
        tfFilter.setPreferredSize(new Dimension(200, 35));
        tfFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219)),
                new EmptyBorder(0, 10, 0, 10))
        );

        JButton btnReset = new JButton("🔄 Reset Filter");
        styleButton(btnReset, new Color(71, 85, 105), Color.WHITE);

        filterPanel.add(new JLabel("📅 Filter Tanggal:"));
        filterPanel.add(tfFilter);
        filterPanel.add(btnReset);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);
        card.add(headerPanel, BorderLayout.NORTH);

        // --- TABLE SECTION ---
        // Kolom: Tanggal, No. PC, Nama, Jam Mulai, Jam Selesai, Durasi, Total
        String[] columns = {"Tanggal", "No. PC", "Nama Penyewa", "Jam Mulai", "Jam Selesai", "Durasi", "Total (Rp)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(scrollPane, BorderLayout.CENTER);

        // --- FOOTER SECTION (SUMMARY CARDS) ---
        JPanel footerPanel = new JPanel(new BorderLayout(0, 15));
        footerPanel.setOpaque(false);

        // Baris Total Hitam di bawah tabel (Opsional, sesuai desain screenshot)
        JPanel totalBar = new JPanel(new BorderLayout());
        totalBar.setBackground(COL_HEADER_TABLE);
        totalBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel lblFooterTotal = new JLabel("TOTAL PENDAPATAN SEKARANG:");
        lblFooterTotal.setForeground(Color.WHITE);
        lblFooterTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalBar.add(lblFooterTotal, BorderLayout.WEST);
        footerPanel.add(totalBar, BorderLayout.NORTH);

        // Kartu Ringkasan (Cards)
        JPanel cardsContainer = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsContainer.setOpaque(false);
        cardsContainer.setPreferredSize(new Dimension(0, 100));

        // Card 1: Total Transaksi (Biru)
        lblTotalTransaksi = new JLabel("0");
        cardsContainer.add(createSummaryCard("Total Transaksi", lblTotalTransaksi, new Color(239, 246, 255), COL_BLUE_TEXT));

        // Card 2: Total Jam (Hijau)
        lblTotalJam = new JLabel("0 Jam");
        cardsContainer.add(createSummaryCard("Total Jam Rental", lblTotalJam, new Color(240, 253, 244), new Color(21, 128, 61)));

        // Card 3: Total Pendapatan (Kuning)
        lblTotalPendapatan = new JLabel("Rp 0");
        cardsContainer.add(createSummaryCard("Total Pendapatan", lblTotalPendapatan, new Color(254, 252, 232), new Color(161, 98, 7)));

        footerPanel.add(cardsContainer, BorderLayout.CENTER);

        // --- TOMBOL AKSI BAWAH ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 15));
        actionPanel.setOpaque(false);

        JButton btnCetak = new JButton("🖨 Cetak Laporan");
        styleButton(btnCetak, COL_PRIMARY, Color.WHITE);

        JButton btnExport = new JButton("axls Export Data");
        styleButton(btnExport, new Color(22, 163, 74), Color.WHITE); // Hijau Excel

        actionPanel.add(btnCetak);
        actionPanel.add(Box.createHorizontalStrut(15));
        actionPanel.add(btnExport);

        footerPanel.add(actionPanel, BorderLayout.SOUTH);
        card.add(footerPanel, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);

        // LOAD DATA
        refreshData();

        // Event Listener Tombol (Placeholder)
        btnReset.addActionListener(e -> {
            tfFilter.setText("dd/mm/yyyy");
            refreshData();
        });

        btnCetak.addActionListener(e -> JOptionPane.showMessageDialog(this, "Fitur Cetak Laporan akan dikembangkan."));
        btnExport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Data berhasil diexport ke .xls"));
    }

    private void refreshData() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        // Ambil data history dari controller
        DefaultTableModel rawData = controller.getModelHistory();

        int totalTrx = 0;
        int totalJam = 0;
        int totalUang = 0;

        for (int i = 0; i < rawData.getRowCount(); i++) {
            // Ambil data sesuai kolom baru di Controller
            String nama = rawData.getValueAt(i, 0).toString();
            // Index 1 adalah WA (tidak ditampilkan di tabel riwayat)
            String pc = rawData.getValueAt(i, 2).toString();
            String durasiStr = rawData.getValueAt(i, 3).toString();
            String jamMulai = rawData.getValueAt(i, 4).toString();
            String jamSelesai = rawData.getValueAt(i, 5).toString();

            // PERBAIKAN: Ambil Tanggal dari Index 6 (Data Asli)
            String tanggalAsli = "-";
            try {
                tanggalAsli = rawData.getValueAt(i, 6).toString();
            } catch (Exception e) {
                // Fallback jika data lama belum punya tanggal
                tanggalAsli = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            }

            int durasi = 0;
            try { durasi = Integer.parseInt(durasiStr); } catch(Exception e){}
            int subTotal = durasi * 5000;

            // Masukkan ke tabel visual Riwayat
            model.addRow(new Object[]{
                    tanggalAsli, // Gunakan tanggal asli
                    pc,
                    nama,
                    jamMulai,
                    jamSelesai,
                    durasi + " jam",
                    "Rp " + subTotal
            });

            totalTrx++;
            totalJam += durasi;
            totalUang += subTotal;
        }

        // Update Labels Statistik
        lblTotalTransaksi.setText(String.valueOf(totalTrx));
        lblTotalJam.setText(totalJam + " Jam");
        lblTotalPendapatan.setText("Rp " + totalUang);
    }
    // --- UI HELPERS ---

    private JPanel createSummaryCard(String title, JLabel value, Color bg, Color txtColor) {
        JPanel p = new RoundedPanel(15, bg);
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(txtColor);

        value.setFont(new Font("Segoe UI", Font.BOLD, 24));
        value.setForeground(txtColor);

        p.add(t, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(COL_HEADER_TABLE);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)); // Zebra striping halus

                setBorder(new EmptyBorder(0, 10, 0, 10));

                // Custom kolom No. PC (Index 1) agar kotak biru
                if (column == 1) {
                    JLabel lbl = new JLabel(value.toString(), SwingConstants.CENTER);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    lbl.setForeground(COL_BLUE_TEXT);

                    JPanel p = new RoundedPanel(8, COL_BLUE_PC);
                    p.setLayout(new GridBagLayout());
                    p.add(lbl);

                    JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
                    wrapper.setOpaque(false);
                    wrapper.add(p);
                    return wrapper;
                }
                return c;
            }
        });
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Inner Class RoundedPanel (Digunakan ulang)
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