import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

        // Add title label
        add(createTitle(), BorderLayout.NORTH);

        // Panel for device checkboxes
        devicePanel = new JPanel();
        devicePanel.setLayout(new BoxLayout(devicePanel, BoxLayout.Y_AXIS));

        // Wrap devicePanel in another panel for vertical centering
        JPanel centeredDevicePanel = new JPanel();
        centeredDevicePanel.setLayout(new BoxLayout(centeredDevicePanel, BoxLayout.Y_AXIS));
        centeredDevicePanel.add(Box.createVerticalGlue());
        centeredDevicePanel.add(centerComponent(devicePanel));
        centeredDevicePanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(centeredDevicePanel);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(createAddButton());
        buttonPanel.add(createGoBackButton());
        buttonPanel.add(createRefreshButton());
        add(buttonPanel, BorderLayout.SOUTH);

        // Add a component listener to refresh the panel when it becomes visible
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshDeviceList();
            }
        });

        // Initial population of devices
        refreshDeviceList();
    }

    // Create title for the panel
    private JLabel createTitle() {
        JLabel titleLabel = new JLabel("Add New Device", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return titleLabel;
    }

    // Handle the loading GIF for the panel
    private void showLoadingIcon() {
        // Clear existing components and show loading icon
        devicePanel.removeAll();
        JLabel loadingLabel = new JLabel(new ImageIcon("Images/reload_gif.gif"));
        devicePanel.add(centerComponent(loadingLabel));
        devicePanel.revalidate();
        devicePanel.repaint();
    }

    // Refresh the device list
    private void refreshDeviceList() {
        showLoadingIcon();

        // Use SwingWorker to handle UI processing in the background
        SwingWorker<List<HouseholdDevice>, Void> worker = new SwingWorker<List<HouseholdDevice>, Void>() {
            @Override
            protected List<HouseholdDevice> doInBackground() throws Exception {
                Thread.sleep(1500); // Simulate loading delay, it's only a UI delay

                // Handle the fetched devices from the server
                List<HouseholdDevice> fetchedDevices = fetchDevicesFromServer();
                Set<String> existingDeviceIds = new HashSet<>();
                for (HouseholdDevice device : SharedDB.getDevices()) {
                    existingDeviceIds.add(device.getDeviceId());
                }

                // We filter the devices that are already added
                List<HouseholdDevice> filteredDevices = new ArrayList<>();
                for (HouseholdDevice device : fetchedDevices) {
                    if (!existingDeviceIds.contains(device.getDeviceId())) {
                        filteredDevices.add(device);
                    }
                }
                return filteredDevices;
            }

            // It's a catcher for the thread responsible for the UI updates in Swing (The GIF processing)
            @Override
            protected void done() {
                try {
                    receivedDevices = get();
                    displayDeviceList();
                } catch (InterruptedException | ExecutionException e) {
                    // Print the throw if it happens
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // Display the device list
    private void displayDeviceList() {
        devicePanel.removeAll();
        checkBoxes.clear();

        boolean newDevicesFound = false;

        // We display each device, giving the image and info about it
        for (HouseholdDevice device : receivedDevices) {
            JPanel deviceItemPanel = new JPanel();
            deviceItemPanel.setLayout(new BoxLayout(deviceItemPanel, BoxLayout.X_AXIS));

            ImageIcon imageIcon = Utils.decodeBase64ToImage(device.getDeviceImage(), 34, 34);
            JLabel imageLabel = new JLabel(imageIcon);

            JCheckBox checkBox = new JCheckBox(device.getDeviceName() + " - " +
                    device.getDeviceRoom() + " - " + device.getDescription());
            checkBoxes.add(checkBox);

            // Center everything as it should be aligned
            deviceItemPanel.add(Box.createHorizontalGlue());
            deviceItemPanel.add(imageLabel);
            deviceItemPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            deviceItemPanel.add(checkBox);
            deviceItemPanel.add(Box.createHorizontalGlue());

            devicePanel.add(deviceItemPanel);
            newDevicesFound = true;
        }

        if (!newDevicesFound) {
            JLabel noNewDevicesLabel = new JLabel("No new devices available.");
            devicePanel.add(centerComponent(noNewDevicesLabel));
        }

        devicePanel.revalidate();
        devicePanel.repaint();
    }

    // Fetch devices from the server using GET
    private List<HouseholdDevice> fetchDevicesFromServer() {
        List<HouseholdDevice> devices = new ArrayList<>();

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

    // We retrieve the check-boxed devices to add
    private List<HouseholdDevice> getSelectedDevices() {
        List<HouseholdDevice> selectedDevices = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                selectedDevices.add(receivedDevices.get(i));
            }
        }
        return selectedDevices;
    }

    // Add button
    private JButton createAddButton() {
        JButton addButton = new JButton("Add Selected Devices");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<HouseholdDevice> selectedDevices = getSelectedDevices();

                for (HouseholdDevice device : selectedDevices) {
                    SharedDB.addDevice(device);
                }

                refreshDeviceList();
            }
        });
        return addButton;
    }

    // Go back to main screen button
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

    // Refresh the UI button
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

    // Use this component to center everything
    private Component centerComponent(JComponent component) {
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(component);
        box.add(Box.createHorizontalGlue());
        return box;
    }
}
