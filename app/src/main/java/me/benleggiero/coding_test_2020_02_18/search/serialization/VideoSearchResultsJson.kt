package me.benleggiero.coding_test_2020_02_18.search.serialization

import com.google.gson.*
import kotlinx.coroutines.*
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.*
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.VideoSearchResults.*
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.VideoSearchResults.Video.*
import java.net.*


/**
 * The results of a video search
 */
class VideoSearchResultsJson(
    val videos: List<VideoJson>
) {
    constructor(results: VideoSearchResults): this(
        videos= results.videos.map(::VideoJson)
    )

    companion object {
        fun load(url: URL, onComplete: OnReadDidComplete) {
            GlobalScope.launch {
                onComplete(runCatching { load(url) })
            }
        }


        @Throws
        private fun load(url: URL) = runBlocking {
            GsonBuilder()
                .serializeNulls()
                .create()
                .fromJson(url.readText(), VideoSearchResultsJson::class.java)
        }
    }



    class VideoJson(
        val id: String,
        val sources: List<SourceJson>,
        val title: String,
        val artist: String?,
        val description: String?,
        val poster: String?
    ) {
        constructor(video: Video): this(
            id= video.id,
            sources= video.sources.map(::SourceJson),
            title= video.title,
            artist= video.artist,
            description= video.description,
            poster= video.posterUrl.toString()
        )



        fun jsonString(): String =
            GsonBuilder()
                .serializeNulls()
                .create()
                .toJson(this)



        companion object {
            operator fun invoke(jsonString: String) = runCatching {
                GsonBuilder()
                    .serializeNulls()
                    .create()
                    .fromJson(jsonString, VideoJson::class.java)
            }
                .getOrNull()
        }



        class SourceJson(
            val file: String
            // In the future, perhaps a resolution or format would go here, to help choose the proper source at runtime
        ) {
            constructor(source: Source): this(
                file = source.videoUrl.toString()
            )
        }
    }
}



typealias OnReadDidComplete = (Result<VideoSearchResultsJson>) -> Unit
