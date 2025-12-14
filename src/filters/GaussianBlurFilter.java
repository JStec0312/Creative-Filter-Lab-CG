package filters;

public class GaussianBlurFilter extends AbstractConvolutionalMaskFilterAbstract {
    public GaussianBlurFilter() {
        k = new int[]{
                1, 2, 1,
                2, 4, 2,
                1, 2, 1
        };
    }

    @Override
    public String id() {
        return "GaussianBlur";
    }

    @Override
    public String displayName() {
        return "Gaussian Blur";
    }

}
