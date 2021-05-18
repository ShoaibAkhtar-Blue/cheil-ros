package com.example.cheilros.datavm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cheilros.data.AppSetting
import com.example.cheilros.data.UserData
import com.example.cheilros.data.UserPermission
import com.example.cheilros.database.AppSettingDatabase
import com.example.cheilros.database.UserDataDatabase
import com.example.cheilros.database.UserPermissionDatabase
import com.example.cheilros.datarepo.AppSettingsRepository
import com.example.cheilros.datarepo.UserDataRepository
import com.example.cheilros.datarepo.UserPermissionRepository
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

class UserDataViewModel(application: Application): AndroidViewModel(application){

    val fetchAllUser: LiveData<List<UserData>>
    val getAllUser: List<UserData>
    private val repository: UserDataRepository

    init {
        val userDataDao = UserDataDatabase.getDatabase(application).userDataDao()
        repository = UserDataRepository(userDataDao)
        fetchAllUser = repository.fetchAllUser
        getAllUser = repository.getAllUser
    }

    fun addUser(userdata: UserData){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(userdata)
        }
    }

    fun nukeTable(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.nukeTable()
        }
    }
}

class UserPermissionViewModel(application: Application): AndroidViewModel(application){

    val fetchAllPermission: LiveData<List<UserPermission>>
    val getAllPermission: List<UserPermission>
    private val repository: UserPermissionRepository

    init {
        val userPermissionDao = UserPermissionDatabase.getDatabase(application).userPermissionDao()
        repository = UserPermissionRepository(userPermissionDao)
        fetchAllPermission = repository.fetchAllPermission
        getAllPermission = repository.getAllPermission
    }

    fun addPermission(userpermission: UserPermission){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPermission(userpermission)
        }
    }

    fun nukeTable(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.nukeTable()
        }
    }
}