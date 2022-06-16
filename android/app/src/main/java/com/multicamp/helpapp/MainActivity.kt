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
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private var permission_state = false
    var sttIntent: Intent? = null
    var recognizer:SpeechRecognizer? = null
    var edittool:EditText? = null
    var editNum:EditText? = null
    var ttsObj: TextToSpeech? = null
    var count:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edittool = voiceText
        editNum = voiceNum
        ttsObj = TextToSpeech(this,TextToSpeech.OnInitListener {
            if(it!=TextToSpeech.ERROR){
                ttsObj?.language = Locale.KOREAN
            }
        })
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            permission_state = true
            printToast("권한이 설정되었습니다.")
        } else {
            permission_state = false
            printToast("권한을 설정해야 합니다.")
            //2. 권한이 없는 경우 권한을 설정하는 메시지를 띄운다.
            ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.RECORD_AUDIO),
                    1000
            )
        }
        sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        sttIntent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,packageName)
        sttIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR")
        val nextIntent= Intent(this, HomeActivity::class.java)
        var clickLister = (object : RecognitionListener{

            override fun onReadyForSpeech(params: Bundle?) {
                printToast("음성인식을 시작합니다.")
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
                    searchText.setText(data.get(i))
                }

                thread{
                    val utteranceId = this.hashCode().toString() + ""
                    var jsonobj= JSONObject()
                    jsonobj.put("name",searchText.text)
                    val client= OkHttpClient()
                    val jsondata=jsonobj.toString()
                    val builder= Request.Builder()
                    val url="http://13.52.187.248:8000/searchProduct"
                    builder.url(url)
                    builder.post(RequestBody.create(MediaType.parse("application/json"),jsondata))
                    val myrequest: Request =builder.build()
                    val response: Response =client.newCall(myrequest).execute()
                    var result:String?=response.body()?.string()
                    print(result)
                    result = result?.replace("\""," ")?.trim()
                    result = result?.replace("{", " ")
                    result = result?.replace("}"," ")
                    var result2 = result?.length.toString()
                    Log.d("test",result.toString())
                    ttsObj?.speak(result2 + "개의 상품이 있습니다.",TextToSpeech.QUEUE_FLUSH,null, utteranceId)
                }

            }
        })

        var listener = (object : RecognitionListener{

            override fun onReadyForSpeech(params: Bundle?) {
                printToast("음성인식을 시작합니다.")
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
                count ++

                // 상품리스트를 db에 저장
                var list:ArrayList<String>

                for(i in data.indices){
                    edittool?.setText(data.get(i))
                }
                var textcount = count.toString()
                editNum?.setText(textcount)
                var voiceMsg:String = edittool?.text.toString()
                var voiceNum:String = editNum?.text.toString()
                val utteranceId = this.hashCode().toString() + ""
                when (voiceMsg) {

                    "완료" -> {
                        //음성이 발생되면 처리하고 싶은 기능을 구현
                        ttsObj?.speak("쇼핑을 완료하고 메인화면으로 이동합니다..",TextToSpeech.QUEUE_FLUSH,null,
                                utteranceId)
                        startActivity(nextIntent)
                    }
                    "취소" -> {
                        //음성이 발생되면 처리하고 싶은 기능을 구현

                        ttsObj?.speak("취소하고 메인화면을 이동합니다..",TextToSpeech.QUEUE_FLUSH,null,
                                utteranceId)
                        startActivity(nextIntent)
                    }
                    else -> {

                        ttsObj?.speak("장바구니에 $voiceMsg 를 담았습니다. 현재 장바구니에는 $voiceNum 개 상품이 있습니다.",TextToSpeech.QUEUE_FLUSH,null,
                                utteranceId)
                    }
                        }

                Log.d("recog","onResults")
            }
        })
        btnvoice.setOnClickListener {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this)
            recognizer?.setRecognitionListener(clickLister)
            recognizer?.startListening(sttIntent)
        }

        btnvoice.setOnLongClickListener {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this)
            recognizer?.setRecognitionListener(listener)
            recognizer?.startListening(sttIntent)
            false
        }


    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 && grantResults.isNotEmpty()) { //권한의 성공 설정에 대한 결과가 있다는 의미
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permission_state = true
                printToast("권한 설정 마무리 완료")
            } else {
                printToast("권한 설정을 하지 않았으므로 기능을 사용할 수 없습니다.")
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
    
    private fun printToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}