package br.ufpe.cin.android.podcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import kotlinx.android.synthetic.main.activity_episode_detail.*


class EpisodeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        detail_item_title.text = intent.getStringExtra("title")
        detail_item_description.text = intent.getStringExtra("description")
        detail_item_pubDate.text = intent.getStringExtra("pubDate")
    }
}
