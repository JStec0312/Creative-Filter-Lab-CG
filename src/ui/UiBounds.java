package ui;

public class UiBounds {
    public int x, y, w, h;

    public UiBounds(int x, int y, int w, int h) {
        set(x, y, w, h);
    }

    public void set(int x, int y, int w, int h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
    }

    public boolean contains(int mx, int my) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}
