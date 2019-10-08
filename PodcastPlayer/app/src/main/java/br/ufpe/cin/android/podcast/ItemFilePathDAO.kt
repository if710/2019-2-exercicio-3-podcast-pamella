package br.ufpe.cin.android.podcast

import androidx.room.*


@Dao
interface ItemFilePathDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemPath(vararg itemFilePaths: ItemFilePath)

    @Query("SELECT * FROM itemFilePaths")
    fun findAllItemsPath() : Array<ItemFilePath>

    @Query("SELECT * FROM itemFilePaths WHERE title LIKE :q")
    fun search(q : String) : ItemFilePath
}