import Classes.Device;

import javax.swing.*;
import java.awt.*;

import Classes.*;

import java.awt.event.*;
import java.util.*;

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
        this.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e){
                refreshDevicesPanel();
            }
        });
        add(new JScrollPane(devicesPanel), BorderLayout.CENTER);
        refreshDevicesPanel();
    }

    private void refreshDevicesPanel() {
        devicesPanel.removeAll();

        if (SharedDB.getDevices().isEmpty()) {
            JLabel noDevicesLabel = new JLabel("No devices connected");
            noDevicesLabel.setHorizontalAlignment(SwingConstants.CENTER);
            devicesPanel.add(noDevicesLabel);
        } else {
            for (HouseholdDevice device : SharedDB.getDevices()) {
                JPanel devicePanel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5); // Add padding
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // Add image
                ImageIcon imageIcon = Utils.decodeBase64ToImage(device.getDeviceImage(), 34, 34);
                JLabel imageLabel = new JLabel(imageIcon);
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridheight = 2;
                gbc.weightx = 0;
                devicePanel.add(imageLabel, gbc);

                // Add device info
                JLabel deviceInfo = new JLabel(device.getDeviceName() + " | Room: " + device.getDeviceRoom() + " | Description: " + device.getDescription());
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.gridheight = 1;
                gbc.weightx = 1;
                devicePanel.add(deviceInfo, gbc);

                // Add buttons
                gbc.gridx = 1;
                gbc.gridy = 1;
                gbc.gridheight = 1;
                gbc.anchor = GridBagConstraints.WEST;

                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

               JButton actionsDataButton = new JButton("Actions & Data");
                actionsDataButton.addActionListener(e -> {
                    DataActionPanel dataActionPanel = new DataActionPanel(cardLayout,cardPanel,device);
                    cardPanel.add(dataActionPanel, "DataActionPanel");
                    cardLayout.show(cardPanel,"DataActionPanel");
                });
                buttonPanel.add(actionsDataButton);

                JButton removeButton = new JButton("Remove");
                removeButton.addActionListener(e -> {
                    SharedDB.removeDevice(device.getDeviceId());
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
