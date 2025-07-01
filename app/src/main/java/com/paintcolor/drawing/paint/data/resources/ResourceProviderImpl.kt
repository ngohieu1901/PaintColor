package com.paintcolor.drawing.paint.data.resources

import android.content.Context
import androidx.annotation.StringRes
import com.paintcolor.drawing.paint.domain.resource.ResourceProvider

class ResourceProviderImpl (
    private val context: Context
) : ResourceProvider {
    override fun getString(@StringRes resId: Int): String = context.getString(resId)
}