package com.example.projectfinalpaseador.ui.theme.walks

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfinalpaseador.data.api.RetrofitClient
import com.example.projectfinalpaseador.data.model.Walk
import com.example.projectfinalpaseador.data.repository.TokenRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class WalkDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenRepository = TokenRepository(application)
    private val apiService = RetrofitClient.getApiService(application)

    private fun getAuthHeader(): String = "Bearer ${tokenRepository.getToken() ?: ""}"

    var walk by mutableStateOf<Walk?>(null)
    var isLoading by mutableStateOf(false)
    var successMessage by mutableStateOf<String?>(null)

    // Simulamos cargar el paseo (En una app real, haríamos GET /walks/{id} o lo pasaríamos por argumento)
    // Para simplificar, usaremos el objeto Walk que pasamos desde la lista.
    fun setWalkData(w: Walk) {
        walk = w
    }

    // PUNTO 8: Iniciar
    fun startWalk() {
        if (walk == null) return
        viewModelScope.launch {
            try {
                val response = apiService.startWalk(getAuthHeader(), walk!!.id)
                if (response.isSuccessful) {
                    walk = walk!!.copy(status = "walking") // Actualizamos UI localmente
                    successMessage = "¡Paseo Iniciado!"
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // PUNTO 11: Finalizar
    fun endWalk() {
        if (walk == null) return
        viewModelScope.launch {
            try {
                val response = apiService.endWalk(getAuthHeader(), walk!!.id)
                if (response.isSuccessful) {
                    walk = walk!!.copy(status = "finished")
                    successMessage = "¡Paseo Finalizado!"
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // PUNTO 10: Subir Foto
    fun uploadEvidence(file: File) {
        if (walk == null) return
        viewModelScope.launch {
            isLoading = true
            try {
                val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("photo", file.name, reqFile)
                val response = apiService.uploadWalkPhoto(getAuthHeader(), walk!!.id, body)
                if (response.isSuccessful) {
                    successMessage = "Foto subida con éxito"
                }
            } catch (e: Exception) { e.printStackTrace() }
            finally { isLoading = false }
        }
    }
}