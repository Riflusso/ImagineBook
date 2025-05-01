package io.github.jumperonjava.imaginebook.image;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.jumperonjava.imaginebook.resolvers.AsyncImageDownloader;
import io.github.jumperonjava.imaginebook.Imaginebook;
import net.minecraft.client.gui.DrawContext;
import org.joml.Quaternionf;

import java.util.Locale;
import java.util.Objects;


public class ImageData {
    private String url;
    public float x;
    public float y;
    public float widthFraction = 1;
    public float heightFraction = 1;
    public float rotation;

    public ImageData(String url, short x, short y, float widthFraction, float heightFraction) {
        this.setUrl(url);
        this.x = x;
        this.y = y;
        this.widthFraction = widthFraction;
        this.heightFraction = heightFraction;
    }

    public ImageData(ImageData image) {
        this.setUrl(image.getUrl());
        this.x = image.x;
        this.y = image.y;
        this.widthFraction = image.widthFraction;
        this.heightFraction = image.heightFraction;
        this.rotation = image.rotation;
    }

    public ImageData() {
                
    }

    public static boolean isMouseOverImage(ImageData imageData, double mouseX, double mouseY, int i) {
        int bookX = i - 96;
        int bookY = 2;

        var image = imageData.getImage();

        double imageX1 = imageData.x() + bookX;
        double imageY1 = imageData.y() + bookY;
        double imageX2 = imageX1 + imageData.renderWidth();
        double imageY2 = imageY1 + imageData.renderHeight();

        double minX = Math.min(imageX1, imageX2);
        double maxX = Math.max(imageX1, imageX2);
        double minY = Math.min(imageY1, imageY2);
        double maxY = Math.max(imageY1, imageY2);

        return mouseX >= minX && mouseX < maxX && mouseY >= minY && mouseY < maxY;
    }

    public void renderImage(DrawContext context, int bookX, int bookY) {
        var image = getImage();
        var size = image.getSize();
        var w = renderWidth();
        var h = renderHeight();
        context.getMatrices().push();

        context.getMatrices().translate(
                bookX + x(),
                bookY + y(),
                0);


        context.getMatrices().translate(w / 2, h / 2, 0);
        context.getMatrices().multiply(new Quaternionf().rotateZ((float) Math.toRadians(rotation)));
        context.getMatrices().translate(-w / 2, -h / 2, 0);

        context.getMatrices().scale(widthFraction, heightFraction, 1);
        image.render(context);

        context.getMatrices().pop();

    }

    public BookDrawable getImage() {
        try{
            var split = this.getUrl().split(":",2);
            return Imaginebook.getResolver(split[0]).resolve(split[1]);
        }catch(Exception e){
            return AsyncImageDownloader.ERROR_IMAGE;
        }
    }

    public String url() {
        return getUrl();
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float widthFraction() {
        return widthFraction;
    }

    public float heightFraction() {
        return heightFraction;
    }
    public float renderWidth() {
        return widthFraction * (float) getImage().getSize().getWidth();
    }

    public float renderHeight() {
        return heightFraction * (float) getImage().getSize().getHeight();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ImageData) obj;
        return Objects.equals(this.getUrl(), that.getUrl()) &&
                this.x == that.x &&
                this.y == that.y &&
                this.widthFraction == that.widthFraction &&
                this.heightFraction == that.heightFraction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(), x, y, widthFraction, heightFraction);
    }

    @Override
    public String toString() {
        return "ImageDefinition[" +
                "url=" + getUrl() + ", " +
                "x=" + x + ", " +
                "y=" + y + ", " +
                "width=" + widthFraction + ", " +
                "height=" + heightFraction + ']';
    }

    public String bookString() {
        return String.format(Locale.US, "[%s,%.2f,%.2f,%.2f,%.2f,%.2f]",
                getUrl(),
                x,
                y,
                widthFraction * 100,
                heightFraction * 100,
                rotation
        ).replaceAll("\\.00(?!\\d)", "").replaceAll("(\\.\\d)0(?!\\d)", "$1");
    }


    public void rotate(float degrees) {
        this.rotation += degrees;
        this.rotation = Math.floorMod((int) rotation, 360);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url.replace(",","");
    }
}
