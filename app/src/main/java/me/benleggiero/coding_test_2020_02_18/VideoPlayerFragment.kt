package me.benleggiero.coding_test_2020_02_18

import android.net.*
import android.os.*
import android.view.*
import android.view.View.*
import android.view.ViewGroup.LayoutParams.*
import androidx.fragment.app.*
import com.google.android.material.snackbar.*
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.item_detail.*
import kotlinx.android.synthetic.main.item_detail.view.*
import me.benleggiero.coding_test_2020_02_18.VideoPlayerFragment.LoadingVars.UserPlayState.*
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.VideoSearchResults.*

/**
 * A fragment representing a single video player screen.
 * This fragment is either contained in a [ItemListActivity]
 * in two-pane mode (on tablets) or a [ItemDetailActivity]
 * on handsets.
 */
class VideoPlayerFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var video: Video? = null

    private var playbackWorksheet = LoadingVars()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { bundle ->
            if (bundle.containsKey(argument_videoJsonString)) {
                video = bundle.getString(argument_videoJsonString)?.let{ Video(jsonString= it) }
            }
        }

        activity?.toolbar_layout?.title = video?.title ?: context?.getString(R.string.loading_with_ellipsis) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.item_detail, container, false)

        // Show the dummy content as text in a TextView.
        rootView.item_detail.text = video?.description ?: ""

        video?.posterUri?.let { posterImageUrl ->
            rootView.posterImageView.setImageURI(Uri.parse(posterImageUrl.toString()))
            if (!playbackWorksheet.isReadyToPlay) {
                rootView.posterImageView.visibility = VISIBLE
            }
        }

        rootView.playPauseButton.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.loading_with_ellipsis), Snackbar.LENGTH_SHORT).show()
        }

        val videoUri = video
            ?.sources
            ?.firstOrNull() // TODO: Pick the best current source
            ?.videoUri

        if (null != videoUri) {
            rootView.playerView.setVideoURI(videoUri)
            rootView.playerView.setOnPreparedListener { mediaPlayer ->
                bufferingSpinner.visibility = GONE
                posterImageView.visibility = INVISIBLE
                navigationControlsContainer.visibility = VISIBLE

                videoHolder.minimumHeight = 0
                videoHolder.layoutParams.height = WRAP_CONTENT

                playerView.seekTo(1)

                playbackWorksheet.isReadyToPlay = true
                if (playbackWorksheet.playWhenReady) {
                    playerView.start()
                }
            }

            rootView.playPauseButton.setOnClickListener {
                togglePlayback()
            }
        }

        return rootView
    }


    fun togglePlayback() {

        if (playbackWorksheet.isReadyToPlay) {
            when (playbackWorksheet.userPlayState) {
                playing -> playerView.pause()
                paused -> playerView.start()
            }
            playbackWorksheet.userPlayState = playbackWorksheet.userPlayState.toggled()

            playPauseButton.isChecked = playbackWorksheet.userPlayState == playing
        }
        else {
            playbackWorksheet.playWhenReady = true
        }
    }



    companion object {
        /**
         * The fragment argument representing the video JSON string that this fragment represents.
         */
        const val argument_videoJsonString = "video.jsonString"
    }



    private class LoadingVars(
        var isReadyToPlay: Boolean = false,
        var playWhenReady: Boolean = false,
        var userPlayState: UserPlayState = UserPlayState.paused
    ) {
        enum class UserPlayState {
            playing,
            paused,
            ;

            fun toggled() = when (this) {
                playing -> paused
                paused -> playing
            }
        }
    }
}
