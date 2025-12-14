package layers;

import actions.ApplyFilterAction;
import actions.UndoManager;
import enums.BlendMode;
import filters.AbstractImageFilter;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import state.EditorState;

import java.util.ArrayList;
import java.util.List;

public class LayerManager {
    private final PApplet app;
    private final EditorState editorState;
    private final List<Layer> layers = new ArrayList<>();
    private final UndoManager undoManager = new UndoManager();
    public LayerManager(PApplet app, EditorState editorState) {
        this.app = app;
        this.editorState = editorState;
    }
    public void initFromImage(PImage image){
        editorState.canvasWidth = image.width;
        editorState.canvasHeight = image.height;
        PGraphics base = app.createGraphics(image.width, image.height, PConstants.P2D);
        base.beginDraw();
        base.clear();
        base.image(image, 0, 0);
        base.endDraw();
        Layer baseLayer = new Layer("Base", base);
        PGraphics paint = app.createGraphics(image.width, image.height, PConstants.P2D);
        paint.beginDraw();
        paint.clear();
        paint.endDraw();
        Layer paintLayer = new Layer("Paint", paint);
        paintLayer.blendMode = enums.BlendMode.NORMAL;
        layers.clear();
        layers.add(baseLayer);
        layers.add(paintLayer);
    }
    public List<Layer> getLayers() {
        return layers;
    }
    public Layer getActiveLayer() {
        int idx = editorState.activeLayerIndex;
        if (idx < 0 || idx >= layers.size()) {
            return layers.get(0);
        }
        return layers.get(idx);
    }
    public void render(){
        float offsetX = (app.width  - editorState.canvasWidth)  / 2.0f;
        float offsetY = (app.height - editorState.canvasHeight) / 2.0f;
        editorState.offsetX = offsetX;
        editorState.offsetY = offsetY;

        if (layers.isEmpty()) return;
        app.pushMatrix();
        app.translate(offsetX, offsetY); // Przesuwamy cały świat
        app.noFill();
        app.stroke(50);
        app.rect(-1, -1, editorState.canvasWidth+2, editorState.canvasHeight+2);
        Layer base = layers.get(0);
        app.image(base.pg, 0,0 );
        for(int i = 1; i<layers.size(); i++){
            Layer layer = layers.get(i);
            if (!layer.visible) continue;
            app.pushStyle();
            applyBlendMode(layer.blendMode);
            app.tint(255, layer.opacity * 255);
            app.image(layer.pg, 0, 0);
            app.popStyle();
            app.blendMode(PConstants.BLEND);
        }
        app.popMatrix();
    }
    private void applyBlendMode(BlendMode mode){
        switch (mode){
            case ADD -> app.blendMode(PConstants.ADD);
            case MULTIPLY -> app.blendMode(PConstants.MULTIPLY);
            case SCREEN -> app.blendMode(PConstants.SCREEN);
            default -> app.blendMode(PConstants.BLEND);
        }
    }

    public void handleBrushStroke(int screenX, int screenY, int prevScreenX, int prevScreenY) {
        if (layers.isEmpty()) return;

        Layer active = getActiveLayer();
        if (active.visible == false){
            return;
        }
        if (active == layers.get(0)) {
            System.out.println("Cannot paint on Base layer!");
            return;
        }
        float x = screenX - editorState.offsetX;
        float y = screenY - editorState.offsetY;
        float px = prevScreenX - editorState.offsetX;
        float py = prevScreenY - editorState.offsetY;

        PGraphics pg = active.pg;
        pg.beginDraw();

        if (editorState.activeTool == enums.ToolType.BRUSH) {
            pg.stroke(editorState.activeColor);
            pg.strokeWeight(editorState.brushSize);
            pg.strokeCap(PConstants.ROUND);
            pg.line(px, py, x, y);

        } else if (editorState.activeTool == enums.ToolType.ERASER) {
            pg.blendMode(PConstants.REPLACE);
            pg.noStroke();
            pg.fill(0, 0);
            pg.circle(x, y, editorState.brushSize);
            pg.blendMode(PConstants.BLEND);
        }

        pg.endDraw();
    }
    public void toggleLayerVisibility(int index){
        if (index < 0 || index >= layers.size()) return;
        Layer layer = layers.get(index);
        layer.visible = !layer.visible;
    }

    public int getLayersCount(){
        return layers.size();
    }

    public void clampActiveLayerIndex() {
        int n = layers.size();
        if (n == 0) {
            editorState.activeLayerIndex = -1;
            return;
        }
        editorState.activeLayerIndex = Math.max(0, Math.min(editorState.activeLayerIndex, n - 1));
    }
    public void deleteLayer(int idx) {
        if (idx < 0 || idx >= layers.size()) return;

        layers.remove(idx);
        clampActiveLayerIndex();
    }
    public Layer getLayer(int index) {
        if (index < 0 || index >= layers.size()) return null;
        return layers.get(index);
    }
    public void applyFilterWithUndo(int layerIndex, AbstractImageFilter filter, float intensity){
        if (layers.isEmpty()) return;
        Layer layer = layers.get(layerIndex);
        PImage before = layer.pg.get();
        filter.apply(layer.pg, intensity);
        PImage after = layer.pg.get();
        undoManager.push(new ApplyFilterAction(this, layerIndex, before, after));
    }
    public void undo() { undoManager.undo(); }
    public void redo() { undoManager.redo(); }
    public void clearHistory() { undoManager.clear(); }
    public void copyLayer(int index){
        if (index < 0 || index >= layers.size()) return;
        Layer original = layers.get(index);
        PGraphics newPg = app.createGraphics(original.pg.width, original.pg.height, PConstants.P2D);
        newPg.beginDraw();
        newPg.image(original.pg, 0, 0);
        newPg.endDraw();
        Layer copy = new Layer(original.name + " Copy", newPg);
        copy.visible = original.visible;
        copy.opacity = original.opacity;
        copy.blendMode = original.blendMode;
        layers.add(index + 1, copy);
    }
    public float changeLayerOpacity(int layerIndex, float delta){
        Layer layer = layers.get(layerIndex);
        layer.opacity += delta;

        layer.opacity = Math.max(0, Math.min(1, layer.opacity + delta));
        return layer.opacity;
    }
    public PImage flattenImage(){
        if (layers.isEmpty()) return null;
        int w = editorState.canvasWidth;
        int h = editorState.canvasHeight;
        PGraphics finalImage = app.createGraphics(w, h, PConstants.P2D);
        finalImage.beginDraw();
        finalImage.clear();
        for (Layer layer: layers){
            if (!layer.visible) continue;
            finalImage.pushStyle();
            finalImage.blendMode(PConstants.BLEND);
            finalImage.tint(255, layer.opacity * 255);
            finalImage.image(layer.pg, 0,0);
            finalImage.noTint();
            finalImage.popStyle();
        }
        finalImage.endDraw();
        return finalImage.get();
    }



}
