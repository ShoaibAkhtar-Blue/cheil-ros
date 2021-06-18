package com.example.cheilros.activities

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.example.cheilros.R
import com.example.cheilros.helpers.*
import com.example.cheilros.models.CheckInOutModel
import com.example.cheilros.models.HookBin
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.parameter.Flash
import io.fotoapparat.parameter.Zoom
import io.fotoapparat.result.transformer.scaled
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.dialog_add_visit.*
import kotlinx.android.synthetic.main.dialog_photo_preview.*
import kotlinx.android.synthetic.main.dialog_photo_preview.btnCancel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.math.roundToInt


class CameraActivity : AppCompatActivity() {

    lateinit var CSP: CustomSharedPref

    private val client = OkHttpClient()

    lateinit var locationManager: LocationManager

    private val permissionsDelegate = PermissionsDelegate(this)

    private var permissionsGranted: Boolean = false
    private var activeCamera: Camera = Camera.Back

    private lateinit var fotoapparat: Fotoapparat
    private lateinit var cameraZoom: Zoom.VariableZoom

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        CSP = CustomSharedPref(this)

        permissionsGranted = permissionsDelegate.hasCameraPermission()

        if (permissionsGranted) {
            cameraView.visibility = View.VISIBLE
        } else {
            permissionsDelegate.requestCameraPermission()
        }

        fotoapparat = Fotoapparat(
            context = this,
            view = cameraView,
            focusView = focusView,
            logger = logcat(),
            lensPosition = activeCamera.lensPosition,
            cameraConfiguration = activeCamera.configuration,
            cameraErrorCallback = { Log.e(LOGGING_TAG, "Camera error: ", it) }
        )

        capture onClick takePicture()
        switchCamera onClick changeCamera()
        //torchSwitch onCheckedChanged toggleFlash()
    }

    // this method saves the image to gallery
    private fun saveMediaToStorage(bitmap: Bitmap): String {
        // Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        // Output stream
        var fos: OutputStream? = null

        /*// For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            this.contentResolver?.also { resolver ->

                // Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {
                    Log.i("directoryImg", Environment.DIRECTORY_PICTURES)
                    Log.i("directoryImg", Environment.getExternalStorageDirectory(Environment.DIRECTORY_PICTURES).toString())
                    // putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                // Inserting the contentValues to
                // contentResolver and getting the Uri
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                // Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            // These for devices running on android < Q
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            Log.i("directoryImg1", "${imagesDir.absolutePath}/$filename")
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {

            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this , "Captured View and saved to Gallery" , Toast.LENGTH_SHORT).show()
        }*/

        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        Log.i("directoryImg1", "${imagesDir.absolutePath}/$filename")
        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)
        fos?.use {

            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Captured View and saved to Gallery", Toast.LENGTH_SHORT).show()
        }
        return "${imagesDir.absolutePath}/$filename"
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun takePicture(): () -> Unit = {
        val photoResult = fotoapparat
            .autoFocus()
            .takePicture()

        photoResult
            .saveToFile(
                File(
                    getExternalFilesDir("photos"),
                    "photo.jpg"
                )
            )

        photoResult
            .toBitmap(scaled(scaleFactor = 0.25f))
            .whenAvailable { photo ->
                photo
                    ?.let {
                        Log.i(
                            LOGGING_TAG,
                            "New photo captured. Bitmap length: ${it.bitmap.byteCount}"
                        )

                        val bitmapImg: Bitmap = it.bitmap

                        val imageView = findViewById<ImageView>(R.id.result)

                        if (CSP.getData("fragName").equals("ActivityDetail")) {
                            println("ActivityDetail")
                            val savedImagePath: String = saveMediaToStorage(bitmapImg)
                            println(savedImagePath)
                            CSP.saveData("ActivityDetail_SESSION_IMAGE", savedImagePath)
                            finish()
                        } else if (CSP.getData("fragName").equals("TrainingDetail")) {
                            println("TrainingDetail")
                            val savedImagePath: String = saveMediaToStorage(bitmapImg)
                            println(savedImagePath)
                            CSP.saveData("TrainingDetail_SESSION_IMAGE", savedImagePath)
                            finish()
                        } else if (CSP.getData("fragName").equals("Dashboard")) {
                            println("Dashboard")
                            val savedImagePath: String = saveMediaToStorage(bitmapImg)
                            println(savedImagePath)
                            CSP.saveData("Dashboard_SESSION_IMAGE", savedImagePath)
                            finish()
                        } else {
                            imageView.setImageBitmap(it.bitmap)
                            imageView.rotation = (-it.rotationDegrees).toFloat()

                            val dialog = Dialog(this)
                            dialog.setContentView(R.layout.dialog_photo_preview)
                            dialog.setCancelable(false)
                            dialog.setCanceledOnTouchOutside(true)

                            dialog.imgPrev.setImageBitmap(it.bitmap)
                            dialog.imgPrev.rotation = (-it.rotationDegrees).toFloat()

                            //Remarks Permission
                            if (CSP.getData("sess_visit_status_id").equals("1")) {
                                if (CSP.getData("CheckIn_Remarks").equals("N"))
                                    dialog.etRemarksJP.visibility = View.GONE
                            } else {
                                if (CSP.getData("CheckOut_Camera").equals("N"))
                                    dialog.etRemarksJP.visibility = View.GONE
                            }

                            dialog.btnUpload.setOnClickListener {
                                dialog.btnUpload.isEnabled = false
                                dialog.btnUpload.isClickable = false
                                dialog.btnCancel.isEnabled = false
                                dialog.btnCancel.isClickable = false

                                dialog.btnUpload.text = "Uploading"

                                //region Save File

//                            val exteranlStorageState = Environment.getExternalStorageState()
//                            if(exteranlStorageState.equals(Environment.MEDIA_MOUNTED)){
//                                val storageDir = Environment.getStorageDirectory().toString()
//                            }

                                /*val file_path = Environment.getStorageDirectory().path + "/ROS"
                                println(file_path)
                                val dir = File(file_path)
                                if (!dir.exists()) dir.mkdirs()

                                val file = File(dir, "ros.png")
                                val fOut = FileOutputStream(file)
                                bitmapImg.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                                fOut.flush()
                                fOut.close()*/
                                //endregion

                                //region Get Location
                                var lat: String = "0"
                                var lng: String = "0"

                                locationManager =
                                    this@CameraActivity.getSystemService(LOCATION_SERVICE) as LocationManager
                                if (ActivityCompat.checkSelfPermission(
                                        this@CameraActivity,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                        this@CameraActivity,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return@setOnClickListener
                                }
                                locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    5000,
                                    0F,
                                    object :
                                        LocationListener {
                                        override fun onLocationChanged(location: Location) {
                                            lat = location.latitude.toString()
                                            lng = location.longitude.toString()
                                            println("loc: ${location.latitude}")
                                        }

                                    })
                                //endregion
                                val savedImagePath: String = saveMediaToStorage(bitmapImg)
                                var checkTypeAPI: String = if (CSP.getData("sess_visit_status_id")
                                        .equals("1")
                                ) "CheckIn" else "CheckOut"

                                /*val uploadImgFile:File =  File(savedImagePath)
                                println(uploadImgFile.exists())*/

                                sendCheckInOutRequest(
                                    "${CSP.getData("base_url")}/${checkTypeAPI}.asmx/${checkTypeAPI}Img?VisitID=${
                                        CSP.getData(
                                            "sess_visit_id"
                                        )
                                    }&Longitude=$lng&Latitude=$lat&Remarks=${dialog.etRemarksJP.text}",
                                    savedImagePath
                                )
                            }
                            dialog.btnCancel.setOnClickListener {
                                dialog.dismiss()
                            }

                            dialog.show()

                            /*val intent = Intent(applicationContext, PhotoPreviewActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                            //intent.putExtra("capturedImage", it.bitmap)
                            applicationContext.startActivity(intent)*/
                        }
                    }
                    ?: Log.e(LOGGING_TAG, "Couldn't capture photo.")
            }
    }

    private fun changeCamera(): () -> Unit = {
        activeCamera = when (activeCamera) {
            Camera.Front -> Camera.Back
            Camera.Back -> Camera.Front
        }

        fotoapparat.switchTo(
            lensPosition = activeCamera.lensPosition,
            cameraConfiguration = activeCamera.configuration
        )

        adjustViewsVisibility()

        //torchSwitch.isChecked = false

        Log.i(
            LOGGING_TAG,
            "New camera position: ${if (activeCamera is Camera.Back) "back" else "front"}"
        )
    }

    private fun toggleFlash(): (CompoundButton, Boolean) -> Unit = { _, isChecked ->
        fotoapparat.updateConfiguration(
            UpdateConfiguration(
                flashMode = if (isChecked) {
                    firstAvailable(
                        torch(),
                        off()
                    )
                } else {
                    off()
                }
            )
        )

        Log.i(LOGGING_TAG, "Flash is now ${if (isChecked) "on" else "off"}")
    }

    override fun onStart() {
        super.onStart()
        if (permissionsGranted) {
            fotoapparat.start()
            adjustViewsVisibility()
        }
    }

    override fun onStop() {
        super.onStop()
        if (permissionsGranted) {
            fotoapparat.stop()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissionsDelegate.resultGranted(
                requestCode,
                permissions as Array<String>, grantResults
            )
        ) {
            permissionsGranted = true
            fotoapparat.start()
            adjustViewsVisibility()
            cameraView.visibility = View.VISIBLE
        }
    }

    private fun adjustViewsVisibility() {
        /*fotoapparat.getCapabilities()
            .whenAvailable { capabilities ->
                capabilities
                    ?.let {
                        (it.zoom as? Zoom.VariableZoom)
                            ?.let { zoom -> setupZoom(zoom) }
                            ?: run { zoomSeekBar.visibility = View.GONE }

                        torchSwitch.visibility =
                            if (it.flashModes.contains(Flash.Torch)) View.VISIBLE else View.GONE
                    }
                    ?: Log.e(LOGGING_TAG, "Couldn't obtain capabilities.")
            }*/

        switchCamera.visibility = if (fotoapparat.isAvailable(front())) View.VISIBLE else View.GONE
    }

    private fun setupZoom(zoom: Zoom.VariableZoom) {
        /*zoomSeekBar.max = zoom.maxZoom
        cameraZoom = zoom
        zoomSeekBar.visibility = View.VISIBLE
        zoomSeekBar onProgressChanged { updateZoom(zoomSeekBar.progress) }
        updateZoom(0)*/
    }

    private fun updateZoom(progress: Int) {
        /*fotoapparat.setZoom(progress.toFloat() / zoomSeekBar.max)
        val value = cameraZoom.zoomRatios[progress]
        val roundedValue = ((value.toFloat()) / 10).roundToInt().toFloat() / 10
        zoomLvl.text = String.format("%.1f ×", roundedValue)*/
    }


    fun sendCheckInOutRequest(url: String, imgPath: String) {

        var checkType: String =
            if (CSP.getData("sess_visit_status_id").equals("1")) "CheckInImage" else "CheckOutImage"

        println(checkType)

        val client = OkHttpClient()
        val sourceFile = File(imgPath)
        val mimeType = CoreHelperMethods(this@CameraActivity).getMimeType(sourceFile)
        val fileName: String = sourceFile.name

        try {
            val requestBody: RequestBody =
                MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(
                        checkType,
                        fileName,
                        sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                    )
                    .build()

            val request: Request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Sneaker.with(this@CameraActivity) // Activity, Fragment or ViewGroup
                            .setTitle("Error!!")
                            .setMessage(e.message.toString())
                            .sneakWarning()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    println(body)

                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, CheckInOutModel::class.java)
                    println(apiData)
                    if (apiData.status == 200) {
                        runOnUiThread {
                            Sneaker.with(this@CameraActivity) // Activity, Fragment or ViewGroup
                                .setTitle("Success!!")
                                .setMessage("Data Updated")
                                .sneakSuccess()
                            onBackPressed()
                        }
                    } else {
                        runOnUiThread {
                            Sneaker.with(this@CameraActivity) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not Updated.")
                                .sneakWarning()
                        }
                    }
                }
            })


            /* val response: Response = client.newCall(request).execute()

             println(response.body!!.string())

             if (response.isSuccessful) {
                 Log.d("File upload", "success")
             } else {
                 Log.e("File upload", "failed")
             }*/
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.e("File upload", "failed")
        }

//        var checkType: String =
//            if (CSP.getData("sess_visit_status_id").equals("1")) "CheckInImage" else "CheckOutImage"
//
//
//        val requestBody = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart(
//                checkType, "${System.currentTimeMillis()}.jpg",
//                File(imgPath).asRequestBody("image/*".toMediaType())
//            )
//            .build()
//
//        val request = Request.Builder()
//            .url(url)
//            .post(requestBody)
//            .build()

        /*client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {
                    Sneaker.with(this@CameraActivity) // Activity, Fragment or ViewGroup
                        .setTitle("Error!!")
                        .setMessage(e.message.toString())
                        .sneakWarning()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

//                val gson = GsonBuilder().create()
//                val apiData = gson.fromJson(body, HookBin::class.java)
//                println(apiData)
                *//*if (apiData.status == 200) {
                    runOnUiThread {
                        Sneaker.with(this@CameraActivity) // Activity, Fragment or ViewGroup
                            .setTitle("Success!!")
                            .setMessage("Data Updated")
                            .sneakSuccess()
                        onBackPressed()
                    }
                } else {
                    runOnUiThread {
                        Sneaker.with(this@CameraActivity) // Activity, Fragment or ViewGroup
                            .setTitle("Error!!")
                            .setMessage("Data not Updated.")
                            .sneakWarning()
                    }
                }*//*
            }
        })*/
    }

}

const val LOGGING_TAG = "Fotoapparat Example"

sealed class Camera(
    val lensPosition: LensPositionSelector,
    val configuration: CameraConfiguration
) {

    object Back : Camera(
        lensPosition = back(),
        configuration = CameraConfiguration(
            previewResolution = firstAvailable(
                wideRatio(highestResolution()),
                standardRatio(highestResolution())
            ),
            previewFpsRange = highestFps(),
            flashMode = off(),
            focusMode = firstAvailable(
                continuousFocusPicture(),
                autoFocus(),
                fixed()
            ),
            frameProcessor = {
                // Do something with the preview frame
            }
        )
    )

    object Front : Camera(
        lensPosition = front(),
        configuration = CameraConfiguration(
            previewResolution = firstAvailable(
                wideRatio(highestResolution()),
                standardRatio(highestResolution())
            ),
            previewFpsRange = highestFps(),
            flashMode = off(),
            focusMode = firstAvailable(
                fixed(),
                autoFocus()
            )
        )
    )
}