package com.example.cheilros.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cheilros.dao.AppSettingDao
import com.example.cheilros.dao.UserDataDao
import com.example.cheilros.dao.UserPermissionDao
import com.example.cheilros.data.AppSetting
import com.example.cheilros.data.UserData
import com.example.cheilros.data.UserPermission

@Database(entities = [AppSetting::class], version = 2, exportSchema = false)
abstract class AppSettingDatabase: RoomDatabase() {

    abstract fun appSettingDao(): AppSettingDao

    companion object{
        @Volatile
        private var INSTANCE: AppSettingDatabase? = null

        fun getDatabase(context: Context): AppSettingDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppSettingDatabase::class.java,
                    "app_settings"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }
    }

}

@Database(entities = [UserData::class], version = 1, exportSchema = false)
abstract class UserDataDatabase: RoomDatabase() {

    abstract fun userDataDao(): UserDataDao

    companion object{
        @Volatile
        private var INSTANCE: UserDataDatabase? = null

        fun getDatabase(context: Context): UserDataDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDataDatabase::class.java,
                    "user_data"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

@Database(entities = [UserPermission::class], version = 1, exportSchema = false)
abstract class UserPermissionDatabase: RoomDatabase() {

    abstract fun userPermissionDao(): UserPermissionDao

    companion object{
        @Volatile
        private var INSTANCE: UserPermissionDatabase? = null

        fun getDatabase(context: Context): UserPermissionDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserPermissionDatabase::class.java,
                    "user_permission"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}