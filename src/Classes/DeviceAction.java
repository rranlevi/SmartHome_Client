package Classes;

public class DeviceAction {
    private String name;
    private String description;
    private Widget widget;
    private boolean isAvailable;
    private DeviceChannel actionChannel;
    private DeviceChannel dataChannel;

    public DeviceAction(String name, String description, Widget widget, boolean isAvailable, DeviceChannel actionChannel, DeviceChannel dataChannel) {
        this.name = name;
        this.description = description;
        this.widget = widget;
        this.isAvailable = isAvailable;
        this.actionChannel = actionChannel;
        this.dataChannel = dataChannel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public DeviceChannel getActionChannel() {
        return actionChannel;
    }

    public void setActionChannel(DeviceChannel actionChannel) {
        this.actionChannel = actionChannel;
    }

    public DeviceChannel getDataChannel() {
        return dataChannel;
    }

    public void setDataChannel(DeviceChannel dataChannel) {
        this.dataChannel = dataChannel;
    }

}