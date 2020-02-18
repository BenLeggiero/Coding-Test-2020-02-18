package me.benleggiero.coding_test_2020_02_18

import android.content.*
import android.os.*
import android.view.*
import androidx.fragment.app.*
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.DefaultRenderersFactory.*
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.*
import com.google.android.exoplayer2.ui.*
import com.google.android.exoplayer2.util.*
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.item_detail.view.*
import me.benleggiero.coding_test_2020_02_18.convenienceExtensions.*
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.VideoSearchResults.*



// A large chunk of this code comes from Google's example app for their ExoPlayer:
// https://github.com/google/ExoPlayer



/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListActivity]
 * in two-pane mode (on tablets) or a [ItemDetailActivity]
 * on handsets.
 */
class ItemDetailFragment: Fragment(), PlaybackPreparer {

    /**
     * The content this fragment is presenting.
     */
    private var item: Video? = null

    private var title: CharSequence?
        get() { return activity?.toolbar_layout?.title }
        set(newValue) { activity?.toolbar_layout?.title = newValue }


    // MARK: Player stuff

    private var player: SimpleExoPlayer? = null
    private var mediaSource: MediaSource? = null
    private var trackSelector: DefaultTrackSelector? = null
    private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null
    private var startWindow: Int = 0
    private var startPosition: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializePlayer()

        arguments?.let { bundle ->
            if (bundle.containsKey(argument_videoJsonString)) {
                item = bundle.getString(argument_videoJsonString)?.let{ Video.fromJsonString(it) }
                this.title = item?.title ?: context?.getString(R.string.loading_with_ellipsis) ?: ""
//                this.player?.
            }


            val builder = DefaultTrackSelector.ParametersBuilder(/* context= */ requireContext())
            if (bundle.getBoolean(TUNNELING_EXTRA, false)) {
                builder.setTunnelingAudioSessionId(C.generateAudioSessionIdV21(/* context= */ requireContext()))
            }
            trackSelectorParameters = builder.build()
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

    private fun initializePlayer() {
        if (player == null) {

            mediaSource = createTopLevelMediaSource() ?: return

            val trackSelectionFactory = AdaptiveTrackSelection.Factory()

            val preferExtensionDecoders = false
            val renderersFactory = DefaultRenderersFactory(/* context= */ requireContext())
                .setExtensionRendererMode(EXTENSION_RENDERER_MODE_PREFER)

            val trackSelector = DefaultTrackSelector(/* context= */ requireContext(), trackSelectionFactory)
            trackSelector.setParameters(trackSelectorParameters ?: return Log.e("", "No track selector parameters"))
            this.trackSelector = trackSelector
//            lastSeenTrackGroupArray = null

            val player = SimpleExoPlayer.Builder(/* context= */ requireContext(), renderersFactory)
                .setTrackSelector(trackSelector)
                .build()
            player.addListener(PlayerEventListener())
            player.playWhenReady = true
            player.addAnalyticsListener(EventLogger(trackSelector))
            playerView.player = player
            playerView.setPlaybackPreparer(this)
            this.player = player
        }

        val haveStartPosition = startWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player?.seekTo(startWindow, startPosition)
        }
        player?.prepare(mediaSource ?: return, !haveStartPosition, false)
//        updateButtonVisibility()
    }

    private fun createTopLevelMediaSource(): MediaSource? {
//        val action = intent.action
//        val actionIsListView = ACTION_VIEW_LIST == action
//        if (!actionIsListView && ACTION_VIEW != action) {
//            showToast(getString(R.string.unexpected_intent_action, action))
//            finish()
//            return null
//        }

        val intentAsSample = Sample.createFromIntent(intent)
        val samples = if (intentAsSample is Sample.PlaylistSample)
            (intentAsSample as Sample.PlaylistSample).children
        else
            arrayOf<UriSample>(intentAsSample as UriSample)

        var seenAdsTagUri = false
        for (sample in samples) {
            seenAdsTagUri = seenAdsTagUri or (sample.adTagUri != null)
            if (!Util.checkCleartextTrafficPermitted(sample.uri)) {
                showToast(R.string.error_cleartext_not_permitted)
                return null
            }
            if (Util.maybeRequestReadExternalStoragePermission(/*activity= */ requireActivity(), sample.uri)) {
                // The player will be reinitialized if the permission is granted.
                return null
            }
        }

        val mediaSources = arrayOfNulls<MediaSource>(samples.size)
        for (i in samples.indices) {
            mediaSources[i] = createLeafMediaSource(samples[i])
            val subtitleInfo = samples[i].subtitleInfo
            if (subtitleInfo != null) {
                if (Util.maybeRequestReadExternalStoragePermission(
                        /* activity= */ this, subtitleInfo!!.uri
                    )
                ) {
                    // The player will be reinitialized if the permission is granted.
                    return null
                }
                val subtitleFormat = Format.createTextSampleFormat(
                    null,
                    subtitleInfo!!.mimeType,
                    C.SELECTION_FLAG_DEFAULT,
                    subtitleInfo!!.language
                )/* id= */
                val subtitleMediaSource = SingleSampleMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(subtitleInfo!!.uri, subtitleFormat, C.TIME_UNSET)
                mediaSources[i] = MergingMediaSource(mediaSources[i], subtitleMediaSource)
            }
        }
        var mediaSource = if (mediaSources.size == 1) mediaSources[0] else ConcatenatingMediaSource(*mediaSources)

        if (seenAdsTagUri) {
            val adTagUri = samples[0].adTagUri
            if (actionIsListView) {
                showToast(R.string.unsupported_ads_in_concatenation)
            } else {
                if (adTagUri != loadedAdTagUri) {
                    releaseAdsLoader()
                    loadedAdTagUri = adTagUri
                }
                val adsMediaSource = createAdsMediaSource(mediaSource, adTagUri)
                if (adsMediaSource != null) {
                    mediaSource = adsMediaSource
                } else {
                    showToast(R.string.ima_not_loaded)
                }
            }
        } else {
            releaseAdsLoader()
        }

        return mediaSource
    }

    private inner class PlayerEventListener : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, @Player.State playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                showControls()
            }
            updateButtonVisibility()
        }

        override fun onPlayerError(e: ExoPlaybackException) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition()
                initializePlayer()
            } else {
                updateButtonVisibility()
                showControls()
            }
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
            updateButtonVisibility()
            if (trackGroups !== lastSeenTrackGroupArray) {
                val mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo()
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo!!.getTypeSupport(C.TRACK_TYPE_VIDEO) == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast(R.string.error_unsupported_video)
                    }
                    if (mappedTrackInfo!!.getTypeSupport(C.TRACK_TYPE_AUDIO) == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast(R.string.error_unsupported_audio)
                    }
                }
                lastSeenTrackGroupArray = trackGroups
            }
        }
    }



    // MARK: - PlaybackPreparer

    override fun preparePlayback() {
        player?.retry()
    }



    // MARK: - Statics

    companion object {
        val ABR_ALGORITHM_EXTRA = "abr_algorithm"
        val ABR_ALGORITHM_DEFAULT = "default"
        val ABR_ALGORITHM_RANDOM = "random"

        val TUNNELING_EXTRA = "tunneling"

        val ACTION_VIEW = "me.benleggiero.coding_test_2020_02_18.action.VIEW"

        /**
         * The fragment argument representing the video JSON string that this fragment represents.
         */
        const val argument_videoJsonString = "video.jsonString"
    }
}
