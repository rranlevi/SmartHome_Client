package classes;

import java.util.List;

class DeviceActionController {
    private String name;
    private String description;
    private List<DeviceAction> deviceActions;

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

    public List<DeviceAction> getDeviceActions() {
        return deviceActions;
    }

    public void setDeviceActions(List<DeviceAction> deviceActions) {
        this.deviceActions = deviceActions;
    }
}