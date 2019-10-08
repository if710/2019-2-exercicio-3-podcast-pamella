package br.ufpe.cin.android.podcast

import androidx.room.*


@Dao
interface ItemFeedDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemFeeds(vararg itemFeeds:ItemFeed)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListItemFeeds(itemFeeds: List<ItemFeed>)

    @Query("SELECT * FROM itemFeeds")
    fun allItemFeeds() : Array<ItemFeed>

    @Query("SELECT * FROM itemFeeds WHERE title LIKE :q")
    fun search(q : String) : ItemFeed
}