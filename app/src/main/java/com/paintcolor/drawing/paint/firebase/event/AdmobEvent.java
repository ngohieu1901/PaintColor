package com.paintcolor.drawing.paint.firebase.event;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import timber.log.Timber;

public class AdmobEvent {
    private static final String TAG = "AdmobEvent";
    public static void logEvent(Context context, String nameEvent, Bundle params) {
        Timber.tag(TAG).e(nameEvent);
        FirebaseAnalytics.getInstance(context).logEvent(nameEvent, params);
    }
}
