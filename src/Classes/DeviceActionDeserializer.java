package Classes;

import com.google.gson.*;

import java.lang.reflect.Type;

public class DeviceActionDeserializer implements JsonDeserializer<DeviceAction> {

    @Override
    public DeviceAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        boolean isAvailable = jsonObject.get("isAvailable").getAsBoolean();

        // Deserialize the Widget object, determining the correct subclass based on a type field
        JsonObject widgetJsonObject = jsonObject.getAsJsonObject("widget");
        Widget widget = context.deserialize(widgetJsonObject, determineWidgetClass(widgetJsonObject));

        DeviceChannel actionChannel = context.deserialize(jsonObject.get("actionChannel"), DeviceChannel.class);
        DeviceChannel dataChannel = context.deserialize(jsonObject.get("dataChannel"), DeviceChannel.class);

        return new DeviceAction(name, description, widget, isAvailable, actionChannel, dataChannel);
    }

    private Class<? extends Widget> determineWidgetClass(JsonObject widgetJsonObject) {
        String type = widgetJsonObject.get("type").getAsString(); // Assuming there's a "type" field in the JSON
        return switch (type) {
            case "Slider" -> Slider.class;
            case "Switch" -> Switch.class;
            case "Dropdown" -> Dropdown.class;
            default -> throw new IllegalArgumentException("Unknown widget type: " + type);
        };
    }
}
