import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import Classes.HouseholdDevice;
import Classes.RequestStatus;
import Enums.RestPath;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class AddDevicesPanel extends JPanel {
    private List<HouseholdDevice> receivedDevices;
    private List<JCheckBox> checkBoxes;

    public AddDevicesPanel() {
        this.receivedDevices = fetchDevicesFromServer();
        this.checkBoxes = new ArrayList<>();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Add vertical glue for top spacing
        add(Box.createVerticalGlue());

        // Create checkboxes for each device and add them centered
        for (HouseholdDevice device : receivedDevices) {
            JCheckBox checkBox = new JCheckBox(device.getDeviceName() + " - " + device.getDescription());
            checkBoxes.add(checkBox);
            add(centerComponent(checkBox));
        }

        // Add the "Add Selected Devices" button, centered
        add(centerComponent(createAddButton()));

        // Add vertical glue for bottom spacing
        add(Box.createVerticalGlue());
    }

    private List<HouseholdDevice> fetchDevicesFromServer() {
        // Placeholder list to be returned
        List<HouseholdDevice> devices = new ArrayList<>();

        // Fetch available devices from server
        RequestStatus getDiscDevicesStatus = SharedDB.restWrapper.sendGet(RestPath.GET_DISC_DEVICES_PATH);
        if (getDiscDevicesStatus.isSuccess()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<HouseholdDevice>>() {}.getType();
            devices = gson.fromJson(getDiscDevicesStatus.getMessage(), listType);
        } else {
            System.out.println("GET Request Failed: " + getDiscDevicesStatus.getMessage());
        }

        return devices;
    }

    private List<HouseholdDevice> getSelectedDevices() {
        List<HouseholdDevice> selectedDevices = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                selectedDevices.add(receivedDevices.get(i));
            }
        }
        return selectedDevices;
    }

    private JButton createAddButton() {
        JButton addButton = new JButton("Add Selected Devices");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<HouseholdDevice> selectedDevices = getSelectedDevices();
                // Add selected devices to SharedDB.devices
                SharedDB.devices.addAll(selectedDevices);

                // For confirmation, print the selected devices
                System.out.println("Selected Devices Added:");
                for (HouseholdDevice device : selectedDevices) {
                    System.out.println(device.getDeviceName() + " - " + device.getDescription());
                }
            }
        });
        return addButton;
    }

    private Component centerComponent(JComponent component) {
        // Wrap the component in a Box with horizontal glue for centering
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(component);
        box.add(Box.createHorizontalGlue());
        return box;
    }
}
