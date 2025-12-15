package com.example.projectfinalpaseador.data.model

import com.google.gson.annotations.SerializedName

data class Review(
    val id: Int = 0,
    val rating: Int = 0,
    val comment: String? = null,
    @SerializedName("walk_id") val walkId: Int = 0,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,

    @SerializedName("user_id") val userId: Int? = null,
    val user: SimpleUser? = null,
    @SerializedName("walk") val walkInfo: SimpleWalk? = null
) {
    fun getCommentSafe(): String = comment ?: "Sin comentario"

    fun getOwnerNameSafe(): String = user?.name ?: walkInfo?.ownerName ?: "Cliente"

    fun getPetNameSafe(): String = walkInfo?.petName ?: "Mascota"

    fun getDateSafe(): String = createdAt ?: "Sin fecha"

    fun getWalkLinkText(): String = "Ver paseo #${walkId}"
}

data class SimpleUser(
    val id: Int = 0,
    val name: String? = null,
    val email: String? = null
)

data class SimpleWalk(
    val id: Int = 0,
    @SerializedName("pet_name") val petName: String? = null,
    @SerializedName("owner_name") val ownerName: String? = null,
    @SerializedName("scheduled_at") val scheduledAt: String? = null,
    val status: String? = null
)

