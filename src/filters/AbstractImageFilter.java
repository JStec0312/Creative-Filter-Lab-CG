package filters;

import processing.core.PGraphics;

public abstract class AbstractImageFilter {
    public abstract String id();
    public abstract String displayName();

    public abstract void apply(PGraphics target, float intensity);


    protected RGBA toRGBA(int c){
        int a = (c >>> 24) & 0xFF;
        int r = (c >>> 16) & 0xFF;
        int g = (c >>> 8) & 0xFF;
        int b = c & 0xFF;
        return new RGBA(r,g,b,a);
    }

    protected int toColorInt(RGBA color){
        return ((color.a << 24) | (color.r << 16) | (color.g << 8) | color.b);
    }
    protected int clamp(int v, int lo, int hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }
    protected float constrain(float v, float lo, float hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }
}
