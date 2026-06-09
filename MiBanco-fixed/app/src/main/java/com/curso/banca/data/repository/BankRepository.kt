package com.curso.banca.data.repository

import com.curso.banca.data.model.Account
import com.curso.banca.data.model.BankTransaction
import com.curso.banca.data.model.Beneficiary
import com.curso.banca.data.model.BeneficiaryRequest
import com.curso.banca.data.model.FundRequest
import com.curso.banca.data.model.TransactionRequest
import com.curso.banca.data.network.ApiClient
import com.curso.banca.data.network.ApiException
import com.curso.banca.data.network.apiCall

object BankRepository {
    suspend fun createAccountIfNeeded(): Result<Account> {
        val existing = apiCall { ApiClient.api.getAccount() }
        if (existing.isSuccess) return existing
        val error = existing.exceptionOrNull()
        if (error is ApiException && error.errorCode == "no_account") {
            return apiCall { ApiClient.api.createAccount() }
        }
        return apiCall { ApiClient.api.createAccount() }
    }

    suspend fun getAccount(): Result<Account> = apiCall { ApiClient.api.getAccount() }

    suspend fun fundAccount(amount: Long): Result<Account> =
        apiCall { ApiClient.api.fundAccount(FundRequest(amount)) }

    suspend fun getBeneficiaries(): Result<List<Beneficiary>> =
        apiCall { ApiClient.api.getBeneficiaries() }

    suspend fun createBeneficiary(request: BeneficiaryRequest): Result<Beneficiary> =
        apiCall { ApiClient.api.createBeneficiary(request) }

    suspend fun updateBeneficiary(id: String, request: BeneficiaryRequest): Result<Beneficiary> =
        apiCall { ApiClient.api.updateBeneficiary(id, request) }

    suspend fun deleteBeneficiary(id: String): Result<Unit> =
        apiCall { ApiClient.api.deleteBeneficiary(id) }.map { Unit }

    suspend fun getTransactions(): Result<List<BankTransaction>> =
        apiCall { ApiClient.api.getTransactions() }

    suspend fun createTransaction(
        beneficiaryId: String,
        amount: Long,
        description: String?
    ): Result<BankTransaction> =
        apiCall { ApiClient.api.createTransaction(TransactionRequest(beneficiaryId, amount, description)) }
}
