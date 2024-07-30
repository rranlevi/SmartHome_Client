package Classes;

import java.util.List;

public class HouseholdDevice extends Device {
    private String deviceImage;
    private DeviceDataController deviceDataController;
    private DeviceActionController deviceActionController;
    private String deviceRoom;

    public HouseholdDevice(String deviceId, String deviceName, String deviceRoom, String description, DeviceDataController deviceDataController, DeviceActionController deviceActionController, String deviceImage, List<Info> deviceFactoryInfo) {
        super(deviceId, deviceName, description, deviceFactoryInfo);
        this.deviceRoom = deviceRoom;
        this.deviceDataController = deviceDataController;
        this.deviceActionController = deviceActionController;
        this.deviceImage = deviceImage;
    }

    public String getDeviceImage() {
        return deviceImage;
    }

    public void setDeviceImage(String deviceImage) {
        this.deviceImage = deviceImage;
    }

    public DeviceDataController getDeviceDataController() {
        return deviceDataController;
    }

    public void setDeviceDataController(DeviceDataController deviceDataController) {
        this.deviceDataController = deviceDataController;
    }

    public DeviceActionController getDeviceActionController() {
        return deviceActionController;
    }

    public void setDeviceActionController(DeviceActionController deviceActionController) {
        this.deviceActionController = deviceActionController;
    }

    public String getDeviceRoom() {
        return deviceRoom;
    }

    public void setDeviceRoom(String deviceRoom) {
        this.deviceRoom = deviceRoom;
    }
}