import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

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

        // Initialize and add title section
        JLabel titleLabel = createTitle();
        add(titleLabel, BorderLayout.NORTH);

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

    private JLabel createTitle() {
        JLabel titleLabel = new JLabel("Data & Action", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return titleLabel;
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

    private JPanel createDataSection() {
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

    private JPanel createActionSection() {
        JPanel actionSection = new JPanel(new BorderLayout());

        JLabel actionTitle = new JLabel("Actions");
        actionTitle.setFont(new Font("Arial", Font.BOLD, 16));
        actionTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        actionSection.add(actionTitle, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
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

                        for(String item : ((Dropdown) action.getWidget()).getListOptions()){
                            actionCombobox.addItem(item);
                        }
                        RequestStatus dropDownData = SharedDB.restWrapper.sendGet(action.getDataChannel().getChannelPath());
                        actionCombobox.setSelectedItem(dropDownData.getMessage());
                        //TODO: add action
                        actionCombobox.addActionListener(_ -> {
                            String selectedItem = (String) actionCombobox.getSelectedItem();
                            SharedDB.restWrapper.sendPost(action.getActionChannel().getChannelPath(), selectedItem);
                            showLoadingIcon(); // For simulating server response
                            initializeComponents();
                        });
                        actionPanel.add(actionComboboxLabel);
                        actionPanel.add(actionCombobox);
                        break;

                    case "Slider":
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

                        actionSliderLabel.setToolTipText(action.getDescription());
                        actionSliderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        actionSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
                        sliderValueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        sendButton.setAlignmentX(Component.LEFT_ALIGNMENT);

                        // Update the slider value label when the slider is moved
                        RequestStatus sliderData = SharedDB.restWrapper.sendGet(action.getDataChannel().getChannelPath());
                        actionSlider.setValue(Integer.parseInt(sliderData.getMessage()));
                        sliderValueLabel.setText("Value: " + actionSlider.getValue());
                        actionSlider.addChangeListener(_ -> sliderValueLabel.setText("Value: " + actionSlider.getValue()));

                        // Send button action to send the slider value
                        sendButton.addActionListener(_ -> {
                            SharedDB.restWrapper.sendPost(action.getActionChannel().getChannelPath(), String.valueOf(actionSlider.getValue()));
                            showLoadingIcon(); // For simulating server response
                            initializeComponents();
                            sliderValueLabel.setText("Value: " + actionSlider.getValue());
                        });

                        actionPanel.add(actionSliderLabel);
                        actionPanel.add(actionSlider);
                        actionPanel.add(sliderValueLabel);
                        actionPanel.add(sendButton);
                        break;

                    case "Switch":
                        JToggleButton actionSwitch = new JToggleButton(action.getName());
                        actionSwitch.setToolTipText(action.getDescription());
                        actionSwitch.setAlignmentX(Component.LEFT_ALIGNMENT);
                        RequestStatus switchData = SharedDB.restWrapper.sendGet(action.getDataChannel().getChannelPath());
                        switch (switchData.getMessage()) {
                            case "On":
                                actionSwitch.setSelected(true);
                                break;
                            case "Off":
                                actionSwitch.setSelected(false);
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
