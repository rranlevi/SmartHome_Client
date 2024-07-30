package Classes;

public class Info {
    private String infoName;
    private String infoValue;
    private String unit;

    public Info(String infoName, String infoValue, String unit) {
        this.infoName = infoName;
        this.infoValue = infoValue;
        this.unit = unit;
    }

    public String getInfoName() {
        return infoName;
    }

    public void setInfoName(String infoName) {
        this.infoName = infoName;
    }

    public String getInfoValue() {
        return infoValue;
    }

    public void setInfoValue(String infoValue) {
        this.infoValue = infoValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}