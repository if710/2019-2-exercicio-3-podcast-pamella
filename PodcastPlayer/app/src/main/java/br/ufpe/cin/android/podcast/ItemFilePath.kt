package br.ufpe.cin.android.podcast

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName="itemfilepaths")
class ItemFilePath (
    @PrimaryKey var title: String,
    var path: String
) {
    override fun toString(): String {
        return title
    }
}