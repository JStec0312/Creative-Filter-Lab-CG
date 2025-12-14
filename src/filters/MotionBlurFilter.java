package filters;

public class MotionBlurFilter extends AbstractConvolutionalMaskFilterAbstract {
    public MotionBlurFilter() {
        k = new int[]{
                2, 2, 2,
                0, 0, 0,
                0, 0, 0
        };
    }

    @Override
    public String id() {
        return "MotionBlur";
    }

    @Override
    public String displayName() {
        return "Motion Blur";
    }


}
