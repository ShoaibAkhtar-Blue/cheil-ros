package com.example.cheilros.fragments

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.cheilros.R
import com.example.cheilros.activities.Camera
import com.example.cheilros.activities.LOGGING_TAG
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.helpers.PermissionsDelegate
import com.example.cheilros.helpers.onClick
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.parameter.Zoom
import io.fotoapparat.result.transformer.scaled
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.dialog_photo_preview.*
import kotlinx.android.synthetic.main.fragment_camera.view.*
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class CameraFragment : BaseFragment() {


    lateinit var viewCV: View

    //private val client = OkHttpClient()

    lateinit var locationManager: LocationManager

    lateinit var permissionsDelegate: PermissionsDelegate

    private var permissionsGranted: Boolean = false
    private var activeCamera: Camera = Camera.Back

    private lateinit var fotoapparat: Fotoapparat
    private lateinit var cameraZoom: Zoom.VariableZoom

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewCV = inflater.inflate(R.layout.fragment_camera, container, false)

        permissionsDelegate = PermissionsDelegate(requireActivity())

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        if (permissionsGranted) {
            viewCV.cameraView.visibility = View.VISIBLE
        } else {
            permissionsDelegate.requestCameraPermission()
        }

        fotoapparat = Fotoapparat(
            context = requireContext(),
            view = viewCV.cameraView,
            focusView = focusView,
            logger = logcat(),
            lensPosition = activeCamera.lensPosition,
            cameraConfiguration = activeCamera.configuration,
            cameraErrorCallback = { Log.e(LOGGING_TAG, "Camera error: ", it) }
        )

        viewCV.capture onClick takePicture()
        viewCV.switchCamera onClick changeCamera()

        return view
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


    }

    // this method saves the image to gallery
    private fun saveMediaToStorage(bitmap: Bitmap): String {
        // Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        // Output stream
        var fos: OutputStream? = null

        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        Log.i("directoryImg1", "${imagesDir.absolutePath}/$filename")
        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)
        fos?.use {

            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(requireContext(), "Captured View and saved to Gallery", Toast.LENGTH_SHORT).show()
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
                    requireActivity().getExternalFilesDir("photos"),
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

                        val imageView = viewCV.result

                        imageView.setImageBitmap(it.bitmap)
                        imageView.rotation = (-it.rotationDegrees).toFloat()

                        /*val dialog = Dialog(this)
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

                            *//*val file_path = Environment.getStorageDirectory().path + "/ROS"
                            println(file_path)
                            val dir = File(file_path)
                            if (!dir.exists()) dir.mkdirs()

                            val file = File(dir, "ros.png")
                            val fOut = FileOutputStream(file)
                            bitmapImg.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                            fOut.flush()
                            fOut.close()*//*
                            //endregion

                            //region Get Location
                            var lat: String = "0"
                            var lng: String = "0"

                            locationManager =
                                requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
                            if (ActivityCompat.checkSelfPermission(
                                    requireActivity(),
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                    requireActivity(),
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
                                0F
                            ) { location ->
                                lat = location.latitude.toString()
                                lng = location.longitude.toString()
                                println("loc: ${location.latitude}")
                            }
                            //endregion
                            val savedImagePath: String = saveMediaToStorage(bitmapImg)
                            var checkTypeAPI: String = if (CSP.getData("sess_visit_status_id")
                                    .equals("1")
                            ) "CheckIn" else "CheckOut"
                        }
                        dialog.btnCancel.setOnClickListener {
                            dialog.dismiss()
                        }

                        dialog.show()*/
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

    private fun adjustViewsVisibility() {
        switchCamera.visibility = if (fotoapparat.isAvailable(front())) View.VISIBLE else View.GONE
    }


    val LOGGING_TAG = "Fotoapparat Example"

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

}


