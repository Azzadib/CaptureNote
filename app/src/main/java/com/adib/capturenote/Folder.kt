package com.adib.capturenote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Folder (
    var id: Int = 0,
    var name: String = ""
): Parcelable
