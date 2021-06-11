package com.example.cheilros.helpers

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cheilros.R

class PermissionsDelegate(private val activity: Activity) {
    fun hasCameraPermission(): Boolean {
        val permissionCheckResult = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        )
        return permissionCheckResult == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE
        )
    }

    fun resultGranted(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ): Boolean {
        if (requestCode != REQUEST_CODE) {
            return false
        }
        if (grantResults.size < 1) {
            return false
        }
        if (permissions[0] != Manifest.permission.CAMERA) {
            return false
        }
        val noPermissionView = activity.findViewById<View>(R.id.no_permission)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            noPermissionView.visibility = View.GONE
            return true
        }
        requestCameraPermission()
        noPermissionView.visibility = View.VISIBLE
        return false
    }

    companion object {
        private const val REQUEST_CODE = 10
    }
}