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

@Entity(tableName = "user_data")
data class UserData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val memberID: Int,
    val typeID: Int,
    val memberName: String,
    val singleID: String,
    val email: String,
    val divisionID: Int,
    val divisionName: String,
    val imagePath: String,
    val teamType: String,
    val regionName: String,
    val MarketType: Int
)

@Entity(tableName = "user_permission")
data class UserPermission(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val permissionID: Int,
    val permissionName: String,
    val permissionValue: String
)