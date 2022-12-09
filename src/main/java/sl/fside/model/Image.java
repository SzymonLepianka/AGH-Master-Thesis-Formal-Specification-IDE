package sl.fside.model;

import java.io.*;
import java.util.*;

public class Image {

    private final UUID id;

    private File imageFile;

    public Image(UUID id, File imageFile) {
        this.id = id;
        this.imageFile = imageFile;
    }

    public UUID getId() {
        return id;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

}
