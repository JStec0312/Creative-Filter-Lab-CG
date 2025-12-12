package ui;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import state.EditorState;

public class ColorPicker extends UiComponent {
    private PGraphics paletteImage;

    public ColorPicker(PApplet app, EditorState state, int x, int y, int w, int h) {
        super(app, state, x, y, w, h);
        generatePalette();
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        generatePalette();
    }

    private void generatePalette() {
        paletteImage = app.createGraphics(bounds.w, bounds.h);
        paletteImage.beginDraw();
        paletteImage.colorMode(PConstants.HSB, bounds.w, bounds.h, bounds.h);

        for (int i = 0; i < bounds.w; i++) {
            for (int j = 0; j < bounds.h; j++) {
                paletteImage.stroke(i, bounds.h, bounds.h - j);
                paletteImage.point(i, j);
            }
        }
        paletteImage.endDraw();
    }

    @Override
    public void render() {
        app.pushStyle();

        // bg
        app.noStroke();
        app.fill(50);
        app.rect(bounds.x - 5, bounds.y - 5, bounds.w + 10, bounds.h + 10 + 30, 10);

        // palette
        app.image(paletteImage, bounds.x, bounds.y);

        // preview
        app.stroke(255);
        app.fill(state.activeColor);
        app.rect(bounds.x, bounds.y + bounds.h + 5, bounds.w, 20, 6);

        app.popStyle();
    }

    @Override
    public boolean mousePressed(int mx, int my) {
        if (!hit(mx, my)) return false;

        int localX = mx - bounds.x;
        int localY = my - bounds.y;
        int c = paletteImage.get(localX, localY);
        state.activeColor = c | 0xFF000000;
        return true;
    }
}
