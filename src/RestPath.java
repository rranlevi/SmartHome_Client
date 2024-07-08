public enum RestPath {
    POST_PATH("/api/postPath"),
    GET_PATH("/api/getPath");

    private final String path;

    RestPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
