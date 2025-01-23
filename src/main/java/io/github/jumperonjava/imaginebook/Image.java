package io.github.jumperonjava.imaginebook;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class Image {
    private final Identifier identifier;
    private final ImageSize size;

    public ImageSize getSize() {
        return size;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Image(Identifier identifier, ImageSize size) {
        this.identifier = identifier;
        this.size = size;
    }
    public Image(Identifier identifier) {
        this.identifier = identifier;
        //? if >= 1.21.4 {
        /*var texture = MinecraftClient.getInstance().getTextureManager().getTexture(identifier);
        *///?} else {
        var texture = MinecraftClient.getInstance().getTextureManager().getOrDefault(identifier,MissingSprite.getMissingSpriteTexture());
        //?}
        if (texture instanceof NativeImageBackedTexture imageBackedTexture){
            size = new ImageSize(imageBackedTexture.getImage().getWidth(), imageBackedTexture.getImage().getHeight());
        }
        else {
            size = new ImageSize(64,64);
        }
    }
    public static class ImageSize {
        private final int width;
        private final int height;

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        private int min() {
            return Math.min(width, height);
        }

        public int getWidth() {
            return width*100/min();
        }

        public int getHeight() {
            return height*100/min();
        }
    }
}
