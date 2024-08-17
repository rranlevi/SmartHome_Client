import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.TimerTask;
import Classes.*;
import java.util.Timer;


public class DataActionPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final HouseholdDevice device;
    private static Integer counter = 1;
    private JLabel dataLabel;
    private Timer timer;


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
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Add central container with data and action sections
        JPanel centralPanel = createCentralPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(centralPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Add return button
        JButton returnButton = createReturnButton();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(returnButton);
        add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JPanel createTitlePanel() {
        // Create the main panel with a GridBagLayout for flexible layout management
        JPanel devicePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Set padding around components within the GridBagLayout
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Components will stretch horizontally to fill the space

        // Create a panel to hold the icon and device information on the left side
        JPanel descriptionPanel = new JPanel(new BorderLayout());

        // Decode the device image and add it to a JLabel
        ImageIcon imageIcon = Utils.decodeBase64ToImage(device.getDeviceImage(), 34, 34);
        JLabel imageLabel = new JLabel(imageIcon);
        descriptionPanel.add(imageLabel, BorderLayout.WEST); // Align the image to the left side of the description panel

        // Create a label for the device information (name, room, description) and add it to the right of the image
        JLabel deviceInfo = new JLabel(
                "<html>" + device.getDeviceName() +
                "<br>Room: " + device.getDeviceRoom() +
                "<br>Description: " + Utils.fixNumOfChars(device.getDescription(), 20) + "</html>");
        deviceInfo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Add some space around the text
        deviceInfo.setToolTipText("Description: " + device.getDescription());
        descriptionPanel.add(deviceInfo, BorderLayout.CENTER); // Center the text next to the image

        // Position the description panel on the left side of the devicePanel
        gbc.gridx = 0; // Column 0
        gbc.gridy = 0; // Row 0
        gbc.anchor = GridBagConstraints.WEST; // Align it to the left
        devicePanel.add(descriptionPanel, gbc); // Add the description panel to the device panel

        // Create the "Data & Action" title and style it
        JLabel titleLabel = new JLabel("Data & Action", JLabel.RIGHT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Set font style and size
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add some padding around the title

        // Position the title on the right side of the devicePanel
        gbc.gridx = 1; // Column 1
        gbc.gridy = 0; // Same row as the description
        gbc.anchor = GridBagConstraints.EAST; // Align it to the right
        devicePanel.add(titleLabel, gbc); // Add the title label to the device panel

        return devicePanel;
    }

    private JPanel createCentralPanel() {
        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));

        // Create and add the data section to the central panel
        JPanel dataSection = createDataSection();
        centralPanel.add(dataSection);

        // Add a vertical gap (10 pixels high) between the data section and the action section
        centralPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Create and add the action section to the central panel
        JPanel actionSection = createActionSection();
        centralPanel.add(actionSection);

        return centralPanel;
    }

    private JPanel createUnderlinedTitle(String title) {
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel, BorderLayout.NORTH);

        // Create a horizontal separator (underline) with a black color
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.BLACK);
        titlePanel.add(separator, BorderLayout.SOUTH);

        return titlePanel;
    }


    private JPanel createDataSection() {
        JPanel dataSection = new JPanel(new BorderLayout());
        dataSection.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 20)); // Add padding

        // Create the underlined title
        JPanel dataTitlePanel = createUnderlinedTitle("Data");
        dataSection.add(dataTitlePanel, BorderLayout.NORTH);

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));

        // Iterate through each data item provided by the device's data controller
        for (DeviceInfo data : device.getDeviceDataController().getDeviceData()) {
            RequestStatus requestStatus;
            try {
                requestStatus = SharedDB.restWrapper.sendGet(data.getChannel().getChannelPath());
            } catch (Exception e) {
                System.out.println("[ERROR] Couldn't get data from server");
                e.printStackTrace();
                return new JPanel() {{
                    add(new JLabel("[ERROR] Couldn't get data from server"));
                }};
            }

            // Create a panel for each data item
            JPanel dataItemPanel = new JPanel();
            dataItemPanel.setLayout(new BoxLayout(dataItemPanel, BoxLayout.Y_AXIS)); // Changed to vertical layout
            dataItemPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Increased spacing between items


            // Create a label for the data item's name with styling
            JLabel infoNameLabel = new JLabel(data.getDeviceInfo().getInfoName());
            infoNameLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Larger font size
            infoNameLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Square border
            infoNameLabel.setOpaque(true);
            infoNameLabel.setBackground(new Color(230, 230, 230)); // Light gray background
            infoNameLabel.setPreferredSize(new Dimension(200, 40)); // Increased size for square look
            infoNameLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text inside the square
            infoNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Check if the data is an image
            if (requestStatus.getMessage().startsWith("image:")) {
                try {
                    ImageIcon imageIcon = Utils.decodeBase64ToImage(requestStatus.getMessage().replace("image:", ""), 400, 400);
                    dataLabel = new JLabel(imageIcon);
                } catch (Exception e) {
                    System.out.println("[ERROR] Couldn't covert message to Image");
                    dataLabel = new JLabel("[ERROR] Couldn't covert message to Image");
                }
            } else {
                dataLabel = new JLabel(requestStatus.getMessage());
                dataLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Larger font size
                dataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                dataLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // Adding some space below the info name
            }// End of if (requestStatus.getMessage().startsWith("image:"))

            dataItemPanel.add(infoNameLabel);
            dataItemPanel.add(dataLabel);

            dataPanel.add(dataItemPanel);
        }// End of for (DeviceInfo data : device.getDeviceDataController().getDeviceData())

        dataSection.add(dataPanel, BorderLayout.CENTER);

        return dataSection;
    } // End of createDataSection function


    private JPanel createActionSection() {
        JPanel actionSection = new JPanel(new BorderLayout());
        actionSection.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // Add padding

        // Create the underlined title
        JPanel actionTitlePanel = createUnderlinedTitle("Actions");
        actionSection.add(actionTitlePanel, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add spacing between actions

        // Iterate through each action provided by the device's action controller
        for (DeviceAction action : device.getDeviceActionController().getDeviceActions()) {
            if (action.isAvailable()) {
                // Handle different types of widgets associated with actions
                switch (action.getWidget().getClass().getSimpleName()) {
                    case "Dropdown":
                        JLabel actionComboboxLabel = new JLabel(action.getName());
                        JComboBox actionCombobox = new JComboBox();
                        actionComboboxLabel.setToolTipText(action.getDescription());
                        actionCombobox.setMaximumSize(new Dimension(Integer.MAX_VALUE, actionCombobox.getPreferredSize().height));
                        actionComboboxLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        actionCombobox.setAlignmentX(Component.LEFT_ALIGNMENT);

                        // Populate the combo box with options from the action's widget
                        for (String item : ((Dropdown) action.getWidget()).getListOptions()) {
                            actionCombobox.addItem(item);
                        }

                        try {
                            RequestStatus dropDownData = SharedDB.restWrapper.sendGet(action.getDataChannel().getChannelPath());
                            actionCombobox.setSelectedItem(dropDownData.getMessage());
                            // Add an action listener to handle combo box changes and update the server
                            actionCombobox.addActionListener(_ -> {
                                String selectedItem = (String) actionCombobox.getSelectedItem();
                                try {
                                    SharedDB.restWrapper.sendPost(action.getActionChannel().getChannelPath(), selectedItem);
                                    showLoadingIcon(); // Simulate server response
                                    initializeComponents();
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null, "[ERROR] Couldn't send data to server", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            });
                        } catch (Exception ex) {
                            return new JPanel() {{
                                add(new JLabel("[ERROR] Couldn't get data from server"));
                            }};
                        }

                        // Create a container panel with FlowLayout for proper alignment
                        JPanel dropdownContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
                        dropdownContainer.add(actionComboboxLabel);
                        dropdownContainer.add(actionCombobox);

                        // Add the switchContainer directly to the actionPanel
                        actionPanel.add(dropdownContainer);
                        break;

                    case "Slider":
                        JLabel actionSliderLabel = new JLabel(action.getName());
                        JSlider actionSlider = new JSlider(
                                Integer.parseInt(((Slider) action.getWidget()).getSliderMinValue()),
                                Integer.parseInt(((Slider) action.getWidget()).getSliderMaxValue()));
                        JLabel sliderValueLabel = new JLabel();
                        JButton sendButton = new JButton("Send"); // Send button to send the slider value to the server
                        sendButton.setToolTipText(action.getDescription());

                        // Set the appearance of the slider
                        actionSlider.setUI(new GradientSliderUI(actionSlider));
                        actionSlider.setPreferredSize(new Dimension(200, 50)); // Adjust the width
                        actionSlider.setOpaque(false);
                        try {
                            // Fetch initial value from the server and set the slider's value
                            RequestStatus sliderData = SharedDB.restWrapper.sendGet(action.getDataChannel().getChannelPath());
                            actionSlider.setValue(Integer.parseInt(sliderData.getMessage()));
                            sliderValueLabel.setText("Value: " + actionSlider.getValue());

                            // Update the slider value label when the slider is moved
                            actionSlider.addChangeListener(_ -> sliderValueLabel.setText("Value: " + actionSlider.getValue()));

                            sendButton.addActionListener(_ -> {
                                try {
                                    SharedDB.restWrapper.sendPost(action.getActionChannel().getChannelPath(), String.valueOf(actionSlider.getValue()));
                                    showLoadingIcon(); // Simulate server response
                                    initializeComponents();
                                    sliderValueLabel.setText("Value: " + actionSlider.getValue());
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null, "[ERROR] Couldn't send data to server", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            });
                        } catch (Exception ex) {
                            return new JPanel() {{
                                add(new JLabel("[ERROR] Couldn't get data from server"));
                            }};
                        }

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
                        JPanel sliderContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
                        sliderContainer.add(sliderLabelPanel);
                        sliderContainer.add(valueSendPanel);

                        // Add the sliderContainer directly to the actionPanel
                        actionPanel.add(sliderContainer);
                        break;

                    case "Switch":
                        JToggleButton actionSwitch = new JToggleButton(action.getName());
                        actionSwitch.setToolTipText(action.getDescription());
                        actionSwitch.setAlignmentX(Component.LEFT_ALIGNMENT);
                        try {
                            // Fetch the current state from the server and update the switch's state
                            RequestStatus switchData = SharedDB.restWrapper.sendGet(action.getDataChannel().getChannelPath());

                            // Style the power switch with custom colors
                            switch (switchData.getMessage()) {
                                case "On":
                                    actionSwitch.setSelected(true);
                                    if (action.getName().equals("Power")) {
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

                            // Add an item listener to handle switch state changes and update the server
                            actionSwitch.addItemListener(ev -> {
                                try {
                                    if (ev.getStateChange() == ItemEvent.SELECTED) {
                                        SharedDB.restWrapper.sendPost(action.getActionChannel().getChannelPath(), "On");
                                    } else if (ev.getStateChange() == ItemEvent.DESELECTED) {
                                        SharedDB.restWrapper.sendPost(action.getActionChannel().getChannelPath(), "Off");
                                    }
                                    showLoadingIcon(); // Simulate server response
                                    initializeComponents();
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null, "[ERROR] Couldn't send data to server", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            });
                        } catch (Exception ex) {
                            return new JPanel() {{
                                add(new JLabel("[ERROR] Couldn't get data from server"));
                            }};
                        }


                        // Create a container panel with FlowLayout for proper alignment
                        JPanel switchContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
                        switchContainer.add(actionSwitch);

                        // Add the switchContainer directly to the actionPanel
                        actionPanel.add(switchContainer);
                        break;

                    case "CameraStream":
                        // Create a timer to refresh the camera stream image
                        timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                // Refresh image here
                                refreshImage(action.getActionChannel().getChannelPath(), action.getDataChannel().getChannelPath());
                            }
                        }, 50, 50);
                        break;
                } // End of switch (action.getWidget().getClass().getSimpleName())
            } // End of if (action.isAvailable())
        } // End of for (DeviceAction action : device.getDeviceActionController().getDeviceActions())

        actionSection.add(actionPanel, BorderLayout.CENTER);
        return actionSection;
    } // End of createActionSection function

    private JButton createReturnButton() {
        JButton returnButton = new JButton("Go back to main screen");
        returnButton.addActionListener(_ -> {
            // Cancel the timer to stop the CameraStream refresh
            if (timer != null) {
                timer.cancel();
                timer.purge(); // Clean up canceled tasks
            }
            // Switch the view back to the main panel using CardLayout
            cardLayout.show(cardPanel, "MainPanel");
        });
        return returnButton;
    }

    private void showLoadingIcon() {
        // Create an overlay panel
        JPanel overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Set the background color to fully opaque white
                g.setColor(new Color(255, 255, 255, 255));
                // Fill the panel with the white color
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlayPanel.setOpaque(false); // Set the overlay panel as non-opaque to layer on top of existing content
        overlayPanel.setLayout(new GridBagLayout()); // Use GridBagLayout to center the loading icon

        // Add the loading icon to the overlay panel
        JLabel loadingLabel = new JLabel(new ImageIcon("Images/reload_gif.gif"));
        overlayPanel.add(loadingLabel); // Center the loading label within the overlay panel

        // Get the layered pane of the root frame to add the overlay panel
        JLayeredPane layeredPane = getRootPane().getLayeredPane();
        // Set the bounds of the overlay panel to cover the entire frame
        overlayPanel.setBounds(0, 0, getRootPane().getWidth(), getRootPane().getHeight());
        // Add the overlay panel to the modal layer, so it appears on top of everything else
        layeredPane.add(overlayPanel, JLayeredPane.MODAL_LAYER);

        // Revalidate and repaint the frame to make sure the overlay is visible
        revalidate();
        repaint();

        // Create a new thread to remove the overlay panel after a delay
        new Thread(() -> {
            try {
                // Simulate a loading delay of 400 milliseconds
                Thread.sleep(400);
            } catch (InterruptedException ex) {
                ex.printStackTrace(); // Handle any interruption exceptions
            } finally {
                // Remove the overlay panel from the layered pane on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    layeredPane.remove(overlayPanel);
                    revalidate(); // Revalidate and repaint to refresh the display
                    repaint();
                });
            }
        }).start();
    }

    private void refreshImage(String actionChannelPath, String dataChannelPath) {
        try {
            SharedDB.restWrapper.sendPost(actionChannelPath, (counter++).toString());
            RequestStatus requestStatus = SharedDB.restWrapper.sendGet(dataChannelPath);
            ImageIcon imageIcon = Utils.decodeBase64ToImage(requestStatus.getMessage().replace("image:", ""), 400, 400);
            dataLabel.setIcon(imageIcon); // Update the dataLabel with the new image
        } catch (Exception e) {
            System.out.println("[ERROR] Couldn't covert message to Image");
            dataLabel = new JLabel("[ERROR] Couldn't covert message to Image");
        }
        dataLabel.revalidate();
        dataLabel.repaint();
    }
} // End of DataActionPanel Class

// Custom UI class for a gradient-colored slider track
class GradientSliderUI extends BasicSliderUI {

    // Constructor that takes a JSlider component
    public GradientSliderUI(JSlider slider) {
        super(slider); // Call the superclass constructor to set up the slider UI
    }

    // Override the method to paint the slider track with a gradient
    @Override
    public void paintTrack(Graphics g) {
        // Cast the Graphics object to Graphics2D for advanced painting features
        Graphics2D g2d = (Graphics2D) g;

        // Get the bounds of the slider track
        Rectangle trackBounds = trackRect;

        // Create a gradient paint from blue to red along the width of the track
        GradientPaint gp = new GradientPaint(
                trackBounds.x, trackBounds.y, Color.BLUE,       // Start color (blue) at the beginning of the track
                trackBounds.width, trackBounds.y, Color.RED     // End color (red) at the end of the track
        );

        // Set the gradient paint to the Graphics2D object
        g2d.setPaint(gp);

        // Fill the track with the gradient paint, adjusting for the center position
        g2d.fillRect(trackBounds.x, trackBounds.y + (trackBounds.height / 2) - 2, trackBounds.width, 4);
    }

    // Override the method to paint the slider thumb
    @Override
    public void paintThumb(Graphics g) {
        // Set the thumb color to white before painting it
        g.setColor(Color.WHITE);

        // Call the superclass method to paint the thumb with the chosen color
        super.paintThumb(g);
    }
}