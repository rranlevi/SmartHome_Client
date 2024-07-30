package Classes;

public class Slider extends Widget {
    private String sliderValue;
    private String sliderUnit;

    public Slider(String text, String sliderValue, String sliderUnit) {
        super(text);
        this.sliderValue = sliderValue;
        this.sliderUnit = sliderUnit;
    }

    public String getSliderValue() {
        return sliderValue;
    }

    public void setSliderValue(String sliderValue) {
        this.sliderValue = sliderValue;
    }

    public String getSliderUnit() {
        return sliderUnit;
    }

    public void setSliderUnit(String sliderUnit) {
        this.sliderUnit = sliderUnit;
    }
}
