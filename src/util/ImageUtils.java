package util;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
* Util klass som skalar bilder och sätter default bild om ingen bild hittas
 */

// Final så ingen kan ärva klassen
public final class ImageUtils {

    // Privat konstruktor så ingen kan skapa en instans
    private ImageUtils() {}

    public static ImageIcon createScaledImageIcon(String path, int width, int height) {

        // Sätter defaultbild om filepathen inte hittas
        File file = new File(path);
        if (!file.exists() || !file.isFile())  {
            path = "src/resources/pictures/default_picture.png";
        }

        ImageIcon originalIcon = new ImageIcon(path);
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return new ImageIcon(scaledImage);
    }
}
