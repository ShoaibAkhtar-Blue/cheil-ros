package com.example.cheilros.datavm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cheilros.data.AppSetting
import com.example.cheilros.database.AppSettingDatabase
import com.example.cheilros.datarepo.AppSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppSettingViewModel(application: Application): AndroidViewModel(application){

    val fetchAllData: LiveData<List<AppSetting>>
    val getAllSetting: List<AppSetting>
    private val repository: AppSettingsRepository

    init {
        val appSettingDao = AppSettingDatabase.getDatabase(application).appSettingDao()
        repository = AppSettingsRepository(appSettingDao)
        fetchAllData = repository.fetchAllSetting
        getAllSetting = repository.getAllSetting
    }

    fun addSettings(appsetting: AppSetting){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSettings(appsetting)
        }
    }

    fun nukeTable(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.nukeTable()
        }
    }

}