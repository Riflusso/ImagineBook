package io.github.jumperonjava.imaginebook.mixin.accessor;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.util.math.Rect2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BookEditScreen.PageContent.class)
public interface PageContentAccessor {
    @Accessor("lines")
    BookEditScreen.Line[] getLines();

    @Accessor("selectionRectangles")
    Rect2i[] getSelectionRectangles();

    @Accessor("position")
    BookEditScreen.Position getPosition();

    @Accessor("atEnd")
    boolean isAtEnd();
}
