# Aplikasi Pemesanan Warnet

Aplikasi Pemesanan Warnet adalah sebuah aplikasi desktop yang memungkinkan pengguna untuk melakukan pemesanan ruang komputer di warnet dengan menggunakan teknologi Java Swing. Aplikasi ini menerapkan operasi CRUD (Create, Read, Update, Delete) untuk mengelola data pemesanan.

## Fitur

- **Pemesanan**: Pengguna dapat melakukan pemesanan komputer di warnet.
- **Login**: Pengguna dapat masuk ke aplikasi dengan menggunakan kredensial yang valid.
- **Manajemen Data**: Admin dapat mengelola data pemesanan, termasuk menambah, mengedit, dan menghapus pemesanan.
- **Riwayat Pemesanan**: Melihat riwayat pemesanan sebelumnya.
- **Dashboard**: Menyajikan informasi mengenai status komputer yang tersedia dan terpakai.

## Teknologi yang Digunakan

- **Java**: Bahasa pemrograman utama untuk aplikasi.
- **Java Swing**: Untuk antarmuka pengguna grafis (GUI).
- **Serialization**: Untuk penyimpanan data (menggunakan file `.ser`).
- **Maven**: Untuk manajemen proyek dan dependensi.

## Struktur Proyek
UAP_PROLAN
│
├── src
│ ├── controller # Kode pengendali aplikasi
│ │ └── RentalController.java
│ ├── Main # Kelas utama aplikasi
│ │ └── App.java
│ ├── util # Utilitas tambahan seperti pengelolaan data
│ │ └── DataStorage.java
│ └── View # Tampilan aplikasi (GUI)
│ ├── Dashboard.java # Tampilan untuk dashboard
│ ├── Login.java # Tampilan untuk login
│ ├── Order.java # Tampilan untuk pemesanan
│ └── Riwayat.java # Tampilan riwayat pemesanan
│
├── target # Folder hasil build dan file serialization
│ ├── data_active.ser # Data pemesanan aktif
│ ├── data_history.ser # Riwayat pemesanan
│ ├── data_rental.ser # Data komputer yang disewa
│ └── mvnw # Wrapper Maven
│
├── module-info.java # Modul-info untuk pengaturan modul Java
├── README.md # File ini
└── pom.xml # File konfigurasi Maven


## Cara Menggunakan

1. **Clone Proyek**  
   Clone repositori ini ke dalam sistem Anda:
git clone https://github.com/revaandh/UAP_PROLAN.git


2. **Instalasi dan Setup**  
Pastikan Anda memiliki Java Development Kit (JDK) dan Maven terpasang di sistem Anda.  
- **JDK**: Unduh dari [situs resmi Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) atau gunakan OpenJDK.
- **Maven**: Ikuti instruksi di [situs resmi Maven](https://maven.apache.org/install.html).

3. **Build Proyek**  
Setelah semua tergantung terinstal, jalankan perintah berikut di direktori proyek:

4. **Jalankan Aplikasi**  
Untuk menjalankan aplikasi, Anda dapat menggunakan perintah berikut:

## Kontribusi

Jika Anda ingin berkontribusi pada proyek ini, silakan ikuti langkah-langkah berikut:

1. Fork repositori ini.
2. Buat branch baru (`git checkout -b fitur-baru`).
3. Lakukan perubahan yang diinginkan dan lakukan commit (`git commit -am 'Menambahkan fitur baru'`).
4. Push ke branch Anda (`git push origin fitur-baru`).
5. Kirim pull request.

## Lisensi

Proyek ini dilisensikan di bawah Lisensi MIT - lihat [LICENSE](LICENSE) untuk detail.

## Kontak

- **Nama**: [Bayu adi nugroho & Revansa Adha Dwi Yuliarto]
- **Email**: [Bayu888@gmail.com]

