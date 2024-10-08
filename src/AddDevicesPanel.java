import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import Classes.*;
import Enums.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.*;

public class AddDevicesPanel extends JPanel {
    private List<HouseholdDevice> receivedDevices;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTable table; // Reference to JTable
    private JLabel loadingLabel; // Label for loading GIF
    private JScrollPane scrollPane; // ScrollPane for JTable
    private JLabel noDevicesLabel; // Label to display "No new devices available."

    public AddDevicesPanel(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        setLayout(new BorderLayout());

        // Add title label
        add(createTitle(), BorderLayout.NORTH);

        // Create the JTable to display devices
        String[] columnNames = {"Icon", "Device Name", "Room", "Description", "Select"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Make only the checkbox column editable
            }
        };
        // Initialize the JTable with the custom table model
        table = new JTable(tableModel) {

            // Override the getColumnClass method to define the class type for each column
            @Override
            public Class<?> getColumnClass(int column) {
                // Use a switch expression to determine the class type based on the column index
                return switch (column) {
                    case 0 -> ImageIcon.class;  // Column 0 should hold ImageIcon objects (for device icons)
                    case 4 -> Boolean.class;    // Column 4 should hold Boolean objects (for checkboxes)
                    default -> String.class;    // All other columns should hold String objects (for text data)
                };
            }
        };
        table.setRowHeight(40); // Set a consistent row height for all rows

        // Wrap the table in a JScrollPane
        scrollPane = new JScrollPane(table);

        // Create the loading GIF label
        loadingLabel = new JLabel(new ImageIcon("Images/reload_gif.gif"));
        loadingLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create the "No new devices available" label
        noDevicesLabel = new JLabel("No new devices available.", JLabel.CENTER);
        noDevicesLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Initially show the loading label
        add(loadingLabel, BorderLayout.CENTER);

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

    // Show the loading GIF while refreshing or loading from server
    private void showLoadingIcon() {
        remove(scrollPane); // Remove the JTable's scroll pane
        remove(noDevicesLabel); // Remove the no devices label if it was displayed
        add(loadingLabel, BorderLayout.CENTER); // Show the loading label
        revalidate();
        repaint();
    }

    // Hide the loading GIF and show the table or the "No devices" label
    private void hideLoadingIcon() {
        remove(loadingLabel); // Remove the loading label
        revalidate();
        repaint();
    }

    // Refresh the device list
    private void refreshDeviceList(DefaultTableModel tableModel) {
        showLoadingIcon(); // Show loading GIF

        // Use SwingWorker to handle UI processing in the background (thread)
        SwingWorker<List<HouseholdDevice>, Void> worker = new SwingWorker<List<HouseholdDevice>, Void>() {
            @Override
            protected List<HouseholdDevice> doInBackground() throws Exception {
                try {
                    Thread.sleep(1000); // Simulate loading delay

                    // Fetch devices from the server
                    List<HouseholdDevice> fetchedDevices = fetchDevicesFromServer();
                    Set<String> existingDeviceIds = new HashSet<>();
                    for (HouseholdDevice device : SharedDB.getDevices()) {
                        existingDeviceIds.add(device.getDeviceId());
                    }

                    // Filter out devices that are already added
                    List<HouseholdDevice> filteredDevices = new ArrayList<>();
                    for (HouseholdDevice device : fetchedDevices) {
                        if (!existingDeviceIds.contains(device.getDeviceId())) {
                            filteredDevices.add(device);
                        }
                    }
                    return filteredDevices;

                } catch (Exception e) {
                    e.printStackTrace();
                    return Collections.emptyList();
                }
            }

            @Override
            protected void done() {
                try {
                    receivedDevices = get();
                    if (receivedDevices.isEmpty()) {
                        showNoDevicesMessage(); // Show the "No devices" message
                    } else {
                        displayDeviceList(tableModel); // Show the table with devices
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    hideLoadingIcon(); // Hide loading GIF and show the appropriate view
                }
            }
        };
        worker.execute();
    }

    // Display the device list in the JTable
    private void displayDeviceList(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Clear existing rows

        if (receivedDevices.isEmpty()) {
            showNoDevicesMessage(); // Show the "No devices" message
        } else {
            remove(noDevicesLabel); // Remove the no devices label if it was displayed
            add(scrollPane, BorderLayout.CENTER); // Add the table back to the panel

            for (HouseholdDevice device : receivedDevices) {
                // Image icon for each device
                ImageIcon imageIcon = Utils.decodeBase64ToImage(device.getDeviceImage(), 34, 34);

                // Add row to table model
                tableModel.addRow(new Object[]{imageIcon, device.getDeviceName(), device.getDeviceRoom(), device.getDescription(), false});
            }

            // Set a custom cell renderer to add the tooltip to the "Description" column
            table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    // If the component we received is of type JComponent
                    if (c instanceof JComponent) {
                        JComponent jc = (JComponent) c;

                        // Set the full description as the tooltip
                        if (value != null) {
                            jc.setToolTipText(value.toString());
                        } else {
                            jc.setToolTipText(null);
                        }
                    }
                    return c;
                }
            });

            revalidate();
            repaint();
        }
    }

    // Show the "No new devices available" message
    private void showNoDevicesMessage() {
        remove(scrollPane); // Remove the JTable if it was displayed
        add(noDevicesLabel, BorderLayout.CENTER); // Add the no devices label
        revalidate();
        repaint();
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
            Boolean isSelected = (Boolean) tableModel.getValueAt(i, 4); // Checkbox is in column 4
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
}
