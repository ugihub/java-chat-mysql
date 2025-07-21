-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Waktu pembuatan: 21 Jul 2025 pada 01.25
-- Versi server: 8.0.30
-- Versi PHP: 8.3.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `chatapp`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `invite`
--

CREATE TABLE `invite` (
  `id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `room_private_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `message`
--

CREATE TABLE `message` (
  `id` bigint NOT NULL,
  `content` text NOT NULL,
  `timestamp` datetime DEFAULT CURRENT_TIMESTAMP,
  `user_id` bigint DEFAULT NULL,
  `room_public_id` bigint DEFAULT NULL,
  `room_private_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `message`
--

INSERT INTO `message` (`id`, `content`, `timestamp`, `user_id`, `room_public_id`, `room_private_id`) VALUES
(99, 'lomba sihir', '2025-07-06 17:19:07', 1, 12, NULL),
(101, 'p[ppppp', '2025-07-11 11:25:39', 1, 12, NULL),
(103, 'lomba sihirrr\\', '2025-07-20 07:52:30', 1, 12, NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `room_private`
--

CREATE TABLE `room_private` (
  `id` bigint NOT NULL,
  `name` varchar(100) NOT NULL,
  `invite_code` varchar(50) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `owner_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `room_private`
--

INSERT INTO `room_private` (`id`, `name`, `invite_code`, `user_id`, `owner_id`) VALUES
(24, 'master', '73C666CC', NULL, 2),
(25, 'Matcha', '0E35E0E2', NULL, 1);

-- --------------------------------------------------------

--
-- Struktur dari tabel `room_private_invitation`
--

CREATE TABLE `room_private_invitation` (
  `id` bigint NOT NULL,
  `status` varchar(255) NOT NULL,
  `invited_user_id` bigint NOT NULL,
  `room_private_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `room_private_invitation`
--

INSERT INTO `room_private_invitation` (`id`, `status`, `invited_user_id`, `room_private_id`) VALUES
(14, 'APPROVED', 1, 24),
(15, 'DENIED', 2, 25);

-- --------------------------------------------------------

--
-- Struktur dari tabel `room_private_participants`
--

CREATE TABLE `room_private_participants` (
  `room_id` bigint NOT NULL,
  `user_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `room_private_participants`
--

INSERT INTO `room_private_participants` (`room_id`, `user_id`) VALUES
(24, 1);

-- --------------------------------------------------------

--
-- Struktur dari tabel `room_public`
--

CREATE TABLE `room_public` (
  `id` bigint NOT NULL,
  `name` varchar(100) NOT NULL,
  `owner_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `room_public`
--

INSERT INTO `room_public` (`id`, `name`, `owner_id`) VALUES
(12, 'obrolan jam 3 pagi', 1),
(13, 'master', 1);

-- --------------------------------------------------------

--
-- Struktur dari tabel `user`
--

CREATE TABLE `user` (
  `id` bigint NOT NULL,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data untuk tabel `user`
--

INSERT INTO `user` (`id`, `username`, `email`, `password`) VALUES
(1, 'Ugi', 'ugisugiman6@gmail.com', '$2a$10$0VtoWcwcscHySEx9FIyePuDOL7JXuQYJ79vxLQFRZDMR2YMeHHdIy'),
(2, 'master', 'ugisugiman86@gmail.com', '$2a$10$m5hp9i1x3rXP2/wYh5VbyOJUy.Y.pSG/1OtLR.giKTS5AdYd/r7ee');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `invite`
--
ALTER TABLE `invite`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `room_private_id` (`room_private_id`);

--
-- Indeks untuk tabel `message`
--
ALTER TABLE `message`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `room_public_id` (`room_public_id`),
  ADD KEY `room_private_id` (`room_private_id`);

--
-- Indeks untuk tabel `room_private`
--
ALTER TABLE `room_private`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `invite_code` (`invite_code`),
  ADD KEY `FK2rol6fqv82cdjst8fgb9es7f0` (`user_id`),
  ADD KEY `FKnn7o7hy690q6qwu37v37e6s6r` (`owner_id`);

--
-- Indeks untuk tabel `room_private_invitation`
--
ALTER TABLE `room_private_invitation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKjuhrv68euaqv8ixcww8x9wywu` (`invited_user_id`),
  ADD KEY `FKrar40l0g9gvr0qyy7f611vc9q` (`room_private_id`);

--
-- Indeks untuk tabel `room_private_participants`
--
ALTER TABLE `room_private_participants`
  ADD PRIMARY KEY (`room_id`,`user_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indeks untuk tabel `room_public`
--
ALTER TABLE `room_public`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKevei3u3iaqgq4b0tvhwqma2e6` (`owner_id`);

--
-- Indeks untuk tabel `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `invite`
--
ALTER TABLE `invite`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `message`
--
ALTER TABLE `message`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=105;

--
-- AUTO_INCREMENT untuk tabel `room_private`
--
ALTER TABLE `room_private`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT untuk tabel `room_private_invitation`
--
ALTER TABLE `room_private_invitation`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT untuk tabel `room_public`
--
ALTER TABLE `room_public`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT untuk tabel `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `invite`
--
ALTER TABLE `invite`
  ADD CONSTRAINT `invite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `invite_ibfk_2` FOREIGN KEY (`room_private_id`) REFERENCES `room_private` (`id`);

--
-- Ketidakleluasaan untuk tabel `message`
--
ALTER TABLE `message`
  ADD CONSTRAINT `message_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `message_ibfk_2` FOREIGN KEY (`room_public_id`) REFERENCES `room_public` (`id`),
  ADD CONSTRAINT `message_ibfk_3` FOREIGN KEY (`room_private_id`) REFERENCES `room_private` (`id`);

--
-- Ketidakleluasaan untuk tabel `room_private`
--
ALTER TABLE `room_private`
  ADD CONSTRAINT `FK2rol6fqv82cdjst8fgb9es7f0` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKnn7o7hy690q6qwu37v37e6s6r` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`);

--
-- Ketidakleluasaan untuk tabel `room_private_invitation`
--
ALTER TABLE `room_private_invitation`
  ADD CONSTRAINT `FKjuhrv68euaqv8ixcww8x9wywu` FOREIGN KEY (`invited_user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKrar40l0g9gvr0qyy7f611vc9q` FOREIGN KEY (`room_private_id`) REFERENCES `room_private` (`id`);

--
-- Ketidakleluasaan untuk tabel `room_private_participants`
--
ALTER TABLE `room_private_participants`
  ADD CONSTRAINT `room_private_participants_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `room_private` (`id`),
  ADD CONSTRAINT `room_private_participants_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Ketidakleluasaan untuk tabel `room_public`
--
ALTER TABLE `room_public`
  ADD CONSTRAINT `FKevei3u3iaqgq4b0tvhwqma2e6` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
