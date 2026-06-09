package com.curso.banca.data.model

data class Account(
    val id: String = "",
    val ownerId: String = "",
    val accountNumber: String = "",
    val balance: Long = 0,
    val createdAt: FirebaseTimestamp? = null
)

data class FundRequest(val amount: Long)
