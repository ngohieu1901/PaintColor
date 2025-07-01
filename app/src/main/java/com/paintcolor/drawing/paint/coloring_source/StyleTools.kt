package com.paintcolor.drawing.paint.coloring_source

enum class StyleTools {
    PENCIL,
    FILL,
    BRUSH,
    BIG_BRUSH,
    ERASER,
    SPRAY,
    MARKER,
    TECH_PEN,
    TEST;

    override fun toString(): String {
        return when (this) {
            PENCIL -> "Pencil"
            FILL -> "Fill"
            BRUSH -> "Brush"
            BIG_BRUSH -> "Big Brush"
            ERASER -> "Eraser"
            SPRAY -> "Spray"
            MARKER -> "Marker"
            TECH_PEN -> "Tech Pen"
            TEST -> "Test"
        }
    }
}