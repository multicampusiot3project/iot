package com.multicamp.helpapp


import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech

import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : AppCompatActivity() {

    var stt_intent: Intent? = null
    var recognizer:SpeechRecognizer? = null
    var ttsObj: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        ttsObj = TextToSpeech(this, TextToSpeech.OnInitListener {
            if(it!= TextToSpeech.ERROR){
                ttsObj?.language = Locale.KOREAN
            }
        })

        stt_intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        stt_intent?.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,packageName)
        stt_intent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR")
        val utteranceId = this.hashCode().toString() + "0"
        var mBackWait:Long = 0
        button.setOnClickListener {
            val listIntent = Intent(this, MainActivity::class.java)
            startActivity(listIntent)
            ttsObj?.speak("쇼핑리스트 화면으로 하는 버튼입니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
            if(System.currentTimeMillis() - mBackWait >=2000 ) {
                ttsObj?.speak("쇼핑리스트 화면으로 이동합니다.", TextToSpeech.QUEUE_FLUSH,null,
                        utteranceId)
                mBackWait = System.currentTimeMillis()
                startActivity(listIntent)
            }
            recognizer = SpeechRecognizer.createSpeechRecognizer(this)
            recognizer?.startListening(stt_intent)
        }

        button2.setOnClickListener {
            val searchIntent = Intent(this, Product::class.java)
            startActivity(searchIntent)
            ttsObj?.speak("상품검색 화면으로 이동하는 버튼입니다", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
            if(System.currentTimeMillis() - mBackWait >=2000 ) {
                ttsObj?.speak("쇼핑 검색 화면으로 이동합니다.", TextToSpeech.QUEUE_FLUSH,null,
                        utteranceId)
                mBackWait = System.currentTimeMillis()
                startActivity(searchIntent)
            }
            recognizer = SpeechRecognizer.createSpeechRecognizer(this)
            recognizer?.startListening(stt_intent)
        }

        button3.setOnClickListener {
            val searchIntent = Intent(this, Product::class.java)
            startActivity(searchIntent)
            ttsObj?.speak("검색화면으로 이동하는 페이지 입니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
            if(System.currentTimeMillis() - mBackWait >=2000 ) {
                mBackWait = System.currentTimeMillis()
                startActivity(searchIntent)
            }
            recognizer = SpeechRecognizer.createSpeechRecognizer(this)
            recognizer?.startListening(stt_intent)
        }

        button4.setOnClickListener {
            val logoutIntent = Intent(this, Product::class.java)
            startActivity(logoutIntent)
            ttsObj?.speak("상품화면으로 이동하는 버튼입니다.", TextToSpeech.QUEUE_FLUSH,null,
                    utteranceId)
            if(System.currentTimeMillis() - mBackWait >=2000 ) {
                mBackWait = System.currentTimeMillis()
                startActivity(logoutIntent)
            }
            recognizer = SpeechRecognizer.createSpeechRecognizer(this)
            recognizer?.startListening(stt_intent)
        }
    }
}