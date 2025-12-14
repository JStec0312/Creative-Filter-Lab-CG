package filters;


import processing.core.PGraphics;

public class MosaicFilter extends AbstractImageFilter {
    private  int blockSize = 8;

    @Override
    public String id() {
        return "Mosaic";
    }

    @Override
    public String displayName() {
        return "Mosaic";
    }

    @Override
    public void apply(PGraphics target, float intensity) {
        target.beginDraw();
        target.loadPixels();
        intensity = intensity/2f;
        blockSize = Math.max(1, Math.round(1 + intensity * (blockSize-1)));
        final int w = target.width;
        final int h = target.height;
        int[] src= target.pixels.clone();
        int[] dst= target.pixels;
        for(int by=0; by<h; by+=blockSize){
            int yEnd = Math.min(by+blockSize, h);
            for (int bx =0; bx<w; bx+=blockSize){
                int xEnd = Math.min(bx+blockSize, w);
                long sumR=0, sumG=0, sumB=0;
                int count=0;
                // First pass: calculate average color
                for (int y = by; y<yEnd; y++){
                    for (int x=bx; x<xEnd; x++){
                        int c = src[y * w + x];
                        sumR += (c >>> 16) & 0xFF;
                        sumG += (c >>> 8) & 0xFF;
                        sumB += c & 0xFF;
                        count++;
                    }
                }
                int r = (int) (sumR/count) & 0xFF;
                int g = (int) (sumG/count) & 0xFF;
                int b = (int) (sumB/count) & 0xFF;
                int avg = (0xFF << 24) | (r << 16) | (g << 8) | b;
                for (int y = by; y < yEnd; y++) {
                    int row = y * w;
                    for (int x = bx; x < xEnd; x++) {
                        dst[row + x] = avg;
                    }
                }

            }
        }
        target.updatePixels();
        target.endDraw();
    }
}
