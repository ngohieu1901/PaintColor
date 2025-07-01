package com.paintcolor.drawing.paint.presentations.feature.container

import com.paintcolor.drawing.paint.base.BaseMvvmViewModel

class ContainerViewModel : BaseMvvmViewModel<ContainerUiState>() {
    override fun initState(): ContainerUiState {
        return ContainerUiState(state = "")
    }
}