package me.benleggiero.coding_test_2020_02_18.convenienceExtensions

import android.net.*
import android.widget.*
import java.net.*


fun VideoView.setVideoUrl(url: URL) {
    setVideoURI(Uri.parse(url.toString()))
}