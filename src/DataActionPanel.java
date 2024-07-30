import Classes.HouseholdDevice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DataActionPanel extends JPanel{
    public DataActionPanel(CardLayout cardLayout, JPanel cardPanel, HouseholdDevice device) {
        setLayout(new BorderLayout());
        add(new JLabel("Data & Action: "), BorderLayout.NORTH);


    }
}



//JButton button2 = new JButton("Device X");
//        button2.addActionListener(new ActionListener() {
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        // Create a HouseholdDevice object
//        // Create a new DataActionPanel for this device
//        DataActionPanel dataActionPanel = new DataActionPanel(cardLayout, cardPanel, SharedDB.devices.get(1));
//
//        // Add the new panel to the cardPanel and switch to it
//        cardPanel.add(dataActionPanel, "DataActionPanel");
//        cardLayout.show(cardPanel, "DataActionPanel");
//    }
//});
//
//add(button2, BorderLayout.SOUTH);