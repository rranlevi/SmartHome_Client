import Classes.Device;

import javax.swing.*;
import java.awt.*;
import Classes.*;
import java.awt.event.*;
import java.util.Comparator;
import java.util.List;

public class MainPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel devicesPanel;

    public MainPanel(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        setLayout(new BorderLayout());

        // Create and add the title at the top
        JLabel titleLabel = new JLabel("Main Menu", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add padding around the title
        add(titleLabel, BorderLayout.NORTH);

        // Create the button and the dropdown for sorting
        JButton button = new JButton("Add New Devices");
        button.addActionListener(_ -> cardLayout.show(cardPanel, "AddDevicesPanel"));
        JComboBox<String> sortComboBox = createSortComboBox();

        // Create a panel to hold the button and dropdown with the dropdown above the button
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        // Ensure the dropdown and button stay within the window's width and are centered
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sortPanel.add(sortComboBox);
        southPanel.add(sortPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(button);
        southPanel.add(buttonPanel);

        // Add some padding to ensure it doesn’t touch the window edges
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(southPanel, BorderLayout.SOUTH);

        devicesPanel = new JPanel();
        devicesPanel.setLayout(new BoxLayout(devicesPanel, BoxLayout.Y_AXIS));

        this.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                refreshDevicesPanel();
            }
        });

        JScrollPane scrollPane = new JScrollPane(devicesPanel);
        scrollPane.getViewport().setOpaque(false); // Make sure background is clear
        add(scrollPane, BorderLayout.CENTER);

        refreshDevicesPanel();
    }

    private void refreshDevicesPanel() {
        devicesPanel.removeAll();

        if (SharedDB.getDevices().isEmpty()) {
            // Center the "No devices connected" message
            JLabel noDevicesLabel = new JLabel("No devices connected");
            noDevicesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            devicesPanel.add(Box.createVerticalGlue());
            devicesPanel.add(noDevicesLabel);
            devicesPanel.add(Box.createVerticalGlue());
        } else {
            devicesPanel.add(Box.createVerticalGlue());
            for (HouseholdDevice device : SharedDB.getDevices()) {
                JPanel devicePanel = new JPanel(new BorderLayout());
                devicePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                // Add image
                ImageIcon imageIcon = Utils.decodeBase64ToImage(device.getDeviceImage(), 34, 34);
                JLabel imageLabel = new JLabel(imageIcon);
                devicePanel.add(imageLabel, BorderLayout.WEST);

                // Add device info
                JLabel deviceInfo = new JLabel("<html>" + device.getDeviceName() + "<br>Room: " + device.getDeviceRoom() + "<br>Description: " + device.getDescription() + "</html>");
                deviceInfo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                devicePanel.add(deviceInfo, BorderLayout.CENTER);

                // Add buttons
                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

                JButton actionsDataButton = new JButton("Actions & Data");
                actionsDataButton.addActionListener(e -> {
                    DataActionPanel dataActionPanel = new DataActionPanel(cardLayout, cardPanel, device);
                    cardPanel.add(dataActionPanel, "DataActionPanel");
                    cardLayout.show(cardPanel, "DataActionPanel");
                });
                buttonPanel.add(actionsDataButton);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(e -> {
                    SharedDB.removeDevice(device.getDeviceId());
                    refreshDevicesPanel(); // Refresh the panel after removal
                });
                buttonPanel.add(removeButton);

                devicePanel.add(buttonPanel, BorderLayout.EAST);

                devicePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, devicePanel.getMinimumSize().height));

                devicesPanel.add(centerComponent(devicePanel));
            }
            devicesPanel.add(Box.createVerticalGlue());
        }

        devicesPanel.revalidate();
        devicesPanel.repaint();
    }

    private Component centerComponent(JComponent component) {
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(component);
        box.add(Box.createHorizontalGlue());
        return box;
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

        SharedDB.getDevices().sort(comparator);
    }

    // Create a dropdown for sorting options
    private JComboBox<String> createSortComboBox() {
        String[] sortOptions = {"Sort by Name", "Sort by Room"};
        JComboBox<String> sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.setSelectedIndex(0); // Default to sorting by name

        sortComboBox.addActionListener(e -> {
            sortDevices(sortComboBox.getSelectedItem().toString());
            refreshDevicesPanel();
        });

        return sortComboBox;
    }
}
