package au.edu.swin.sdmd.emojinary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import com.google.firebase.firestore.DocumentId

@Parcelize
data class Movie(
    @DocumentId var documentId: String = "",
    var emoji: String = "", var difficulty: Int = 0,
    var answers: List<String> = listOf(), var username: String = ""
): Parcelable {
}

