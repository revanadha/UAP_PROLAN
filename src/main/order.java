package main;

import controller.RentalController;
import javax.swing.*;
import java.awt.*;

public class order extends JDialog {
    private JTextField tfNama, tfWa;
    private JComboBox<String> cbTempat, cbLokasi, cbJamMulai, cbJamSelesai;
    private int editRowIndex = -1; // -1 artinya Mode Tambah Baru

    // Konstruktor Overloading: Jika index == -1 (Tambah), jika index >= 0 (Edit)
    public order(JFrame parent, RentalController controller, int rowIndex, Object[] existingData) {
        super(parent, rowIndex == -1 ? "Form Tambah Pesanan" : "Edit Pesanan", true);
        this.editRowIndex = rowIndex;

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input Fields
        tfNama = new JTextField();
        tfWa = new JTextField();
        cbTempat = new JComboBox<>(new String[]{"Begawan", "Sulfat"});
        cbLokasi = new JComboBox<>(new String[]{"Reguler GEMERS 1", "Reguler GEMERS 2", "VVIP"});

        String[] jam = new String[16];
        for (int i = 0; i < 16; i++) jam[i] = String.format("%02d.00", i + 8);
        cbJamMulai = new JComboBox<>(jam);
        cbJamSelesai = new JComboBox<>(jam);

        // Jika mode EDIT, isi form dengan data lama
        if (existingData != null) {
            tfNama.setText(existingData[0].toString());
            tfWa.setText(existingData[1].toString());
            cbTempat.setSelectedItem(existingData[2].toString());
            cbLokasi.setSelectedItem(existingData[3].toString());
            cbJamMulai.setSelectedItem(existingData[4].toString());
            cbJamSelesai.setSelectedItem(existingData[5].toString());
        }

        formPanel.add(new JLabel("Nama Pemesan:")); formPanel.add(tfNama);
        formPanel.add(new JLabel("WhatsApp:")); formPanel.add(tfWa);
        formPanel.add(new JLabel("Cabang:")); formPanel.add(cbTempat);
        formPanel.add(new JLabel("Paket PC/Console:")); formPanel.add(cbLokasi);
        formPanel.add(new JLabel("Jam Mulai:")); formPanel.add(cbJamMulai);
        formPanel.add(new JLabel("Jam Selesai:")); formPanel.add(cbJamSelesai);

        JButton btnSimpan = new JButton(rowIndex == -1 ? "Simpan Baru" : "Update Data");
        btnSimpan.setBackground(rowIndex == -1 ? new Color(33, 150, 243) : new Color(76, 175, 80)); // Biru (Add), Hijau (Edit)
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("SansSerif", Font.BOLD, 14));

        btnSimpan.addActionListener(e -> {
            String valid = controller.validasiInput(tfNama.getText(), tfWa.getText());
            if (valid.equals("OK")) {
                Object[] dataBaru = new Object[]{
                        tfNama.getText(), tfWa.getText(), cbTempat.getSelectedItem(),
                        cbLokasi.getSelectedItem(), cbJamMulai.getSelectedItem(), cbJamSelesai.getSelectedItem()
                };

                if (editRowIndex == -1) {
                    controller.tambahData(dataBaru);
                } else {
                    controller.updateData(editRowIndex, dataBaru);
                }

                JOptionPane.showMessageDialog(this, "Data Berhasil Disimpan!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, valid, "Error Validasi", JOptionPane.WARNING_MESSAGE);
            }
        });

        add(formPanel, BorderLayout.CENTER);
        add(btnSimpan, BorderLayout.SOUTH);
    }
}