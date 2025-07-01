package com.paintcolor.drawing.paint.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.paintcolor.drawing.paint.firebase.ads.AdsHelper
import com.paintcolor.drawing.paint.domain.model.LanguageModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SystemUtils {
    fun saveLocale(context: Context, lang: String?) {
        setPreLanguage(context, lang)
    }

    // Load lại ngôn ngữ đã lưu và thay đổi chúng
    fun setLocale(context: Context) : Context {
        val language = getPreLanguage(context)
        val langToApply = if (language.isBlank()) Locale.getDefault().toString() else language
        return changeLang(langToApply, context)
    }

    // method phục vụ cho việc thay đổi ngôn ngữ.
    fun changeLang(lang: String, context: Context): Context {
        val deviceLanguageParts = when {
            lang.contains("_") -> lang.split("_")
            lang.contains("-") -> lang.split("-")
            else -> listOf(lang)
        }
        val appLanguageCode = if (deviceLanguageParts.size > 1) {
            Locale(deviceLanguageParts[0], deviceLanguageParts[1])
        } else {
            Locale(deviceLanguageParts[0])
        }

        Locale.setDefault(appLanguageCode)

        val config = Configuration(context.resources.configuration)
        config.setLocale(appLanguageCode)

        return context.createConfigurationContext(config)
    }

    fun getPreLanguage(mContext: Context): String {
        val preferences = mContext.getSharedPreferences("data", Context.MODE_PRIVATE)
        return preferences.getString("KEY_LANGUAGE", "").toString()
    }

    fun setPreLanguage(context: Context, language: String?) {
        if (language != null && language != "") {
            val preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString("KEY_LANGUAGE", language)
            editor.apply()
        }
    }

    fun listLanguage(): MutableList<LanguageModel> {
        val languageList = listOf(
            LanguageModel("Español", "es"),
            LanguageModel("Français", "fr"),
            LanguageModel("हिन्दी", "hi"),
            LanguageModel("English", "en"),
            LanguageModel("Português (Brazil)", "pt-rBR"),
            LanguageModel("Português (Portu)", "pt-rPT"),
            LanguageModel("日本語", "ja"),
            LanguageModel("Deutsch", "de"),
            LanguageModel("中文 (简体)", "zh-rCN"),
            LanguageModel("中文 (繁體)", "zh-rTW"),
            LanguageModel("عربي", "ar"),
            LanguageModel("বাংলা", "bn"),
            LanguageModel("Русский", "ru"),
            LanguageModel("Türkçe", "tr"),
            LanguageModel("한국인", "ko"),
            LanguageModel("Indonesian", "in")
        )
        return languageList.toMutableList()
    }

    fun haveNetworkConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        val haveConnectedWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val haveConnectedMobile = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

        return haveConnectedWifi || haveConnectedMobile
    }

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)
        for (service in runningServices) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun currentDateFormatted(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    fun copyTextToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun shareUrl(context: Context, text: String) {
        if (context is Activity)
            AdsHelper.disableResume(context)
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(intent, "Share text via")
        context.startActivity(shareIntent)
    }
}