package filters;

import processing.core.PGraphics;

public class GrayScaleFilterAbstract extends AbstractImageFilter {

    @Override
    public String id() {
        return "GrayScale";
    }

    @Override
    public String displayName() {
        return "grayscale";
    }

    @Override
    public void apply(PGraphics target, float intensity) {
        target.beginDraw();
        target.loadPixels();
        for (int i = 0; i < target.pixels.length; i++) {
            int c = target.pixels[i];
            RGBA color = toRGBA(c);
            int gray = (int)(0.299 * color.r + 0.587 * color.g + 0.114 * color.b);
            RGBA newRgba = new RGBA(gray, gray, gray, color.a);
            target.pixels[i] = toColorInt(newRgba);
        }
        target.updatePixels();
        target.endDraw();
    }
}
