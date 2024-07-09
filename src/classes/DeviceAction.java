package classes;

class DeviceAction {
    private String name;
    private String description;
    private Widget widget;
    private boolean isAvailable;
    private DeviceChannel channel;

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

    public DeviceChannel getChannel() {
        return channel;
    }

    public void setChannel(DeviceChannel channel) {
        this.channel = channel;
    }

    public ActionStatus doAction() {
        // Implementation here
        return new ActionStatus();
    }
}