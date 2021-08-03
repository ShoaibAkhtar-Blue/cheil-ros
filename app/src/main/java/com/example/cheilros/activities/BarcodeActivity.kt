package com.example.cheilros.activities

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.fragment_barcode.*

class BarcodeActivity : AppCompatActivity() {

    lateinit var CSP: CustomSharedPref

    //private lateinit var sv_barcode: SurfaceView
    private lateinit var detector: BarcodeDetector
    private lateinit var cameraSource: CameraSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)
        CSP = CustomSharedPref(this)

        val builder = AlertDialog.Builder(this)
        var taskHandler = Handler()
        var runnable = object : Runnable {
            override fun run() {
                cameraSource.stop()
                val alert = builder.create()
                alert.show()
                taskHandler.removeCallbacksAndMessages(null)
            }
        }

        detector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            @SuppressLint("MissingPermission")
            override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
                val barcodes = p0?.detectedItems
                if (barcodes!!.size() > 0) {
                    //builder.setMessage(barcodes.valueAt(0).displayValue)
                    builder.setMessage("Your Barcode: ${barcodes.valueAt(0).displayValue}")
                    builder.setPositiveButton("Save") { dialog, which ->
                        if (CSP.getData("activity_barcodes").equals("")) {
                            CSP.saveData("activity_barcodes", barcodes.valueAt(0).displayValue)
                        } else {
                            CSP.saveData(
                                "activity_barcodes",
                                "${CSP.getData("activity_barcodes")},${barcodes.valueAt(0).displayValue}"
                            )
                        }

                        cameraSource.stop()
                        finish()
                    }

                    builder.setNegativeButton("Cancel") {dialog, which ->
                        cameraSource.start(sv_barcode.holder)
                        dialog.dismiss()
                    }
                    taskHandler.post(runnable)
                }
            }
        })
        cameraSource = CameraSource.Builder(this, detector).setRequestedPreviewSize(1024, 768)
            .setRequestedFps(30f).setAutoFocusEnabled(true).build()
        sv_barcode.holder.addCallback(object : SurfaceHolder.Callback2 {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if (ContextCompat.checkSelfPermission(
                        this@BarcodeActivity,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                )
                    cameraSource.start(sv_barcode.holder)
                else ActivityCompat.requestPermissions(
                    this@BarcodeActivity,
                    arrayOf(android.Manifest.permission.CAMERA),
                    123
                )
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {

            }

            override fun surfaceRedrawNeeded(holder: SurfaceHolder) {

            }

        })


        /*val builder = AlertDialog.Builder(this)
        var taskHandler = Handler()
        var runnable = object : Runnable {
            override fun run() {
                cameraSource.stop()
                val alert = builder.create()
                alert.show()
                taskHandler.removeCallbacksAndMessages(null)
            }
        }

        detector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            @SuppressLint("MissingPermission")
            override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
                val barcodes = p0?.detectedItems
                if (barcodes!!.size() > 0) {

                    if (CSP.getData("activity_barcodes").equals("")) {
                        CSP.saveData("activity_barcodes", barcodes.valueAt(0).displayValue)
                    } else {
                        CSP.saveData(
                            "activity_barcodes",
                            "${CSP.getData("activity_barcodes")},${barcodes.valueAt(0).displayValue}"
                        )
                    }
                    cameraSource.stop()
                    finish()
                }
            }
        })

        cameraSource =
            CameraSource.Builder(this, detector).setRequestedPreviewSize(1024, 768)
                .setRequestedFps(30f).setAutoFocusEnabled(true).build()

        sv_barcode.holder.addCallback(object : SurfaceHolder.Callback2 {
            override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
                print("1")
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                print("2")
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                print("3")
                cameraSource.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                print("4")
                if (ContextCompat.checkSelfPermission(
                        this@BarcodeActivity,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                )
                    cameraSource.start(sv_barcode.holder)
                else ActivityCompat.requestPermissions(
                    this@BarcodeActivity,
                    arrayOf(android.Manifest.permission.CAMERA),
                    123
                )
            }

        })*/


    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                cameraSource.start(sv_barcode.holder)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.release()
        cameraSource.stop()
        cameraSource.release()
    }


}