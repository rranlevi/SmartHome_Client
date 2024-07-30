package Classes;

import java.util.List;

public class DeviceDataController {
    private String name;
    private String description;
    private List<DeviceInfo> deviceData;

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

    public List<DeviceInfo> getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(List<DeviceInfo> deviceData) {
        this.deviceData = deviceData;
    }
}