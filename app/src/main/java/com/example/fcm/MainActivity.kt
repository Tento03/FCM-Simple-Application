package com.example.fcm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.fcm.api.UserApi
import com.example.fcm.databinding.ActivityMainBinding
import com.example.fcm.model.FcmTokenRequest
import com.example.fcm.model.MessageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getToken()
        binding.buttonSend.setOnClickListener(){
            var message=binding.editText
            if (message.text.isNotEmpty()){
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val senderId="user2"
                        val receiverId="user1"
                        val message=binding.editText.text.toString()
                        val messageRequest=MessageRequest(senderId,receiverId,message)

                        val response=UserApi.apiService.send_message(messageRequest)
                        if (response.isSuccessful) {
                            Log.d("SendMessage", "Pesan berhasil dikirim")
                        } else {
                            Log.e("SendMessage", "Gagal mengirim pesan: ${response.code()}")
                        }
                    }
                    catch (e:Exception){
                        Log.e("SendMessage", "Error: ${e.message}")
                    }
                }
            }
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(){
            if (!it.isSuccessful){
                Log.w("TAG_ERROR","Fetching token registration failed",it.exception)
            }
            val token=it.result
            Log.d("TAG_FCM","$token")
            sendTokentoServer(token)
        }
    }

    private fun sendTokentoServer(token: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
//                val userId= FirebaseAuth.getInstance().currentUser?.uid
                val userId= "user3"
                val fcmTokenRequest=FcmTokenRequest(userId!!, token!!)

                val response=UserApi.apiService.register_token(fcmTokenRequest)
                if (response.isSuccessful){
                    Log.d("FCM","Token berhasil dikirim ke server")
                }
                else{
                    Log.d("FCM","Token gagal dikirim ke server")
                }
            }
            catch (e:Exception){
                Log.e("FCM","error saat mengirim token : ${e.message}")
            }
        }
    }
}