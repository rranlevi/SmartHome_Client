package Classes;

public class Widget {
    private String type;

    public Widget(String text){
        this.type = text;
    }

    public String getText() {
        return type;
    }

    public void setText(String text) {
        this.type = text;
    }
}
