# SISKA

SISKA adalah aplikasi desktop untuk mengelola **sistem informasi absensi dan kartu izin siswa**. Aplikasi dibangun dengan Java Swing, menggunakan MySQL sebagai penyimpanan data, serta JasperReports untuk pembuatan laporan.

## Fitur utama

- Autentikasi admin menggunakan username/password atau RFID.
- Pengelolaan data siswa dan akun admin.
- Pencatatan, pencarian, dan pemfilteran absensi berdasarkan tanggal maupun kelas.
- Pengelolaan kartu izin siswa.
- Rekap absensi harian, bulanan, dan semester.
- Visualisasi statistik absensi dan siswa.
- Cetak laporan melalui JasperReports.

## Teknologi

- Java 8
- Java Swing
- Apache Ant dan NetBeans
- MySQL 8
- MySQL Connector/J
- JasperReports 5
- JFreeChart
- JCalendar

## Struktur proyek

```text
.
├── src/
│   ├── form/            # Source Java, form NetBeans, dan template laporan
│   └── icon/            # Aset ikon aplikasi
├── Laporan/             # Template laporan tambahan
├── nbproject/           # Konfigurasi proyek NetBeans
├── siska.sql            # Skema dan data contoh database
├── build.xml            # Skrip build Apache Ant
└── manifest.mf          # Manifest aplikasi
```

## Prasyarat

Pastikan perangkat telah memiliki:

1. JDK 8.
2. Apache NetBeans dengan dukungan proyek Java Ant.
3. MySQL Server 8.x.
4. Library Java yang tercantum pada bagian [Dependensi](#dependensi).

## Instalasi

### 1. Clone repository

```bash
git clone https://github.com/jayabelajar/siska.git
cd siska
```

### 2. Siapkan database

Buat database bernama `siska`, lalu impor dump yang tersedia:

```bash
mysql -u root -p siska < siska.sql
```

Alternatifnya, impor [`siska.sql`](siska.sql) melalui phpMyAdmin atau aplikasi pengelola MySQL lainnya.

### 3. Atur koneksi database

Sesuaikan nilai URL, username, dan password MySQL di `src/form/Koneksi.java`:

```java
String url = "jdbc:mysql://localhost:3306/siska";
String user = "root";
String password = "";
```

> Jangan commit kredensial database produksi. Konfigurasi bawaan hanya ditujukan untuk lingkungan pengembangan lokal.

### 4. Konfigurasikan dependensi

Buka proyek melalui NetBeans, kemudian tambahkan library berikut melalui **Project Properties > Libraries**:

- AbsoluteLayout
- JCalendar 1.4
- MySQL Connector/J
- JasperReports 5.0.0 beserta dependensinya
- JFreeChart 1.0.19 beserta JCommon
- JUnit 4.11 dan Hamcrest Core 1.3 untuk pengujian

Konfigurasi `nbproject/project.properties` masih memuat beberapa path JAR lokal dari mesin pengembang sebelumnya. Arahkan ulang referensi tersebut ke lokasi library pada perangkat Anda sebelum melakukan build.

### 5. Jalankan aplikasi

Jalankan class utama `form.Masuk` melalui NetBeans, atau gunakan Ant setelah seluruh dependensi terkonfigurasi:

```bash
ant clean jar
java -jar dist/siska.jar
```

## Akun demo

Dump database menyediakan akun contoh berikut untuk pengujian lokal:

| Username | Password | RFID |
|---|---|---|
| `admin` | `1234` | `123` |
| `admin2` | `12345` | `456` |

> Akun tersebut menggunakan password teks biasa dan tidak boleh dipakai di lingkungan produksi. Ganti seluruh kredensial contoh serta terapkan hashing password sebelum deployment nyata.

## Catatan pengembangan

- File dalam `build/` dan `dist/` merupakan hasil kompilasi dan tidak disimpan di Git.
- File `.jasper` dipertahankan karena saat ini dimuat langsung oleh aplikasi ketika mencetak laporan.
- Perubahan antarmuka sebaiknya dilakukan melalui NetBeans GUI Builder agar file `.java` dan `.form` tetap sinkron.

## Kontribusi

1. Buat branch dari `main`.
2. Lakukan perubahan dan pengujian yang diperlukan.
3. Gunakan pesan commit yang jelas dan ringkas.
4. Ajukan pull request dengan deskripsi perubahan.
