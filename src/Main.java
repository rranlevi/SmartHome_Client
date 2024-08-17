import javax.swing.*;
import java.awt.CardLayout;

public class Main {
    public static void main(String[] args) {
        SharedDB.loadDevices(); // Has to be first!

        // Create a new frame
        JFrame frame = new JFrame("Smart Home Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 800);

        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

        MainPanel mainPanel = new MainPanel(cardLayout, cardPanel);
        AddDevicesPanel devicesPanel = new AddDevicesPanel(cardLayout,cardPanel);

        cardPanel.add(mainPanel, "MainPanel");
        cardPanel.add(devicesPanel, "AddDevicesPanel");

        frame.add(cardPanel);
        frame.setVisible(true);
    }
}
