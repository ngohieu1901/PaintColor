package com.paintcolor.drawing.paint.widget

inline fun <T, R> Iterable<T>.filterMapInline(
    predicate: (T) -> Boolean,
    transform: (T) -> R
): List<R> {
    val result = mutableListOf<R>()
    for (item in this) {
        if (predicate(item)) {
            result.add(transform(item))
        }
    }
    return result
}