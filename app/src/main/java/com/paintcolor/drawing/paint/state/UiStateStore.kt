package com.paintcolor.drawing.paint.state

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import timber.log.Timber

class UiStateStore<T : Any>(
    initialState: T,
) {
    private val _uiStateFlow = MutableStateFlow(initialState)
    val uiStateFlow get() = _uiStateFlow.asStateFlow()

    val currentUiState: T get() = _uiStateFlow.value

    suspend fun collect(collector: FlowCollector<T>) {
        Timber.e("collectData: " + _uiStateFlow.value)
        _uiStateFlow.collect(collector)
    }

    suspend fun collectLatest(action: suspend (uiState: T) -> Unit) {
        Timber.e("collectData: " + _uiStateFlow.value)
        _uiStateFlow.collectLatest(action)
    }

    fun updateStateUi(uiState: T) {
        Timber.e("updateStateUi: " + _uiStateFlow.value)
        _uiStateFlow.update { uiState }
    }
}