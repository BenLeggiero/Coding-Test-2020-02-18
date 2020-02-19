package me.benleggiero.coding_test_2020_02_18.search.dataStructures

import android.net.*
import me.benleggiero.coding_test_2020_02_18.convenienceExtensions.*



enum class Filetype {
    /** MPEG files; `mp4`... */
    mpeg,

    /** HLS files; `m3u8`... */
    hls,

    ;



    companion object {
        operator fun invoke(uri: Uri) = when (uri.pathExtension()) {
            "mp4" -> mpeg
            "m3u8" -> hls
            else -> null
        }
    }
}