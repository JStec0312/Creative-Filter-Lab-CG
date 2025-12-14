package filters;

import processing.core.PGraphics;

public abstract class AbstractConvolutionalMaskFilterAbstract extends AbstractImageFilter {
    protected   int [] k; // 2D flattened
    protected float bias() { return 0f; }

    @Override
    public void apply(PGraphics target, float intensity) {


        intensity = intensity/2f;
        float t = constrain(intensity, 0f, 1f);

        int sum = 0;
        for (int v : k) sum += v;

        float scale = (sum != 0) ? (1f / sum) : 1f;
        float b = bias();

        target.beginDraw();
        target.loadPixels();

        final int w = target.width;
        final int h = target.height;

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

                int r0 = (c11 >>> 16) & 0xFF;
                int g0 = (c11 >>>  8) & 0xFF;
                int b0 =  c11         & 0xFF;

                float r =
                        k[0] * ((c00 >>> 16) & 0xFF) + k[1] * ((c01 >>> 16) & 0xFF) + k[2] * ((c02 >>> 16) & 0xFF) +
                                k[3] * ((c10 >>> 16) & 0xFF) + k[4] * ((c11 >>> 16) & 0xFF) + k[5] * ((c12 >>> 16) & 0xFF) +
                                k[6] * ((c20 >>> 16) & 0xFF) + k[7] * ((c21 >>> 16) & 0xFF) + k[8] * ((c22 >>> 16) & 0xFF);

                float g =
                        k[0] * ((c00 >>>  8) & 0xFF) + k[1] * ((c01 >>>  8) & 0xFF) + k[2] * ((c02 >>>  8) & 0xFF) +
                                k[3] * ((c10 >>>  8) & 0xFF) + k[4] * ((c11 >>>  8) & 0xFF) + k[5] * ((c12 >>>  8) & 0xFF) +
                                k[6] * ((c20 >>>  8) & 0xFF) + k[7] * ((c21 >>>  8) & 0xFF) + k[8] * ((c22 >>>  8) & 0xFF);

                float bl =
                        k[0] * (c00 & 0xFF) + k[1] * (c01 & 0xFF) + k[2] * (c02 & 0xFF) +
                                k[3] * (c10 & 0xFF) + k[4] * (c11 & 0xFF) + k[5] * (c12 & 0xFF) +
                                k[6] * (c20 & 0xFF) + k[7] * (c21 & 0xFF) + k[8] * (c22 & 0xFF);

                int rf = clamp(Math.round(r * scale + b), 0, 255);
                int gf = clamp(Math.round(g * scale + b), 0, 255);
                int bf = clamp(Math.round(bl * scale + b), 0, 255);

                //  wynik=oryginal + t*(filtered - oryginal)
                int nr = clamp(Math.round(r0 + (rf - r0) * t), 0, 255);
                int ng = clamp(Math.round(g0 + (gf - g0) * t), 0, 255);
                int nb = clamp(Math.round(b0 + (bf - b0) * t), 0, 255);
                dst[y1 * w + x1] = (a << 24) | (nr << 16) | (ng << 8) | nb;
            }
        }

        target.updatePixels();
        target.endDraw();
    }

}
