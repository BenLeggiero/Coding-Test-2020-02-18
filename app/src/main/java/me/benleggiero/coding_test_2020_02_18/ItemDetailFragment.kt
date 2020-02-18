package me.benleggiero.coding_test_2020_02_18

import android.os.*
import android.view.*
import androidx.fragment.app.*
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.item_detail.view.*
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.VideoSearchResults.*

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListActivity]
 * in two-pane mode (on tablets) or a [ItemDetailActivity]
 * on handsets.
 */
class ItemDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var item: Video? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { bundle ->
            if (bundle.containsKey(argument_videoJsonString)) {
                item = bundle.getString(argument_videoJsonString)?.let{ Video.fromJsonString(it) }
                activity?.toolbar_layout?.title = item?.title ?: context?.getString(R.string.loading_with_ellipsis) ?: ""
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.item_detail, container, false)

        // Show the dummy content as text in a TextView.
        rootView.item_detail.text = item?.description ?: ""

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the video JSON string that this fragment represents.
         */
        const val argument_videoJsonString = "video.jsonString"
    }
}
