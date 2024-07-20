import Classes.RequestStatus;
import Enums.RestPath;
import Classes.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import javax.swing.*;
import java.awt.FlowLayout;

public class Main {
    public static void main(String[] args) {
        // Create a new frame
        JFrame frame = new JFrame("Swing Button Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        // Create a panel to hold the button
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // Create a new button
        JButton button = new JButton("Click Me!");
        JLabel lbl1 = new JLabel("Harel the gay");
        // Add the button to the panel
        panel.add(button);
        panel.add(lbl1);
        // Add the panel to the frame
        frame.add(panel);

        // Make the frame visible
        frame.setVisible(true);

    }

//        RestWrapper restWrapper = new RestWrapper();
//
//        // Test POST request
//        RequestStatus postStatus = restWrapper.sendPost(RestPath.POST_PATH, "Test Payload");
//        System.out.println("POST Request Success: " + postStatus.isSuccess());
//        System.out.println("POST Request Message: " + postStatus.getMessage());
//
//        // Test GET request
//        RequestStatus getStatus = restWrapper.sendGet(RestPath.GET_PATH);
//        System.out.println("GET Request Success: " + getStatus.isSuccess());
//        System.out.println("GET Request Message: " + getStatus.getMessage());
//
//        // Test GET request for devices
//        RequestStatus getDiscDevicesStatus = restWrapper.sendGet(RestPath.GET_DISC_DEVICES_PATH);
//        if (getStatus.isSuccess()) {
//            Gson gson = new Gson();
//            Type listType = new TypeToken<List<HouseholdDevice>>() {}.getType();
//            List<HouseholdDevice> devices = gson.fromJson(getDiscDevicesStatus.getMessage(), listType);
//
//            for (HouseholdDevice device : devices) {
//                System.out.println("Device: " + device.getDeviceName() + ", Room: " + device.getDeviceRoom());
//            }
//        } else {
//            System.out.println("GET Request Failed: " + getDiscDevicesStatus.getMessage());
//        }
}
