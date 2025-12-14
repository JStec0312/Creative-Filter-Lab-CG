package filters;

import processing.core.PGraphics;

//mask
// [-1, -1, -1]
// [-1,  9, -1]
// [-1, -1, -1]


public class EdgeSharpeningFilterAbstract extends AbstractImageFilter {

    @Override
    public String id() {
        return "EdgeSharpening";
    }

    @Override
    public String displayName() {
        return "Edge Sharpening";
    }
    @Override
    public void apply(PGraphics target, float intensity) {
        target.beginDraw();
        target.loadPixels();

        int w = target.width;
        int h = target.height;

        int[] src = target.pixels.clone();
        int[] dst = target.pixels;

        for (int y = 0; y < h; y++) {
            int y0 = clamp(y - 1, 0, h - 1);
            int y1 = y;
            int y2 = clamp(y + 1, 0, h - 1);

            for (int x = 0; x < w; x++) {
                int x0 = clamp(x - 1, 0, w - 1);
                int x1 = x;
                int x2 = clamp(x + 1, 0, w - 1);

                int c00 = src[y0 * w + x0];
                int c01 = src[y0 * w + x1];
                int c02 = src[y0 * w + x2];

                int c10 = src[y1 * w + x0];
                int c11 = src[y1 * w + x1];
                int c12 = src[y1 * w + x2];

                int c20 = src[y2 * w + x0];
                int c21 = src[y2 * w + x1];
                int c22 = src[y2 * w + x2];

                int a = (c11 >>> 24) & 0xFF;

                int r11 = (c11 >>> 16) & 0xFF;
                int g11 = (c11 >>>  8) & 0xFF;
                int b11 =  c11         & 0xFF;

                int sumR =
                        ((c00 >>> 16) & 0xFF) + ((c01 >>> 16) & 0xFF) + ((c02 >>> 16) & 0xFF) +
                                ((c10 >>> 16) & 0xFF) +                         ((c12 >>> 16) & 0xFF) +
                                ((c20 >>> 16) & 0xFF) + ((c21 >>> 16) & 0xFF) + ((c22 >>> 16) & 0xFF);

                int sumG =
                        ((c00 >>>  8) & 0xFF) + ((c01 >>>  8) & 0xFF) + ((c02 >>>  8) & 0xFF) +
                                ((c10 >>>  8) & 0xFF) +                         ((c12 >>>  8) & 0xFF) +
                                ((c20 >>>  8) & 0xFF) + ((c21 >>>  8) & 0xFF) + ((c22 >>>  8) & 0xFF);

                int sumB =
                        (c00 & 0xFF) + (c01 & 0xFF) + (c02 & 0xFF) +
                                (c10 & 0xFF) +               (c12 & 0xFF) +
                                (c20 & 0xFF) + (c21 & 0xFF) + (c22 & 0xFF);

                int nr = clamp(9 * r11 - sumR, 0, 255);
                int ng = clamp(9 * g11 - sumG, 0, 255);
                int nb = clamp(9 * b11 - sumB, 0, 255);

                dst[y1 * w + x1] = (a << 24) | (nr << 16) | (ng << 8) | nb;
            }
        }

        target.updatePixels();
        target.endDraw();
    }

}
