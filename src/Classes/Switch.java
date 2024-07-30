package Classes;

public class Switch extends Widget {
    private boolean switchIsOn;

    public Switch(String text, boolean switchIsOn){
        super(text);
        this.switchIsOn = switchIsOn;
    }

    public boolean isSwitchIsOn() {
        return switchIsOn;
    }

    public void setSwitchIsOn(boolean switchIsOn) {
        this.switchIsOn = switchIsOn;
    }
}