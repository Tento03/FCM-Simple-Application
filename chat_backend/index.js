const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const admin = require('firebase-admin');

// Inisialisasi Firebase Admin SDK
const serviceAccount = require('./serviceAccountKey.json');
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const app = express();
const PORT = 3000;

// Middleware
app.use(bodyParser.json());
app.use(cors());

// Database sederhana (bisa diganti dengan database nyata)
const messages = [];
const userTokens = {};

// Endpoint untuk mendaftarkan token FCM
app.post('/register_token', async (req, res) => {
  const { user_id, fcm_token } = req.body;
  if (!user_id || !fcm_token) {
    return res.status(400).json({ error: 'user_id and fcm_token are required' });
  }

  // Simpan token FCM ke database sementara
  userTokens[user_id] = fcm_token;
  console.log(`Token FCM untuk user ${user_id} berhasil disimpan`);
  res.status(200).json({ message: 'Token FCM berhasil disimpan' });
});

// Endpoint untuk mengirim pesan
app.post('/send_message', async (req, res) => {
  const { sender_id, receiver_id, message, title } = req.body;

  // Validasi input
  if (!sender_id || !receiver_id || !message) {
    return res.status(400).json({ error: 'sender_id, receiver_id, and message are required' });
  }

  // Gunakan nilai default jika title tidak disertakan
  const notificationTitle = title || 'Pesan Baru';

  // Gunakan isi pesan langsung sebagai body notifikasi
  const notificationBody = message;

  // Simpan pesan ke database sementara
  messages.push({ sender_id, receiver_id, message });
  console.log(`Pesan dari ${sender_id} ke ${receiver_id}: ${message}`);

  // Kirim notifikasi ke lawan bicara
  const receiverToken = userTokens[receiver_id];
  if (!receiverToken) {
    return res.status(404).json({ error: 'Token FCM penerima tidak ditemukan' });
  }

  // Payload notifikasi
  const messagePayload = {
    token: receiverToken,
    notification: {
      title: notificationTitle, // Judul notifikasi
      body: notificationBody,   // Isi notifikasi (sesuai pesan yang dikirim)
    },
    data: {
      sender_id,                // ID pengirim
      message,                  // Isi pesan
    },
  };

  try {
    const response = await admin.messaging().send(messagePayload);
    console.log('Notifikasi berhasil dikirim:', response);
    res.status(200).json({ message: 'Pesan dan notifikasi berhasil dikirim' });
  } catch (error) {
    console.error('Gagal mengirim notifikasi:', error.message);
    res.status(500).json({ error: `Gagal mengirim notifikasi: ${error.message}` });
  }
});

// Jalankan server
app.listen(PORT, () => {
  console.log(`Server berjalan di http://localhost:${PORT}`);
});