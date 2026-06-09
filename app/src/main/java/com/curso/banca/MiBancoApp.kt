package com.curso.banca

import android.app.Application
import com.curso.banca.data.repository.ProfileRepository
import com.google.firebase.FirebaseApp

class MiBancoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        ProfileRepository.init(this)
    }
}
