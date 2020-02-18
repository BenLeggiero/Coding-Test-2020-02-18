package me.benleggiero.coding_test_2020_02_18.search.dataStructures

import android.content.*
import me.benleggiero.coding_test_2020_02_18.*
import me.benleggiero.coding_test_2020_02_18.search.serialization.*
import me.benleggiero.coding_test_2020_02_18.search.serialization.VideoSearchResultsJson.*
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
        val id: String,
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


        fun jsonString(): String {
            return VideoJson(this).jsonString()
        }



        companion object {
            operator fun invoke(json: VideoJson): Video? {
                return Video(
                    id = json.id,
                    sources = json.sources.map { Source(it) },
                    title = json.title,
                    artist = json.artist,
                    description = json.description,
                    posterUrl = json.poster?.let { runCatching { URL(it) }.getOrNull() }
                )
            }


            fun fromJsonString(jsonString: String): Video? {
                return Video(VideoJson(jsonString= jsonString) ?: return null)
            }


            fun errorPlaceholder(it: Throwable, context: Context): Video {
                return Video(
                    id = "E060883A-A79E-4EC6-A27E-880CC966CEAF",
                    title = context.getString(R.string.video_loading_error_title),
                    description = context.getString(R.string.video_loading_error_description___message) + it.message,
                    artist = null,
                    sources = emptyList(),
                    posterUrl = null
                )
            }
        }



        class Source(
            val videoUrl: URL
            // In the future, perhaps a resolution or format would go here, to help choose the proper source at runtime
        ) {

            companion object {
                operator fun invoke(json: VideoJson.SourceJson) = Source(
                    videoUrl = URL(json.file)
                )
            }
        }
    }
}