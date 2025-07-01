package com.paintcolor.drawing.paint.presentations.feature.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paintcolor.drawing.paint.domain.repository.FileRepository
import com.paintcolor.drawing.paint.state.LoadingState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class FilesViewModel(
    private val fileRepository: FileRepository
): ViewModel() {
    val loadingState by lazy { LoadingState() }

    private val _downloadResult = MutableSharedFlow<Boolean>(replay = 0, extraBufferCapacity = 1, BufferOverflow.DROP_OLDEST)
    val downloadResult = _downloadResult.asSharedFlow()

    private val _deleteResult = MutableSharedFlow<Boolean>(replay = 0, extraBufferCapacity = 1, BufferOverflow.DROP_OLDEST)
    val deleteResult = _deleteResult.asSharedFlow()

    fun downloadImage(path: String) {
        viewModelScope.launch {
            loadingState.updateLoadingState(isShowLoading = true)
            fileRepository.copyFileToDirectoryDownload(path).fold(
                    onSuccess = { pathImageCopied ->
                        Timber.d("pathImageCopied: $pathImageCopied")
                        _downloadResult.tryEmit(true)
                        loadingState.updateLoadingState(isShowLoading = false)
                    },
                    onFailure = {
                        Timber.d("downloadImageError: $it")
                        _downloadResult.tryEmit(false)
                        loadingState.updateLoadingState(isShowLoading = false)
                    }
            )
        }
    }

    fun deleteFile(path: String) {
        viewModelScope.launch {
            loadingState.updateLoadingState(isShowLoading = true)

            fileRepository.deleteFile(path).fold(
                onSuccess = { deleted ->
                    loadingState.updateLoadingState(isShowLoading = false)
                    _deleteResult.tryEmit(deleted)
                },
                onFailure = { error ->
                    Timber.d("deleteFileError: $error")
                    loadingState.updateLoadingState(isShowLoading = false)
                    _deleteResult.tryEmit(false)
                }
            )
        }
    }
}