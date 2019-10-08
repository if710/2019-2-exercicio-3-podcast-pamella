package br.ufpe.cin.android.podcast

import android.content.Context
import androidx.room.*


@Database(entities= arrayOf(ItemFeed::class), version=1)
abstract class ItemFeedDB : RoomDatabase() {
    abstract fun itemFeedDAO(): ItemFeedDAO
    companion object {
        private var INSTANCE : ItemFeedDB? = null

        fun getDatabase(ctx : Context) : ItemFeedDB {
            if (INSTANCE == null) {
                synchronized(ItemFeedDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        ctx.applicationContext,
                        ItemFeedDB::class.java,
                        "itemFeeds.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }

    }
}