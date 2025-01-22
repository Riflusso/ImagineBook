package io.github.jumperonjava.imaginebook;

import net.minecraft.client.texture.NativeImage;

import java.util.Locale;
import java.util.Objects;

public class ImageData {
    public String url;
    public float x;
    public float y;
    public float width=1;
    public float height=1;
    public float rotation;

    public ImageData(String url, short x, short y, float width, float height) {
        this.url = url;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public ImageData(ImageData image) {
        this.url = image.url;
        this.x = image.x;
        this.y = image.y;
        this.width = image.width;
        this.height = image.height;
        this.rotation = image.rotation;
    }
    public ImageData() {}

    public static boolean isMouseOverImage(ImageData imageData, double mouseX, double mouseY, int i) {
        int bookX = i - 96;
        int bookY = 2;

        var image = new ImageRequest(imageData.url);
        Image.ImageSize nativeImage = image.getTexture().getRight();

        double imageX1 = imageData.x() + bookX;
        double imageY1 = imageData.y() + bookY;
        double imageX2 = imageX1 + imageData.width(nativeImage);
        double imageY2 = imageY1 + imageData.height(nativeImage);

        double minX = Math.min(imageX1, imageX2);
        double maxX = Math.max(imageX1, imageX2);
        double minY = Math.min(imageY1, imageY2);
        double maxY = Math.max(imageY1, imageY2);

        return mouseX >= minX && mouseX < maxX && mouseY >= minY && mouseY < maxY;
    }

    public String url() {
        return url;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float width(Image.ImageSize nativeImage) {
        return width*nativeImage.getWidth();
    }

    public float height(Image.ImageSize nativeImage) {
        return height*nativeImage.getHeight();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ImageData) obj;
        return Objects.equals(this.url, that.url) &&
                this.x == that.x &&
                this.y == that.y &&
                this.width == that.width &&
                this.height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, x, y, width, height);
    }

    @Override
    public String toString() {
        return "ImageDefinition[" +
                "url=" + url + ", " +
                "x=" + x + ", " +
                "y=" + y + ", " +
                "width=" + width + ", " +
                "height=" + height + ']';
    }

    public String bookString() {
        return String.format(Locale.US, "[%s,%.2f,%.2f,%.2f,%.2f,%.2f]",
                url,
                x,
                y,
                width * 100,
                height * 100,
                rotation
        ).replaceAll("\\.00(?!\\d)", "").replaceAll("(\\.\\d)0(?!\\d)", "$1");
    }


    public void rotate(float degrees) {
        this.rotation += degrees;
        this.rotation = Math.floorMod((int) rotation,360);
    }
}
