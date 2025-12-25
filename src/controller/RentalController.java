package controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class RentalController {
    private final String FILE_ACTIVE = "data_active.ser";
    private final String FILE_HISTORY = "data_history.ser";

    private DefaultTableModel modelActive;
    private DefaultTableModel modelHistory;
    private Timer timerCekWaktu;

    public RentalController() {
        // Model Aktif
        modelActive = new DefaultTableModel(new String[]{"Nama", "WA", "Tempat", "Paket", "Jam Mulai", "Jam Selesai"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Model Riwayat
        modelHistory = new DefaultTableModel(new String[]{"Nama", "WA", "Tempat", "Paket", "Jam Mulai", "Jam Selesai"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        muatData();
        jalankanTimerOtomatis();
    }

    public DefaultTableModel getModel() { return modelActive; }
    public DefaultTableModel getModelHistory() { return modelHistory; }

    private void jalankanTimerOtomatis() {
        timerCekWaktu = new Timer(5000, e -> cekPesananKadaluarsa());
        timerCekWaktu.start();
    }

    private void cekPesananKadaluarsa() {
        LocalTime sekarang = LocalTime.now();
        boolean adaPerubahan = false;

        for (int i = modelActive.getRowCount() - 1; i >= 0; i--) {
            try {
                String jamMulaiStr = modelActive.getValueAt(i, 4).toString().replace(".", ":").trim();
                String jamSelesaiStr = modelActive.getValueAt(i, 5).toString().replace(".", ":").trim();

                LocalTime jamMulai = LocalTime.parse(jamMulaiStr, DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime jamSelesai = LocalTime.parse(jamSelesaiStr, DateTimeFormatter.ofPattern("HH:mm"));

                boolean sudahSelesai = false;

                if (jamSelesai.isBefore(jamMulai)) {
                    // KASUS LINTAS HARI (Contoh: Mulai 23.00, Selesai 01.00)
                    // Pesanan selesai jika:
                    // Sekarang > Jam Selesai TAPI Sekarang < Jam Mulai
                    // (Artinya sudah lewat tengah malam dan lewat jam selesai)
                    if (sekarang.isAfter(jamSelesai) && sekarang.isBefore(jamMulai)) {
                        sudahSelesai = true;
                    }
                } else {
                    // KASUS NORMAL (Hari yang sama)
                    if (sekarang.isAfter(jamSelesai)) {
                        sudahSelesai = true;
                    }
                }

                if (sudahSelesai) {
                    Vector rowData = (Vector) modelActive.getDataVector().get(i);
                    modelHistory.addRow(new Object[]{
                            rowData.get(0), rowData.get(1), rowData.get(2),
                            rowData.get(3), rowData.get(4), rowData.get(5)
                    });
                    modelActive.removeRow(i);
                    adaPerubahan = true;
                }
            } catch (Exception ex) {
                // Abaikan error format jam, jangan hapus data
            }
        }

        if (adaPerubahan) simpanData();
    }

    public boolean isPCBooked(String pcName) {
        // Loop semua data di tabel aktif
        for (int i = 0; i < modelActive.getRowCount(); i++) {
            // Kolom "Tempat" ada di index ke-2
            String bookedPC = modelActive.getValueAt(i, 2).toString();

            // Jika nama PC sama (misal "PC 1" == "PC 1"), berarti sedang dipakai
            if (bookedPC.equalsIgnoreCase(pcName)) {
                return true;
            }
        }
        return false; // Tidak ditemukan, berarti PC Aman (Kosong)
    }

    public void tambahData(Object[] data) {
        modelActive.addRow(data);
        simpanData();
    }

    public void hapusData(int row) {
        if (row >= 0) {
            modelActive.removeRow(row);
            simpanData();
        }
    }

    private void simpanData() {
        simpanFile(FILE_ACTIVE, modelActive.getDataVector());
        simpanFile(FILE_HISTORY, modelHistory.getDataVector());
    }

    private void simpanFile(String path, Vector data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(data);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void muatData() {
        muatFile(FILE_ACTIVE, modelActive);
        muatFile(FILE_HISTORY, modelHistory);
    }

    private void muatFile(String path, DefaultTableModel model) {
        File file = new File(path);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Vector<Vector> data = (Vector<Vector>) ois.readObject();
            for (Vector row : data) model.addRow(row);
        } catch (Exception e) { e.printStackTrace(); }
    }
}