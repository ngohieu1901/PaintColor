package com.paintcolor.drawing.paint.presentations.feature.coloring

import androidx.lifecycle.viewModelScope
import com.paintcolor.drawing.paint.base.BaseMvvmViewModel
import com.paintcolor.drawing.paint.data.result.DataResult
import com.paintcolor.drawing.paint.domain.model.Template
import com.paintcolor.drawing.paint.domain.model.Template.Companion.DOWNLOADED
import com.paintcolor.drawing.paint.domain.model.Template.Companion.DOWNLOADING
import com.paintcolor.drawing.paint.domain.model.TemplateCategory
import com.paintcolor.drawing.paint.domain.repository.TemplateRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class ColoringViewModel(
    private val templateRepository: TemplateRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : BaseMvvmViewModel<ColoringUiState>() {
    override fun initState(): ColoringUiState = ColoringUiState()

    private val downloadMap by lazy { mutableMapOf<Long, String>() }

    init {
        getAllTemplateOnServer()
    }

    fun downloadTemplate(imageUrl: String) {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            val downloadId = templateRepository.downloadTemplate(imageUrl)
            downloadMap[downloadId] = imageUrl
            updateStateDownloadTemplate(imageUrl, DOWNLOADING)
        }
    }

    fun handleDownloadComplete(downloadId: Long) {
        val imagePath = downloadMap[downloadId] ?: return
        updateStateDownloadTemplate(imagePath, DOWNLOADED)
        downloadMap.remove(downloadId)
    }

    private fun updateStateDownloadTemplate(imagePath: String, newStateDownload: Int) {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            val updatedListTemplateCategory =
                currentState.listTemplateCategory.map { templateCategory ->
                    val templateInCategory = templateCategory.listTemplate.map { template ->
                        if (template.imagePath == imagePath) {
                            template.copy(stateDownload = newStateDownload)
                        } else {
                            template
                        }
                    }
                    templateCategory.copy(listTemplate = templateInCategory)
                }

            val updatedListTemplate = currentState.listAllTemplate.map { template ->
                if (template.imagePath == imagePath) {
                    template.copy(stateDownload = newStateDownload)
                } else {
                    template
                }
            }

            val updatedListTemplateInCategory =
                currentState.listTemplateInCategory.map { template ->
                    if (template.imagePath == imagePath) {
                        template.copy(stateDownload = newStateDownload)
                    } else {
                        template
                    }
                }

            dispatchStateUi(
                currentState.copy(
                    listTemplateCategory = updatedListTemplateCategory,
                    listAllTemplate = updatedListTemplate,
                    listTemplateInCategory = updatedListTemplateInCategory
                )
            )
        }
    }

    fun updateStateRewardTemplate(template: Template) {
        viewModelScope.launch(exceptionHandler + ioDispatcher) {
            val updatedListAllTemplate = currentState.listAllTemplate.map {
                if (it.imagePath == template.imagePath) {
                    it.copy(isViewedReward = true)
                } else {
                    it
                }
            }
            val updatedListTemplateInCategory = currentState.listTemplateInCategory.map {
                if (it.imagePath == template.imagePath) {
                    it.copy(isViewedReward = true)
                } else {
                    it
                }
            }
            dispatchStateUi(
                currentState.copy(
                    listAllTemplate = updatedListAllTemplate,
                    listTemplateInCategory = updatedListTemplateInCategory
                )
            )
        }
    }

    fun getAllTemplateInCategory(nameCategory: String) {
        viewModelScope.launch(exceptionHandler) {
            val listTemplateInCategory = currentState.listAllTemplate
                .filter { it.category == nameCategory }
                .mapIndexed { index, template ->
                    template.copy(isViewedReward = if (index < 3) true else template.isViewedReward)
                }

            val countViewedReward = if (listTemplateInCategory.any { !it.isViewedReward }) 0 else 2
            dispatchStateUi(currentState.copy(listTemplateInCategory = listTemplateInCategory, countWatchedVideoReward = countViewedReward))
        }
    }

    private fun getAllTemplateOnServer() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            when (val result = templateRepository.getAllTemplateOnServer()) {
                is DataResult.Error -> {
                    dispatchStateError(result.exception)
                }

                is DataResult.Success -> {
                    val listTemplateCategory = result.data.groupBy { it.category }
                        .map { (categoryName, templatesInCategory) ->
                            TemplateCategory(
                                nameCategory = categoryName,
                                listTemplate = templatesInCategory.filter { template -> template.imagePath.isNotBlank() }
                                    .take(3)
                            )
                        }.filter { templateCategory -> templateCategory.listTemplate.isNotEmpty() }

                    dispatchStateUi(
                        ColoringUiState(
                            listTemplateCategory = listTemplateCategory,
                            listAllTemplate = result.data
                        )
                    )
                }
            }
            dispatchStateLoading(false)
        }
    }

    fun updateCountWatchedVideoReward(count: Int) {
        dispatchStateUi(currentState.copy(countWatchedVideoReward = count))
    }

    fun turnOffAllRewardAdsInCategory(category: String) {
        viewModelScope.launch(exceptionHandler) {
            val updatedListAllTemplate =
                currentState.listAllTemplate.map {
                    it.copy(
                        isViewedReward = if (it.category == category) true else it.isViewedReward,
                    )
                }
            val updatedListTemplateInCategory = currentState.listTemplateInCategory.map {
                it.copy(
                    isViewedReward = true
                )
            }
            dispatchStateUi(
                currentState.copy(
                    listAllTemplate = updatedListAllTemplate,
                    listTemplateInCategory = updatedListTemplateInCategory
                )
            )
        }
    }
}