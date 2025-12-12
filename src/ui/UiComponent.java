package ui;

import processing.core.PApplet;
import state.EditorState;

public abstract class UiComponent {
    protected final PApplet app;
    protected final EditorState state;
    protected final UiBounds bounds;

    protected UiComponent(PApplet app, EditorState state, int x, int y, int w, int h) {
        this.app = app;
        this.state = state;
        this.bounds = new UiBounds(x, y, w, h);
    }

    public void setPosition(int x, int y) { bounds.x = x; bounds.y = y; }
    public void setSize(int w, int h) { bounds.w = w; bounds.h = h; }
    public boolean hit(int mx, int my) { return bounds.contains(mx, my); }

    public abstract void render();
    public boolean mousePressed(int mx, int my) { return false; }
    public boolean mouseDragged(int mx, int my) { return false; }
}
