package me.benleggiero.coding_test_2020_02_18.search.dataStructures

import javax.xml.transform.Source

class VideoSearchResults(
    val videos: List<Video>
) {

    class Video(
        val sources: List<Source>,
        val title: String,
        val artist: String
    )
}