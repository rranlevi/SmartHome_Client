import javax.swing.*;
import java.awt.*;
import java.util.Base64;

public class Utils {
    public static ImageIcon decodeBase64ToImage(String base64String, int size_X, int size_Y) {
        byte[] imageBytes = Base64.getDecoder().decode(base64String);
        Image image = Toolkit.getDefaultToolkit().createImage(imageBytes);
        Image scaledImage = image.getScaledInstance(size_X, size_Y, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    public static String fixNumOfChars(String input, int numChars) {
        if (input.length() > numChars - 3) {  // 18 characters + 2 for the ".."
            return input.substring(0, numChars - 3) + "...";
        } else {
            return String.format("%-20s", input); // Pads with spaces on the right if the string is less than 20 characters
        }
    }
}
