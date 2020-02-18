package me.benleggiero.coding_test_2020_02_18.search.dataStructures

import java.net.URI



/**
 * The results of a video search
 */
class VideoSearchResults(
    val videos: List<Video>
) {

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