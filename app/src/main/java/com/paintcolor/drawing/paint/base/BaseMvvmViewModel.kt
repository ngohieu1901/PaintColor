package com.paintcolor.drawing.paint.base

import androidx.lifecycle.ViewModel
import com.paintcolor.drawing.paint.state.ErrorsState
import com.paintcolor.drawing.paint.state.LoadingState
import com.paintcolor.drawing.paint.state.UiStateStore
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber

abstract class BaseMvvmViewModel<S : Any>: ViewModel() {
    abstract fun initState(): S

    val uiStateStore by lazy { UiStateStore(this.initState()) }

    val errorsState by lazy { ErrorsState() }

    val loadingState by lazy { LoadingState() }

    val currentState: S get() = uiStateStore.currentUiState

    protected val exceptionHandler by lazy { CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "${this::class.java.simpleName}: ${exception.message}")
    } }

    protected fun dispatchStateUi(uiState: S) {
        this.uiStateStore.updateStateUi(uiState = uiState)
    }

    protected fun dispatchStateError(error: Throwable) {
        errorsState.emitError(error)
    }

    protected fun dispatchStateLoading(isShowLoading: Boolean){
        loadingState.updateLoadingState(isShowLoading)
    }
}