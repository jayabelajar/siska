-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Waktu pembuatan: 17 Okt 2026 pada 05.46
-- Versi server: 8.0.30
-- Versi PHP: 8.4.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Basis data: `siska`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `absensi`
--

CREATE TABLE `absensi` (
  `id_absensi` int NOT NULL,
  `NIS` int DEFAULT NULL,
  `STATUS` enum('Hadir','Sakit','Izin','Alpa') NOT NULL,
  `Tanggal` date NOT NULL,
  `Waktu_Masuk` time DEFAULT NULL,
  `Waktu_Pulang` time DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data untuk tabel `absensi`
--

INSERT INTO `absensi` (`id_absensi`, `NIS`, `STATUS`, `Tanggal`, `Waktu_Masuk`, `Waktu_Pulang`) VALUES
(1, 101, 'Hadir', '2025-05-25', '07:00:00', '15:00:00'),
(2, 102, 'Sakit', '2025-05-25', '00:00:00', '00:00:00'),
(3, 103, 'Izin', '2025-05-25', '09:00:00', '15:00:00'),
(4, 101, 'Hadir', '2025-04-15', '07:00:00', '15:00:00'),
(5, 102, 'Izin', '2025-04-15', '08:00:00', '15:00:00'),
(6, 103, 'Sakit', '2025-04-15', '00:00:00', '00:00:00'),
(7, 101, 'Hadir', '2026-10-17', '07:00:00', '15:00:00'),
(8, 102, 'Hadir', '2026-10-17', '07:00:00', '15:00:00'),
(9, 103, 'Hadir', '2026-10-17', '07:00:00', '15:00:00');

-- --------------------------------------------------------

--
-- Struktur dari tabel `admin`
--

CREATE TABLE `admin` (
  `id_admin` int NOT NULL,
  `Nama` varchar(100) NOT NULL,
  `Username` varchar(50) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `RFID` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data untuk tabel `admin`
--

INSERT INTO `admin` (`id_admin`, `Nama`, `Username`, `Password`, `RFID`) VALUES
(1, 'admin', 'admin', '1234', '123'),
(2, 'admin2', 'admin2', '12345', '456');

-- --------------------------------------------------------

--
-- Struktur dari tabel `kartu_izin`
--

CREATE TABLE `kartu_izin` (
  `id_kartu_izin` int NOT NULL,
  `NIS` int DEFAULT NULL,
  `Status_Izin` enum('Sakit','Izin') NOT NULL,
  `Jam_Keluar` time NOT NULL,
  `Jam_Kembali` time NOT NULL DEFAULT '15:00:00',
  `Keterangan` text,
  `id_absensi` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data untuk tabel `kartu_izin`
--

INSERT INTO `kartu_izin` (`id_kartu_izin`, `NIS`, `Status_Izin`, `Jam_Keluar`, `Jam_Kembali`, `Keterangan`, `id_absensi`) VALUES
(5, 101, 'Sakit', '03:20:02', '05:20:02', 'sakit flu', NULL),
(6, 102, 'Izin', '12:32:59', '14:32:59', '', NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `siswa`
--

CREATE TABLE `siswa` (
  `NIS` int NOT NULL,
  `Nama` varchar(100) NOT NULL,
  `Kelas` enum('7','8','9') NOT NULL,
  `jenis_kelamin` enum('L','P') NOT NULL DEFAULT 'L'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data untuk tabel `siswa`
--

INSERT INTO `siswa` (`NIS`, `Nama`, `Kelas`, `jenis_kelamin`) VALUES
(101, 'Ahmad Fikri', '7', 'L'),
(102, 'Siti Aminah', '8', 'P'),
(103, 'Budi Santoso', '9', 'L');

--
-- Indeks untuk tabel yang dibuang
--

--
-- Indeks untuk tabel `absensi`
--
ALTER TABLE `absensi`
  ADD PRIMARY KEY (`id_absensi`),
  ADD KEY `NIS` (`NIS`);

--
-- Indeks untuk tabel `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id_admin`),
  ADD UNIQUE KEY `Username` (`Username`);

--
-- Indeks untuk tabel `kartu_izin`
--
ALTER TABLE `kartu_izin`
  ADD PRIMARY KEY (`id_kartu_izin`),
  ADD UNIQUE KEY `unique_izin` (`NIS`,`id_absensi`),
  ADD KEY `NIS` (`NIS`),
  ADD KEY `fk_izin_absensi` (`id_absensi`);

--
-- Indeks untuk tabel `siswa`
--
ALTER TABLE `siswa`
  ADD PRIMARY KEY (`NIS`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `absensi`
--
ALTER TABLE `absensi`
  MODIFY `id_absensi` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT untuk tabel `admin`
--
ALTER TABLE `admin`
  MODIFY `id_admin` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT untuk tabel `kartu_izin`
--
ALTER TABLE `kartu_izin`
  MODIFY `id_kartu_izin` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `absensi`
--
ALTER TABLE `absensi`
  ADD CONSTRAINT `absensi_ibfk_1` FOREIGN KEY (`NIS`) REFERENCES `siswa` (`NIS`);

--
-- Ketidakleluasaan untuk tabel `kartu_izin`
--
ALTER TABLE `kartu_izin`
  ADD CONSTRAINT `fk_izin_absensi` FOREIGN KEY (`id_absensi`) REFERENCES `absensi` (`id_absensi`),
  ADD CONSTRAINT `kartu_izin_ibfk_1` FOREIGN KEY (`NIS`) REFERENCES `siswa` (`NIS`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
