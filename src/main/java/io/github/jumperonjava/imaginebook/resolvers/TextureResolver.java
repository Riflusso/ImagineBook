package io.github.jumperonjava.imaginebook.resolvers;

import io.github.jumperonjava.imaginebook.AsyncImageDownloader;
import io.github.jumperonjava.imaginebook.Image;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import static io.github.jumperonjava.imaginebook.AsyncImageDownloader.ERROR_IMAGE;

public class TextureResolver implements Resolver {
    public static final TextureResolver INSTANCE = new TextureResolver();

    @Override
    public Image resolve(String path) {
        var id = Identifier.tryParse(path);
        if (id == null) {
            return ERROR_IMAGE;
        }
        return new Image(id);
    }
}
