package au.edu.swin.sdmd.emojinary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    var emoji: String = "", var difficulty: Int = 0,
    var answers: List<String> = listOf(), var username: String = ""
): Parcelable {
}