package com.curso.banca

import android.app.Application
import com.google.firebase.FirebaseApp

class MiBancoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
