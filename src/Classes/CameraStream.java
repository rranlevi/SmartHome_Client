package Classes;

public class CameraStream extends Widget {
    private String currentFrame;

    public CameraStream(String text, String currentFrame) {
        super(text);
        this.currentFrame = currentFrame;
    }

    public String getCurrentFrame() {
        return this.currentFrame;
    }

    public void setCurrentFrame(String currentFrame) {
        this.currentFrame = currentFrame;
    }
}
