package Classes;

import java.util.List;

abstract public class Device {
    private String deviceId;
    private String deviceName;
    private String description;
    private List<Info> deviceFactoryInfo;

    public String getDeviceName() {
        return deviceName;
    }
    public String getDeviceId()
    {
        return deviceId;
    }
    public  void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Info> getDeviceFactoryInfo() {
        return deviceFactoryInfo;
    }

    public void setDeviceFactoryInfo(List<Info> deviceFactoryInfo) {
        this.deviceFactoryInfo = deviceFactoryInfo;
    }
}