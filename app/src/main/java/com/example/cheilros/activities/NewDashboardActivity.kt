package com.example.cheilros.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.cheilros.R
import kotlinx.android.synthetic.main.activity_new_dashboard.view.*

class NewDashboardActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_dashboard)

        setupToolbar()
    }

    fun setupToolbar(title: String = "Title"){
        toolbar= findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        toolbar.toolbar_title.text = title
    }
}