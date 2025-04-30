package com.hdev.prometeoai.Model

import java.util.UUID

data class Mensaje(
    val id: String = UUID.randomUUID().toString(),
    val texto: String,
    val rol: Rol
)

enum class Rol {
    USER,
    IA
}