import javax.swing.*;
import java.awt.*;
import Classes.*;

public class DataActionPanel extends JPanel {

    public DataActionPanel(CardLayout cardLayout, JPanel cardPanel, HouseholdDevice device) {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Initialize and add title section
        JLabel titleLabel = createTitle();
        add(titleLabel, BorderLayout.NORTH);

        // Initialize and add central container with data and action sections
        JPanel centralPanel = createCentralPanel(device);
        add(centralPanel, BorderLayout.CENTER);

        // Initialize and add return button
        JButton returnButton = createReturnButton(cardLayout, cardPanel);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
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
        centralPanel.setBackground(Color.WHITE);

        JPanel dataSection = createDataSection(device);
        centralPanel.add(dataSection);
        centralPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Adding space between sections

        JPanel actionSection = createActionSection(device);
        centralPanel.add(actionSection);

        return centralPanel;
    }

    private JPanel createDataSection(HouseholdDevice device) {
        JPanel dataSection = new JPanel(new BorderLayout());
        dataSection.setBackground(Color.WHITE);

        JLabel dataTitle = new JLabel("Data");
        dataTitle.setFont(new Font("Arial", Font.BOLD, 16));
        dataTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dataSection.add(dataTitle, BorderLayout.NORTH);

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setBackground(Color.WHITE);
        for (DeviceInfo data : device.getDeviceDataController().getDeviceData()) {
            JLabel dataLabel = new JLabel(data.getDeviceInfo().getInfoName() + ": " + data.getDeviceInfo().getInfoValue());
            dataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            dataPanel.add(dataLabel);
        }
        dataSection.add(dataPanel, BorderLayout.CENTER);

        return dataSection;
    }

    private JPanel createActionSection(HouseholdDevice device) {
        JPanel actionSection = new JPanel(new BorderLayout());
        actionSection.setBackground(Color.WHITE);

        JLabel actionTitle = new JLabel("Actions");
        actionTitle.setFont(new Font("Arial", Font.BOLD, 16));
        actionTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        actionSection.add(actionTitle, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBackground(Color.WHITE);
        for (DeviceAction action : device.getDeviceActionController().getDeviceActions()) {
            if (action.isAvailable()) {
                switch (action.getWidget().getText()) {
                    case "Button":
                        JButton actionButton = new JButton(action.getName());
                        actionButton.setToolTipText(action.getDescription());
                        actionButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                        actionPanel.add(actionButton);
                        break;

                    case "Slider":
                        JLabel actionSliderLabel = new JLabel(action.getName());
                        JSlider actionSlider = new JSlider();
                        actionSliderLabel.setToolTipText(action.getDescription());
                        actionSliderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        actionSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
                        actionPanel.add(actionSliderLabel);
                        actionPanel.add(actionSlider);
                        break;

                    case "Switch":
                        JToggleButton actionSwitch = new JToggleButton(action.getName());
                        actionSwitch.setToolTipText(action.getDescription());
                        actionSwitch.setAlignmentX(Component.LEFT_ALIGNMENT);
                        actionPanel.add(actionSwitch);
                        break;
                }
            }
        }
        actionSection.add(actionPanel, BorderLayout.CENTER);

        return actionSection;
    }

    private JButton createReturnButton(CardLayout cardLayout, JPanel cardPanel) {
        JButton returnButton = new JButton("Return to Main Menu");
        returnButton.setFont(new Font("Arial", Font.PLAIN, 18));
        //returnButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //returnButton.setBackground(Color.WHITE);
        //returnButton.setToolTipText("Return to Main Screen");

        returnButton.addActionListener(e -> cardLayout.show(cardPanel, "MainPanel"));
        return returnButton;
    }


}
