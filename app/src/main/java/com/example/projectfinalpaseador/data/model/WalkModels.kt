// data/model/Walk.kt
package com.example.projectfinalpaseador.data.model

import com.google.gson.annotations.SerializedName

data class Walk(
    val id: Int = 0,
    val status: String? = null,
    @SerializedName("scheduled_at") val scheduledAt: String? = null,
    @SerializedName("duration_minutes") val durationMinutes: Int? = null,
    val notes: String? = null,

    // Pet info
    @SerializedName("pet_id") val petId: Int? = null,
    @SerializedName("pet_name") val petName: String? = null,
    @SerializedName("pet_type") val petType: String? = null,
    @SerializedName("pet_photo") val petPhoto: String? = null,
    @SerializedName("pet_notes") val petNotes: String? = null,

    // Owner info
    @SerializedName("owner_id") val ownerId: Int? = null,
    @SerializedName("owner_name") val ownerName: String? = null,
    @SerializedName("owner_email") val ownerEmail: String? = null,

    // Walker info
    @SerializedName("walker_id") val walkerId: Int? = null,
    @SerializedName("walker_name") val walkerName: String? = null,

    // Address info
    @SerializedName("user_address_id") val userAddressId: Int? = null,
    val address: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,

    // Timestamps
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("started_at") val startedAt: String? = null,
    @SerializedName("finished_at") val finishedAt: String? = null
) {
    fun getPetNameSafe(): String = petName ?: "Mascota sin nombre"
    fun getOwnerNameSafe(): String = ownerName ?: "Dueño desconocido"
    fun getScheduledAtSafe(): String = scheduledAt ?: "Sin fecha"
    fun getStatusSafe(): String = status ?: "Desconocido"
    fun getAddressSafe(): String = address ?: "Sin dirección"
    fun getDurationSafe(): String = "${durationMinutes ?: 30} minutos"
    fun getNotesSafe(): String = notes ?: "Sin notas"
    fun getPetTypeSafe(): String = petType ?: "Mascota"

    // Helper para verificar si es para hoy
    fun isToday(): Boolean {
        return scheduledAt?.contains(java.time.LocalDate.now().toString()) ?: false
    }

    // Helper para verificar si se puede iniciar
    fun canStart(): Boolean {
        return status == "accepted" && isToday()
    }

    // Helper para verificar si está en curso
    fun isInProgress(): Boolean {
        return status == "walking" || status == "in_progress"
    }

    // Helper para verificar si está finalizado
    fun isFinished(): Boolean {
        return status == "finished" || status == "completed"
    }
}

data class WalkPhoto(
    val id: Int = 0,
    @SerializedName("walk_id") val walkId: Int = 0,
    val photo: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

