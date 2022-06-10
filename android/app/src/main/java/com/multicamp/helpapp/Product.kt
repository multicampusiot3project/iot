package com.multicamp.helpapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_product.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Product : AppCompatActivity() {
    val sub_topic = "android/#"
    val server_uri ="tcp://13.52.187.248" //broker의 ip와 port
    var mymqtt : MyMqtt? = null

    private val REQUEST_TAKE_PHOTO = 1
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        mymqtt= MyMqtt(this,server_uri)
        mymqtt?.connect(arrayOf<String>(sub_topic))
        takePictureIntent()
    }

    // 사진 찍는 인텐트
    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                // 사진 파일을 만듭니다.
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.d("test", "error: $ex")
                    null
                }
                // photoUri를 보내는 코드
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.multicamp.helpapp",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            galleryAddPic()
            setPic()
        }
    }
    // 사진 파일을 만드는 메소드
    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            Log.d("test", "currentPhotoPath : $currentPhotoPath")
        }
    }
    // 갤러리에 파일을 추가하는 함수.
    fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            Log.d("test", "currentPhotoPath2 : $currentPhotoPath")
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }
    // ImageView에 사진을 넣는 메소드
    fun setPic() {
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height
        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            val photoW: Int = outWidth
            val photoH: Int = outHeight
            // 이미지 배율 변수
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }

}