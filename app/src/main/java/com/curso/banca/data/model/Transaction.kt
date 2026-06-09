package com.curso.banca.data.model

data class BankTransaction(
    val id: String = "",
    val ownerId: String = "",
    val fromAccount: String = "",
    val toAccount: String = "",
    val toBeneficiaryId: String = "",
    val amount: Long = 0,
    val description: String = "",
    val status: String = "",
    val direction: String = "out",
    val date: FirebaseTimestamp? = null
)

data class TransactionRequest(
    val toBeneficiaryId: String,
    val amount: Long,
    val description: String?
)
