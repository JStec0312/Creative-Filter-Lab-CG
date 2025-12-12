package ui;

import layers.Layer;
import layers.LayerManager;
import processing.core.PApplet;
import state.EditorState;

import java.util.List;

public class LayerPanel extends UiComponent {
    private final LayerManager layerManager;

    private static final int MARGIN = 20;
    private static final int PADDING = 10;
    private static final int HEADER_H = 34;
    private static final int ROW_H = 48;
    private static final int CORNER = 10;

    private static final int CHECKBOX = 16;
    private static final int CHECKBOX_LEFT = 10;
    private static final int CHECKBOX_GAP = 10;

    public LayerPanel(PApplet app, EditorState state, LayerManager layerManager, int w, int h) {
        super(app, state, 0, 0, w, h);
        this.layerManager = layerManager;
    }

    public void layoutRight(int screenW, int screenH) {
        bounds.x = screenW - bounds.w - MARGIN;
        bounds.y = MARGIN;
        bounds.h = Math.min(bounds.h, screenH - 2 * MARGIN);
    }

    private int listTop() { return bounds.y + HEADER_H + PADDING; }
    private int listX() { return bounds.x + PADDING; }
    private int listW() { return bounds.w - 2 * PADDING; }

    private int rowIndexAt(int my) {
        int top = listTop();
        if (my < top) return -1;
        return (my - top) / ROW_H;
    }

    @Override
    public void render() {
        List<Layer> layers = layerManager.getLayers();
        if (!layers.isEmpty() && state.activeLayerIndex >= layers.size()) {
            state.activeLayerIndex = layers.size() - 1;
        }
        app.noStroke();
        app.fill(40);
        app.rect(bounds.x, bounds.y, bounds.w, bounds.h, CORNER);

        // header
        app.fill(55);
        app.rect(bounds.x, bounds.y, bounds.w, HEADER_H, CORNER);

        app.stroke(25);
        app.line(bounds.x, bounds.y + HEADER_H, bounds.x + bounds.w, bounds.y + HEADER_H);
        app.noStroke();

        app.fill(235);
        app.textAlign(PApplet.LEFT, PApplet.CENTER);
        app.textSize(14);
        app.text("Layers", bounds.x + PADDING, bounds.y + HEADER_H / 2f);

        int rx = listX();
        int rw = listW();
        int top = listTop();

        for (int i = 0; i < layers.size(); i++) {
            int ry = top + i * ROW_H;
            if (ry + ROW_H > bounds.y + bounds.h - PADDING) break;

            drawRow(layers.get(i), i, rx, ry, rw, ROW_H, i == state.activeLayerIndex);
        }
    }

    private void drawRow(Layer layer, int index, int rx, int ry, int rw, int rh, boolean active) {
        app.noStroke();
        app.fill(active ? 75 : 50);
        app.rect(rx, ry, rw, rh, 8);

        int cx = rx + CHECKBOX_LEFT;
        int cy = ry + (rh - CHECKBOX) / 2;

        app.stroke(210);
        app.noFill();
        app.rect(cx, cy, CHECKBOX, CHECKBOX, 3);

        if (layer.visible) {
            app.noStroke();
            app.fill(210);
            app.rect(cx + 3, cy + 3, CHECKBOX - 6, CHECKBOX - 6, 2);
        }

        app.noStroke();
        app.fill(index == 0 ? 170 : 235);
        app.textAlign(PApplet.LEFT, PApplet.CENTER);
        app.textSize(12);

        String name = index == 0 ? layer.name + " (base)" : layer.name;
        app.text(name, cx + CHECKBOX + CHECKBOX_GAP, ry + rh / 2f);
    }

    @Override
    public boolean mousePressed(int mx, int my) {
        if (!hit(mx, my)) return false;

        List<Layer> layers = layerManager.getLayers();
        int idx = rowIndexAt(my);
        if (idx < 0 || idx >= layers.size()) return true;
        if (idx == 0) {
            state.activeLayerIndex = 0;
            return true;
        }

        int rx = listX();
        int checkboxRightEdge = rx + CHECKBOX_LEFT + CHECKBOX; // klik w checkbox
        if (mx <= checkboxRightEdge + CHECKBOX_GAP) {
            layerManager.toggleLayerVisibility(idx);
        } else {
            state.activeLayerIndex = idx;
        }
        return true;
    }
    public int left() { return bounds.x; }
    public int top() { return bounds.y; }
    public int widthPx() { return bounds.w; }
    public int heightPx() { return bounds.h; }
}
