// ui/theme/walks/WalksViewModel.kt
package com.example.projectfinalpaseador.ui.theme.walks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfinalpaseador.data.api.RetrofitClient
import com.example.projectfinalpaseador.data.model.Walk
import com.example.projectfinalpaseador.data.repository.TokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalksViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenRepository = TokenRepository(application)
    private val apiService = RetrofitClient.getApiService(application)

    private val _pendingWalks = MutableStateFlow<List<Walk>>(emptyList())
    val pendingWalks: StateFlow<List<Walk>> = _pendingWalks.asStateFlow()

    private val _acceptedWalks = MutableStateFlow<List<Walk>>(emptyList())
    val acceptedWalks: StateFlow<List<Walk>> = _acceptedWalks.asStateFlow()

    private val _historyWalks = MutableStateFlow<List<Walk>>(emptyList())
    val historyWalks: StateFlow<List<Walk>> = _historyWalks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private fun getAuthHeader(): String = "Bearer ${tokenRepository.getToken() ?: ""}"

    fun loadAllWalks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                try {
                    val response = apiService.getPendingWalks(getAuthHeader())
                    _pendingWalks.value = if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
                } catch (_: Exception) { _pendingWalks.value = emptyList() }

                try {
                    val response = apiService.getAcceptedWalks(getAuthHeader())
                    _acceptedWalks.value = if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
                } catch (_: Exception) { _acceptedWalks.value = emptyList() }

                try {
                    val response = apiService.getWalkHistory(getAuthHeader())
                    _historyWalks.value = if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
                } catch (_: Exception) { _historyWalks.value = emptyList() }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun acceptWalk(walkId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.acceptWalk(getAuthHeader(), walkId)
                if (response.isSuccessful) loadAllWalks()
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun rejectWalk(walkId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.rejectWalk(getAuthHeader(), walkId)
                if (response.isSuccessful) loadAllWalks()
            } catch (e: Exception) { _error.value = e.message }
        }
    }
}
