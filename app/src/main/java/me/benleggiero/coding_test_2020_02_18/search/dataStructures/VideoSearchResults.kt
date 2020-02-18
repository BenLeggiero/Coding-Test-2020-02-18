package me.benleggiero.coding_test_2020_02_18.search.dataStructures

import me.benleggiero.coding_test_2020_02_18.search.serialization.*
import java.net.*



/**
 * The results of a video search
 */
class VideoSearchResults(
    val videos: List<Video>
) {
    fun filter(predicate: (Video) -> Boolean): VideoSearchResults {
        return VideoSearchResults(videos = this.videos.filter(predicate))
    }



    companion object {
        operator fun invoke(json: VideoSearchResultsJson): VideoSearchResults? {
            return VideoSearchResults(
                videos = json.videos.map { videoJson ->
                    Video(videoJson) ?: return@invoke null
                }
            )
        }
    }



    class Video(
        val sources: List<Source>,
        val title: String,
        val artist: String?,
        val description: String?,
        val posterUrl: URL?
    ) {
        fun anyTextContains(userTextSearch: String): Boolean {
            return title.contains(userTextSearch)
                    || (artist?.contains(userTextSearch) ?: false)
                    || (description?.contains(userTextSearch) ?: false)
        }

        companion object {
            operator fun invoke(json: VideoSearchResultsJson.VideoJson): Video? {
                return Video(
                    sources = json.sources.map { Source(it) },
                    title = json.title,
                    artist = json.artist,
                    description = json.description,
                    posterUrl = json.posterUri?.let { URL(it) }
                )
            }
        }



        class Source(
            val videoUrl: URL
            // In the future, perhaps a resolution or format would go here, to help choose the proper source at runtime
        ) {

            companion object {
                operator fun invoke(json: VideoSearchResultsJson.VideoJson.SourceJson) = Source(
                    videoUrl = URL(json.videoUrl)
                )
            }
        }
    }
}