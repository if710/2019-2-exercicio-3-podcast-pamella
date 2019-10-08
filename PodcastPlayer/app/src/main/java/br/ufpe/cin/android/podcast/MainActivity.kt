package br.ufpe.cin.android.podcast

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL

class MainActivity : AppCompatActivity() {

    var playerService: PlayerService? = null
    var isBound = false
    lateinit var itemFeedAdapter : ItemFeedAdapter

    private val serviceConn = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            playerService = null
            isBound = false
        }

        override fun onServiceConnected(p0: ComponentName?, b: IBinder?) {
            val binder = b as PlayerService.MusicBinder
            playerService = binder.playerService
            itemFeedAdapter.playerService = playerService
            isBound = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list_item_feed.layoutManager = LinearLayoutManager(this)
        itemFeedAdapter = ItemFeedAdapter(applicationContext)
        list_item_feed.adapter = itemFeedAdapter

        val musicServiceIntent = Intent(this, PlayerService::class.java)
        startService(musicServiceIntent)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0
            )
        }

        doAsync {
            val url = "https://s3-us-west-1.amazonaws.com/podcasts.thepolyglotdeveloper.com/podcast.xml?fbclid=IwAR3X1VxOU4OFdxG-2m0IKHLwDXHFRavdx1ZndZ1T53OLRQk_XQlE168N1bI"
            val db = ItemFeedDB.getDatabase(applicationContext)
            var listItemFeeds: List<ItemFeed> = emptyList()

            try {
                val podcast = URL(url).readText()
                listItemFeeds = Parser.parse(podcast)
                db.itemFeedDAO().insertListItemFeeds(listItemFeeds)

                uiThread {
                    list_item_feed.adapter = ItemFeedAdapter(applicationContext)
                }
            } catch (error:Throwable) {
                Log.e("ERROR",error.message.toString())
            }


        }
    }

    override fun onStart() {
        super.onStart()
        if (!isBound) {
            val bindIntent = Intent(this, PlayerService::class.java)
            isBound = bindService(bindIntent, serviceConn, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        unbindService(serviceConn)
        super.onStop()
    }
}
