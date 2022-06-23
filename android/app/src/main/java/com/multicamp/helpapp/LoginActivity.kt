package com.multicamp.helpapp

import android.Manifest
import android.content.Context
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
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity(){
    private var permission_state = false
    private var sttIntent: Intent? = null
    private var recognizer: SpeechRecognizer? = null
    private var ttsObj: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        loginID.setOnClickListener {
            ttsObj?.speak("아이디 입력 버튼입니다. 꾹 누르면 음성으로 아이디 입력이 가능합니다.", TextToSpeech.QUEUE_FLUSH,null,
                utteranceId)
        }
        val idListener = (object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                ttsObj?.stop()
                Toast.makeText(applicationContext,"음성인식을 시작합니다.",Toast.LENGTH_LONG).show()
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
                    loginID?.text =  data.get(i)
                }

                Log.d("recog","onResults")

            }
        })

        loginID.setOnLongClickListener {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this)
            recognizer?.setRecognitionListener(idListener)
            recognizer?.startListening(sttIntent)
            false
        }

        register.setOnClickListener {

            ttsObj?.speak("회원가입 페이지로 이동하는 버튼입니다. 꾹 누르면 회원가입 페이지로 이동합니다", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
        }
        register.setOnLongClickListener {

            val registerIntent = Intent(this, RegisterActivity::class.java)
            ttsObj?.speak("회원 가입 페이지으로 이동합니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
            startActivity(registerIntent)
            false
        }

        val pwListener = (object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                ttsObj?.stop()
                Toast.makeText(applicationContext,"음성인식을 시작합니다.",Toast.LENGTH_LONG).show()
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
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> message = "퍼미션이 설정되지 않음"
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
                    loginPW?.text = data.get(i)
                }

                Log.d("recog","onResults")
            }
        })

        loginPW.setOnClickListener {
            ttsObj?.speak("비밀번호 입력 버튼입니다. 꾹 누르면 음성으로 비밀번호 입력이 가능합니다", TextToSpeech.QUEUE_FLUSH,null,
                utteranceId)
        }

        loginPW.setOnLongClickListener {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this)
            recognizer?.setRecognitionListener(pwListener)
            recognizer?.startListening(sttIntent)
            false
        }


        loginSubmit.setOnClickListener {
            thread{
                val jsonobj=JSONObject()
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
                    //Preference객체를 추출한다.
                    //Context.MODE_PRIVATE는 기존 데이터 유지하지않고 싹 지우는 거고
                    //Context.MODE_APPEND는 기존 데이터 위에 추가하는 것이다.
                    val mySetting = getSharedPreferences("network_conf", Context.MODE_PRIVATE)

                    //데이터 저장을 위한 객체를 추출
                    val saveObj = mySetting.edit()
                    saveObj.putString("sessionid",loginID.text.toString())
                    saveObj.commit()
                    val pref = getSharedPreferences("network_conf", Context.MODE_PRIVATE)
                    var data = "${pref.getString("sessionid","")}"
                    Log.d("test",result!!+"ok result here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                    Log.d("test",data)
                    startActivity(nextIntent)
                }else{
                    Log.d("test","else result here !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                    runOnUiThread {Toast.makeText(this,"로그인 실패!!",Toast.LENGTH_SHORT).show()}
                    val utteranceId = this.hashCode().toString() + "0"
                    ttsObj?.speak("로그인 실패했습니다. 아이디와 로그인을 다시한번 확인해주세요", TextToSpeech.QUEUE_FLUSH,null,
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