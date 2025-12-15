
package com.example.projectfinalpaseador.ui.theme.home

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfinalpaseador.data.api.RetrofitClient
import com.example.projectfinalpaseador.data.model.AvailabilityRequest
import com.example.projectfinalpaseador.data.model.WalkerProfile
import com.example.projectfinalpaseador.data.repository.TokenRepository
import com.example.projectfinalpaseador.services.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenRepository = TokenRepository(application)
    private val apiService = RetrofitClient.getApiService(application)

    private val _isAvailable = MutableStateFlow(false)
    val isAvailable: StateFlow<Boolean> = _isAvailable.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val _userProfile = MutableStateFlow<WalkerProfile?>(null)
    val userProfile: StateFlow<WalkerProfile?> = _userProfile.asStateFlow()

    private val _isLoadingProfile = MutableStateFlow(false)
    val isLoadingProfile: StateFlow<Boolean> = _isLoadingProfile.asStateFlow()

    init {

        if (!tokenRepository.hasToken()) {
            _error.value = "No hay sesión activa"
        } else {
        
            loadCurrentState()
        }
    }

    fun clearError() {
        _error.value = null
    }

    private fun loadCurrentState() {
        viewModelScope.launch {
            try {
                val token = tokenRepository.getToken()
                if (!token.isNullOrBlank()) {
                
                    loadUserProfile()
                    _isAvailable.value = false
                }
            } catch (e: Exception) {
   
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _isLoadingProfile.value = true
            try {
                val token = tokenRepository.getToken()
                if (!token.isNullOrBlank()) {
                    try {
         
                        val response = apiService.getCurrentUser("Bearer $token")
                        if (response.isSuccessful && response.body() != null) {
                            val profile = response.body()!!

                            _userProfile.value = profile
                    
                            tokenRepository.saveUserInfo(profile.name, profile.email, profile.photo)
            
                            _isAvailable.value = profile.isAvailable
                        } else {
               
                            loadProfileFromLocal()
                        }
                    } catch (e: Exception) {
              
                        println("Server failed, using local data: ${e.message}")
                        loadProfileFromLocal()
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar perfil: ${e.message}"
            } finally {
                _isLoadingProfile.value = false
            }
        }
    }

    private fun loadProfileFromLocal() {
        val localName = tokenRepository.getUserName()
        val localEmail = tokenRepository.getUserEmail()
        val localPhoto = tokenRepository.getUserPhoto()

        if (localName != null || localEmail != null) {
            _userProfile.value = WalkerProfile(
                name = localName,
                email = localEmail,
                photo = localPhoto,
                isAvailable = false 
            )
        } else {
            _error.value = "No se pudo cargar información del perfil"
        }
    }

    private fun getAuthHeader(): String {
        val token = tokenRepository.getToken() ?: ""
        return "Bearer $token"
    }

    fun toggleAvailability() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
              
                val token = tokenRepository.getToken()
                if (token.isNullOrBlank()) {
                    _error.value = "No hay sesión activa"
                    return@launch
                }

                val newAvailability = !_isAvailable.value
                val request = AvailabilityRequest(is_available = newAvailability)

                try {
                    val response = apiService.setAvailability("Bearer $token", request)

                    if (response.isSuccessful) {
                        _isAvailable.value = newAvailability

                        try {
                            val intent = Intent(getApplication(), LocationService::class.java)
                            if (newAvailability) {
                                intent.action = "START_LOCATION_UPDATES"
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    getApplication<Application>().startForegroundService(intent)
                                } else {
                                    getApplication<Application>().startService(intent)
                                }
                            } else {
                                intent.action = "STOP_LOCATION_UPDATES"
                                getApplication<Application>().startService(intent)
                            }
                        } catch (serviceException: Exception) {
                        
                            _error.value = "Disponibilidad cambiada, pero no se pudo iniciar servicio de ubicación"
                        }

                    } else {
                        _error.value = when (response.code()) {
                            401 -> "Sesión expirada"
                            403 -> "No autorizado"
                            500 -> "Error del servidor"
                            else -> "Error al cambiar disponibilidad: ${response.code()}"
                        }
                    }
                } catch (networkException: Exception) {
                    _error.value = "Error de red: ${networkException.message}"
                }

            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshProfile() {
        loadUserProfile()
    }

    fun logout() {
        tokenRepository.clearToken()
    }
}
