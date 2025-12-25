package main;

import controller.RentalController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Vector;

public class tabel extends JFrame {
    private RentalController controller;
    private JTable table;

    public tabel() {
        controller = new RentalController();

        setTitle("Dashboard Admin - Rental Warnet");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. NAVIGATION BAR ---
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        navPanel.setBackground(Color.DARK_GRAY); // Warna lebih modern

        JButton btnTambah = new JButton("Tambah (+)");
        JButton btnRiwayat = new JButton("Riwayat");
        JButton btnKeluar = new JButton("Logout");

        styleButton(btnTambah, new Color(33, 150, 243));
        styleButton(btnRiwayat, new Color(255, 152, 0));
        styleButton(btnKeluar, new Color(244, 67, 54));

        navPanel.add(btnTambah);
        navPanel.add(btnRiwayat);
        navPanel.add(Box.createHorizontalGlue()); // Dorong logout ke kanan (jika pakai BoxLayout, tapi flow cukup spasi)
        navPanel.add(btnKeluar);

        add(navPanel, BorderLayout.NORTH);

        // --- 2. TABEL ---
        table = new JTable(controller.getModel());
        table.setRowHeight(35); // Baris lebih tinggi
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- 3. PANEL AKSI (BAWAH) ---
        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBawah.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnEdit = new JButton("Edit Data");
        JButton btnHapus = new JButton("Hapus Data");

        styleButton(btnEdit, new Color(76, 175, 80)); // Hijau
        styleButton(btnHapus, new Color(150, 0, 0)); // Merah

        panelBawah.add(btnEdit);
        panelBawah.add(btnHapus);
        add(panelBawah, BorderLayout.SOUTH);

        // --- EVENT HANDLING ---

        // Tambah Baru (Index -1)
        btnTambah.addActionListener(e -> {
            new order(this, controller, -1, null).setVisible(true);
        });

        // Edit Data
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                // Ambil data dari baris terpilih
                int colCount = table.getColumnCount();
                Object[] currentData = new Object[colCount];
                for (int i = 0; i < colCount; i++) {
                    currentData[i] = table.getValueAt(row, i);
                }

                // Buka form order dengan mode EDIT
                new order(this, controller, row, currentData).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit!");
            }
        });

        // Hapus Data
        btnHapus.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                if(JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == 0) {
                    controller.hapusData(row);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris data yang ingin dihapus!");
            }
        });

        btnRiwayat.addActionListener(e -> new Riwayat(this, controller).setVisible(true));
        btnKeluar.addActionListener(e -> {
            new main().setVisible(true);
            dispose();
        });
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(140, 40));
    }
}