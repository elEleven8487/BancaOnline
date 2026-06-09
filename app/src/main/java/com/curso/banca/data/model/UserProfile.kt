package com.curso.banca.data.model

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val celular: String = "",
    val fechaNacimiento: Long = 0L,
    val fotoUrl: String = ""
) {
    val nombreCompleto: String
        get() = listOf(nombre, apellidos).filter { it.isNotBlank() }.joinToString(" ")
}
