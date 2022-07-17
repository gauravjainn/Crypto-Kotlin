package com.siltech.cryptochat.data.db.messages.s

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.ChatDatas
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.ChatsTypeConverter
import com.siltech.cryptochat.data.db.messages.s.ffff.aa.SmsDatas

//import com.siltech.cryptochat.data.db.messages.MessagesDB

@Database(entities = [ChatDatas::class, SmsDatas::class], version = 1, exportSchema = false)
//@TypeConverters(ChatsTypeConverter::class)
abstract class SMSDATABASE: RoomDatabase() {
    abstract fun getDao(): SMSDAO

    companion object{
        @Volatile
        var INSTANCE: SMSDATABASE? = null

        fun getInstance(context: Context): SMSDATABASE{
            val temporaryInstance = SMSDATABASE.INSTANCE
            synchronized(this){
                if(temporaryInstance != null){
                    return temporaryInstance
                }else{
                    val instance = Room.databaseBuilder(context.applicationContext, SMSDATABASE::class.java, "chaa")
                        .build()
                    SMSDATABASE.INSTANCE = instance
                    return instance
                }
            }
        }
    }
}