package com.example.cheilros.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.activity_new_dashboard.view.*


class NewDashboardActivity : AppCompatActivity() {

    lateinit var userLocation: Location
    private lateinit var toolbar: Toolbar
    lateinit var CSP: CustomSharedPref
    private val UPDATE_REQUEST_CODE = 1500

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;

        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    val TAG = "MainActivity"

    // Storage Permissions
    private val REQUEST_LOCATION = 1
    private val PERMISSIONS_LOCATION = arrayOf<String>(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // inside a basic activity
    private var locationManager : LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_dashboard)

        verifyLocationPermissions(this)
        // Create persistent LocationManager reference
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        getLocation()
        CSP = CustomSharedPref(this)
        inAppUpdatesCheck()
        //setupToolbar()
    }

    fun getLocation(){
        try {
            // Request location updates
            Log.d(TAG,"setOnClickListener")
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)



        } catch(ex: SecurityException) {
            ex.printStackTrace()
            Log.d(TAG, "Security Exception, no location available")
        }
    }

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d(TAG, "onLocationChanged()")
            userLocation = location
//            posc_lat.text = ("lat: " + location.latitude)
//            posc_long.text = ("lon:" + location.longitude)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.d(TAG, "onStatusChanged()")

        }
        override fun onProviderEnabled(provider: String) {
            Log.d(TAG, "onProviderEnabled()")

        }
        override fun onProviderDisabled(provider: String) {
            Log.d(TAG, "onProviderDisabled()")

        }
    }

    override fun onResume() {
        super.onResume()
        inAppUpdatesCheck()
    }

    fun setupToolbar(title: String = "Title") {
        toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        toolbar.toolbar_title.text = title
    }

    fun inAppUpdatesCheck() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this@NewDashboardActivity, UPDATE_REQUEST_CODE)
                    }catch (ex: IntentSender.SendIntentException){
                        Log.e("Error_", ex.message.toString())
                    }

            }
        }

    }

    fun pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                //permission already granted
                pickImageFromGallery();
            }
        } else {
            //system OS is < Marshmallow
            pickImageFromGallery();
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_LOCATION -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }

    private fun verifyLocationPermissions(activity: Activity?) {
        // Check if we have write permission
        val permission: Int = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_LOCATION,
                REQUEST_LOCATION
            )
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val selectedImageUri: Uri = data?.data!!
            val picturePath: String =
                getPath(this, selectedImageUri).toString()
            Log.d("Picture Path", picturePath)

            CSP.saveData("sess_gallery_img", picturePath)

            println("Image Added")
            //image_view.setImageURI(data?.data)
        }
        if (requestCode == UPDATE_REQUEST_CODE){
            Toast.makeText(this, "Downloading Start", Toast.LENGTH_SHORT).show()
            if(resultCode != RESULT_OK)
                Log.e("Error_", "Update Flow Failed")
        }
    }

    fun getPath(context: Context, uri: Uri?): String? {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = context.getContentResolver().query(uri!!, proj, null, null, null)!!
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(column_index)
            }
            cursor.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }
}