package me.benleggiero.coding_test_2020_02_18.dummy

import me.benleggiero.coding_test_2020_02_18.search.*
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.*
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.VideoSearchQuery.*
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.VideoSearchResults.*
import me.benleggiero.coding_test_2020_02_18.search.serialization.*
import java.net.*
import kotlin.Result.Companion.success

class MockVideoSearchEngine: VideoSearchEngine {

    var cachedResults: VideoSearchResults? = null


    override fun performSearch(query: VideoSearchQuery, onComplete: VideoSearchDidComplete) {
        filteredResults(query= query) { result ->
            onComplete(result)
        }
    }


    private fun filteredResults(query: VideoSearchQuery, onComplete: VideoSearchDidComplete) {
        fetchResultsOrUseCache { result ->
            onComplete(result.map { results ->
                results.filter { video ->
                    video.isMatch(query)
                }
            })
        }
    }


    private fun fetchResultsOrUseCache(onComplete: VideoSearchDidComplete){
        val cachedResults = this.cachedResults
        if (null != cachedResults) {
            onComplete(success(cachedResults))
        }
        else {
            fetchResults { result ->
                result.onSuccess {
                    this.cachedResults = it
                }
                onComplete(result)
            }
        }
    }


    private fun fetchResults(onComplete: VideoSearchDidComplete) {
        VideoSearchResultsJson.load(mockURL) { result ->
            onComplete(result.mapCatching { VideoSearchResults(it) ?: throw ImproperlyFormattedJson(it) })
        }
    }



    companion object {
        val mockUrlString = "https://benleggiero.github.io/Coding-Test-2020-02-18/video-search-results.json"
        val mockURL = URL(mockUrlString)
    }
}

private fun Video.isMatch(query: VideoSearchQuery) = when (query) {
    is trending -> true
    is freeformText -> this.anyTextContains(query.userTextSearch)
}
