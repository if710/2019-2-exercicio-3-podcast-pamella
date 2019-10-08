package br.ufpe.cin.android.podcast

import android.content.Context
import androidx.room.*


@Database(entities= arrayOf(ItemFilePath::class), version=1)
abstract class ItemFilePathDB : RoomDatabase() {
    abstract fun itemFilePathDAO() : ItemFilePathDAO
    companion object {
        private var INSTANCE : ItemFilePathDB? = null

        fun getDatabase(ctx: Context) : ItemFilePathDB {
            if(INSTANCE == null) {
                synchronized(ItemFilePathDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        ctx.applicationContext,
                        ItemFilePathDB::class.java,
                        "itemsPath.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}