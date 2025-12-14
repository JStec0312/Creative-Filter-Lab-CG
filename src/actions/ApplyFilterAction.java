package actions;

import layers.Layer;
import layers.LayerManager;
import processing.core.PImage;

public class ApplyFilterAction implements UndoableAction {
    private final LayerManager layerManager;
    private final int layerIndex;
    private final PImage before;
    private final PImage after;

    public ApplyFilterAction(LayerManager layerManager, int layerIndex, PImage before, PImage after) {
        this.layerManager = layerManager;
        this.layerIndex = layerIndex;
        this.before = before;
        this.after = after;
    }

    @Override
    public void undo() {
        paste(before);
    }

    @Override
    public void redo() {
        paste(after);
    }

    private void paste(PImage img) {
        Layer layer = layerManager.getLayer(layerIndex);
        layer.pg.beginDraw();
        layer.pg.image(img, 0, 0);
        layer.pg.endDraw();
    }
}