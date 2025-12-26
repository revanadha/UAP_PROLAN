package controller;

import util.DataStorage; // Import penyimpanan baru
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
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
        // PERBAIKAN: Tambah kolom "Tanggal" di urutan terakhir (Index 6)
        String[] columns = {"Nama", "WA", "Tempat", "Paket", "Jam Mulai", "Jam Selesai", "Tanggal"};

        modelActive = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        modelHistory = new DefaultTableModel(columns, 0) {
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
                    if (sekarang.isAfter(jamSelesai) && sekarang.isBefore(jamMulai)) sudahSelesai = true;
                } else {
                    if (sekarang.isAfter(jamSelesai)) sudahSelesai = true;
                }

                if (sudahSelesai) {
                    Vector rowData = (Vector) modelActive.getDataVector().get(i);
                    // Pindahkan SEMUA data termasuk Tanggal ke history
                    modelHistory.addRow(new Object[]{
                            rowData.get(0), rowData.get(1), rowData.get(2),
                            rowData.get(3), rowData.get(4), rowData.get(5),
                            rowData.get(6) // Tanggal ikut dipindah
                    });
                    modelActive.removeRow(i);
                    adaPerubahan = true;
                }
            } catch (Exception ex) {}
        }
        if (adaPerubahan) simpanData();
    }

    public boolean isPCBooked(String pcName) {
        for (int i = 0; i < modelActive.getRowCount(); i++) {
            if (modelActive.getValueAt(i, 2).toString().equalsIgnoreCase(pcName)) return true;
        }
        return false;
    }

    // Update Tambah Data: Otomatis masukkan Tanggal Hari Ini
    public void tambahData(Object[] data) {
        // Data dari view hanya 6 item, kita tambah item ke-7 yaitu Tanggal
        Object[] fullData = new Object[7];
        System.arraycopy(data, 0, fullData, 0, 6);

        // Simpan tanggal format dd/MM/yyyy
        fullData[6] = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        modelActive.addRow(fullData);
        simpanData();
    }

    public void updateData(int row, String nama, String pc, String durasi, String jamSelesai) {
        if (row >= 0 && row < modelActive.getRowCount()) {
            modelActive.setValueAt(nama, row, 0);
            modelActive.setValueAt(pc, row, 2);
            modelActive.setValueAt(durasi, row, 3);
            modelActive.setValueAt(jamSelesai, row, 5);
            simpanData();
        }
    }

    public void hapusData(int row) {
        if (row >= 0) {
            modelActive.removeRow(row);
            simpanData();
        }
    }

    // --- MENGGUNAKAN CLASS BARU (DataStorage) ---
    private void simpanData() {
        DataStorage.simpan(FILE_ACTIVE, modelActive);
        DataStorage.simpan(FILE_HISTORY, modelHistory);
    }

    private void muatData() {
        DataStorage.muat(FILE_ACTIVE, modelActive);
        DataStorage.muat(FILE_HISTORY, modelHistory);
    }
}