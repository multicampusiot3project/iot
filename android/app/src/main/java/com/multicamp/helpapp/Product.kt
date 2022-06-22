package com.multicamp.helpapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_product.*
import okhttp3.*
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject
import java.io.*
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class Product : AppCompatActivity() {
    val sub_topic = "android/#"
    val server_uri ="tcp://13.52.187.248:1883" //broker의 ip와 port
    var mymqtt : MyMqtt? = null
    var imgCount = 0
    var sttIntent: Intent? = null
    var recognizer: SpeechRecognizer? = null
    var ttsObj: TextToSpeech? = null

    // ViewBinding
    // Permisisons
    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE    )
    val PERMISSIONS_REQUEST = 100
    // Request Code
    private val BUTTON2 = 200



    override fun onCreate(savedInstanceState: Bundle?) {
        //Mqtt통신을 수행할 Mqtt객체를 생성
        mymqtt = MyMqtt(this, server_uri)
        //블커에서 메시지가 전달되면 호출될 메소드를 넘기기
        mymqtt?.mysetCallback(::onReceived)
        //브로커연결
        mymqtt?.connect(arrayOf<String>(sub_topic)) //여기에 토픽 추가

        ttsObj = TextToSpeech(this,TextToSpeech.OnInitListener {
            if(it!=TextToSpeech.ERROR){
                ttsObj?.language = Locale.KOREAN
            }
        })

        sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        sttIntent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,packageName)
        sttIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        checkPermissions(PERMISSIONS, PERMISSIONS_REQUEST)

        btn2.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, BUTTON2)
            }
        }

    }

    private fun checkPermissions(permissions: Array<String>, permissionsRequest: Int): Boolean {
        val permissionList : MutableList<String> = mutableListOf()
        for(permission in permissions){
            val result = ContextCompat.checkSelfPermission(this, permission)
            if(result != PackageManager.PERMISSION_GRANTED){
                    permissionList.add(permission)
                }
            }
        if(permissionList.isNotEmpty()){
            ActivityCompat.requestPermissions(this, permissionList.toTypedArray(), PERMISSIONS_REQUEST)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for(result in grantResults){
            if(result != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "권한 승인 부탁드립니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                BUTTON2-> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    saveBitmapAsJPGFile(imageBitmap)
                    views.setImageBitmap(imageBitmap)
                }
            }
        }
    }

    private fun newJpgFileName() : String {
        imgCount += 1
        val pref = getSharedPreferences("network_conf", Context.MODE_PRIVATE)
        //저장된 데이터 가져오기
        var data = "${pref.getString("main","")},"
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        data = data.replace(",","")
        var filename = data
        if(filename == "과자") {
            filename = "snack"
        }
        Log.d("filename",filename)
        return "${sdf}.jpg"
    }
    private fun saveBitmapAsJPGFile(bitmap: Bitmap) {
        val path = File(filesDir, "image")
        if(!path.exists()){
            path.mkdirs()
        }
        val file = File(path, newJpgFileName())
        var imageFile: FileOutputStream? = null
        try{
            file.createNewFile()
            imageFile = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageFile)
            Toast.makeText(this, file.absolutePath, Toast.LENGTH_LONG).show()
            Log.d("test",file.absolutePath)


            val stream:ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val imagedata :String = Base64.encodeToString(stream.toByteArray(),Base64.DEFAULT)
            Log.d("test",imagedata)

            mymqtt?.publish("android/product",imagedata)
            mymqtt?.publish("android/picture", "start")
            print(imageFile)
            imageFile.close()
        } catch (e: Exception){
            null
        }}

    fun onReceived(topic:String,message: MqttMessage){
        //토픽의 수신을 처리
        //EditText에 내용을 출력하기, 영상출력, .... 도착된 메시지안에서 온도랑 습도 데이터를 이용해서 차트그리기,
        // 모션 detact가 전달되면 Notification도 발생시키기.....
        val msg = String(message.payload)
        val massage = String(message.payload, Charset.forName("UTF-8"))
        val massage2 = String(message.payload, Charset.forName("EUC-kR"))
        Log.d("msg",msg)

        if(topic == "android/sendmessage") {
            val massage = String(message.payload, Charset.forName("UTF-8"))
            Log.d("test",msg)
            val utteranceId = this.hashCode().toString() + "0"
            ttsObj?.speak("해당 상품 정보는 $massage2",TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
        }

        if(topic == "android/pictureResult") {
            val massage = String(message.payload, Charset.forName("UTF-8"))
            Log.d("test",msg)
            val utteranceId = this.hashCode().toString() + "0"
            thread {
                var jsonobj= JSONObject()
                jsonobj.put("id","$massage")
                val client= OkHttpClient()
                val jsondata=jsonobj.toString()
                val builder= Request.Builder()
                val urls="http://13.52.187.248:8000/pictureName"
                builder.url(urls)
                builder.post(RequestBody.create(MediaType.parse("application/json"),jsondata))
                val myrequests: Request =builder.build()
                val responses: Response =client.newCall(myrequests).execute()
//                    val urls="http://13.52.187.248:8000/searchcount"
//                    builder.url(urls)
//                    builder.post(RequestBody.create(MediaType.parse("application/json"),jsondata))
//                    val myrequests: Request =builder.build()
//                    val responses: Response =client.newCall(myrequests).execute()
//                    var count:String?=responses.body()?.string()
//                    count = count?.replace("\""," ")?.trim()
                val url="http://13.52.187.248:8000/pictureName"
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
                result = result?.replace("name", "")
                result = result?.replace("["," ")
                result = result?.replace("]"," ")
                result = result?.split(':').toString()
                val priceUrl="http://13.52.187.248:8000/picturePrice"
                builder.url(priceUrl)
                builder.post(RequestBody.create(MediaType.parse("application/json"),jsondata))
                val priceMyRequest: Request =builder.build()
                val priceResponse: Response =client.newCall(priceMyRequest).execute()
                var priceResult:String?=priceResponse.body()?.string()
                priceResult = priceResult?.replace("\"","")?.trim()
                priceResult = priceResult?.replace("{", "")
                priceResult = priceResult?.replace("}","")
                priceResult = priceResult?.replace("[","")
                priceResult = priceResult?.replace("]","")
                priceResult = priceResult?.replace("price", "")
                priceResult = priceResult?.replace("[","")
                priceResult = priceResult?.replace("]","")
                priceResult = priceResult?.replace(",","")

                val manufactureUrl="http://13.52.187.248:8000/pictureManufacture"
                builder.url(manufactureUrl)
                builder.post(RequestBody.create(MediaType.parse("application/json"),jsondata))
                val manufactureRequest: Request =builder.build()
                val manufactureResponse: Response =client.newCall(manufactureRequest).execute()
                var manufactureResult:String?=manufactureResponse.body()?.string()
                manufactureResult = manufactureResult?.replace("\""," ")?.trim()
                manufactureResult = manufactureResult?.replace("{", " ")
                manufactureResult = manufactureResult?.replace("}"," ")
                manufactureResult = manufactureResult?.replace("["," ")
                manufactureResult = manufactureResult?.replace("]"," ")
                manufactureResult = manufactureResult?.replace("manufacture", "")
                manufactureResult = manufactureResult?.replace("["," ")
                manufactureResult = manufactureResult?.replace("]"," ")
                manufactureResult = manufactureResult?.split(':').toString()

                ttsObj?.speak("사진 찍은 제품은 상품은 $result, 제조사는 $manufactureResult 가격은  $priceResult 입니다.",TextToSpeech.QUEUE_FLUSH,null, utteranceId)

            }

        }
    }


}