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
        SharedDB.loadDevices(); // Has to be first!

        // Create a new frame
        JFrame frame = new JFrame("Swing Button Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1080, 1024);

        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

//        // Test GET request for devices
//        RequestStatus getDiscDevicesStatus = SharedDB.restWrapper.sendGet(RestPath.GET_DISC_DEVICES_PATH);
//        if (getDiscDevicesStatus.isSuccess()) {
//            Gson gson = new Gson();
//            Type listType = new TypeToken<List<HouseholdDevice>>() {}.getType();
//            SharedDB.devices = gson.fromJson(getDiscDevicesStatus.getMessage(), listType);
//        } else {
//            System.out.println("GET Request Failed: " + getDiscDevicesStatus.getMessage());
//        }

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
}
