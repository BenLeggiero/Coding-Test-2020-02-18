@file:Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
package me.benleggiero.coding_test_2020_02_18.search.serialization


/**
 * Thrown when we've already converted a JSON string into a JSON object, but could not convert that JSON object into a
 * regular object
 */
class ImproperlyFormattedJson(val json: VideoSearchResultsJson)
    : Exception("Could not convert JSON object into regular object: $json")
