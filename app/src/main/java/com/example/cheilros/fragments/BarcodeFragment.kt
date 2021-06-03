package com.example.cheilros.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cheilros.R
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.fragment_barcode.*


class BarcodeFragment : Fragment() {
    //private lateinit var svBarcode: SurfaceView

    private lateinit var detector: BarcodeDetector
    private lateinit var cameraSource: CameraSource

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_barcode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val builder = AlertDialog.Builder(requireContext())
        var taskHandler = Handler()
        var runnable = object:Runnable{
            override fun run() {
                cameraSource.stop()
                val alert = builder.create()
                alert.show()
                taskHandler.removeCallbacksAndMessages(null)
            }
        }

        detector = BarcodeDetector.Builder(requireContext()).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}
            @SuppressLint("MissingPermission")
            override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
                val barcodes = p0?.detectedItems
                if (barcodes!!.size() > 0) {
                    builder.setMessage(barcodes.valueAt(0).displayValue)
                    builder.setPositiveButton("Ok") { dialog, which ->
                        cameraSource.start(sv_barcode.holder)
                    }
                    taskHandler.post(runnable)
                }
            }
        })

        cameraSource = CameraSource.Builder(requireContext(), detector).setRequestedPreviewSize(1024, 768)
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
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    cameraSource.start(sv_barcode.holder)
                else ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA), 123)
            }

        })


    }


}