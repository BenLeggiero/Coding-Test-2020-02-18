package me.benleggiero.coding_test_2020_02_18.convenienceExtensions

import android.net.*
import java.net.*


fun URL.pathExtension() = path.substringAfterLast('.').ifEmpty { null }
fun Uri.pathExtension() = lastPathSegment?.substringAfterLast('.')