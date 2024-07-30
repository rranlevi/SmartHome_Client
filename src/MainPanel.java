import Classes.Device;

import javax.swing.*;
import java.awt.*;

import Classes.*;
import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel devicesPanel;

    public MainPanel(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        setLayout(new BorderLayout());

        JButton button = new JButton("Add New Devices");
        button.addActionListener(_ -> cardLayout.show(cardPanel, "AddDevicesPanel"));
        add(button, BorderLayout.NORTH);

        devicesPanel = new JPanel();
        devicesPanel.setLayout(new BoxLayout(devicesPanel, BoxLayout.Y_AXIS));

        add(new JScrollPane(devicesPanel), BorderLayout.CENTER);

        refreshDevicesPanel();
    }

    private void refreshDevicesPanel() {
        devicesPanel.removeAll();

        if (SharedDB.devices.isEmpty()) {
            JLabel noDevicesLabel = new JLabel("No devices connected");
            noDevicesLabel.setHorizontalAlignment(SwingConstants.CENTER);
            devicesPanel.add(noDevicesLabel);
        } else {
            for (HouseholdDevice device : SharedDB.devices) {
                // Decode the Base64 image string to ImageIcon and scale it

                JPanel devicePanel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 1;
                gbc.gridx = 0;
                gbc.gridy = 0;
                ImageIcon imageIcon = Utils.decodeBase64ToImage(device.getDeviceImage(),34,34);
                JLabel imageLabel = new JLabel(imageIcon);
                devicePanel.add(imageLabel, BorderLayout.WEST);

                JLabel deviceInfo = new JLabel(device.getDeviceName() + " | Room: " + device.getDeviceRoom() + " | Description: " + device.getDescription());
                devicePanel.add(deviceInfo, gbc);

                gbc.gridx = 1;
                gbc.weightx = 0;
                gbc.anchor = GridBagConstraints.EAST;

                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

                JButton actionsDataButton = new JButton("Actions & Data");
                actionsDataButton.addActionListener(e -> {
                    // Placeholder for opening a new panel
                    DataActionPanel dataActionPanel = new DataActionPanel(cardLayout,cardPanel,device);
                    cardPanel.add(dataActionPanel, "DataActionPanel");
                    cardLayout.show(cardPanel,"DataActionPanel");
                });
                buttonPanel.add(actionsDataButton);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(e -> {
                    SharedDB.devices.remove(device);
                    refreshDevicesPanel(); // Refresh the panel after removal
                });
                buttonPanel.add(removeButton);

                devicePanel.add(buttonPanel, gbc);

                devicesPanel.add(devicePanel);
            }
        }

        devicesPanel.revalidate();
        devicesPanel.repaint();
    }
}
