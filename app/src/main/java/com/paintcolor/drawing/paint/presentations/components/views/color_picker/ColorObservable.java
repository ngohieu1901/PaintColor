package com.paintcolor.drawing.paint.presentations.components.views.color_picker;

import com.paintcolor.drawing.paint.presentations.components.views.color_picker.ColorObserver;

public interface ColorObservable {

    void subscribe(com.paintcolor.drawing.paint.presentations.components.views.color_picker.ColorObserver observer);

    void unsubscribe(ColorObserver observer);

    int getColor();
}
