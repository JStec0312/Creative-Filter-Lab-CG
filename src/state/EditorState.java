package state;

import enums.ToolType;

public class EditorState {
    public int canvasWidth;
    public int canvasHeight;
    public ToolType activeTool = ToolType.BRUSH;
    public float brushSize = 20;
    public float filterIntensity = 1.0f;
    public int activeLayerIndex = 0;
    public float offsetX;   // dodane
    public float offsetY;
    public int  activeColor = 0xff000000; // domyślny kolor czarny


}
