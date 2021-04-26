package com.example.cheilros

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setupActionBarWithNavController(findNavController(R.id.main_fragment))

        val navController = findNavController(R.id.main_fragment)

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