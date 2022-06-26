package com.multicamp.helpapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_register_tab.*
import okhttp3.*
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.thread

class RegisterActivity : AppCompatActivity() {
    private var permission_state = false
    private var sttIntent: Intent? = null
    private var recognizer: SpeechRecognizer? = null
    private var ttsObj: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_tab)

        ttsObj = TextToSpeech(this,TextToSpeech.OnInitListener {
            if(it!=TextToSpeech.ERROR){
                ttsObj?.language = Locale.KOREAN
            }
        })
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            permission_state = true
        } else {
            permission_state = false
            //2. 권한이 없는 경우 권한을 설정하는 메시지를 띄운다.
            ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.RECORD_AUDIO),
                    1000
            )
        }
        sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        sttIntent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,packageName)
        sttIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR")
        val utteranceId = this.hashCode().toString() + "0"
        id.setOnClickListener {
            ttsObj?.speak("아이디 입력 버튼입니다. 꾹 누르면 음성으로 아이디 입력이 가능합니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
        }
        var idListener = (object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                ttsObj?.stop()
                Toast.makeText(applicationContext,"음성인식을 시작합니다.", Toast.LENGTH_LONG).show()
            }

            override fun onBeginningOfSpeech() {
                Log.d("recog","onBeginningOfSpeech")
            }
            override fun onRmsChanged(rmsdB: Float) {
                Log.d("recog","onRmsChanged")
            }
            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d("recog","onBufferReceived")
            }
            override fun onPartialResults(partialResults: Bundle?) {
                Log.d("recog","onPartialResults")
            }
            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("recog","onEvent")
            }
            override fun onEndOfSpeech() {
                Log.d("recog","onEndOfSpeech")
            }

            override fun onError(error: Int) {
                var message =""
                when(error){
                    SpeechRecognizer.ERROR_AUDIO -> message = "오디오 에러"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> message = "퍼미션이 설정되지 않음";
                    SpeechRecognizer.ERROR_CLIENT -> message = "클라이언트 에러"
                    SpeechRecognizer.ERROR_NETWORK ->  message = "네트워크 에러"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> message = "다른 작업 처리 중이라 바쁨"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> message = "말을 너무 길게 해서 시간초과"
                }
                Log.d("recog",message)
            }
            override fun onResults(results: Bundle?) {
                var  data:ArrayList<String> =
                        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>

                for(i in data.indices){
                    id?.text =  data.get(i)
                }

                val utteranceId = this.hashCode().toString() + "0"


                Log.d("recog","onResults")

            }
        })

        id.setOnLongClickListener {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this)
            recognizer?.setRecognitionListener(idListener)
            recognizer?.startListening(sttIntent)
            false
        }


        var pwListener = (object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                ttsObj?.stop()
                Toast.makeText(applicationContext,"음성인식을 시작합니다.", Toast.LENGTH_LONG).show()
            }

            override fun onBeginningOfSpeech() {
                Log.d("recog","onBeginningOfSpeech")
            }
            override fun onRmsChanged(rmsdB: Float) {
                Log.d("recog","onRmsChanged")
            }
            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d("recog","onBufferReceived")
            }
            override fun onPartialResults(partialResults: Bundle?) {
                Log.d("recog","onPartialResults")
            }
            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("recog","onEvent")
            }
            override fun onEndOfSpeech() {
                Log.d("recog","onEndOfSpeech")
            }

            override fun onError(error: Int) {
                var message =""
                when(error){
                    SpeechRecognizer.ERROR_AUDIO -> message = "오디오 에러"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> message = "퍼미션이 설정되지 않음";
                    SpeechRecognizer.ERROR_CLIENT -> message = "클라이언트 에러"
                    SpeechRecognizer.ERROR_NETWORK ->  message = "네트워크 에러"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> message = "다른 작업 처리 중이라 바쁨"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> message = "말을 너무 길게 해서 시간초과"
                }
                Log.d("recog",message)
            }
            override fun onResults(results: Bundle?) {
                var  data:ArrayList<String> =
                        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>

                for(i in data.indices){
                    password?.text = data.get(i)
                }

                Log.d("recog","onResults")
            }
        })

        password.setOnClickListener {
            ttsObj?.speak("비밀번호 입력 버튼입니다. 꾹 누르면 음성으로 비밀번호 입력이 가능합니다", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
        }

        password.setOnLongClickListener {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this)
            recognizer?.setRecognitionListener(pwListener)
            recognizer?.startListening(sttIntent)
            false
        }

        var nameListener = (object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                ttsObj?.stop()
                Toast.makeText(applicationContext,"음성인식을 시작합니다.", Toast.LENGTH_LONG).show()
            }

            override fun onBeginningOfSpeech() {
                Log.d("recog","onBeginningOfSpeech")
            }
            override fun onRmsChanged(rmsdB: Float) {
                Log.d("recog","onRmsChanged")
            }
            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d("recog","onBufferReceived")
            }
            override fun onPartialResults(partialResults: Bundle?) {
                Log.d("recog","onPartialResults")
            }
            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("recog","onEvent")
            }
            override fun onEndOfSpeech() {
                Log.d("recog","onEndOfSpeech")
            }

            override fun onError(error: Int) {
                var message =""
                when(error){
                    SpeechRecognizer.ERROR_AUDIO -> message = "오디오 에러"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> message = "퍼미션이 설정되지 않음";
                    SpeechRecognizer.ERROR_CLIENT -> message = "클라이언트 에러"
                    SpeechRecognizer.ERROR_NETWORK ->  message = "네트워크 에러"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> message = "다른 작업 처리 중이라 바쁨"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> message = "말을 너무 길게 해서 시간초과"
                }
                Log.d("recog",message)
            }
            override fun onResults(results: Bundle?) {
                var  data:ArrayList<String> =
                        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>

                for(i in data.indices){
                    name?.text = data.get(i)
                }

                Log.d("recog","onResults")
            }
        })

        name.setOnClickListener {
            ttsObj?.speak("이름 입력 버튼입니다. 꾹 누르면 음성으로 비밀번호 입력이 가능합니다", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
        }

        name.setOnLongClickListener {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this)
            recognizer?.setRecognitionListener(nameListener)
            recognizer?.startListening(sttIntent)
            false
        }

        registerSubmit.setOnClickListener {
            thread{
                var jsonobj= JSONObject()
                jsonobj.put("id",id.text)
                jsonobj.put("password",password.text)
                jsonobj.put("name", name.text)
                val client= OkHttpClient()
                val jsondata=jsonobj.toString()
                val builder= Request.Builder()
                val url="http://13.52.187.248:8000/writeUser"
                val nextIntent= Intent(this,HomeActivity::class.java)
                builder.url(url)
                builder.post(RequestBody.create(MediaType.parse("application/json"),jsondata))
                val myrequest: Request =builder.build()
                val response: Response =client.newCall(myrequest).execute()
                var result:String?=response.body()?.string()
                print(result)
                Log.d("test",result+"test result 11111111111111111111111111111111111111")
                result = result?.replace("\""," ")?.trim()
                if(result=="ok"){
                    Log.d("test",result!!+"ok result here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                    startActivity(nextIntent)
                }else{
                    Log.d("test","else result here !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                    runOnUiThread { Toast.makeText(this,"로그인 실패!!", Toast.LENGTH_SHORT).show()}
                    val utteranceId = this.hashCode().toString() + "0"
                    ttsObj?.speak("회원가입 실패했습니다. 다시한번 회원가입을 해주세요.", TextToSpeech.QUEUE_FLUSH,null,
                            utteranceId)
                }
                Log.d("test",result!!+"result here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (ttsObj != null) {
            ttsObj?.stop()
            ttsObj?.shutdown()
            ttsObj = null
        }
        if (recognizer != null) {
            recognizer?.destroy()
            recognizer?.cancel()
            recognizer = null
        }
    }



}