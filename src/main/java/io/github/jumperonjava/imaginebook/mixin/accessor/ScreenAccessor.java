package io.github.jumperonjava.imaginebook.mixin.accessor;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Invoker("remove")
    void invokeRemove(Element element);

    @Accessor("drawables")
    List<Drawable> getDrawables();

    @Invoker("blur")
    void invokeBlur();
}
