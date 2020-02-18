package me.benleggiero.coding_test_2020_02_18.search.serialization

import com.google.gson.*
import kotlinx.coroutines.*
import java.net.*


/**
 * The results of a video search
 */
class VideoSearchResultsJson(
    val videos: List<Video>
) {

    companion object {
        fun load(url: URL, onComplete: OnReadDidComplete) {
            GlobalScope.launch {
                onComplete(runCatching { load(url) })
            }
        }


        @Throws
        private fun load(url: URL) = runBlocking {
            GsonBuilder()
                .create()
                .fromJson(url.readText(), VideoSearchResultsJson::class.java)
        }
    }



    class Video(
        val sources: List<Source>,
        val title: String,
        val artist: String?,
        val description: String?,
        val posterUri: String?
    ) {

        class Source(
            val videoUrl: String
            // In the future, perhaps a resolution or format would go here, to help choose the proper source at runtime
        )
    }
}



typealias OnReadDidComplete = (Result<VideoSearchResultsJson>) -> Unit
