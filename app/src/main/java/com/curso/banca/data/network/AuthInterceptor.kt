package com.curso.banca.data.network

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val user = FirebaseAuth.getInstance().currentUser
            ?: throw IOException("No hay usuario autenticado.")
        val token = try {
            Tasks.await(user.getIdToken(false)).token
        } catch (e: Exception) {
            throw IOException("No se pudo obtener el token de Firebase.", e)
        } ?: throw IOException("Token vacio.")

        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
