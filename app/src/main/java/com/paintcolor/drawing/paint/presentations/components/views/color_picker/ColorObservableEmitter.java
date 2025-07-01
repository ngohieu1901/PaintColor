package com.paintcolor.drawing.paint.presentations.components.views.color_picker;

import com.paintcolor.drawing.paint.presentations.components.views.color_picker.ColorObservable;
import com.paintcolor.drawing.paint.presentations.components.views.color_picker.ColorObserver;

import java.util.ArrayList;
import java.util.List;

class ColorObservableEmitter implements ColorObservable {

    private List<com.paintcolor.drawing.paint.presentations.components.views.color_picker.ColorObserver> observers = new ArrayList<>();
    private int color;

    @Override
    public void subscribe(com.paintcolor.drawing.paint.presentations.components.views.color_picker.ColorObserver observer) {
        if (observer == null) return;
        observers.add(observer);
    }

    @Override
    public void unsubscribe(com.paintcolor.drawing.paint.presentations.components.views.color_picker.ColorObserver observer) {
        if (observer == null) return;
        observers.remove(observer);
    }

    @Override
    public int getColor() {
        return color;
    }

    void onColor(int color, boolean fromUser, boolean shouldPropagate) {
        this.color = color;
        for (ColorObserver observer : observers) {
            observer.onColor(color, fromUser, shouldPropagate);
        }
    }

}
