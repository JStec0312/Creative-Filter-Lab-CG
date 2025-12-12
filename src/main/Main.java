package main;

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

        loadDebugImage();
    }

    @Override
    public void draw() {
        background(30);
        layoutUi();
        layerManager.render();
        hud.render();
        layerPanel.render();
        colorPicker.render();
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
    }

    public void fileSelected(File file) {
        if (file == null) {
            System.out.println("File not selected");
            return;
        }

        PImage image = loadImage(file.getAbsolutePath());
        if (image == null) {
            System.out.println("Failed to load image");
            return;
        }

        layerManager.initFromImage(image);
        editorState.activeLayerIndex = Math.min(1, layerManager.getLayers().size() - 1);
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
}
