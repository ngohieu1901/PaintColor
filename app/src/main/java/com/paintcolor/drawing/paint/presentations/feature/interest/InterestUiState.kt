package com.paintcolor.drawing.paint.presentations.feature.interest

import com.paintcolor.drawing.paint.domain.model.InterestModel

sealed interface InterestUiState {
    data object Loading : InterestUiState
    data class Success(val listInterest: List<InterestModel>) : InterestUiState
    data class Error(val message: String) : InterestUiState
}