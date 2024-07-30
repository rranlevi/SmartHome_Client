import Classes.HouseholdDevice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SharedDB {
    private static List<HouseholdDevice> devices;
    public static RestWrapper restWrapper = new RestWrapper();
    private static final String FILE_PATH = "devices.json";

    public static List<HouseholdDevice> getDevices() {
        return devices;
    }

    public static void setDevices(List<HouseholdDevice> householdDevices){
        devices = householdDevices;
        saveDevices();
    }

    public static void addDevice(HouseholdDevice householdDevice){
        devices.add(householdDevice);
        saveDevices();
    }

    public static void removeDevice(String deviceId){
        HouseholdDevice householdDeviceToRemove = null;
        for (HouseholdDevice householdDevice : devices){
            if (householdDevice.getDeviceId().equals(deviceId))
                householdDeviceToRemove = householdDevice;
        }
        devices.remove(householdDeviceToRemove);
        saveDevices();
    }

    // Save devices to a JSON file
    public static void saveDevices() {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(devices, writer);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // Load devices from a JSON file
    public static void loadDevices() {
        if (Files.exists(Paths.get(FILE_PATH))) {
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(FILE_PATH)) {
                Type listType = new TypeToken<ArrayList<HouseholdDevice>>(){}.getType();
                devices = gson.fromJson(reader, listType);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
