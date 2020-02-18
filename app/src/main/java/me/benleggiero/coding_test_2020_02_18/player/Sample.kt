/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.benleggiero.coding_test_2020_02_18.player

import android.content.*
import android.net.*
import com.google.android.exoplayer2.util.*
import me.benleggiero.coding_test_2020_02_18.*
import java.util.*


/* package */ internal abstract class Sample(val name: String?) {

    class UriSample(
        name: String,
        val uri: Uri,
        val extension: String,
        val isLive: Boolean,
        val drmInfo: DrmInfo?,
        val adTagUri: Uri?,
        val sphericalStereoMode: String?,
        internal var subtitleInfo: SubtitleInfo?
    ) : Sample(name) {

        override fun addToIntent(intent: Intent) {
            intent.setAction(ItemDetailFragment.ACTION_VIEW).data = uri
            intent.putExtra(ItemDetailFragment.IS_LIVE_EXTRA, isLive)
            intent.putExtra(ItemDetailFragment.SPHERICAL_STEREO_MODE_EXTRA, sphericalStereoMode)
            addPlayerConfigToIntent(intent, /* extrasKeySuffix= */ "")
        }

        fun addToPlaylistIntent(intent: Intent, extrasKeySuffix: String) {
            intent.putExtra(ItemDetailFragment.URI_EXTRA + extrasKeySuffix, uri.toString())
            intent.putExtra(ItemDetailFragment.IS_LIVE_EXTRA + extrasKeySuffix, isLive)
            addPlayerConfigToIntent(intent, extrasKeySuffix)
        }

        private fun addPlayerConfigToIntent(intent: Intent, extrasKeySuffix: String) {
            intent
                .putExtra(EXTENSION_EXTRA + extrasKeySuffix, extension)
                .putExtra(
                    AD_TAG_URI_EXTRA + extrasKeySuffix, adTagUri?.toString()
                )
            drmInfo?.addToIntent(intent, extrasKeySuffix)
            if (subtitleInfo != null) {
                subtitleInfo!!.addToIntent(intent, extrasKeySuffix)
            }
        }

        companion object {

            fun createFromIntent(uri: Uri?, intent: Intent, extrasKeySuffix: String): UriSample {
                val extension = intent.getStringExtra(EXTENSION_EXTRA + extrasKeySuffix)
                val adsTagUriString = intent.getStringExtra(AD_TAG_URI_EXTRA + extrasKeySuffix)
                val isLive = intent.getBooleanExtra(IS_LIVE_EXTRA + extrasKeySuffix, /* defaultValue= */ false)
                val adTagUri = if (adsTagUriString != null) Uri.parse(adsTagUriString) else null
                return UriSample(
                    null,
                    uri,
                    extension,
                    isLive,
                    DrmInfo.createFromIntent(intent, extrasKeySuffix),
                    adTagUri, null,
                    SubtitleInfo.createFromIntent(intent, extrasKeySuffix)
                )/* name= *//* sphericalStereoMode= */
            }
        }
    }

    class PlaylistSample(name: String, vararg children: UriSample) : Sample(name) {

        val children: Array<UriSample>

        init {
            this.children = children
        }

        override fun addToIntent(intent: Intent) {
            intent.action = ItemDetailFragment.ACTION_VIEW_LIST
            for (i in children.indices) {
                children[i].addToPlaylistIntent(intent, /* extrasKeySuffix= */ "_$i")
            }
        }
    }

    class DrmInfo(
        val drmScheme: UUID,
        val drmLicenseUrl: String,
        val drmKeyRequestProperties: Array<String>,
        val drmMultiSession: Boolean
    ) {

        fun addToIntent(intent: Intent, extrasKeySuffix: String) {
            Assertions.checkNotNull(intent)
            intent.putExtra(DRM_SCHEME_EXTRA + extrasKeySuffix, drmScheme.toString())
            intent.putExtra(DRM_LICENSE_URL_EXTRA + extrasKeySuffix, drmLicenseUrl)
            intent.putExtra(DRM_KEY_REQUEST_PROPERTIES_EXTRA + extrasKeySuffix, drmKeyRequestProperties)
            intent.putExtra(DRM_MULTI_SESSION_EXTRA + extrasKeySuffix, drmMultiSession)
        }

        companion object {

            fun createFromIntent(intent: Intent, extrasKeySuffix: String): DrmInfo? {
                val schemeKey = DRM_SCHEME_EXTRA + extrasKeySuffix
                val schemeUuidKey = DRM_SCHEME_UUID_EXTRA + extrasKeySuffix
                if (!intent.hasExtra(schemeKey) && !intent.hasExtra(schemeUuidKey)) {
                    return null
                }
                val drmSchemeExtra = if (intent.hasExtra(schemeKey))
                    intent.getStringExtra(schemeKey)
                else
                    intent.getStringExtra(schemeUuidKey)
                val drmScheme = Util.getDrmUuid(drmSchemeExtra!!)
                val drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL_EXTRA + extrasKeySuffix)
                val keyRequestPropertiesArray =
                    intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES_EXTRA + extrasKeySuffix)
                val drmMultiSession = intent.getBooleanExtra(DRM_MULTI_SESSION_EXTRA + extrasKeySuffix, false)
                return DrmInfo(drmScheme, drmLicenseUrl, keyRequestPropertiesArray, drmMultiSession)
            }
        }
    }

    class SubtitleInfo(uri: Uri, mimeType: String, val language: String?) {

        val uri: Uri
        val mimeType: String

        init {
            this.uri = Assertions.checkNotNull(uri)
            this.mimeType = Assertions.checkNotNull(mimeType)
        }

        fun addToIntent(intent: Intent, extrasKeySuffix: String) {
            intent.putExtra(SUBTITLE_URI_EXTRA + extrasKeySuffix, uri.toString())
            intent.putExtra(SUBTITLE_MIME_TYPE_EXTRA + extrasKeySuffix, mimeType)
            intent.putExtra(SUBTITLE_LANGUAGE_EXTRA + extrasKeySuffix, language)
        }

        companion object {

            fun createFromIntent(intent: Intent, extrasKeySuffix: String): SubtitleInfo? {
                return if (!intent.hasExtra(SUBTITLE_URI_EXTRA + extrasKeySuffix)) {
                    null
                } else SubtitleInfo(
                    Uri.parse(intent.getStringExtra(SUBTITLE_URI_EXTRA + extrasKeySuffix)),
                    intent.getStringExtra(SUBTITLE_MIME_TYPE_EXTRA + extrasKeySuffix),
                    intent.getStringExtra(SUBTITLE_LANGUAGE_EXTRA + extrasKeySuffix)
                )
            }
        }
    }

    abstract fun addToIntent(intent: Intent)

    companion object {

        fun createFromIntent(intent: Intent): Sample {
            if (ACTION_VIEW_LIST.equals(intent.action)) {
                val intentUris = ArrayList<String>()
                var index = 0
                while (intent.hasExtra(URI_EXTRA + "_" + index)) {
                    intentUris.add(intent.getStringExtra(URI_EXTRA + "_" + index))
                    index++
                }
                val children = arrayOfNulls<UriSample>(intentUris.size)
                for (i in children.indices) {
                    val uri = Uri.parse(intentUris[i])
                    children[i] = UriSample.createFromIntent(uri, intent, /* extrasKeySuffix= */ "_$i")
                }
                return PlaylistSample(null, *children)
            } else {
                return UriSample.createFromIntent(intent.data, intent, /* extrasKeySuffix= */ "")
            }
        }
    }
}
