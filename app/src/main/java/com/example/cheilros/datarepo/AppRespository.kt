package com.example.cheilros.datarepo

import androidx.lifecycle.LiveData
import com.example.cheilros.dao.AppSettingDao
import com.example.cheilros.data.AppSetting

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