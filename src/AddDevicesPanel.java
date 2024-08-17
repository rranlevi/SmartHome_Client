import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;
import java.util.List;
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

        // Create a panel to hold the dropdown and buttons
        JPanel dropdownAndButtonsPanel = new JPanel();
        dropdownAndButtonsPanel.setLayout(new BoxLayout(dropdownAndButtonsPanel, BoxLayout.Y_AXIS));

        // Create a panel for the dropdown with FlowLayout to prevent stretching
        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JComboBox<String> sortComboBox = createSortComboBox();
        dropdownPanel.add(sortComboBox);

        // Add the dropdown panel to the dropdownAndButtonsPanel
        dropdownAndButtonsPanel.add(dropdownPanel);

        // Panel for the buttons with FlowLayout to keep them next to each other
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center-aligned buttons
        buttonPanel.add(createAddButton());
        buttonPanel.add(createGoBackButton());
        buttonPanel.add(createRefreshButton());

        // Add the button panel to the dropdownAndButtonsPanel
        dropdownAndButtonsPanel.add(buttonPanel);

        // Add the combined dropdown and buttons panel to the bottom of the screen
        add(dropdownAndButtonsPanel, BorderLayout.SOUTH);

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
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
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

        // Use SwingWorker to handle UI processing in the background (thread)
        SwingWorker<List<HouseholdDevice>, Void> worker = new SwingWorker<List<HouseholdDevice>, Void>() {
            @Override
            protected List<HouseholdDevice> doInBackground() throws Exception {

                try {
                    Thread.sleep(1000); // Simulate loading delay, it's only a UI delay

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

                } catch (Exception e) {
                    // Print the throw if it happens
                    e.printStackTrace();
                    return Collections.emptyList();
                }
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
        devicePanel.removeAll(); // Clear all components
        checkBoxes.clear(); // Clear the checkBox list to avoid duplicates

        boolean newDevicesFound = false;

        // Concatenate and add the device list if necessary
        for (HouseholdDevice device : receivedDevices) {
            JPanel deviceItemPanel = new JPanel();
            deviceItemPanel.setLayout(new BoxLayout(deviceItemPanel, BoxLayout.X_AXIS));

            // Image icon for each device that we receive
            ImageIcon imageIcon = Utils.decodeBase64ToImage(device.getDeviceImage(), 34, 34);
            JLabel imageLabel = new JLabel(imageIcon);

            // Checkbox for each device that we receive
            JCheckBox checkBox = new JCheckBox(device.getDeviceName() + " - " +
                    device.getDeviceRoom() + " - " + device.getDescription());
            checkBoxes.add(checkBox);

            // Add everything to the device panel
            deviceItemPanel.add(Box.createHorizontalGlue());
            deviceItemPanel.add(imageLabel);
            deviceItemPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            deviceItemPanel.add(checkBox);
            deviceItemPanel.add(Box.createHorizontalGlue());

            devicePanel.add(deviceItemPanel);
            newDevicesFound = true;
        }

        // If we haven't found newly added devices
        if (!newDevicesFound) {
            JLabel noNewDevicesLabel = new JLabel("No new devices available.");
            devicePanel.add(centerComponent(noNewDevicesLabel));
        }

        devicePanel.revalidate(); // Revalidate to refresh the layout
        devicePanel.repaint(); // Repaint to ensure the panel is updated
    }

    // Fetch devices from the server using GET
    private List<HouseholdDevice> fetchDevicesFromServer() {
        List<HouseholdDevice> devices = new ArrayList<>();

        // We get the devices
        RequestStatus getDiscDevicesStatus = SharedDB.restWrapper.sendGet(RestPath.GET_DISC_DEVICES_PATH);
        if (getDiscDevicesStatus.isSuccess()) {
            Gson gson = GsonUtil.createGson();
            Type listType = new TypeToken<List<HouseholdDevice>>() {}.getType();
            devices = gson.fromJson(getDiscDevicesStatus.getMessage(), listType);
        } else {
            System.out.println("GET Request Failed: " + getDiscDevicesStatus.getMessage());
        }

        return devices;
    }

    // Sort devices using a comparator based on the selected option
    private void sortDevices(String sortBy) {
        Comparator<HouseholdDevice> comparator;

        if ("Sort by Name".equals(sortBy)) {
            comparator = Comparator.comparing(HouseholdDevice::getDeviceName, String.CASE_INSENSITIVE_ORDER);
        } else if ("Sort by Room".equals(sortBy)) {
            comparator = Comparator.comparing(HouseholdDevice::getDeviceRoom, String.CASE_INSENSITIVE_ORDER);
        } else {
            return; // If an unknown sorting option is selected, do nothing
        }

        receivedDevices.sort(comparator);

        // Revalidate and repaint after sorting to ensure the UI is updated
        devicePanel.revalidate();
        devicePanel.repaint();
    }

    // Create a dropdown for sorting options
    private JComboBox<String> createSortComboBox() {
        String[] sortOptions = {"Sort by Name", "Sort by Room"};
        JComboBox<String> sortComboBox = new JComboBox<>(sortOptions);

        // Set the preferred size of the JComboBox to match the buttons
        sortComboBox.setPreferredSize(new Dimension(150, 30)); // Width of 150px, height of 30px

        sortComboBox.setSelectedIndex(0); // Default to sorting by name

        sortComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortDevices(sortComboBox.getSelectedItem().toString());
                displayDeviceList();
            }
        });

        return sortComboBox;
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
