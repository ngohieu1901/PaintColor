package com.paintcolor.drawing.paint.state

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest

class ErrorsState {
    private val _errors = MutableSharedFlow<Throwable>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    suspend fun collect(action: suspend (value: Throwable) -> Unit) {
        _errors.collect(action)
    }

    suspend fun collectLatest(action: suspend (value: Throwable) -> Unit) {
        _errors.collectLatest(action)
    }

    fun emitError(t: Throwable) {
        _errors.tryEmit(t)
    }
}