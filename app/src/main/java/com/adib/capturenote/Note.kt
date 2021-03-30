package com.adib.capturenote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note (
    var id: Int = 0,
    var title: String = "",
    var content: String = "",
    var created: String = "",
    var updated: String = "",
    var hasImage: Int = 0,
    var image: String = "",
    var folderId: Int = 0
): Parcelable