package com.paintcolor.drawing.paint.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paintcolor.drawing.paint.event_bus.DownloadCompleteEvent
import org.greenrobot.eventbus.EventBus

class DownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            EventBus.getDefault().post(DownloadCompleteEvent(downloadId))
        }
    }
}