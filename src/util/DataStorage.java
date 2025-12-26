package util;

import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.Vector;

public class DataStorage {

    // Menyimpan data tabel ke file .ser
    public static void simpan(String filepath, DefaultTableModel model) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
            oos.writeObject(model.getDataVector());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Memuat data dari file .ser ke tabel
    public static void muat(String filepath, DefaultTableModel model) {
        File file = new File(filepath);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath))) {
            Vector<Vector> data = (Vector<Vector>) ois.readObject();
            for (Vector row : data) {
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}