package com.curso.banca.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

object AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun currentUser(): FirebaseUser? = auth.currentUser

    suspend fun login(email: String, password: String): Result<FirebaseUser> = try {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        Result.success(result.user ?: error("No se pudo obtener el usuario."))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun register(email: String, password: String): Result<FirebaseUser> = try {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        Result.success(result.user ?: error("No se pudo crear el usuario."))
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun logout() {
        auth.signOut()
    }
}
