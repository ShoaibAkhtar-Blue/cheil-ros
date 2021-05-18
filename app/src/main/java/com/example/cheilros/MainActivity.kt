package com.example.cheilros

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setupActionBarWithNavController(findNavController(R.id.main_fragment))

        val navController = findNavController(R.id.main_fragment)

        // Build the request with the permissions you would like to request and send it.
        permissionsBuilder(Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE).build().send { result ->
            // Handle the result, for example check if all the requested permissions are granted.
            if (result.allGranted()) {
                // All the permissions are granted.
            }
        }

        //Change Initial Nav Graph
//        val navGraph = navController.getGraph();
//        navGraph.setStartDestination(R.id.login_graph);
//        navController.setGraph(navGraph);
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}