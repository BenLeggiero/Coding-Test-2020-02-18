package me.benleggiero.coding_test_2020_02_18.search.dataStructures

sealed class VideoSearchQuery {
    object trending: VideoSearchQuery()
    class freeformText(val userTextSearch: String): VideoSearchQuery()
    class filetype(val filetype: Filetype): VideoSearchQuery()
}