package br.ufpe.cin.android.podcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import org.jetbrains.anko.doAsync


class DownloadReceiver(holder: ItemFeedAdapter.ViewHolder) : BroadcastReceiver() {
    private val itemActionDownload = holder.action_download
    private val itemTitle = holder.title

    override fun onReceive(c: Context, intent: Intent) {
        itemActionDownload.isEnabled = true
        Toast.makeText(
            c,
            "Download succeed!",
            Toast.LENGTH_LONG
        ).show()

        val itemFilePath = intent.getStringExtra("downloadFilePath")
        val db = ItemFilePathDB.getDatabase(c)

        doAsync {
            db.itemFilePathDAO().insertItemPath(ItemFilePath(itemTitle.toString(), itemFilePath!!))
        }
    }
}