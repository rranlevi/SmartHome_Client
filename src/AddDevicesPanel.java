import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    private JTable table; // Reference to JTable

    public AddDevicesPanel(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.checkBoxes = new ArrayList<>();
        setLayout(new BorderLayout());

        // Add title label
        add(createTitle(), BorderLayout.NORTH);

        // Create the JTable to display devices
        String[] columnNames = {"Icon", "Device Info", "Device Description" , "Select"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Make only the checkbox column editable
            }
        };
        table = new JTable(tableModel) {
            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 0 -> ImageIcon.class;
                    case 3 -> Boolean.class;
                    case 2 -> JLabel.class;
                    default -> String.class;
                };
            }
        };
        table.setRowHeight(40); // Set a consistent row height for all rows

        // Wrap the table in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Create a panel to hold the dropdown and buttons
        JPanel dropdownAndButtonsPanel = new JPanel();
        dropdownAndButtonsPanel.setLayout(new BoxLayout(dropdownAndButtonsPanel, BoxLayout.Y_AXIS));

        // Create a panel for the dropdown with FlowLayout to prevent stretching
        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JComboBox<String> sortComboBox = createSortComboBox(tableModel);
        dropdownPanel.add(sortComboBox);

        // Add the dropdown panel to the dropdownAndButtonsPanel
        dropdownAndButtonsPanel.add(dropdownPanel);

        // Panel for the buttons with FlowLayout to keep them next to each other
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center-aligned buttons
        buttonPanel.add(createAddButton(tableModel));
        buttonPanel.add(createGoBackButton());
        buttonPanel.add(createRefreshButton(tableModel));

        // Add the button panel to the dropdownAndButtonsPanel
        dropdownAndButtonsPanel.add(buttonPanel);

        // Add the combined dropdown and buttons panel to the bottom of the screen
        add(dropdownAndButtonsPanel, BorderLayout.SOUTH);

        // Add a component listener to refresh the panel when it becomes visible
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshDeviceList(tableModel);
            }
        });

        // Initial population of devices
        refreshDeviceList(tableModel);
    }

    // Create title for the panel
    private JLabel createTitle() {
        JLabel titleLabel = new JLabel("Add New Device", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return titleLabel;
    }

    // Handle the loading GIF for the panel
    private void showLoadingIcon(DefaultTableModel tableModel) {
        // Clear existing components and show loading icon
        tableModel.setRowCount(0);
        JLabel loadingLabel = new JLabel(new ImageIcon("Images/reload_gif.gif"));
        tableModel.addRow(new Object[]{loadingLabel, "Loading...", null});
    }

    // Refresh the device list
    private void refreshDeviceList(DefaultTableModel tableModel) {
        showLoadingIcon(tableModel);

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
                    displayDeviceList(tableModel);
                } catch (InterruptedException | ExecutionException e) {
                    // Print the throw if it happens
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // Display the device list in the JTable
    private void displayDeviceList(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Clear existing rows

        for (HouseholdDevice device : receivedDevices) {
            // Image icon for each device
            ImageIcon imageIcon = Utils.decodeBase64ToImage(device.getDeviceImage(), 34, 34);

            // Add row to table model
            JLabel deviceInfo = new JLabel(
                    "<html>" + device.getDeviceName() +
                            "<br>Room: " + device.getDeviceRoom() +
                            "<br>Description: " + Utils.fixNumOfChars(device.getDescription(), 40) + "</html>");
            deviceInfo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            deviceInfo.setToolTipText(device.getDescription());

            tableModel.addRow(new Object[]{imageIcon, device.getDeviceName() + " - "
                    + device.getDeviceRoom() + " - ", deviceInfo, false});
        }

        if (receivedDevices.isEmpty()) {
            tableModel.addRow(new Object[]{null, "No new devices available.", null});
        }
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
    private void sortDevices(String sortBy, DefaultTableModel tableModel) {
        Comparator<HouseholdDevice> comparator;

        if ("Sort by Name".equals(sortBy)) {
            comparator = Comparator.comparing(HouseholdDevice::getDeviceName, String.CASE_INSENSITIVE_ORDER);
        } else if ("Sort by Room".equals(sortBy)) {
            comparator = Comparator.comparing(HouseholdDevice::getDeviceRoom, String.CASE_INSENSITIVE_ORDER);
        } else {
            return; // If an unknown sorting option is selected, do nothing
        }

        // Sort the list of devices
        Collections.sort(receivedDevices, comparator);

        // Update the table model to reflect the sorted list
        displayDeviceList(tableModel);
    }

    // Create a dropdown for sorting options
    private JComboBox<String> createSortComboBox(DefaultTableModel tableModel) {
        String[] sortOptions = {"Sort by Name", "Sort by Room"};
        JComboBox<String> sortComboBox = new JComboBox<>(sortOptions);

        // Set the preferred size of the JComboBox to match the buttons
        sortComboBox.setPreferredSize(new Dimension(150, 30)); // Width of 150px, height of 30px

        sortComboBox.setSelectedIndex(0); // Default to sorting by name

        sortComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortDevices(sortComboBox.getSelectedItem().toString(), (DefaultTableModel) table.getModel());
            }
        });

        return sortComboBox;
    }

    // We retrieve the check-boxed devices to add
    private List<HouseholdDevice> getSelectedDevices(DefaultTableModel tableModel) {
        List<HouseholdDevice> selectedDevices = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean isSelected = (Boolean) tableModel.getValueAt(i, 2);
            if (isSelected != null && isSelected) {
                selectedDevices.add(receivedDevices.get(i));
            }
        }
        return selectedDevices;
    }

    // Add button
    private JButton createAddButton(DefaultTableModel tableModel) {
        JButton addButton = new JButton("Add Selected Devices");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<HouseholdDevice> selectedDevices = getSelectedDevices(tableModel);

                for (HouseholdDevice device : selectedDevices) {
                    SharedDB.addDevice(device);
                }

                refreshDeviceList(tableModel);
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
    private JButton createRefreshButton(DefaultTableModel tableModel) {
        JButton refreshButton = new JButton("Refresh devices");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshDeviceList(tableModel);
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
