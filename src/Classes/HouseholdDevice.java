package Classes;

class HouseholdDevice extends Device {
    private String deviceImage;
    private DeviceDataController deviceDataController;
    private DeviceActionController deviceActionController;
    private String deviceRoom;

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