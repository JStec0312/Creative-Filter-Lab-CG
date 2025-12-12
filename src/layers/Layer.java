package layers;

import enums.BlendMode;
import processing.core.PGraphics;
public class Layer {
    public final String name;
    public final PGraphics pg;
    public boolean visible = true;
    public float opacity = 1.0f;
    public BlendMode blendMode = BlendMode.NORMAL;
    public Layer(String name, PGraphics graphics) {
        this.name = name;
        this.pg = graphics;
    }

}
