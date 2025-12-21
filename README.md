# Pengecek Tahun Kabisat 🗓️

Aplikasi konsol sederhana berbasis Java untuk menentukan apakah suatu tahun adalah Tahun Kabisat.
Program ini dirancang sebagai contoh praktik *refactoring* dengan mengimplementasikan prinsip-prinsip seperti:
**Extract Method**, **Inline Variable**, **Move Method**, **Introduce Parameter Object**, dan **Rename Method/Variable**.

## Struktur Program (Hasil Refactoring)

Program telah dibagi menjadi beberapa komponen untuk meningkatkan modularitas dan keterbacaan:

1.  **`MainApp`**: Kelas utama yang menangani *input* dari pengguna dan mengorkestrasi proses.
2.  **`ITahunChecker`** (Interface) & **`TahunKabisatImpl`**: Mengandung **logika bisnis** utama untuk menentukan kabisat. Terpisah dari I/O.
    * *Refactoring:* **Extract Method** (untuk `isDivisibleBy4`, `isDivisibleBy100`, `isDivisibleBy400`) dan **Inline Variable**.
3.  **`OutputPrinter`**: Bertanggung jawab khusus untuk **mencetak hasil** ke konsol. Terpisah dari logika pengecekan.
    * *Refactoring:* **Move Method/Field**.
4.  **`YearData`**: Objek data sederhana (Parameter Object) untuk mengemas tahun dan label output.
    * *Refactoring:* **Introduce Parameter Object**.

## Cara Menjalankan

### Persyaratan

* Java Development Kit (JDK) 8 atau yang lebih baru.

### Kompilasi dan Eksekusi

Asumsikan semua file berada dalam satu direktori tanpa *package*:

1.  **Kompilasi:**
    ```bash
    javac MainApp.java TahunKabisatImpl.java OutputPrinter.java YearData.java
    ```

2.  **Eksekusi:**
    ```bash
    java MainApp
    ```

### Contoh Sesi
```bash
    === Pengecek Tahun Kabisat === 
    Masukkan tahun yang ingin Anda cek: 2024 
    Hasil: 2024 adalah Tahun Kabisat.
```
