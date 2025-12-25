package main;

import controller.RentalController;
import javax.swing.*;
import java.awt.*;

public class Riwayat extends JDialog {

    public Riwayat(JFrame parent, RentalController controller) {
        super(parent, "Riwayat Transaksi", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);

        // Mengambil model tabel yang sama dari controller
        JTable tableHistory = new JTable(controller.getModel());
        tableHistory.setEnabled(false); // Read-only (tidak bisa diedit/klik)

        add(new JLabel("   Data Riwayat Pesanan Saat Ini:"), BorderLayout.NORTH);
        add(new JScrollPane(tableHistory), BorderLayout.CENTER);

        JButton btnTutup = new JButton("Tutup");
        btnTutup.addActionListener(e -> dispose());
        add(btnTutup, BorderLayout.SOUTH);
    }
}