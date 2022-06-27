package com.multicamp.helpapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import android.widget.Button
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

open class location : AppCompatActivity() {

    val sub_topic = "android/#"
    val server_uri ="tcp://13.52.187.248:1883" //broker의 ip와 port
    var mymqtt : MyMqtt? = null
    val TAG: String = "로그"

    var sttIntent: Intent? = null
    var ttsObj: TextToSpeech? = null
    var count = 1
    val utteranceId = this.hashCode().toString() + "0"
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10

    lateinit var btnStartupdate: Button
    lateinit var btnStopUpdates: Button
    lateinit var txtLat: TextView
    lateinit var txtLong: TextView
    lateinit var txtTime: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        thread {
            var jsonobj= JSONObject()
            jsonobj.put("lno",count)
            val client= OkHttpClient()
            val jsondata=jsonobj.toString()
            val builder= Request.Builder()
            val url="http://13.52.187.248:8000/guardList"
            builder.url(url)
            builder.post(RequestBody.create(MediaType.parse("application/json"),jsondata))
            val myrequest: Request =builder.build()
            val response: Response =client.newCall(myrequest).execute()
            var result:String?=response.body()?.string()
            result = result?.replace("\""," ")?.trim()
            result = result?.replace("{", " ")
            result = result?.replace("}"," ")
            result = result?.replace("["," ")
            result = result?.replace("]"," ")
            result = result?.replace("main", "")
            result = result?.replace("["," ")
            result = result?.replace("]"," ")
            result = result?.replace(" ","")
            result = result?.replace(":","")

            Log.d("testes", result.toString())
            productMainName.text = "상품 매대 : " + count.toString() + ". " + result.toString()
            val mySetting = getSharedPreferences("network_conf", Context.MODE_PRIVATE)

            //데이터 저장을 위한 객체를 추출
            val saveObj = mySetting.edit()
            saveObj.putString("main",result)
            saveObj.commit()
            var resultEnglish = result.toString()
            when (resultEnglish) {
                "디저트" -> {
                    resultEnglish = "dessert"
                }
                "홈클린" -> {
                    resultEnglish = "clean"
                }
                "의약외품" -> {
                    resultEnglish = "drug"
                }
                "상온HMR" -> {
                    resultEnglish = "hmr"
                }
            }
            Log.d("tested", resultEnglish)

            mymqtt?.publish("android/imgName",resultEnglish)

            ttsObj?.speak("보실 상품은 $result 매대에 있습니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
            ttsObj?.stop()
        }

//        val jsonobj=JSONObject()
//        jsonobj.put("id",loginID.text)
//        val client=OkHttpClient()
//        val jsondata=jsonobj.toString()
//        val builder= Request.Builder()
//        val url="http://13.52.187.248:8000/start"
//        builder.url(url)
//        builder.post(RequestBody.create(MediaType.parse("application/json"),jsondata))
//        val myrequest:Request=builder.build()
//        val response:Response=client.newCall(myrequest).execute()
//        Log.d("okay", response.toString())

        //Mqtt통신을 수행할 Mqtt객체를 생성
        mymqtt = MyMqtt(this, server_uri)
        //블커에서 메시지가 전달되면 호출될 메소드를 넘기기
        mymqtt?.mysetCallback(::onReceived)
        //브로커연결
        mymqtt?.connect(arrayOf(sub_topic)) //여기에 토픽 추가

        ttsObj = TextToSpeech(this,TextToSpeech.OnInitListener {
            if(it!=TextToSpeech.ERROR){
                ttsObj?.language = Locale.KOREAN
            }
        })

        sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        sttIntent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,packageName)
        sttIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR")



        // 화면뷰 inflate.
        btnStartupdate = findViewById(R.id.btn_start_upds)
        btnStopUpdates = findViewById(R.id.btn_stop_upds)
        txtLat = findViewById(R.id.txtLat)
        txtLong = findViewById(R.id.txtLong)
        txtTime = findViewById(R.id.txtTime)

        // LocationRequest() deprecated 되서 아래 방식으로 LocationRequest 객체 생성
        // mLocationRequest = LocationRequest() is deprecated
        mLocationRequest =  LocationRequest.create().apply {
            interval = 2000 // 업데이트 간격 단위(밀리초)
            fastestInterval = 1000 // 가장 빠른 업데이트 간격 단위(밀리초)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // 정확성
            maxWaitTime= 2000 // 위치 갱신 요청 최대 대기 시간 (밀리초)
        }

        // 위치 추척 시작 버튼 클릭시 처리
        btnStartupdate.setOnClickListener {
            if (checkPermissionForLocation(this)) {
                startLocationUpdates()
                // View Button 활성화 상태 변경
                btnStartupdate.isEnabled = false
                btnStopUpdates.isEnabled = true
            }
        }

        // 위치 추적 중지 버튼 클릭시 처리
        btnStopUpdates.setOnClickListener {
            stoplocationUpdates()
            txtTime.text = "Updates Stoped"
            // View Button 활성화 상태 변경
            btnStartupdate.isEnabled = true
            btnStopUpdates.isEnabled = false
        }

        searchProductBtn.setOnClickListener {

            ttsObj?.speak("상품 검색 화면으로 이동하는 버튼입니다. 꾹 누르면 쇼핑 검색 화면으로 이동합니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
        }

        searchProductBtn.setOnLongClickListener {

            val searchIntent = Intent(this, Product::class.java)
            ttsObj?.speak("상품 검색 화면으로 이동 합니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
            startActivity(searchIntent)
            false
        }

        nextButton.setOnClickListener {
            ttsObj?.speak("다음 리스트로 이동하는 버튼입니다. 꾹 누르면 다음를 시작합니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
        }

        nextButton.setOnLongClickListener {
            count += 1
            ttsObj?.speak("다음 안내를 시작합니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
            thread {
                var jsonobj= JSONObject()
                jsonobj.put("lno",count)
                val client= OkHttpClient()
                val jsondata=jsonobj.toString()
                val builder= Request.Builder()
                val url="http://13.52.187.248:8000/guardList"
                builder.url(url)
                builder.post(RequestBody.create(MediaType.parse("application/json"),jsondata))
                val myrequest: Request =builder.build()
                val response: Response =client.newCall(myrequest).execute()
                var result:String?=response.body()?.string()
                result = result?.replace("\""," ")?.trim()
                result = result?.replace("{", " ")
                result = result?.replace("}"," ")
                result = result?.replace("["," ")
                result = result?.replace("]"," ")
                result = result?.replace("main", "")
                result = result?.replace("["," ")
                result = result?.replace("]"," ")
                result = result?.replace(" ","")
                Log.d("testes", result.toString())
                productMainName.text = "상품 매대 : " + count.toString() + ". " + result.toString()
                var resultEnglish = result.toString()
                when (resultEnglish) {
                    "디저트" -> {
                        resultEnglish = "dessert"
                    }
                    "홈클린" -> {
                        resultEnglish = "clean"
                    }
                    "의약외품" -> {
                        resultEnglish = "drug"
                    }
                    "상온HMR" -> {
                        resultEnglish = "hmr"
                    }
                }
                Log.d("tested", resultEnglish)

                mymqtt?.publish("android/imgName",resultEnglish)

                ttsObj?.speak("보실 상품은 $result 매대에 있습니다.", TextToSpeech.QUEUE_FLUSH,null,
                        utteranceId)
            }

            false
        }

        beforeButton.setOnClickListener {
            ttsObj?.stop()
            ttsObj?.speak("이전 리스트로 이동하는 버튼입니다. 꾹 누르면 다음를 시작합니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
        }

        beforeButton.setOnLongClickListener {
            ttsObj?.stop()
            count -= 1
            ttsObj?.speak("이전 안내를 시작합니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
            thread {
                var jsonobj= JSONObject()
                jsonobj.put("lno",count)
                val client= OkHttpClient()
                val jsondata=jsonobj.toString()
                val builder= Request.Builder()
                val url="http://13.52.187.248:8000/guardList"
                builder.url(url)
                builder.post(RequestBody.create(MediaType.parse("application/json"),jsondata))
                val myrequest: Request =builder.build()
                val response: Response =client.newCall(myrequest).execute()
                var result:String?=response.body()?.string()
                result = result?.replace("\""," ")?.trim()
                result = result?.replace("{", " ")
                result = result?.replace("}"," ")
                result = result?.replace("["," ")
                result = result?.replace("]"," ")
                result = result?.replace("main", "")
                result = result?.replace("["," ")
                result = result?.replace("]"," ")
                result = result?.replace(" ","")
                Log.d("testes", result.toString())
                productMainName.text = "상품 매대 : " + count.toString() + ". " + result.toString()
                var resultEnglish = result.toString()
                when (resultEnglish) {
                    "디저트" -> {
                        resultEnglish = "dessert"
                    }
                    "홈클린" -> {
                        resultEnglish = "clean"
                    }
                    "의약외품" -> {
                        resultEnglish = "drug"
                    }
                    "상온HMR" -> {
                        resultEnglish = "hmr"
                    }
                }

                Log.d("tested", resultEnglish)

                mymqtt?.publish("android/imgName",resultEnglish)

                ttsObj?.speak("보실 상품은 $result 매대에 있습니다.", TextToSpeech.QUEUE_FLUSH,null,
                        utteranceId)

            }

            false
        }

        complete.setOnClickListener {
            ttsObj?.speak("완료 버튼입니다. 꾹 누르면 메인 화면으로 이동하고 메인페이지로 이동합니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
        }

        complete.setOnLongClickListener {
//            thread {
//                var jsonobj= JSONObject()
//                jsonobj.put("lno",count)
//                val client= OkHttpClient()
//                val jsondata=jsonobj.toString()
//                val builder= Request.Builder()
//                val url="http://13.52.187.248:8000/dellist"
//                builder.url(url)
//                builder.post(RequestBody.create(MediaType.parse("application/json"),jsondata))
//                val myrequest: Request =builder.build()
//                val response: Response =client.newCall(myrequest).execute()
//                var result:String?=response.body()?.string()
//                result = result?.replace("\""," ")?.trim()
//                result = result?.replace("{", " ")
//                result = result?.replace("}"," ")
//                result = result?.replace("["," ")
//                result = result?.replace("]"," ")
//                result = result?.replace("main", "")
//                result = result?.replace("["," ")
//                result = result?.replace("]"," ")
//                result = result?.replace(" ","")
//                Log.d("del", result.toString())
//            }
//
//            thread {
//                var jsonobj= JSONObject()
//                jsonobj.put("lno",count)
//                val client= OkHttpClient()
//                val jsondata=jsonobj.toString()
//                val builder= Request.Builder()
//                val url="http://13.52.187.248:8000/updateprimary"
//                builder.url(url)
//                builder.post(RequestBody.create(MediaType.parse("application/json"),jsondata))
//                val myrequest: Request =builder.build()
//                val response: Response =client.newCall(myrequest).execute()
//                var result:String?=response.body()?.string()
//                result = result?.replace("\""," ")?.trim()
//                result = result?.replace("{", " ")
//                result = result?.replace("}"," ")
//                result = result?.replace("["," ")
//                result = result?.replace("]"," ")
//                result = result?.replace("main", "")
//                result = result?.replace("["," ")
//                result = result?.replace("]"," ")
//                result = result?.replace(" ","")
//                Log.d("count", result.toString())
//            }
            val searchIntent = Intent(this, HomeActivity::class.java)
            ttsObj?.speak("쇼핑을 완료했습니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
            startActivity(searchIntent)
            false
        }


    }

    private fun startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates()")

        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "startLocationUpdates() 두 위치 권한중 하나라도 없는 경우 ")
            return
        }
        Log.d(TAG, "startLocationUpdates() 위치 권한이 하나라도 존재하는 경우")
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청합니다.
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Log.d(TAG, "onLocationResult()")
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location) {
        Log.d(TAG, "onLocationChanged()")
        mLastLocation = location
        val date: Date = Calendar.getInstance().time
        val simpleDateFormat = SimpleDateFormat("hh:mm:ss a")
        txtTime.text = "Updated at : " + simpleDateFormat.format(date) // 갱신된 날짜
        txtLat.text = "LATITUDE : " + mLastLocation.latitude // 갱신 된 위도
        txtLong.text = "LONGITUDE : " + mLastLocation.longitude // 갱신 된 경도
    }

    // 위치 업데이터를 제거 하는 메서드
    private fun stoplocationUpdates() {
        Log.d(TAG, "stoplocationUpdates()")
        // 지정된 위치 결과 리스너에 대한 모든 위치 업데이트를 제거
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
    }

    // 위치 권한이 있는지 확인하는 메서드
    private fun checkPermissionForLocation(context: Context): Boolean {
        Log.d(TAG, "checkPermissionForLocation()")
        // Android 6.0 Marshmallow 이상에서는 지리 확보(위치) 권한에 추가 런타임 권한이 필요합니다.
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "checkPermissionForLocation() 권한 상태 : O")
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                Log.d(TAG, "checkPermissionForLocation() 권한 상태 : X")
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    private fun onReceived(topic:String,message: MqttMessage){
        //토픽의 수신을 처리
        //EditText에 내용을 출력하기, 영상출력, .... 도착된 메시지안에서 온도랑 습도 데이터를 이용해서 차트그리기,
        // 모션 detact가 전달되면 Notification도 발생시키기.....
        val msg = String(message.payload)
        val massage = String(message.payload, Charset.forName("UTF-8"))
        val massage2 = String(message.payload, Charset.forName("EUC-kR"))
        val utteranceId = this.hashCode().toString() + "0"
        when (msg) {
            "person" -> {
                ttsObj?.speak("전방에 사람이 있습니다", TextToSpeech.QUEUE_FLUSH,null,
                        utteranceId)
                Log.d("person","person")
            }
            "left" -> {
                ttsObj?.speak("왼쪽 물체", TextToSpeech.QUEUE_FLUSH,null,
                        utteranceId)
                Log.d("left","left")
            }
            "right" -> {
                ttsObj?.speak("오른쪽 물체", TextToSpeech.QUEUE_FLUSH,null,
                        utteranceId)
                Log.d("right","right")
            }
            "both" -> {
                ttsObj?.speak("양쪽 물체", TextToSpeech.QUEUE_FLUSH,null,
                        utteranceId)
                Log.d("both","both")
            }
            "forward" -> {
                ttsObj?.speak("전방 물체", TextToSpeech.QUEUE_FLUSH,null,
                        utteranceId)
                Log.d("forward","forward")
            }
             else -> {
                ttsObj?.stop()
            }
        }
        

    }

}