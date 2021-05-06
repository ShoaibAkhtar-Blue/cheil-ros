package com.example.cheilros.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cheilros.dao.AppSettingDao
import com.example.cheilros.data.AppSetting

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