package com.multicamp.helpapp

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MyMqtt(context: Context, uri:String) {
    // 안드로이드에서 mqtt 통신ㅇ르 수행할 객체를 생성 - mqttAndroidClient
    var mqttClient: MqttAndroidClient = MqttAndroidClient(context, uri, MqttClient.generateClientId())
    // mqtt 통신을 하기 위햇 브로커서버와 연결, 연결이 끝난 후 콜백메소드 선언
    fun connect(topic:Array<String>) {
        // 연결하기 위해서 여러 가지 정보를 담고 있는 객체
        val mqttConnectOptions = MqttConnectOptions()
        // MqttAndroidClient 객체의 connect를 호출하여 브로커에 연결을 시도
        // 안드로이드 내부에서 브로커에 연결을 성공하면 자동으로 이벤트가 발생하여 이를 처리하는 리스너가 IMQttActionListener
        mqttClient.connect(mqttConnectOptions, null, object:IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                // 접속성공
                Log.d("test","접속성공")
                // 메시지 접속 성공하면 subscirbe 하기 ------------------------------
                // topic 여러
                topic.map {
                    subScribeTopic(it)
                }
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d("test","접속실패")
            }

        })
    }
    fun setCallback(callback: (topic:String,message:MqttMessage)->Unit) {
        mqttClient.setCallback(object :MqttCallback{
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d("test","connectionLost")
                // 메시지가 전송되면 호출 - 액티비티의 메소드
                callback(topic!!, message!!)
            }

            override fun connectionLost(cause: Throwable?) {
                Log.d("test","connectionLost")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d("test","connectionLost")
            }

        })
    }

    fun publish(topic:String, Payload:String, qos:Int=0) {
        if(!mqttClient.isConnected) {
            mqttClient.connect()
        }
        // mqtt로 전송할 메시지 객체를 생성
        val message = MqttMessage()
        // 메시지 객체에 payLoad 와 메시지 전송품질을 설정
        // 네트워크를 전송되므로 byte로 변경
        message.payload = Payload.toByteArray()
        message.qos =qos
        // 메시지 전송하기 (publish) = publish가 성공 / 실패하는 경우 이벤트가 발생하기 때문에 리스너 등록
        // mqttClient.publish(topic.message) - 이 명령문처럼 publish해도 좋음
        // publish 후 콜백이 실행되도록 하고 싶다면
        mqttClient.publish(topic,message,null,object:IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("test", "메시지 전달 성공")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d("test", "메시지 전달 실패")
            }
        })

    }

    // 자동적으로
    private fun subScribeTopic(topic: String, qos:Int=0) {
        mqttClient.subscribe(topic,qos,null,object:IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("test","subScribe 성공")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d("test","subScribe 성공")
            }
        })
    }
}