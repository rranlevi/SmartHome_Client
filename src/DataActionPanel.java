import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Objects;

import Classes.*;


public class DataActionPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final HouseholdDevice device;

    public DataActionPanel(CardLayout cardLayout, JPanel cardPanel, HouseholdDevice device) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.device = device;

        setLayout(new BorderLayout());
        initializeComponents();
    }

    private void initializeComponents() {
        removeAll();

        // Add image and device info below the title
        JPanel deviceInfoPanel = createDeviceInfoPanel();
        add(deviceInfoPanel, BorderLayout.NORTH);

        // Initialize and add central container with data and action sections
        JPanel centralPanel = createCentralPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(centralPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize and add return button
        JButton returnButton = createReturnButton();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(returnButton);
        add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JPanel createDeviceInfoPanel() {
        JPanel devicePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add the icon and description on the left side
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        ImageIcon imageIcon = Utils.decodeBase64ToImage(device.getDeviceImage(), 34, 34);
        JLabel imageLabel = new JLabel(imageIcon);
        descriptionPanel.add(imageLabel, BorderLayout.WEST);

        JLabel deviceInfo = new JLabel("<html>" + device.getDeviceName() + "<br>Room: " + device.getDeviceRoom() + "<br>Description: " + device.getDescription() + "</html>");
        deviceInfo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        descriptionPanel.add(deviceInfo, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        devicePanel.add(descriptionPanel, gbc);

        // Add the "Data & Action" title on the right side
        JLabel titleLabel = new JLabel("Data & Action", JLabel.RIGHT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        devicePanel.add(titleLabel, gbc);

        // Remove the underline separator beneath the title
        // (Previously we added a JSeparator here, now it's removed)

        return devicePanel;
    }


    private JPanel createCentralPanel() {
        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));

        JPanel dataSection = createDataSection();
        centralPanel.add(dataSection);
        centralPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Adding space between sections

        JPanel actionSection = createActionSection();
        centralPanel.add(actionSection);

        return centralPanel;
    }

    private JPanel createUnderlinedTitle(String title) {
        JPanel titlePanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel, BorderLayout.NORTH);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.BLACK);
        titlePanel.add(separator, BorderLayout.SOUTH);

        return titlePanel;
    }


    private JPanel createDataSection() {
        JPanel dataSection = new JPanel(new BorderLayout());
        dataSection.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // Add padding

        // Create the underlined title
        JPanel dataTitlePanel = createUnderlinedTitle("Data");
        dataSection.add(dataTitlePanel, BorderLayout.NORTH);

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        for (DeviceInfo data : device.getDeviceDataController().getDeviceData()) {
            RequestStatus requestStatus = SharedDB.restWrapper.sendGet(data.getChannel().getChannelPath());

            // Create a panel for each data item
            JPanel dataItemPanel = new JPanel();
            dataItemPanel.setLayout(new BoxLayout(dataItemPanel, BoxLayout.Y_AXIS)); // Changed to vertical layout
            dataItemPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Increased spacing between items


            // Style for the info name
            JLabel infoNameLabel = new JLabel(data.getDeviceInfo().getInfoName());
            infoNameLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Larger font size
            infoNameLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Square border
            infoNameLabel.setOpaque(true);
            infoNameLabel.setBackground(new Color(230, 230, 230)); // Light gray background
            infoNameLabel.setPreferredSize(new Dimension(200, 40)); // Increased size for square look
            infoNameLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text inside the square
            infoNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Style for the message
            JLabel dataLabel;
            if (requestStatus.getMessage().length() > 1000) {
                ImageIcon imageIcon = Utils.decodeBase64ToImage(requestStatus.getMessage(), 640, 640);
                dataLabel = new JLabel(imageIcon);
            }
            else {
                dataLabel = new JLabel(requestStatus.getMessage());
                dataLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Larger font size
                dataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                dataLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // Adding some space below the info name
            }

            // Add the info name and message to the data item panel
            dataItemPanel.add(infoNameLabel);
            dataItemPanel.add(dataLabel);


            // Add the data item panel to the main data panel
            dataPanel.add(dataItemPanel);
        }

        dataSection.add(dataPanel, BorderLayout.CENTER);

        return dataSection;
    }


    private JPanel createActionSection() {
        JPanel actionSection = new JPanel(new BorderLayout());
        actionSection.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // Add padding

        // Create the underlined title without the blue line at the top
        JPanel actionTitlePanel = createUnderlinedTitle("Actions");
        actionSection.add(actionTitlePanel, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add spacing between actions

        for (DeviceAction action : device.getDeviceActionController().getDeviceActions()) {
            if (action.isAvailable()) {
                switch (action.getWidget().getClass().getSimpleName()) {
                    case "Dropdown":
                        JLabel actionComboboxLabel = new JLabel(action.getName());
                        JComboBox actionCombobox = new JComboBox();
                        actionComboboxLabel.setToolTipText(action.getDescription());
                        actionCombobox.setMaximumSize(new Dimension(Integer.MAX_VALUE, actionCombobox.getPreferredSize().height));
                        actionComboboxLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        actionCombobox.setAlignmentX(Component.LEFT_ALIGNMENT);

                        for (String item : ((Dropdown) action.getWidget()).getListOptions()) {
                            actionCombobox.addItem(item);
                        }
                        RequestStatus dropDownData = SharedDB.restWrapper.sendGet(action.getDataChannel().getChannelPath());
                        actionCombobox.setSelectedItem(dropDownData.getMessage());
                        actionCombobox.addActionListener(_ -> {
                            String selectedItem = (String) actionCombobox.getSelectedItem();
                            SharedDB.restWrapper.sendPost(action.getActionChannel().getChannelPath(), selectedItem);
                            showLoadingIcon(); // For simulating server response
                            initializeComponents();
                        });

                        // Create a container panel with FlowLayout for proper alignment
                        JPanel dropdownContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        dropdownContainer.add(actionComboboxLabel);
                        dropdownContainer.add(actionCombobox);

                        // Add the switchContainer directly to the actionPanel
                        actionPanel.add(dropdownContainer);
                        break;

                    case "Slider":
                        // Create components
                        JLabel actionSliderLabel = new JLabel(action.getName());
                        JSlider actionSlider;
                        JLabel sliderValueLabel = new JLabel();
                        JButton sendButton = new JButton("Send");

                        // Check if description contains "Temp" and set slider range accordingly
                        if (action.getName().contains("Temp")) {
                            actionSlider = new JSlider(16, 30);
                        } else {
                            actionSlider = new JSlider(); // Default slider range
                        }

                        // Set the appearance of the slider
                        actionSlider.setUI(new GradientSliderUI(actionSlider));
                        actionSlider.setPreferredSize(new Dimension(200, 50)); // Adjust the width
                        actionSlider.setOpaque(false);

                        // Fetch initial value from the server and set the slider's value
                        RequestStatus sliderData = SharedDB.restWrapper.sendGet(action.getDataChannel().getChannelPath());
                        actionSlider.setValue(Integer.parseInt(sliderData.getMessage()));
                        sliderValueLabel.setText("Value: " + actionSlider.getValue());

                        // Update the slider value label when the slider is moved
                        actionSlider.addChangeListener(_ -> sliderValueLabel.setText("Value: " + actionSlider.getValue()));

                        // Send button action to send the slider value
                        sendButton.addActionListener(_ -> {
                            SharedDB.restWrapper.sendPost(action.getActionChannel().getChannelPath(), String.valueOf(actionSlider.getValue()));
                            showLoadingIcon(); // For simulating server response
                            initializeComponents();
                            sliderValueLabel.setText("Value: " + actionSlider.getValue());
                        });

                        // Create a panel for the slider and its label
                        JPanel sliderLabelPanel = new JPanel(new BorderLayout());
                        sliderLabelPanel.add(actionSliderLabel, BorderLayout.NORTH);
                        sliderLabelPanel.add(actionSlider, BorderLayout.CENTER);

                        // Create a panel for the value label and send button
                        JPanel valueSendPanel = new JPanel();
                        valueSendPanel.setLayout(new BoxLayout(valueSendPanel, BoxLayout.Y_AXIS)); // Vertical layout for value and button
                        valueSendPanel.add(sliderValueLabel);
                        valueSendPanel.add(sendButton);

                        // Create a container panel with FlowLayout for proper alignment
                        JPanel sliderContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        sliderContainer.add(sliderLabelPanel);
                        sliderContainer.add(valueSendPanel);

                        // Add the sliderContainer directly to the actionPanel
                        actionPanel.add(sliderContainer);

                        break;

                    case "Switch":
                        JToggleButton actionSwitch = new JToggleButton(action.getName());
                        actionSwitch.setToolTipText(action.getDescription());
                        actionSwitch.setAlignmentX(Component.LEFT_ALIGNMENT);
                        RequestStatus switchData = SharedDB.restWrapper.sendGet(action.getDataChannel().getChannelPath());

                        switch (switchData.getMessage()) {
                            case "On":
                                actionSwitch.setSelected(true);
                                if (action.getName().equals("Power")) {
                                    // Disable the default selected color
                                    actionSwitch.setContentAreaFilled(false);
                                    actionSwitch.setOpaque(true);
                                    actionSwitch.setBackground(Color.GREEN);
                                }
                                break;
                            case "Off":
                                actionSwitch.setSelected(false);
                                if (action.getName().equals("Power")) {
                                    actionSwitch.setBackground(Color.RED);
                                }
                                break;
                        }

                        actionSwitch.addItemListener(ev -> {
                            if (ev.getStateChange() == ItemEvent.SELECTED) {
                                SharedDB.restWrapper.sendPost(action.getActionChannel().getChannelPath(), "On");
                            } else if (ev.getStateChange() == ItemEvent.DESELECTED) {
                                SharedDB.restWrapper.sendPost(action.getActionChannel().getChannelPath(), "Off");
                            }
                            showLoadingIcon(); // For simulating server response
                            initializeComponents();
                        });

                        // Create a container panel with FlowLayout for proper alignment
                        JPanel switchContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        switchContainer.add(actionSwitch);

                        // Add the switchContainer directly to the actionPanel
                        actionPanel.add(switchContainer);
                        break;

                    case "CameraStream":

                        break;
                }
                actionPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add space between actions
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

    private void showLoadingIcon() {
        // Create an overlay panel
        JPanel overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 255)); // Fully opaque
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlayPanel.setOpaque(false);
        overlayPanel.setLayout(new GridBagLayout());

        // Add the loading icon to the overlay panel
        JLabel loadingLabel = new JLabel(new ImageIcon("Images/reload_gif.gif"));
        overlayPanel.add(loadingLabel);

        // Add the overlay panel to the frame's layered pane
        JLayeredPane layeredPane = getRootPane().getLayeredPane();
        overlayPanel.setBounds(0, 0, getRootPane().getWidth(), getRootPane().getHeight());
        layeredPane.add(overlayPanel, JLayeredPane.MODAL_LAYER);

        // Repaint the frame to ensure the overlay is visible
        revalidate();
        repaint();

        // Remove the overlay panel after the delay
        new Thread(() -> {
            try {
                Thread.sleep(200); // Simulate loading delay
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    layeredPane.remove(overlayPanel);
                    revalidate();
                    repaint();
                });
            }
        }).start();
    }
} // End of DataActionPanel Class


// Custom UI class for gradient slider
class GradientSliderUI extends BasicSliderUI {

    public GradientSliderUI(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Rectangle trackBounds = trackRect;
        GradientPaint gp = new GradientPaint(
                trackBounds.x, trackBounds.y, Color.BLUE,
                trackBounds.width, trackBounds.y, Color.RED
        );
        g2d.setPaint(gp);
        g2d.fillRect(trackBounds.x, trackBounds.y + (trackBounds.height / 2) - 2, trackBounds.width, 4);
    }

    @Override
    public void paintThumb(Graphics g) {
        g.setColor(Color.WHITE);
        super.paintThumb(g);
    }
}

