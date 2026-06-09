package com.curso.banca.data.model

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class FirebaseTimestamp(
    @SerializedName("_seconds") val seconds: Long = 0,
    @SerializedName("_nanoseconds") val nanoseconds: Int = 0
) : Parcelable {
    fun toDate(): Date = Date(seconds * 1000)
}
