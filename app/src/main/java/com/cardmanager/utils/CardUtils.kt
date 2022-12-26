package com.cardmanager.utils

import android.nfc.Tag

object CardUtils {
    fun getId(tag: Tag): String? {
        val inArray = tag.id

        var i: Int
        var `in`: Int
        val hex =
            arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F")
        var out = ""
        var j: Int = 0

        while (j < inArray.size) {
            `in` = inArray[j].toInt() and 0xff
            i = `in` shr 4 and 0x0f
            out += hex[i]
            i = `in` and 0x0f
            out += hex[i]
            ++j
        }
        return out
    }
}