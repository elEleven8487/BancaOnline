package com.curso.banca.data.network

import com.curso.banca.data.model.Account
import com.curso.banca.data.model.BankTransaction
import com.curso.banca.data.model.Beneficiary
import com.curso.banca.data.model.BeneficiaryRequest
import com.curso.banca.data.model.DeleteResponse
import com.curso.banca.data.model.FundRequest
import com.curso.banca.data.model.TransactionRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BankApi {
    @POST("account")
    suspend fun createAccount(): Account

    @GET("account")
    suspend fun getAccount(): Account

    @PUT("account")
    suspend fun fundAccount(@Body request: FundRequest): Account

    @POST("beneficiaries")
    suspend fun createBeneficiary(@Body request: BeneficiaryRequest): Beneficiary

    @GET("beneficiaries")
    suspend fun getBeneficiaries(): List<Beneficiary>

    @GET("beneficiaries/{id}")
    suspend fun getBeneficiary(@Path("id") id: String): Beneficiary

    @PUT("beneficiaries/{id}")
    suspend fun updateBeneficiary(
        @Path("id") id: String,
        @Body request: BeneficiaryRequest
    ): Beneficiary

    @DELETE("beneficiaries/{id}")
    suspend fun deleteBeneficiary(@Path("id") id: String): DeleteResponse

    @POST("transaction")
    suspend fun createTransaction(@Body request: TransactionRequest): BankTransaction

    @GET("transaction")
    suspend fun getTransactions(): List<BankTransaction>
}
