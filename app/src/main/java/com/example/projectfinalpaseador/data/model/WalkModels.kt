package com.example.projectfinalpaseador.data.model

import com.google.gson.annotations.SerializedName

data class Walk(
    val id: Int = 0,
    val status: String? = null,
    @SerializedName("scheduled_at") val scheduledAt: String? = null,
    @SerializedName("duration_minutes") val durationMinutes: Int? = null,
    val notes: String? = null,
    @SerializedName("pet_id") val petId: Int? = null,
    @SerializedName("pet_name") val petName: String? = null,
    @SerializedName("pet_type") val petType: String? = null,
    @SerializedName("pet_photo") val petPhoto: String? = null,
    @SerializedName("pet_notes") val petNotes: String? = null,
    @SerializedName("owner_id") val ownerId: Int? = null,
    @SerializedName("owner_name") val ownerName: String? = null,
    @SerializedName("owner_email") val ownerEmail: String? = null,
    @SerializedName("walker_id") val walkerId: Int? = null,
    @SerializedName("walker_name") val walkerName: String? = null,
    @SerializedName("user_address_id") val userAddressId: Int? = null,
    val address: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,

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
    fun isToday(): Boolean {
        return scheduledAt?.contains(java.time.LocalDate.now().toString()) ?: false
    }
    fun canStart(): Boolean {
        return status == "accepted" && isToday()
    }

    fun isInProgress(): Boolean {
        return status == "walking" || status == "in_progress"
    }

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

