package com.example.cheilros.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cheilros.data.AppSetting

@Dao
interface AppSettingDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSettings(setting: AppSetting)

    @Query("SELECT * FROM app_settings ORDER BY id ASC")
    fun fetchAllSetting(): LiveData<List<AppSetting>>

    @Query("SELECT * FROM app_settings")
    fun getAllSetting(): List<AppSetting>


    @Query("DELETE FROM app_settings")
    fun nukeTable()
}