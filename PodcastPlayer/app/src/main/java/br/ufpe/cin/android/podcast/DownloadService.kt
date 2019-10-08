package br.ufpe.cin.android.podcast

import android.app.IntentService
import android.content.Intent
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.jetbrains.anko.doAsync
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class DownloadService : IntentService("DownloadService") {

    companion object {
        const val ACTION_DOWNLOAD = "br.ufpe.cin.android.podcast.services.action.DOWNLOAD_COMPLETE"
    }

    override fun onHandleIntent(intent: Intent?) {
        try {
            val title = intent!!.getStringExtra("item_title")
            val root = getExternalFilesDir(DIRECTORY_DOWNLOADS)
            root?.mkdirs()
            val output = File(root, intent!!.data!!.lastPathSegment)
            if (output.exists()) {
                output.delete()
            }

            val url = URL(intent?.data!!.toString())
            val connection = url.openConnection() as HttpURLConnection
            val fileOS = FileOutputStream(output.path)
            val out = BufferedOutputStream(fileOS)

            try {
                val `in` = connection.inputStream
                val buffer = ByteArray(8192)
                var len = `in`.read(buffer)
                while (len >= 0) {
                    out.write(buffer, 0, len)
                    len = `in`.read(buffer)
                }
                out.flush()
            } finally {
                fileOS.fd.sync()
                out.close()
                connection.disconnect()
            }

            val actionIntent = Intent(ACTION_DOWNLOAD)
            actionIntent.putExtra("downloadFilePath", output.path)

            val db = ItemFilePathDB.getDatabase(applicationContext)
            doAsync {
                db.itemFilePathDAO().insertItemPath(ItemFilePath(title, output.path))
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(actionIntent)

        } catch (exception: IOException) {
            Log.e(javaClass.getName(), "An error occurred, so the download did not succeed.", exception)
        }
    }
}