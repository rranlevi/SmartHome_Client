import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import Classes.HouseholdDevice;
import Classes.RequestStatus;
import Enums.RestPath;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class AddDevicesPanel extends JPanel {
    private List<HouseholdDevice> receivedDevices;
    private List<JCheckBox> checkBoxes;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel devicePanel;
    private JPanel buttonPanel;

    public AddDevicesPanel(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.checkBoxes = new ArrayList<>();
        setLayout(new BorderLayout());

        // Panel for device checkboxes
        devicePanel = new JPanel();
        devicePanel.setLayout(new BoxLayout(devicePanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(devicePanel);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(createAddButton());
        buttonPanel.add(createGoBackButton());
        buttonPanel.add(createRefreshButton());
        add(buttonPanel, BorderLayout.SOUTH);

        // Initial population of devices
        refreshDeviceList();
    }

    private void showLoadingIcon() {
        // Clear existing components and show loading icon
        devicePanel.removeAll();
        // Path to gif
        JLabel loadingLabel = new JLabel(new ImageIcon("Images/reload_gif.gif"));
        devicePanel.add(centerComponent(loadingLabel));
        devicePanel.revalidate();
        devicePanel.repaint();
    }

    private void refreshDeviceList() {
        showLoadingIcon();

        // Create a SwingWorker to fetch devices after the delay
        SwingWorker<List<HouseholdDevice>, Void> worker = new SwingWorker<List<HouseholdDevice>, Void>() {
            @Override
            protected List<HouseholdDevice> doInBackground() throws Exception {
                // Simulate a delay (loading effect) for 1.5 seconds
                Thread.sleep(1500);

                // Fetch devices and filter out existing ones
                List<HouseholdDevice> fetchedDevices = fetchDevicesFromServer();
                Set<String> existingDeviceIds = new HashSet<>();
                for (HouseholdDevice device : SharedDB.getDevices()) {
                    existingDeviceIds.add(device.getDeviceId());
                }

                // Filter devices not already added
                List<HouseholdDevice> filteredDevices = new ArrayList<>();
                for (HouseholdDevice device : fetchedDevices) {
                    if (!existingDeviceIds.contains(device.getDeviceId())) {
                        filteredDevices.add(device);
                    }
                }
                return filteredDevices;
            }

            @Override
            protected void done() {
                try {
                    receivedDevices = get();
                    displayDeviceList();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void displayDeviceList() {
        // Clear existing device checkboxes
        devicePanel.removeAll();
        checkBoxes.clear();

        // Add new device checkboxes
        boolean newDevicesFound = false;
        for (HouseholdDevice device : receivedDevices) {
            // Create a panel for each device to include image and checkbox
            JPanel deviceItemPanel = new JPanel();
            deviceItemPanel.setLayout(new BoxLayout(deviceItemPanel, BoxLayout.X_AXIS));

            // Create image icon and label
            ImageIcon imageIcon = Utils.decodeBase64ToImage(device.getDeviceImage(), 34, 34);
            JLabel imageLabel = new JLabel(imageIcon);

            // Create checkbox with device details
            JCheckBox checkBox = new JCheckBox(device.getDeviceName() + " - " +
                    device.getDeviceRoom() + " - " + device.getDescription());
            checkBoxes.add(checkBox);

            // Add horizontal glue to center components
            deviceItemPanel.add(Box.createHorizontalGlue());
            deviceItemPanel.add(imageLabel);
            deviceItemPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Spacer between image and checkbox
            deviceItemPanel.add(checkBox);
            deviceItemPanel.add(Box.createHorizontalGlue());

            // Add the device panel to the main panel
            devicePanel.add(deviceItemPanel);
            newDevicesFound = true;
        }

        // If no new devices are found, add a message
        if (!newDevicesFound) {
            JLabel noNewDevicesLabel = new JLabel("No new devices available.");
            devicePanel.add(centerComponent(noNewDevicesLabel));
        }

        // Revalidate and repaint to update the UI
        devicePanel.revalidate();
        devicePanel.repaint();
    }

    private List<HouseholdDevice> fetchDevicesFromServer() {
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

                // Add the selected devices to SharedDB.devices
                for (HouseholdDevice device : selectedDevices) {
                    SharedDB.addDevice(device);
                }

                // Refresh the device list to remove added devices
                refreshDeviceList();
            }
        });
        return addButton;
    }

    private JButton createGoBackButton() {
        JButton goBackButton = new JButton("Go back to main screen");
        goBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "MainPanel");
            }
        });
        return goBackButton;
    }

    private JButton createRefreshButton() {
        JButton refreshButton = new JButton("Refresh devices");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshDeviceList();
            }
        });
        return refreshButton;
    }

    private Component centerComponent(JComponent component) {
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(component);
        box.add(Box.createHorizontalGlue());
        return box;
    }
}
