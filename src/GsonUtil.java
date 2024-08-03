import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Classes.*;

public class GsonUtil {
    public static Gson createGson() {
        RuntimeTypeAdapterFactory<Widget> widgetAdapterFactory = RuntimeTypeAdapterFactory
                .of(Widget.class, "text")  // "type" is the JSON field that indicates the type
                .registerSubtype(Dropdown.class, "Dropdown")
                .registerSubtype(Slider.class, "Slider")
                .registerSubtype(Switch.class, "Switch");

        return new GsonBuilder()
                .registerTypeAdapterFactory(widgetAdapterFactory)
                .create();
    }
}
