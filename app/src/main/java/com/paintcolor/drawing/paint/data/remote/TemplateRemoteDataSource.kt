package com.paintcolor.drawing.paint.data.remote

import com.paintcolor.drawing.paint.base.network.BaseRemoteService
import com.paintcolor.drawing.paint.base.network.NetworkResult
import com.paintcolor.drawing.paint.data.apis.TemplateApi
import com.paintcolor.drawing.paint.data.dto.TemplateDto

class TemplateRemoteDataSource(private val templateApi: TemplateApi): BaseRemoteService() {
    suspend fun getAllTemplate(): NetworkResult<List<TemplateDto>> {
        return callApi { templateApi.getAllTemplate() }
    }
}