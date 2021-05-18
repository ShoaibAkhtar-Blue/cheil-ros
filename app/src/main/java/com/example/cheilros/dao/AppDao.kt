package com.example.cheilros.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cheilros.data.AppSetting
import com.example.cheilros.data.UserData
import com.example.cheilros.data.UserPermission

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

@Dao
interface UserDataDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: UserData)

    @Query("SELECT * FROM user_data ORDER BY id ASC")
    fun fetchAllUser(): LiveData<List<UserData>>

    @Query("SELECT * FROM user_data")
    fun getAllUser(): List<UserData>


    @Query("DELETE FROM user_data")
    fun nukeTable()
}

@Dao
interface UserPermissionDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPermission(permission: UserPermission)

    @Query("SELECT * FROM user_permission ORDER BY id ASC")
    fun fetchAllPermission(): LiveData<List<UserPermission>>

    @Query("SELECT * FROM user_permission")
    fun getAllPermission(): List<UserPermission>

    @Query("DELETE FROM user_permission")
    fun nukeTable()
}