package me.benleggiero.coding_test_2020_02_18.search.serialization

import me.benleggiero.coding_test_2020_02_18.search.VideoSearchDidComplete
import java.net.URI
import java.net.URL


/**
 * The results of a video search
 */
class VideoSearchResultsJson(
    val videos: List<Video>
) {

    companion object {
        fun read(url: URL, onComplete: VideoSearchDidComplete) {
            
        }
    }



    class Video(
        val sources: List<Source>,
        val title: String,
        val artist: String?,
        val description: String?,
        val posterUri: URI?
    ) {

        class Source(
            val videoUri: URI
            // In the future, perhaps a resolution or format would go here, to help choose the proper source at runtime
        )
    }
}