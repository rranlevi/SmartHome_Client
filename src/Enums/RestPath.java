package Enums;

public enum RestPath {
    POST_PATH("/api/postPath"),
    GET_PATH("/api/getPath"),
    GET_DISC_DEVICES_PATH("/api/getDiscDevices");

    private final String path;

    RestPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
