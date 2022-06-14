package com.multicamp.helpapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONObject
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity(){

    var stt_intent: Intent? = null
    var recognizer: SpeechRecognizer? = null
    var ttsObj: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginID.setOnClickListener {

        }
        loginPW.setOnClickListener {
            
        }


        loginSubmit.setOnClickListener {
            thread{
                var jsonobj=JSONObject()
                jsonobj.put("id",loginID.text)
                jsonobj.put("password",loginPW.text)
                val client=OkHttpClient()
                val jsondata=jsonobj.toString()
                val builder= Request.Builder()
                val url="http://13.52.187.248:8000/loginandroid"
                val nextIntent= Intent(this,HomeActivity::class.java)
                builder.url(url)
                builder.post(RequestBody.create(MediaType.parse("application/json"),jsondata))
                val myrequest:Request=builder.build()
                val response:Response=client.newCall(myrequest).execute()
                var result:String?=response.body()?.string()
                print(result)
                Log.d("test",result+"test result 11111111111111111111111111111111111111")
                result = result?.replace("\""," ")?.trim()
                if(result=="ok"){
                    Log.d("test",result!!+"ok result here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                    startActivity(nextIntent)
                }else{
                    Log.d("test","else result here !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                    runOnUiThread {Toast.makeText(this,"로그인 실패!!",Toast.LENGTH_SHORT).show()}
                }
                Log.d("test",result!!+"result here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            }
        }
    }
    fun onReceived(topic:String,message:MqttMessage){
        val msg=String(message.payload)
        Log.d("mymqtt","onReceived message 1111111111111")

    }
}