package me.benleggiero.coding_test_2020_02_18.dummy

import me.benleggiero.coding_test_2020_02_18.search.VideoSearchDidComplete
import me.benleggiero.coding_test_2020_02_18.search.VideoSearchEngine
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.VideoSearchQuery
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.VideoSearchResults
import me.benleggiero.coding_test_2020_02_18.search.serialization.VideoSearchResultsJson
import java.net.URL
import kotlin.Result.Companion.success

class MockVideoSearchEngine: VideoSearchEngine {

    var cachedResults: VideoSearchResults? = null


    override fun performSearch(query: VideoSearchQuery, onComplete: VideoSearchDidComplete) {
        filteredResults(query= query) { results ->
            TODO()
        }
    }


    private fun filteredResults(query: VideoSearchQuery, onComplete: VideoSearchDidComplete) {
        fetchResultsOrUseCache { results ->
            onComplete(success(results.filter { it.isMatch(query) }))
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
        VideoSearchResultsJson.read(mockURL) { result ->

        }
    }



    companion object {
        val mockUrlString = "https://benleggiero.github.io/Coding-Test-2020-02-18/video-search-results.json"
        val mockURL = URL(mockUrlString)
    }
}