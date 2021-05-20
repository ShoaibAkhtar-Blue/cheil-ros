package com.example.cheilros.activities

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.example.cheilros.R


class PhotoPreviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_preview)

        //val bmp = this.intent.getParcelableExtra<Parcelable>("bmp") as Bitmap?

        //imgPreview.setImageBitmap(bitmap)
    }
}