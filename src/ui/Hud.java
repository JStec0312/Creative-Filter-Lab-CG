package ui;

import layers.Layer;
import layers.LayerManager;
import processing.core.PApplet;
import state.EditorState;

import java.util.List;

public class Hud {

    private final PApplet app;
    private final EditorState state;
    private final LayerManager layerManager;

    public Hud(PApplet app, EditorState state, LayerManager layerManager) {
        this.app = app;
        this.state = state;
        this.layerManager = layerManager;
    }

    public void render() {
        app.pushStyle();
        app.noStroke();
        app.fill(0, 180);
        app.rect(0, 0, app.width, 70);

        app.fill(255);
        app.textAlign(PApplet.LEFT, PApplet.TOP);
        app.textSize(12);

        String toolInfo = "Tool: " + state.activeTool +
                " | Brush: " + state.brushSize +
                " | Filter intensity: " + String.format("%.2f", state.filterIntensity);
        app.text(toolInfo, 10, 10);

        List<Layer> layers = layerManager.getLayers();
        if (!layers.isEmpty()) {
            StringBuilder sb = new StringBuilder("Layers: ");
            for (int i = 0; i < layers.size(); i++) {
                Layer l = layers.get(i);
                if (i == state.activeLayerIndex) sb.append("[");
                sb.append(i)
                        .append(":")
                        .append(l.name)
                        .append(" ")
                        .append(l.blendMode)
                        .append(" ")
                        .append((int)(l.opacity * 100))
                        .append("%");
                if (i == state.activeLayerIndex) sb.append("]");
                if (i < layers.size() - 1) sb.append(" | ");
            }
            app.text(sb.toString(), 10, 30);
        }

        app.fill(0, 180);
        app.rect(0, app.height - 40, app.width, 40);
        app.fill(220);
        app.textAlign(PApplet.LEFT, PApplet.TOP);

        String shortcuts = "O - open image   S - save   B - brush   E - eraser   1..9 - filters   +/- - params   U/R - undo/redo h/H - hide current layer Z/X - decrease/increase layer opacity L/l - copy active layer DELETE - delete current layer";
        app.text(shortcuts, 10, app.height - 30);

        app.popStyle();
    }
}
