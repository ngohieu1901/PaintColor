package com.paintcolor.drawing.paint.data.apis

import com.paintcolor.drawing.paint.data.dto.TemplateDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface TemplateApi {
    @GET("ASA274_PaintColorV2Controller/all_data_image")
    suspend fun getAllTemplate(): Response<List<TemplateDto>>
}