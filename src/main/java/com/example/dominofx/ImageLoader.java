package com.example.dominofx;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
public class ImageLoader {
    private static final Map<String, Image> imageCache = new HashMap<>();

    public static Image getTileImage(int first, int second) {
        String imagePath = String.format("src/main/resources/com/example/dominofx/Костяшки/%d_%d.png", first, second);
        Image image = imageCache.get(imagePath);

        if (image == null) {
            try {
                File file = new File(imagePath);
                image = new Image(new FileInputStream(file));
                imageCache.put(imagePath, image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

}
