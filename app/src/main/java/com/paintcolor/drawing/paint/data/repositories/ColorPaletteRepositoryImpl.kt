package com.paintcolor.drawing.paint.data.repositories

import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.data.local.ColorPaletteLocalDataSource
import com.paintcolor.drawing.paint.data.result.DataResult
import com.paintcolor.drawing.paint.domain.model.ColorPalette
import com.paintcolor.drawing.paint.domain.repository.ColorPaletteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ColorPaletteRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val localDataSource: ColorPaletteLocalDataSource,
) : ColorPaletteRepository {

    override suspend fun getAllPlainColorPalettes(): DataResult<List<ColorPalette>> =
        withContext(ioDispatcher) {
            try {
                val listBasicColor = listOf(
                    "#FF8A80", "#FFD180", "#FFFF8D",
                    "#CCFF90", "#A7FFEB", "#80D8FF",
                    "#8C9EFF", "#B388FF", "#FF80AB"
                )

                val listSkinToneColor = listOf(
                    "#502B19", "#623A25", "#4E2B18",
                    "#E1927B", "#FCDAC1", "#805E42",
                    "#DC9F70", "#F1C088", "#FDE8B1"
                )

                val listRainbowColor = listOf(
                    "#EA3323", "#EF8D34", "#FAD446",
                    "#B6FB50", "#A7FFEB", "#6EEBFD",
                    "#3C7BED", "#5011F5", "#AE26F6"
                )

                val listPastelColor = listOf(
                    "#FF8A80", "#FFDAC0", "#FFFF8D",
                    "#E2F0CA", "#B6E8D8", "#B5E8FE",
                    "#C6CEEA", "#D9C4FE", "#FEB0CA"
                )

                val listCoolToneColor = listOf(
                    "#F3F3F3", "#B0B0BB", "#7B7787",
                    "#B73979", "#DD4D70", "#461750",
                    "#6A549B", "#6C81BB", "#264283"
                )

                val listWarmToneColor = listOf(
                    "#DECCB0", "#CDAB7A", "#CFAB7A",
                    "#E86C72", "#EEAE9D", "#8C2F13",
                    "#DC6A06", "#EB9773", "#EDA5A5"
                )

                val listNeutralColor = listOf(
                    "#AD9790", "#7E7275", "#4D4044",
                    "#FCDD64", "#E0AEB1", "#BC6F70",
                    "#2E6047", "#4267A7", "#A82B49"
                )

                val listLipColor = listOf(
                    "#EED3B5", "#EEC9C3", "#BB75A7",
                    "#C77C79", "#91585F", "#2D4491",
                    "#A53C8E", "#EC9C93", "#BD4F58"
                )

                val listHairColor = listOf(
                    "#D6BD9F", "#A48861", "#543F26",
                    "#AA5821", "#7E3D1A", "#452311",
                    "#5E6060", "#373737", "#020501"
                )

                val listColorPalettes = mutableListOf<ColorPalette>().apply {
                    add(ColorPalette(listColor = listBasicColor, nameResource = R.string.basic))
                    add(
                        ColorPalette(
                            listColor = listSkinToneColor,
                            nameResource = R.string.skin_tone
                        )
                    )
                    add(ColorPalette(listColor = listRainbowColor, nameResource = R.string.rainbow))
                    add(ColorPalette(listColor = listPastelColor, nameResource = R.string.pastel))
                    add(
                        ColorPalette(
                            listColor = listCoolToneColor,
                            nameResource = R.string.cool_tone
                        )
                    )
                    add(
                        ColorPalette(
                            listColor = listWarmToneColor,
                            nameResource = R.string.warm_tone
                        )
                    )
                    add(ColorPalette(listColor = listNeutralColor, nameResource = R.string.neutral))
                    add(ColorPalette(listColor = listLipColor, nameResource = R.string.lip))
                    add(ColorPalette(listColor = listHairColor, nameResource = R.string.hair))
                }

                return@withContext DataResult.Success(listColorPalettes)
            } catch (e: Exception) {
                return@withContext DataResult.Error(e)
            }
        }

    override suspend fun insertAllColorPalettes(listColorPalettes: List<ColorPalette>) =
        withContext(ioDispatcher) {
            localDataSource.insertAllColorPalettes(listColorPalettes.map { it.toColorPaletteEntity() })
        }

    override suspend fun insertColorPalette(colorPalettes: ColorPalette) {
        localDataSource.insertColorPalette(colorPaletteEntity = colorPalettes.toColorPaletteEntity())
    }

    override suspend fun updateColorPalette(colorPalettes: ColorPalette) {
        localDataSource.updateColorPalette(colorPaletteEntity = colorPalettes.toColorPaletteEntity())
    }

    override suspend fun deleteColorPalette(colorPalettes: ColorPalette) {
        localDataSource.deleteColorPalette(colorPaletteEntity = colorPalettes.toColorPaletteEntity())
    }

    override suspend fun collectColorPalettesPlainFlow(): Flow<List<ColorPalette>> =
        localDataSource.collectColorPalettesPlainFlow()
            .map { it.map { colorPaletteEntity -> colorPaletteEntity.toColorPalette() } }

    override suspend fun collectAllColorPalettesFlow(): Flow<List<ColorPalette>> =
        localDataSource.collectAllColorPalettes()
            .map { it.map { colorPaletteEntity -> colorPaletteEntity.toColorPalette() } }
}