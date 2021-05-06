package com.example.cheilros.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSetting(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val labelID: Int,
    val screenName: String,
    val labelName: String,
    val languageID: Int,
    val imagePath: String,
    val fixedLabelName: String
)