package com.example.tugas;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Vector;

public class HelloApplication extends JFrame {
    private JTextField tfNama, tfWa;
    private JComboBox<String> cbTempat, cbLokasi, cbJamMulai, cbJamSelesai;
    private JTable table;
    private DefaultTableModel model;
    private JTabbedPane tabbedPane;
    private final String FILE_PATH = "data_rental.ser";

    public HelloApplication() {
        setTitle("Dashboard Rental Warnet");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        JPanel panelInput = buatPanelInput();
        JPanel panelData = buatPanelData();

        tabbedPane.addTab("Form Pemesanan", panelInput);
        tabbedPane.addTab("Lihat Data Tabel", panelData);

        add(tabbedPane);

        // Load data awal
        muatData();

        // Auto save saat tutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                simpanData();
            }
        });
    }

    // TAB 1: PANEL UNTUK INPUT DATA
    private JPanel buatPanelInput() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JPanel formGrid = new JPanel(new GridLayout(6, 2, 15, 15));

        tfNama = new JTextField();
        tfWa = new JTextField();
        cbTempat = new JComboBox<>(new String[]{"Begawan", "Sulfat"});
        cbLokasi = new JComboBox<>(new String[]{"Reguler GEMERS 1", "Reguler GEMERS 2", "VVIP", "VVIP ++"});
        cbJamMulai = new JComboBox<>(jamOptions());
        cbJamSelesai = new JComboBox<>(jamOptions());

        formGrid.add(new JLabel("Nama Reservasi:")); formGrid.add(tfNama);
        formGrid.add(new JLabel("No WhatsApp:")); formGrid.add(tfWa);
        formGrid.add(new JLabel("Tempat Rental:")); formGrid.add(cbTempat);
        formGrid.add(new JLabel("Lokasi / Paket:")); formGrid.add(cbLokasi);
        formGrid.add(new JLabel("Jam Mulai:")); formGrid.add(cbJamMulai);
        formGrid.add(new JLabel("Jam Selesai:")); formGrid.add(cbJamSelesai);


        // Panel Tombol di bawah form
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));


        JButton btnTambah = new JButton("Tambah Pesanan");
        JButton btnReset = new JButton("Bersihkan Form");

        btnTambah.setBackground(new Color(33, 150, 243));
        btnTambah.setForeground(Color.WHITE);
        //PANEL TAMBAH


        btnReset.addActionListener(e -> resetForm());

        btnTambah.addActionListener(e -> {
            if (validasiInput()) {
                tambahData();
                JOptionPane.showMessageDialog(this, "Berhasil Menambah Data! Silakan cek di tab 'Lihat Data'.");
                tabbedPane.setSelectedIndex(1); // Otomatis pindah ke tab data setelah isi
            }
        });


        btnPanel.add(btnTambah);

        btnPanel.add(btnReset);

        mainPanel.add(formGrid, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    // TAB 2: PANEL UNTUK MELIHAT TABEL DATA
    private JPanel buatPanelData() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(new String[]{"Nama", "WhatsApp", "Tempat", "Paket", "Jam Mulai", "Jam Selesai"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(25);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Tombol aksi untuk data yang dipilih
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnHapus = new JButton("Hapus Data Terpilih");
        JButton btnUpdate = new JButton("Edit Data");

        btnHapus.setBackground(new Color(244, 67, 54));
        btnHapus.setForeground(Color.WHITE);

        btnHapus.addActionListener(e -> hapusData());
        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                isiFormDariTable();
                tabbedPane.setSelectedIndex(0); // Pindah ke tab input untuk edit
            } else {
                JOptionPane.showMessageDialog(this, "Pilih data di tabel yang ingin diedit!");
            }
        });

        actionPanel.add(btnUpdate);
        actionPanel.add(btnHapus);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private boolean validasiInput() {
        String nama = tfNama.getText().trim();
        String wa = tfWa.getText().trim();
        if (nama.isEmpty() || wa.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Nama dan WA wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!nama.matches("[a-zA-Z\\s]+")) {
            JOptionPane.showMessageDialog(this, "Nama harus huruf saja!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!wa.matches("[0-9]+")) {
            JOptionPane.showMessageDialog(this, "WhatsApp harus angka saja!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private String[] jamOptions() {
        String[] jam = new String[16];
        for (int i = 0; i < 16; i++) jam[i] = String.format("%02d.00", i + 8);
        return jam;
    }

    private void tambahData() {
        model.addRow(new Object[]{tfNama.getText(), tfWa.getText(), cbTempat.getSelectedItem(), cbLokasi.getSelectedItem(), cbJamMulai.getSelectedItem(), cbJamSelesai.getSelectedItem()});
        resetForm();
    }

    private void hapusData() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            if (JOptionPane.showConfirmDialog(this, "Hapus data ini?", "Konfirmasi", 0) == 0) model.removeRow(row);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data dulu!");
        }
    }

    private void resetForm() {
        tfNama.setText(""); tfWa.setText("");
        table.clearSelection();
    }

    private void isiFormDariTable() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            tfNama.setText(model.getValueAt(row, 0).toString());
            tfWa.setText(model.getValueAt(row, 1).toString());
            cbTempat.setSelectedItem(model.getValueAt(row, 2));
            cbLokasi.setSelectedItem(model.getValueAt(row, 3));
            cbJamMulai.setSelectedItem(model.getValueAt(row, 4));
            cbJamSelesai.setSelectedItem(model.getValueAt(row, 5));
        }
    }

    private void simpanData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(model.getDataVector());
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void muatData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            Vector<Vector> data = (Vector<Vector>) ois.readObject();
            for (Vector row : data) model.addRow(row);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HelloApplication().setVisible(true));
    }
}