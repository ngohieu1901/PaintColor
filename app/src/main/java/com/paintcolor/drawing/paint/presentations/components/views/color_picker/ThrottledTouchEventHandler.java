package com.paintcolor.drawing.paint.presentations.components.views.color_picker;

import android.view.MotionEvent;

import com.paintcolor.drawing.paint.presentations.components.views.color_picker.Constants;
import com.paintcolor.drawing.paint.presentations.components.views.color_picker.Updatable;

class ThrottledTouchEventHandler {

    private final int minInterval;
    private final com.paintcolor.drawing.paint.presentations.components.views.color_picker.Updatable updatable;
    private long lastPassedEventTime = 0;

    ThrottledTouchEventHandler(com.paintcolor.drawing.paint.presentations.components.views.color_picker.Updatable updatable) {
        this(com.paintcolor.drawing.paint.presentations.components.views.color_picker.Constants.EVENT_MIN_INTERVAL, updatable);
    }

    private ThrottledTouchEventHandler(int minInterval, Updatable updatable) {
        this.minInterval = minInterval;
        this.updatable = updatable;
    }

    void onTouchEvent(MotionEvent event) {
        if (updatable == null) return;
        long current = System.currentTimeMillis();
        if (current - lastPassedEventTime <= minInterval) {
            return;
        }
        lastPassedEventTime = current;
        updatable.update(event);
    }
}
