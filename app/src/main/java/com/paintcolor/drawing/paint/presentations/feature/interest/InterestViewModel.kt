package com.paintcolor.drawing.paint.presentations.feature.interest

import androidx.lifecycle.viewModelScope
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseMvvmViewModel
import com.paintcolor.drawing.paint.domain.model.InterestModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class InterestViewModel (
    private val ioDispatcher: CoroutineDispatcher
): BaseMvvmViewModel<InterestUiState>() {
    override fun initState(): InterestUiState = InterestUiState.Loading

    fun getAllItemInterest() {
        viewModelScope.launch(ioDispatcher) {
            dispatchStateUi(InterestUiState.Loading)
            val listInterest = mutableListOf<InterestModel>().apply {
                add(InterestModel(imageResource = R.drawable.img_animal, nameResource = R.string.animal, isSelected = false))
                add(InterestModel(imageResource = R.drawable.img_alpha_bet, nameResource = R.string.alphabet, isSelected = false))
                add(InterestModel(imageResource = R.drawable.img_number, nameResource = R.string.numbers, isSelected = false))
                add(InterestModel(imageResource = R.drawable.img_flower, nameResource = R.string.flower, isSelected = false))
                add(InterestModel(imageResource = R.drawable.img_anime, nameResource = R.string.anime, isSelected = false))
                add(InterestModel(imageResource = R.drawable.img_toys, nameResource = R.string.toys, isSelected = false))
                add(InterestModel(imageResource = R.drawable.img_fruits, nameResource = R.string.fruits, isSelected = false))
                add(InterestModel(imageResource = R.drawable.img_birds, nameResource = R.string.birds, isSelected = false))
                add(InterestModel(imageResource = R.drawable.img_cartoons, nameResource = R.string.cartoons, isSelected = false))
            }
            dispatchStateUi(InterestUiState.Success(listInterest))
        }.invokeOnCompletion {
            if (it != null) dispatchStateUi(InterestUiState.Error(it.message.toString()))
        }
    }

    fun selectInterest(interestModel: InterestModel) {
        viewModelScope.launch(ioDispatcher) {
            when (val currentState = currentState) {
                is InterestUiState.Success -> {
                    val listInterest = currentState.listInterest.map {
                        if (it.nameResource == interestModel.nameResource) {
                            it.copy(isSelected = !it.isSelected)
                        } else {
                            it
                        }
                    }
                    dispatchStateUi(InterestUiState.Success(listInterest = listInterest))
                }
                else -> {}
            }
        }.invokeOnCompletion {
            if (it != null) dispatchStateUi(InterestUiState.Error(it.message.toString()))
        }
    }
}