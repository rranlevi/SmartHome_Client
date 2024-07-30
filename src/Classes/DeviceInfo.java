package Classes;

public class DeviceInfo {
    private Info deviceInfo;
    private DeviceChannel channel;

    public DeviceInfo(Info deviceInfo, DeviceChannel channel){
        this.deviceInfo = deviceInfo;
        this.channel = channel;
    }
    public Info getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(Info deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public DeviceChannel getChannel() {
        return channel;
    }

    public void setChannel(DeviceChannel channel) {
        this.channel = channel;
    }

    public void updateInfo() {
        // Implementation here
    }
}