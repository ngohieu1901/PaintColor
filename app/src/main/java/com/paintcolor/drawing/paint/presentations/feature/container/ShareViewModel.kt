package com.paintcolor.drawing.paint.presentations.feature.container

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintcolor.drawing.paint.domain.repository.FileRepository
import com.paintcolor.drawing.paint.state.LoadingState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ShareViewModel(
    private val fileRepository: FileRepository
): ViewModel() {
    val loadingState by lazy { LoadingState() }

    private val _shareIntentResult = MutableSharedFlow<Intent?>(replay = 0, extraBufferCapacity = 1, BufferOverflow.DROP_OLDEST)
    val shareIntentResult = _shareIntentResult.asSharedFlow()

    fun shareImage(path: String) {
        viewModelScope.launch {
            loadingState.updateLoadingState(isShowLoading = true)

            val result = fileRepository.shareFile(path)
            result.onSuccess { intent ->
                _shareIntentResult.tryEmit(intent)
            }.onFailure {
                Timber.d("shareImageError: $it")
                _shareIntentResult.tryEmit(null)
            }

            loadingState.updateLoadingState(isShowLoading = false)
        }
    }
}