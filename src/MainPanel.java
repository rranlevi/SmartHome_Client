import Classes.Device;

import javax.swing.*;
import java.awt.*;

import Classes.*;
import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainPanel extends JPanel {
    public MainPanel(CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());

        JButton button = new JButton("Add New Devices");

        button.addActionListener(_ -> cardLayout.show(cardPanel, "AddDevicesPanel"));
        add(button, BorderLayout.NORTH);

        JPanel devicesPanel = new JPanel();
        devicesPanel.setLayout(new BoxLayout(devicesPanel, BoxLayout.Y_AXIS));

        for (HouseholdDevice device : SharedDB.devices) {
            JPanel devicePanel = new JPanel();
            devicePanel.setLayout(new BorderLayout());

            JCheckBox checkBox = new JCheckBox(device.getDeviceName());
            devicePanel.add(checkBox, BorderLayout.WEST);

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

            JLabel roomLabel = new JLabel("Room: " + device.getDeviceRoom());
            JLabel descriptionLabel = new JLabel("Description: " + device.getDescription());
            infoPanel.add(roomLabel);
            infoPanel.add(descriptionLabel);

            devicePanel.add(infoPanel, BorderLayout.CENTER);

            devicesPanel.add(devicePanel);
        }

        add(new JScrollPane(devicesPanel), BorderLayout.CENTER);
    }
}
