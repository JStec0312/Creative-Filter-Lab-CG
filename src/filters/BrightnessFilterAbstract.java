package filters;

import processing.core.PGraphics;



public class BrightnessFilterAbstract extends AbstractImageFilter {
    @Override
    public String id() {
        return "brightness";
    }

    @Override
    public String displayName() {
        return "brightness";
    }

    @Override
    public void apply(PGraphics target, float intensity) {
        target.beginDraw();
        target.loadPixels();
        for (int i = 0; i < target.pixels.length; i++) {
            int c = target.pixels[i];
            RGBA color = toRGBA(c);
            int delta = Math.round((intensity - 1.0f) * 255f);
            int nr = Math.min(255, Math.max(0, color.r + delta));
            int ng = Math.min(255, Math.max(0, color.g + delta));
            int nb = Math.min(255, Math.max(0, color.b + delta));
            RGBA newRgba = new RGBA(nr, ng, nb, color.a);
            target.pixels[i] = toColorInt(newRgba);
        }
        target.updatePixels();
        target.endDraw();
    }
}

