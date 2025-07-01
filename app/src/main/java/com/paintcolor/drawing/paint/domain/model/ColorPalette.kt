package com.paintcolor.drawing.paint.domain.model

import android.os.Parcelable
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.data.database.entities.ColorPaletteEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColorPalette (
    val id: Long = 0,
    val listColor: List<String> = emptyList(),
    val nameResource: Int = R.string.basic,
    val nameString: String = "",
    val isCustomPalette: Boolean = false,
    val isSelected: Boolean = false
): Parcelable {
    fun toColorPaletteEntity(): ColorPaletteEntity {
        return ColorPaletteEntity(
            id = id,
            listColor = listColor,
            nameResource = nameResource,
            nameString = nameString,
            isCustomPalette = isCustomPalette,
        )
    }
}