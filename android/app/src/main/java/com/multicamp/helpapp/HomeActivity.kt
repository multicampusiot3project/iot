package com.multicamp.helpapp

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*


class HomeActivity : AppCompatActivity() {
    private var sttIntent: Intent? = null
    private var recognizer:SpeechRecognizer? = null
    private var ttsObj: TextToSpeech? = null
    private var detector: GestureDetector? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        ttsObj = TextToSpeech(this, TextToSpeech.OnInitListener {
            if(it!= TextToSpeech.ERROR){
                ttsObj?.language = Locale.KOREAN
            }
        })

        sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        sttIntent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,packageName)
        sttIntent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR")

        val utteranceId = this.hashCode().toString() + "0"

        button.setOnClickListener {

            ttsObj?.speak("장바구니 화면으로 이동하는 버튼입니다. 꾹 누르면 장바구니 화면으로 이동합니다.", TextToSpeech.QUEUE_FLUSH,null,
                utteranceId)
        }
        button.setOnLongClickListener {

            val listIntent = Intent(this, MainActivity::class.java)
            ttsObj?.speak("장바구니 화면으로 이동합니다. ", TextToSpeech.QUEUE_FLUSH,null,
                utteranceId)
            startActivity(listIntent)
            false
        }

        button3.setOnClickListener {

            ttsObj?.speak("쇼핑 안내 화면으로 이동하는 버튼입니다. 꾹 누르면 쇼핑 안내 화면으로 이동합니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
        }

        button3.setOnLongClickListener {

            val shoppingIntent = Intent(this, location::class.java)
            ttsObj?.speak("쇼핑 안내 화면으로 이동합니다.", TextToSpeech.QUEUE_FLUSH,null,
                utteranceId)
            startActivity(shoppingIntent)
            false
        }



        button4.setOnClickListener {
            val logoutIntent = Intent(this, LoginActivity::class.java)
            startActivity(logoutIntent)
            ttsObj?.speak("로그아웃 버튼입니다. 꾹 누르면 로그인 화면으로 이동합니다", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
            setSttOption()
        }

        button4.setOnLongClickListener {
            ttsObj?.speak("로그인 화면으로 이동합니다", TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            val logoutIntent = Intent(this, LoginActivity::class.java)
            startActivity(logoutIntent)
            false
        }


        detector = GestureDetector(this, object : GestureDetector.OnGestureListener {
            override fun onDown(motionEvent: MotionEvent): Boolean {
                println("onDown() 호출됨")
                return true
            }

            override fun onShowPress(motionEvent: MotionEvent) {
                println("onSHowPress() 호출됨")
            }

            override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
                println("onSingleTapUp() 호출됨")
                return true
            }

            override fun onScroll(
                motionEvent: MotionEvent,
                motionEvent1: MotionEvent,
                v: Float,
                v1: Float
            ): Boolean {
                ttsObj?.stop()
                return true
            }

            override fun onLongPress(motionEvent: MotionEvent) {
                println("onLongPress() 호출됨")
            }

            override fun onFling(
                motionEvent: MotionEvent,
                motionEvent1: MotionEvent,
                v: Float,
                v1: Float
            ): Boolean {
                println("onFling() 호출됨 : $v, $v1")
                return true
            }
        })


    }
    private fun setSttOption() {
        recognizer = SpeechRecognizer.createSpeechRecognizer(this)
    }



}