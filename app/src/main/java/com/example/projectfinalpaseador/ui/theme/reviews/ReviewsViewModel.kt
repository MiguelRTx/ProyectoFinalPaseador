package com.example.projectfinalpaseador.ui.theme.reviews

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfinalpaseador.data.api.RetrofitClient
import com.example.projectfinalpaseador.data.model.Review
import com.example.projectfinalpaseador.data.repository.TokenRepository
import kotlinx.coroutines.launch

class ReviewsViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenRepository = TokenRepository(application)
    private val apiService = RetrofitClient.getApiService(application)

    var reviews by mutableStateOf<List<Review>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private fun getAuthHeader(): String = "Bearer ${tokenRepository.getToken() ?: ""}"

    init {
        loadReviews()
    }

    fun loadReviews() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = apiService.getReviews(getAuthHeader())

                if (response.isSuccessful) {
                    val reviewsList = response.body() ?: emptyList()

                    reviewsList.forEach { review ->
                        println("Review ID: ${review.id}")
                        println("Rating: ${review.rating}")
                        println("Comment: ${review.comment}")
                        println("User Name: ${review.user?.name}")
                        println("Walk Owner Name: ${review.walkInfo?.ownerName}")
                        println("Pet Name: ${review.walkInfo?.petName}")
                        println("Final Owner Name: ${review.getOwnerNameSafe()}")
                        println("Final Pet Name: ${review.getPetNameSafe()}")
                        println("---")
                    }

                    reviews = reviewsList
                } else {
                    errorMessage = when(response.code()) {
                        401 -> "No autorizado"
                        404 -> "No se encontraron reviews"
                        else -> "Error al cargar las reviews: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun refreshReviews() {
        loadReviews()
    }
}
