package filters;

import processing.core.PGraphics;

public class InvertFilterAbstract extends AbstractImageFilter {
    @Override
    public String id() {
        return "invert";
    }

    @Override
    public String displayName() {
        return "Invert";
    }

    @Override
    public void apply(PGraphics target, float intensity) {
        target.beginDraw();
        target.loadPixels();
        for (int i = 0; i < target.pixels.length; i++) {
            int c = target.pixels[i];
            RGBA color = toRGBA(c);
            int nr = 255 - color.r;
            int ng = 255 - color.g;
            int nb = 255 - color.b;
            RGBA newRgba = new RGBA(nr, ng, nb, color.a);
            target.pixels[i] = toColorInt(newRgba);
        }

        target.updatePixels();
        target.endDraw();

    }
}
