package com.paintcolor.drawing.paint.data.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.paintcolor.drawing.paint.domain.model.ColorPalette
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class ColorPaletteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listColor: List<String>,
    val nameString: String,
    val nameResource: Int,
    val isCustomPalette: Boolean,
) : Parcelable {
    fun toColorPalette(): ColorPalette {
        return ColorPalette(
            id = id,
            listColor = listColor,
            nameString = nameString,
            nameResource = nameResource,
            isCustomPalette = isCustomPalette,
        )
    }
}