import Classes.RequestStatus;
import Enums.RestPath;
import Classes.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.CardLayout;

public class Main {
    public static void main(String[] args) {
        // Create a new frame
        JFrame frame = new JFrame("Swing Button Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1080, 1024);

        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        SharedDB.devices = new ArrayList<>();

        //TODO: Add all panels here and to the cardPanel
        MainPanel mainPanel = new MainPanel(cardLayout, cardPanel);
        AddDevicesPanel devicesPanel = new AddDevicesPanel();

        cardPanel.add(mainPanel, "MainPanel");
        cardPanel.add(devicesPanel, "AddDevicesPanel");

        // Add the panel to the frame
        frame.add(cardPanel);
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
