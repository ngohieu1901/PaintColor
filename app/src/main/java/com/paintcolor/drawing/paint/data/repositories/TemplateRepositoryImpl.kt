package com.paintcolor.drawing.paint.data.repositories

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.paintcolor.drawing.paint.base.network.NetworkResult
import com.paintcolor.drawing.paint.data.remote.TemplateRemoteDataSource
import com.paintcolor.drawing.paint.data.result.DataResult
import com.paintcolor.drawing.paint.domain.model.Template
import com.paintcolor.drawing.paint.domain.model.Template.Companion.DOWNLOADED
import com.paintcolor.drawing.paint.domain.model.Template.Companion.WAIT_FOR_DOWNLOAD
import com.paintcolor.drawing.paint.domain.repository.TemplateRepository
import com.paintcolor.drawing.paint.utils.FilesUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.File

class TemplateRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val templateRemoteDataSource: TemplateRemoteDataSource,
    private val filesUtils: FilesUtils,
    private val context: Context
): TemplateRepository {

    override suspend fun getAllTemplateOnServer(): DataResult<List<Template>> = withContext(ioDispatcher) {
        try {
            when(val result = templateRemoteDataSource.getAllTemplate()){
                is NetworkResult.Error -> {
                    DataResult.Error(result.exception)
                }
                is NetworkResult.Success -> {
                    val deferredListTemplate = result.data.map {
                        async {
                            it.toTemplate().copy(
                                stateDownload = if (File(
                                        filesUtils.getAbsolutePathDirectoryData(context) + filesUtils.getFileNameFromUrl(
                                            it.imagePath
                                        )
                                    ).exists()
                                )
                                    DOWNLOADED
                                else
                                    WAIT_FOR_DOWNLOAD
                            )
                        }
                    }

                    DataResult.Success(deferredListTemplate.awaitAll())
                }
            }
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun downloadTemplate(imageUrl: String): Long = withContext(ioDispatcher) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(imageUrl))
            .setDestinationUri(Uri.fromFile(filesUtils.createDataFiles(filesUtils.getFileNameFromUrl(imageUrl))))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("Downloading_${filesUtils.getFileNameFromUrl(imageUrl)}")

        val downloadId = downloadManager.enqueue(request)
        return@withContext downloadId
    }
}