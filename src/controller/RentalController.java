package controller;

import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.Vector;

public class RentalController {
    private final String FILE_PATH = "data_rental.ser";
    private DefaultTableModel model;

    public RentalController() {
        model = new DefaultTableModel(new String[]{"Nama", "WhatsApp", "Tempat", "Paket", "Jam Mulai", "Jam Selesai"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        muatData();
    }

    public DefaultTableModel getModel() { return model; }

    public String validasiInput(String nama, String wa) {
        if (nama.isEmpty() || wa.isEmpty()) return "Nama dan WA wajib diisi!";
        if (!nama.matches("[a-zA-Z\\s]+")) return "Nama harus huruf saja!";
        if (!wa.matches("[0-9]+")) return "WhatsApp harus angka saja!";
        return "OK";
    }

    // --- CRUD UPDATE ---

    public void tambahData(Object[] data) {
        model.addRow(data);
        simpanData();
    }

    // FITUR BARU: Update Data
    public void updateData(int row, Object[] data) {
        if (row >= 0 && row < model.getRowCount()) {
            for (int i = 0; i < data.length; i++) {
                model.setValueAt(data[i], row, i);
            }
            simpanData();
        }
    }

    public void hapusData(int row) {
        if (row >= 0) {
            model.removeRow(row);
            simpanData();
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
}