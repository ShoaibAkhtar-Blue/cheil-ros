package com.example.cheilros.datarepo

import androidx.lifecycle.LiveData
import com.example.cheilros.dao.AppSettingDao
import com.example.cheilros.dao.UserDataDao
import com.example.cheilros.dao.UserPermissionDao
import com.example.cheilros.data.AppSetting
import com.example.cheilros.data.UserData
import com.example.cheilros.data.UserPermission

class AppSettingsRepository(private val appsettingDao: AppSettingDao) {

    val fetchAllSetting: LiveData<List<AppSetting>> = appsettingDao.fetchAllSetting()
    val getAllSetting: List<AppSetting> = appsettingDao.getAllSetting()

    suspend fun addSettings(appsetting: AppSetting){
        appsettingDao.addSettings(appsetting)
    }

    fun nukeTable(){
        appsettingDao.nukeTable()
    }

}


class UserDataRepository(private val userDataDao: UserDataDao) {

    val fetchAllUser: LiveData<List<UserData>> = userDataDao.fetchAllUser()
    val getAllUser: List<UserData> = userDataDao.getAllUser()

    suspend fun addUser(userdata: UserData){
        userDataDao.addUser(userdata)
    }

    fun nukeTable(){
        userDataDao.nukeTable()
    }
}

class UserPermissionRepository(private val userPermissionDao: UserPermissionDao) {

    val fetchAllPermission: LiveData<List<UserPermission>> = userPermissionDao.fetchAllPermission()
    val getAllPermission: List<UserPermission> = userPermissionDao.getAllPermission()

    suspend fun addPermission(userpermission: UserPermission){
        userPermissionDao.addPermission(userpermission)
    }

    fun nukeTable(){
        userPermissionDao.nukeTable()
    }
}