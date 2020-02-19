package me.benleggiero.coding_test_2020_02_18

import android.content.*
import android.net.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.appcompat.app.*
import androidx.recyclerview.widget.*
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.android.synthetic.main.item_list_content.view.*
import me.benleggiero.coding_test_2020_02_18.dummy.*
import me.benleggiero.coding_test_2020_02_18.search.*
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.Filetype.*
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.VideoSearchQuery.*
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.VideoSearchResults.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ItemListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    private val searchEngine: VideoSearchEngine = MockVideoSearchEngine() // TODO: Replace this with a real search engine when the server component is ready


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/BenLeggiero/Coding-Test-2020-02-18")))
        }

        if (item_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        setupRecyclerView(item_list)
    }


    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, emptyList<Video>(), twoPane)

        searchEngine.performSearch(filetype(hls)) { result ->
            result.fold(
                onSuccess = {
                    runOnUiThread {
                        recyclerView.adapter =
                            SimpleItemRecyclerViewAdapter(this, it.videos, twoPane)
                    }
                },
                onFailure = {
                    runOnUiThread {
                        recyclerView.adapter =
                            SimpleItemRecyclerViewAdapter(this, listOf(Video.errorPlaceholder(it, this)), twoPane)
                    }
                }
            )
        }
    }

    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: ItemListActivity,
        private val values: List<Video>,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val video = v.tag as Video
                if (twoPane) {
                    val fragment = VideoPlayerFragment().apply {
                        arguments = Bundle().apply {
                            putString(VideoPlayerFragment.argument_videoJsonString, video.jsonString())
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
                        putExtra(VideoPlayerFragment.argument_videoJsonString, video.jsonString())
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val video = values[position]
            holder.idView.text = video.title
            holder.contentView.text = parentActivity.getString(R.string.artist_template, video.artist)

            with(holder.itemView) {
                tag = video
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.id_text
            val contentView: TextView = view.content
        }
    }
}
