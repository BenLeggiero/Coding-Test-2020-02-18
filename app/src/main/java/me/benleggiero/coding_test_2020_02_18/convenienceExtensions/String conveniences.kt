package me.benleggiero.coding_test_2020_02_18.convenienceExtensions

import java.util.*


fun String.nullifyingNullString() = when (toLowerCase(Locale.ROOT)) {
    "null", "(null)", "<null>" -> null
    else -> this
}
