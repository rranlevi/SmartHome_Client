package Classes;

import java.util.List;

public class Dropdown extends Widget {
    private List<String> options;

    public Dropdown(String text, List<String> options) {
        super(text);
        this.options = options;
    }

    public List<String> getListOptions() {
        return this.options;
    }

    public void setListOptions(List<String> options) {
        this.options = options;
    }
}
