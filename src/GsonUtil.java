import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import Classes.*;

public class GsonUtil {
    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(DeviceAction.class, new DeviceActionDeserializer())
                .create();
    }
}
