package com.paintcolor.drawing.paint.presentations.feature.main

import com.paintcolor.drawing.paint.base.BaseMvvmViewModel

class MainViewModel: BaseMvvmViewModel<MainUiState>() {
    override fun initState(): MainUiState = MainUiState()

    fun setSelectedFragment(nameFragment: String) {
        dispatchStateUi(currentState.copy(fragmentSelected = nameFragment))
    }
}