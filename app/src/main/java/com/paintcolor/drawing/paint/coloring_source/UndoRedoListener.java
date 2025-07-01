package com.paintcolor.drawing.paint.coloring_source;

public interface UndoRedoListener {
    void onStackChanged(boolean canUndo, boolean canRedo);
}