package main;

import filters.*;
import layers.LayerManager;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import state.EditorState;
import ui.ColorPicker;
import ui.Hud;
import ui.LayerPanel;

import java.io.File;

public class Main extends PApplet {

    // UI/layout constants
    private static final int UI_MARGIN = 20;
    private static final int HUD_H = 70;

    private static final int PICKER_W = 200;
    private static final int PICKER_H = 150;

    private static final int LAYER_PANEL_W = 220;
    private static final int LAYER_PANEL_H = 700;

    private EditorState editorState;
    private LayerManager layerManager;

    private Hud hud;
    private LayerPanel layerPanel;
    private ColorPicker colorPicker;
    private PImage beforeFull = null;
    private String pendingImagePath = null;
    private String pendingSavePath = null;
    public static void main(String[] args) {
        PApplet.main("main.Main");
    }

    @Override
    public void settings() {
        fullScreen(PConstants.P2D);
        pixelDensity(displayDensity());
        smooth(4);
    }

    @Override
    public void setup() {
        editorState = new EditorState();
        layerManager = new LayerManager(this, editorState);
        hud = new Hud(this, editorState, layerManager);
        layerPanel = new LayerPanel(this, editorState, layerManager, LAYER_PANEL_W, LAYER_PANEL_H);
        colorPicker = new ColorPicker(this, editorState, 0, 0, PICKER_W, PICKER_H);

        surface.setTitle("Creative Filter Lab");

        //loadDebugImage();
    }

    @Override
    public void draw() {
        if (pendingImagePath != null) {
            String path = pendingImagePath;
            pendingImagePath = null;

            PImage img = loadImage(path);
            if (img == null) {
                System.out.println("Failed to load image: " + path);
            } else {
                img = clampToLimits(img);
                layerManager.initFromImage(img);
                layerManager.clearHistory();
                editorState.activeLayerIndex = Math.min(1, layerManager.getLayers().size() - 1);
            }
        }

        if (pendingSavePath !=null){
            String path = pendingSavePath;
            PImage out = layerManager.flattenImage();
            if(out!=null) out.save(path);
        }

        background(30);
        layoutUi();
        layerManager.render();
        hud.render();
        layerPanel.render();
        colorPicker.render();

        if (keyPressed && key == '-') editorState.filterIntensity = max(0.0f, editorState.filterIntensity - 0.01f);
        if (keyPressed && key == '=') editorState.filterIntensity = min(2.0f, editorState.filterIntensity + 0.01f);
    }


    private void layoutUi() {
        layerPanel.layoutRight(width, height);
        int pickerX = width - PICKER_W - UI_MARGIN;
        int pickerY = layerPanel.top() + layerPanel.heightPx() + UI_MARGIN;


        colorPicker.setPosition(pickerX, pickerY);
    }

    @Override
    public void mousePressed() {
        if (layerPanel != null && layerPanel.mousePressed(mouseX, mouseY)) return;
        if (colorPicker != null && colorPicker.mousePressed(mouseX, mouseY)) return;
        if (layerManager != null) {
            layerManager.handleBrushStroke(mouseX, mouseY, mouseX, mouseY);
        }
    }

    @Override
    public void mouseDragged() {
        if (layerPanel != null && layerPanel.hit(mouseX, mouseY)) return;
        if (colorPicker != null && colorPicker.hit(mouseX, mouseY)) return;

        if (layerManager != null) {
            layerManager.handleBrushStroke(mouseX, mouseY, pmouseX, pmouseY);
        }
    }

    @Override
    public void keyPressed() {
        if (key == 'o' || key == 'O') {
            selectInput("Select an image to open:", "fileSelected");
            return;
        }

        if (key == 'b' || key == 'B') editorState.activeTool = enums.ToolType.BRUSH;
        if (key == 'e' || key == 'E') editorState.activeTool = enums.ToolType.ERASER;

        if (key == '[') editorState.brushSize = max(1, editorState.brushSize - 5);
        if (key == ']') editorState.brushSize += 5;

        if (key == 'h' || key == 'H') {
            layerManager.toggleLayerVisibility(editorState.activeLayerIndex);
        }
        if (key == '1') {

            layerManager.applyFilterWithUndo(
                    editorState.activeLayerIndex,
                    new InvertFilterAbstract(),
                    editorState.filterIntensity
            );
        }
        if (key == '2') {
            layerManager.applyFilterWithUndo(
                    editorState.activeLayerIndex,
                    new GrayScaleFilterAbstract(),
                    editorState.filterIntensity
            );
        }
        if (key == '3') {
            layerManager.applyFilterWithUndo(
                    editorState.activeLayerIndex,
                    new BrightnessFilterAbstract(),
                    editorState.filterIntensity
            );
        }
        if (key == '4') {
            layerManager.applyFilterWithUndo(
                    editorState.activeLayerIndex,
                    new ContrastFilterAbstract(),
                    editorState.filterIntensity
            );
        }
        if (key == '5') {
            layerManager.applyFilterWithUndo(
                    editorState.activeLayerIndex,
                    new EdgeSharpeningFilterAbstract(),
                    editorState.filterIntensity
            );
        }
        if (key == '6') {
            layerManager.applyFilterWithUndo(
                    editorState.activeLayerIndex,
                    new GaussianBlurFilter(),
                    editorState.filterIntensity
            );
        }
        if (key == '7') {
            layerManager.applyFilterWithUndo(
                    editorState.activeLayerIndex,
                    new MotionBlurFilter(),
                    editorState.filterIntensity
            );
        }
        if (key == '8') {
            layerManager.applyFilterWithUndo(
                    editorState.activeLayerIndex,
                    new MosaicFilter(),
                    editorState.filterIntensity
            );
        }
        if (key == 'u' || key == 'U') { layerManager.undo(); return; }
        if ( key == 'r' || key == 'R') { layerManager.redo(); return; }
        if (key == 'z' || key == 'Z'){
            if (layerManager==null) return;
            layerManager.changeLayerOpacity(editorState.activeLayerIndex, -0.1f);
        }
        if (key == 'x' || key == 'X') {
            if (layerManager == null) return;
            layerManager.changeLayerOpacity(editorState.activeLayerIndex, 0.1f);
        }
        if (key == 'l' || key == 'L') {
            layerManager.copyLayer(editorState.activeLayerIndex);
        }
        if (key == DELETE){
            layerManager.deleteLayer(editorState.activeLayerIndex);
        }
        if (key == 's' || key == 'S') {
            if (layerManager == null) return;
            selectOutput("Save image as:", "fileSaveSelected");
            return;
        }
    }

    public void fileSaveSelected(File file) {
        if (file == null) {
            return;
        }
        String path = file.getAbsolutePath();
        if (!path.toLowerCase().endsWith(".png") && !path.toLowerCase().endsWith(".jpg")) {
            path += ".png";
        }
        pendingSavePath = path;
    }

    public void fileSelected(File file) {
        if (file == null) {
            System.out.println("File not selected");
            return;
        }

        pendingImagePath = file.getAbsolutePath();
    }

    private void loadDebugImage() {
        PImage image = loadImage(sketchPath("images/lenac.bmp"));
        if (image == null) {
            System.out.println("Debug image not found: " + sketchPath("images/lenac.bmp"));
            return;
        }
        layerManager.initFromImage(image);
        editorState.activeLayerIndex = Math.min(1, layerManager.getLayers().size() - 1);
    }
    private PImage clampToLimits(PImage img) {
        final int MAX_DIM = 1000;

        int w = img.width;
        int h = img.height;
        if (w <= MAX_DIM && h <= MAX_DIM) return img;
        float scale = Math.min((float) MAX_DIM / w, (float) MAX_DIM / h);
        int nw = Math.max(1, Math.round(w * scale));
        int nh = Math.max(1, Math.round(h * scale));
        img.resize(nw, nh);
        return img;
    }
}
