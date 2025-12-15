// data/model/AuthModels.kt
package com.example.projectfinalpaseador.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

data class RegisterResponse(
    val message: String,
    val walker: User? = null
)

data class User(
    val id: Int = 0,
    val name: String? = null,
    val email: String? = null,
    val photo: String? = null
)

data class AvailabilityRequest(
    val is_available: Boolean
)

data class LocationRequest(
    val latitude: String,
    val longitude: String
)

data class WalkerProfile(
    val id: Int = 0,
    val name: String? = null,
    val email: String? = null,
    val photo: String? = null,
    @SerializedName("price_hour") val priceHour: String? = null,
    @SerializedName("is_available") val isAvailable: Boolean = false,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
) {
    fun getNameSafe(): String = name ?: "Paseador"
    fun getPhotoUrl(baseUrl: String = "https://apimascotas.jmacboy.com"): String? {
        return photo?.takeIf { it.isNotBlank() }?.let { photoPath ->
            when {
                // Ya es una URL completa
                photoPath.startsWith("http") -> photoPath

                // Ruta relativa que empieza con /
                photoPath.startsWith("/") -> "${baseUrl.removeSuffix("/")}$photoPath"

                // Ruta relativa sin /
                else -> "${baseUrl.removeSuffix("/")}/$photoPath"
            }
        }
    }

    fun hasPhoto(): Boolean = !photo.isNullOrBlank()
}

