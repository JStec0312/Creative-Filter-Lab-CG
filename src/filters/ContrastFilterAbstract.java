package filters;

import processing.core.PGraphics;

public class ContrastFilterAbstract extends AbstractImageFilter {
    @Override
    public String id() {
        return "ContrastFilter";
    }

    @Override
    public String displayName() {
        return "Contrast";
    }

    @Override
    public void apply(PGraphics target, float intensity) {
        int[] lut = new int[256];
        for (int i = 0; i < 256; i++) {
            int v = Math.round(128 + intensity * (i - 128));
            lut[i] = (v < 0) ? 0 : (v > 255 ? 255 : v);
        }
        target.beginDraw();
        target.loadPixels();

        for (int i = 0; i < target.pixels.length; i++) {
            int c = target.pixels[i];
            RGBA color = toRGBA(c);
            int nr = lut[color.r];
            int ng = lut[color.g];
            int nb = lut[color.b];
            RGBA newRgba = new RGBA(nr, ng, nb, color.a);
            target.pixels[i] = toColorInt(newRgba);
        }
        target.updatePixels();
        target.endDraw();
    }
}
