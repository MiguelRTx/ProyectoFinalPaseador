package com.example.projectfinalpaseador.ui.theme.auth

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfinalpaseador.data.api.RetrofitClient
import com.example.projectfinalpaseador.data.repository.TokenRepository
import com.example.projectfinalpaseador.data.model.LoginRequest

import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.getApiService(application)
    private val tokenRepository = TokenRepository(application)

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var loginSuccess by mutableStateOf(false)
        private set
    var registrationSuccess by mutableStateOf(false)
        private set

    fun isUserLoggedIn(): Boolean {
        return tokenRepository.hasToken()
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = apiService.login(LoginRequest(email, pass))
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    tokenRepository.saveToken(token)

                    // Guardar email por lo menos, ya que es lo que sabemos del login
                    tokenRepository.saveUserInfo(null, email, null)

                    loginSuccess = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = when(response.code()) {
                        401 -> "Email o contraseña incorrectos"
                        404 -> "Usuario no encontrado"
                        422 -> "Datos inválidos"
                        else -> "Error en login: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun register(name: String, email: String, pass: String, priceHour: String, photoFile: File?) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val passBody = pass.toRequestBody("text/plain".toMediaTypeOrNull())
                val priceHourBody = priceHour.toRequestBody("text/plain".toMediaTypeOrNull())
                val photoPart = photoFile?.let {
                    val reqFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("photo", it.name, reqFile)
                }

                val response = apiService.register(emailBody, passBody, nameBody, priceHourBody, photoPart)
                if (response.isSuccessful && response.body() != null) {
                    // Registro exitoso - guardar información básica para futuros usos
                    tokenRepository.saveUserInfo(name, email, null)
                    registrationSuccess = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = when(response.code()) {
                        422 -> "Email ya está registrado o datos inválidos"
                        400 -> "Faltan campos requeridos"
                        else -> "Error en registro: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error desconocido"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        tokenRepository.clearToken()
        loginSuccess = false
        errorMessage = null
    }

    fun clearStates() {
        errorMessage = null
        loginSuccess = false
        registrationSuccess = false
        isLoading = false
    }
}