package com.curso.banca.data.repository

import com.curso.banca.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object ProfileRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun saveProfile(profile: UserProfile): Result<Unit> = try {
        db.collection("profiles").document(profile.uid).set(profile).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observeProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val listener = db.collection("profiles").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(UserProfile::class.java))
            }
        awaitClose { listener.remove() }
    }

    suspend fun updatePhoto(uid: String, uri: String): Result<Unit> = try {
        db.collection("profiles").document(uid).update("fotoUrl", uri).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
