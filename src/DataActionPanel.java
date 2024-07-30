import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import Classes.*;


public class DataActionPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private static final String SERVER_PATH = "http://127.0.0.1";


    public DataActionPanel(CardLayout cardLayout, JPanel cardPanel, HouseholdDevice device) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        setLayout(new BorderLayout());

        // Initialize and add title section
        JLabel titleLabel = createTitle();
        add(titleLabel, BorderLayout.NORTH);

        // Initialize and add central container with data and action sections
        JPanel centralPanel = createCentralPanel(device);
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));
        add(centralPanel, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(centralPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize and add return button
        JButton returnButton = createReturnButton();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(returnButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createTitle() {
        JLabel titleLabel = new JLabel("Data & Action", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return titleLabel;
    }

    private JPanel createCentralPanel(HouseholdDevice device) {
        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));

        JPanel dataSection = createDataSection(device);
        centralPanel.add(dataSection);
        centralPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Adding space between sections

        JPanel actionSection = createActionSection(device);
        centralPanel.add(actionSection);

        return centralPanel;
    }

    private JPanel createDataSection(HouseholdDevice device) {
        JPanel dataSection = new JPanel(new BorderLayout());

        JLabel dataTitle = new JLabel("Data");
        dataTitle.setFont(new Font("Arial", Font.BOLD, 16));
        dataTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dataSection.add(dataTitle, BorderLayout.NORTH);

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        for (DeviceInfo data : device.getDeviceDataController().getDeviceData()) {
            RequestStatus requestStatus = SharedDB.restWrapper.sendGet(data.getChannel().getChannelPath());
            JLabel dataLabel = new JLabel(data.getDeviceInfo().getInfoName() + ": " + requestStatus.getMessage());
            dataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            dataPanel.add(dataLabel);
        }
        dataSection.add(dataPanel, BorderLayout.CENTER);

        return dataSection;
    }

    private JPanel createActionSection(HouseholdDevice device) {
        JPanel actionSection = new JPanel(new BorderLayout());

        JLabel actionTitle = new JLabel("Actions");
        actionTitle.setFont(new Font("Arial", Font.BOLD, 16));
        actionTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        actionSection.add(actionTitle, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        for (DeviceAction action : device.getDeviceActionController().getDeviceActions()) {
            if (action.isAvailable()) {
                switch (action.getWidget().getText()) {
                    case "Dropdown":
                        JLabel actionComboboxLabel = new JLabel(action.getName());
                        JComboBox actionCombobox = new JComboBox();
                        actionComboboxLabel.setToolTipText(action.getDescription());
                        actionComboboxLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        actionCombobox.setAlignmentX(Component.LEFT_ALIGNMENT);
                        //TODO: add action
                        actionCombobox.addActionListener(_ -> {
                            //SharedDB.restWrapper.sendPost(action.getChannel().getChannelPath(), action.p);
                        });
                        actionPanel.add(actionComboboxLabel);
                        actionPanel.add(actionCombobox);
                        break;

                    case "Slider":
                        JLabel actionSliderLabel = new JLabel(action.getName());
                        JSlider actionSlider = new JSlider();
                        actionSliderLabel.setToolTipText(action.getDescription());
                        actionSliderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        actionSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
                        //TODO: add action with send button
                        actionPanel.add(actionSliderLabel);
                        actionPanel.add(actionSlider);
                        break;

                    case "Switch":
                        JToggleButton actionSwitch = new JToggleButton(action.getName());
                        actionSwitch.setToolTipText(action.getDescription());
                        actionSwitch.setAlignmentX(Component.LEFT_ALIGNMENT);
                        RequestStatus temp = SharedDB.restWrapper.sendGet(action.getChannel().getChannelPath());
                        switch (temp.getMessage()){
                            case "On":
                                actionSwitch.setSelected(true);
                                break;
                            case "Off":
                                actionSwitch.setSelected(false);
                                break;
                        }
                        actionSwitch.addItemListener(new ItemListener() {
                            public void itemStateChanged(ItemEvent ev) {
                                if (ev.getStateChange() == ItemEvent.SELECTED) {
                                    SharedDB.restWrapper.sendPost(action.getChannel().getChannelPath(), "On");
                                } else if (ev.getStateChange() == ItemEvent.DESELECTED) {
                                    SharedDB.restWrapper.sendPost(action.getChannel().getChannelPath(), "Off");
                                }
                            }
                        });
                        actionPanel.add(actionSwitch);
                        break;
                }
            }
        }
        actionSection.add(actionPanel, BorderLayout.CENTER);

        return actionSection;
    }

    private JButton createReturnButton() {
        JButton returnButton = new JButton("Go back to main screen");

        returnButton.addActionListener(_ -> cardLayout.show(cardPanel, "MainPanel"));
        return returnButton;
    }
}
