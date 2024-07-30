import Classes.*;

import javax.swing.*;
import java.awt.*;

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

            // Decode the Base64 image string to ImageIcon
            ImageIcon imageIcon = Utils.decodeBase64ToImage(device.getDeviceImage(), 34, 34);
            JLabel imageLabel = new JLabel(imageIcon);
            devicePanel.add(imageLabel, BorderLayout.WEST);

            JCheckBox checkBox = new JCheckBox(device.getDeviceName());
            devicePanel.add(checkBox, BorderLayout.CENTER);

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

            JLabel roomLabel = new JLabel("Room: " + device.getDeviceRoom());
            JLabel descriptionLabel = new JLabel("Description: " + device.getDescription());
            infoPanel.add(roomLabel);
            infoPanel.add(descriptionLabel);

            devicePanel.add(infoPanel, BorderLayout.EAST);

            devicesPanel.add(devicePanel);
        }

        add(new JScrollPane(devicesPanel), BorderLayout.CENTER);
    }
}