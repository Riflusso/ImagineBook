package io.github.jumperonjava.imaginebook.resolvers;

import io.github.jumperonjava.imaginebook.image.BookDrawable;
import io.github.jumperonjava.imaginebook.image.Image;
import net.minecraft.util.Identifier;

import static io.github.jumperonjava.imaginebook.resolvers.AsyncImageDownloader.ERROR_IMAGE;

public class TextureResolver implements Resolver {
    public static final TextureResolver INSTANCE = new TextureResolver();

    @Override
    public BookDrawable resolve(String path) {
        var id = Identifier.tryParse(path);
        if (id == null) {
            return ERROR_IMAGE;
        }
        return new Image(id);
    }
}
