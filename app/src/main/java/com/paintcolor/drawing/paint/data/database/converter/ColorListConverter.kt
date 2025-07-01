package com.paintcolor.drawing.paint.data.database.converter

import androidx.room.TypeConverter

class ColorListConverter {
    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString(",")

    @TypeConverter
    fun toStringList(value: String): List<String> = value.split(",")
}