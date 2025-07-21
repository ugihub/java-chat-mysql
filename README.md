# Aplikasi Chat Publik dan Privat

## 🎯 Fitur Utama
- **Room Publik**: semua pengguna bisa bergabung
- **Room Privat**: hanya bisa diakses dengan kode undangan
- **Sistem Undangan**: owner bisa setujui/tolak pengguna
- **Chat Real-Time**: menggunakan WebSocket (STOMP)
- **Hapus Pesan**: hapus semua/terpilih (hanya owner/pengirim)
- **Notifikasi**: `alert()` saat undangan disetujui/ditolak

## 🧰 Teknologi
- Spring Boot (Java)
- Thymeleaf (Frontend)
- WebSocket (STOMP)
- MySQL
- Spring Security
- Bootstrap 5

## 📁 Struktur Proyek
```
src/
├── main/
│   ├── java/
│   │   └── com.example.demo/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── service/
│   │       ├── controller/
│   │       └── config/
│   └── resources/
│       ├── templates/    → HTML Thymeleaf
│       ├── static/       → CSS/JS
│       └── application.properties
```

## 🚀 Cara Menjalankan
1. Clone proyek:
   ```bash
   git clone [repo-url]
   ```
2. Jalankan database MySQL:
   ```bash
   mysql -u root -p < database.sql
   ```
3. Build proyek:
   ```bash
   ./mvnw clean install
   ```
4. Jalankan aplikasi:
   ```bash
   java -jar target/demo.jar
   ```
5. Buka browser:
   ```bash
   http://localhost:8080/login
   ```

## 📋 Cara Penggunaan
1. **Login** dengan akun Anda
2. **Buat room privat** → sistem generate kode undangan
3. **Gabung room**:
   - Masukkan kode undangan → owner terima notifikasi
   - Owner klik **Setujui** → Anda bisa akses room
4. **Kirim pesan** → langsung muncul di room
5. **Hapus pesan**:
   - Pengirim bisa hapus pesan sendiri
   - Owner bisa hapus semua pesan

## 🛠️ Notifikasi
- Saat undangan disetujui/ditolak, Anda akan menerima notifikasi `alert()`:
  - ✅ "Undangan ke room [nama] disetujui"
  - ❌ "Undangan ke room [nama] ditolak"

## 🛡️ Error Handling
- **Error 500**: Pastikan relasi ManyToMany sesuai dengan tabel `room_private_participants`
- **Error 403**: Hanya owner yang bisa menghapus room/pesan
- **CSRF Token**: Tambahkan `X-CSRF-TOKEN` di header request

## 🗃️ Struktur Database
Kamu Bisa langsung impor database nya menggunakan file schema.sql yang ada di folder demo. Jangan lupa untuk menyesuaikan application.properties dengan database milik kamu.

```
user
  id
  username
  password
  email

room_private
  id
  name
  invite_code
  owner_id → foreign key ke user.id

room_private_participants
  room_id → foreign key ke room_private.id
  user_id → foreign key ke user.id
  PRIMARY KEY(room_id, user_id)

room_private_invitation
  id
  room_private_id → foreign key ke room_private.id
  invited_user_id → foreign key ke user.id
  status → PENDING/APPROVED/DENIED

message
  id
  content
  user_id → foreign key ke user.id
  room_private_id → foreign key ke room_private.id
```