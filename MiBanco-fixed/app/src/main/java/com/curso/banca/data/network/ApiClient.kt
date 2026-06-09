package com.curso.banca.data.network

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class ApiErrorBody(val error: String = "unknown_error", val message: String = "Error desconocido")

class ApiException(
    val errorCode: String,
    message: String,
    val statusCode: Int
) : Exception(message)

object ApiClient {
    private const val BASE_URL = "https://us-central1-bankapp-e47b0.cloudfunctions.net/api/"
    private val gson = Gson()

    val api: BankApi by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(logger)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(BankApi::class.java)
    }
}

suspend fun <T> apiCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: HttpException) {
    val raw = e.response()?.errorBody()?.string()
    val body = try {
        Gson().fromJson(raw, ApiErrorBody::class.java) ?: ApiErrorBody()
    } catch (_: Exception) {
        ApiErrorBody(message = e.message())
    }
    Result.failure(ApiException(body.error, body.message, e.code()))
} catch (e: Exception) {
    Result.failure(e)
}
