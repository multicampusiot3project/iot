package com.multicamp.helpapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_home.*
import org.eclipse.paho.client.mqttv3.MqttMessage

class HomeActivity : AppCompatActivity() {
    val sub_topic = "android/#"
    val server_uri ="tcp://13.52.187.248" //broker의 ip와 port
    var mymqtt : MyMqtt? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        //Mqtt통신을 수행할 Mqtt객체를 생성
        mymqtt = MyMqtt(this, server_uri)
        //블커에서 메시지가 전달되면 호출될 메소드를 넘기기
        mymqtt?.mysetCallback(::onReceived)
        //브로커연결
        mymqtt?.connect(arrayOf<String>(sub_topic)) //여기에 토픽 추가


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        button.setOnClickListener {
            val listIntent = Intent(this, MainActivity::class.java)
            startActivity(listIntent)
        }

        button2.setOnClickListener {
            val searchIntent = Intent(this, Product::class.java)
            startActivity(searchIntent)
        }

        button3.setOnClickListener {
            val searchIntent = Intent(this, Product::class.java)
            startActivity(searchIntent)
        }

        button4.setOnClickListener {
            val logoutIntent = Intent(this, Product::class.java)
            startActivity(logoutIntent)
        }
    }

    fun onReceived(topic:String,message: MqttMessage){
        //토픽의 수신을 처리
        //EditText에 내용을 출력하기, 영상출력, .... 도착된 메시지안에서 온도랑 습도 데이터를 이용해서 차트그리기,
        // 모션 detact가 전달되면 Notification도 발생시키기.....
        val msg = String(message.payload)
    }


}