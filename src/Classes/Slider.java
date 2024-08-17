package Classes;

public class Slider extends Widget {
    private String sliderValue;
    private final String minValue;
    private final String maxValue;
    private String sliderUnit;

    public Slider(String text, String sliderValue, String sliderUnit, String minValue, String maxValue) {
        super(text);
        this.sliderValue = sliderValue;
        this.sliderUnit = sliderUnit;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }
    public String getSliderMinValue() {
        return minValue;
    }
    public String getSliderMaxValue() {
        return maxValue;
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
