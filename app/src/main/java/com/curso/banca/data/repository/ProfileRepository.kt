package com.curso.banca.data.repository

import android.content.Context
import android.util.Log
import com.curso.banca.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object ProfileRepository {
    private const val TAG = "ProfileRepository"
    private const val PREFS = "mibanco_profiles"
    private val db = FirebaseFirestore.getInstance()
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    suspend fun saveProfile(profile: UserProfile): Result<Unit> {
        saveLocal(profile)
        try {
            db.collection("profiles").document(profile.uid).set(profile).await()
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo guardar el perfil en Firestore; se usara copia local.", e)
        }
        return Result.success(Unit)
    }

    fun observeProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        trySend(loadLocal(uid))
        val listener = db.collection("profiles").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "No se pudo leer perfil de Firestore; se conserva copia local.", error)
                    trySend(loadLocal(uid))
                    return@addSnapshotListener
                }
                val remoteProfile = snapshot?.toObject(UserProfile::class.java)
                if (remoteProfile != null) saveLocal(remoteProfile)
                trySend(remoteProfile ?: loadLocal(uid))
            }
        awaitClose { listener.remove() }
    }

    suspend fun updatePhoto(uid: String, uri: String): Result<Unit> {
        loadLocal(uid)?.let { saveLocal(it.copy(fotoUrl = uri)) }
        try {
            db.collection("profiles").document(uid).update("fotoUrl", uri).await()
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo actualizar foto en Firestore; se guardo local.", e)
        }
        return Result.success(Unit)
    }

    private fun saveLocal(profile: UserProfile) {
        prefs().edit()
            .putString("${profile.uid}_uid", profile.uid)
            .putString("${profile.uid}_email", profile.email)
            .putString("${profile.uid}_nombre", profile.nombre)
            .putString("${profile.uid}_apellidos", profile.apellidos)
            .putString("${profile.uid}_celular", profile.celular)
            .putLong("${profile.uid}_fechaNacimiento", profile.fechaNacimiento)
            .putString("${profile.uid}_fotoUrl", profile.fotoUrl)
            .apply()
    }

    private fun loadLocal(uid: String): UserProfile? {
        val preferences = prefs()
        if (!preferences.contains("${uid}_uid")) return null
        return UserProfile(
            uid = preferences.getString("${uid}_uid", uid).orEmpty(),
            email = preferences.getString("${uid}_email", "").orEmpty(),
            nombre = preferences.getString("${uid}_nombre", "").orEmpty(),
            apellidos = preferences.getString("${uid}_apellidos", "").orEmpty(),
            celular = preferences.getString("${uid}_celular", "").orEmpty(),
            fechaNacimiento = preferences.getLong("${uid}_fechaNacimiento", 0L),
            fotoUrl = preferences.getString("${uid}_fotoUrl", "").orEmpty()
        )
    }

    private fun prefs() = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
