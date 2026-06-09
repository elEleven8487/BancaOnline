package com.curso.banca.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Beneficiary(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val lastName: String = "",
    val accountNumber: String = "",
    val alias: String = "",
    val createdAt: FirebaseTimestamp? = null
) : Parcelable {
    val fullName: String
        get() = listOf(name, lastName).filter { it.isNotBlank() }.joinToString(" ")
}

data class BeneficiaryRequest(
    val name: String,
    val lastName: String,
    val accountNumber: String,
    val alias: String
)

data class DeleteResponse(val id: String = "", val deleted: Boolean = false)
