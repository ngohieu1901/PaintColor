package com.paintcolor.drawing.paint.utils

import android.content.Context
import android.content.SharedPreferences

class SharePrefUtils (context: Context) {
    private val pre: SharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pre.edit()

    var isRated
        get() = pre.getBoolean("rated", false)
        set(value) {
            editor.putBoolean("rated", value)
            editor.apply()
        }

    var isPassPermission
        get() = pre.getBoolean("pass_permission", false)
        set(value) {
            editor.putBoolean("pass_permission", value)
            editor.apply()
        }

    var isPassInterest
        get() = pre.getBoolean("isPassInterest", false)
        set(value) {
            editor.putBoolean("isPassInterest", value)
            editor.apply()
        }

    var isPassColorPaletteStart
        get() = pre.getBoolean("isPassColorPaletteStart", false)
        set(value) {
            editor.putBoolean("isPassColorPaletteStart", value)
            editor.apply()
        }

    var idPaletteStartSelected
        get() = pre.getLong("idPaletteStartSelected", -1L)
        set(value) {
            editor.putLong("idPaletteStartSelected", value)
            editor.apply()
        }

    var isUseColorPaletteStart
        get() = pre.getBoolean("isUseColorPaletteStart", false)
        set(value) {
            editor.putBoolean("isUseColorPaletteStart", value)
            editor.apply()
        }

    var countExitApp
        get() = pre.getInt("count_exit_app", 1)
        set(value) {
            editor.putInt("count_exit_app", value)
            editor.apply()
        }

    var isFirstSelectLanguage
        get() = pre.getBoolean("isFirstSelectLanguage", true)
        set(value) {
            editor.putBoolean("isFirstSelectLanguage", value)
            editor.apply()
        }

    var countOpenApp
        get() = pre.getInt("countOpenApp", 0)
        set(value) {
            editor.putInt("countOpenApp", value)
            editor.apply()
        }

    var countOpenHome
        get() = pre.getInt("countOpenHome", 0)
        set(value) {
            editor.putInt("countOpenHome", value)
            editor.apply()
        }

}