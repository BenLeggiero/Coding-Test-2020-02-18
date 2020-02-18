package me.benleggiero.coding_test_2020_02_18.convenienceExtensions

import android.content.*
import android.widget.*

fun Context.showToast(stringId: Int, duration: Int = Toast.LENGTH_LONG) =
    makeToast(stringId= stringId, duration= duration).show()

fun Context.makeToast(stringId: Int, duration: Int = Toast.LENGTH_LONG): Toast =
    Toast.makeText(this, stringId, duration)

fun Context.showToast(string: String, duration: Int = Toast.LENGTH_LONG) =
    makeToast(string= string, duration= duration).show()

fun Context.makeToast(string: String, duration: Int = Toast.LENGTH_LONG): Toast =
    Toast.makeText(this, string, duration)