package br.ufpe.cin.android.podcast

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.net.Uri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.itemlista.view.*
import org.jetbrains.anko.doAsync


class ItemFeedAdapter(private val c: Context) : RecyclerView.Adapter<ItemFeedAdapter.ViewHolder>() {

    var itemFeeds = listOf<ItemFeed>()
    override fun getItemCount(): Int = itemFeeds.size
    var playerService: PlayerService? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(c).inflate(R.layout.itemlista, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.play.isEnabled = false
        val itemFeed = itemFeeds[position]

        doAsync {
            val db = ItemFilePathDB.getDatabase(c)
            val item = db.itemFilePathDAO().search(itemFeed.title)
            if(!item.path.equals("")){
                holder.path = item.path
                holder.play.isEnabled = true
                holder.action_download.isEnabled = false
            }
        }

        holder.title?.text = itemFeed.title
        holder.date?.text = itemFeed.pubDate
        holder.action_download.setOnClickListener{
            holder.action_download.isEnabled = false
            val downloadService = Intent(c, DownloadService::class.java)
            downloadService.data = Uri.parse(itemFeed.downloadLink)
            downloadService.putExtra("fileTitle",itemFeed.title)
            c.startService(downloadService)

            setupReceiver(holder)
        }
        holder.play.setOnClickListener{
            val db = ItemFilePathDB.getDatabase(c)
            val item = db.itemFilePathDAO().search(itemFeed.title)
            playerService!!.playPodcast(item.path, itemFeed.title)
        }
        holder.title.setOnClickListener{
            val intent = Intent(c, EpisodeDetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("title",itemFeed.title)
            intent.putExtra("description",itemFeed.description)
            intent.putExtra("pubDate",itemFeed.pubDate)

            c.startActivity(intent)
        }
    }

    private fun setupReceiver(holder: ViewHolder) {
        val filter = IntentFilter()
        filter.addAction(DownloadService.ACTION_DOWNLOAD)

        val receiver = DownloadReceiver(holder)
        LocalBroadcastManager.getInstance(c).registerReceiver(receiver, filter)
    }

    class ViewHolder (itemFeed : View) : RecyclerView.ViewHolder(itemFeed) {
        val title = itemFeed.item_title
        val date = itemFeed.item_date
        val action_download = itemFeed.item_action
        val play = itemFeed.item_play
        var path = ""

        init {
            action_download.setOnClickListener {
                Toast.makeText(
                    itemView.context,
                    "Download ${title.text} in progress!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}