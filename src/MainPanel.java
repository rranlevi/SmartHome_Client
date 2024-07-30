import Classes.Device;

import javax.swing.*;
import java.awt.CardLayout;
import Classes.*;
import java.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPanel extends JPanel {
        public MainPanel(CardLayout cardLayout, JPanel cardPanel) {

            JButton button = new JButton("Add New Devices");

            // Add an action listener to the button
            button.addActionListener(_ -> {
                // Show the second panel when the button is clicked
                cardLayout.show(cardPanel, "AddDevicesPanel");
            });

            add(button);
        }
}
