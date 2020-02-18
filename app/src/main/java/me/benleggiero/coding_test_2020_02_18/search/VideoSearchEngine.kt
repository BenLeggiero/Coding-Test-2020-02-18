package me.benleggiero.coding_test_2020_02_18.search

import me.benleggiero.coding_test_2020_02_18.search.dataStructures.VideoSearchQuery
import me.benleggiero.coding_test_2020_02_18.search.dataStructures.VideoSearchResults


interface VideoSearchEngine {
    fun performSearch(query: VideoSearchQuery, onComplete: VideoSearchDidComplete)
}



typealias VideoSearchDidComplete = (Result<VideoSearchResults>) -> Unit