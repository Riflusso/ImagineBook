package io.github.jumperonjava.imaginebook.image;

import net.minecraft.client.gui.DrawContext;

public interface BookDrawable {
    Image.ImageSize getSize();

    void render(DrawContext context);
}
