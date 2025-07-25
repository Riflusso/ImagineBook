package io.github.jumperonjava.imaginebook.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.*;

public class VersionFunctions {
    public static void drawTexture(DrawContext context, Identifier identifier, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        //? if < 1.21.3 {
        context.drawTexture(identifier, x, y, u, v, width, height, textureWidth, textureHeight);
        //?} else {
        /*context.drawTexture(RenderLayer::getGuiTextured, identifier, x, y, u, v, width, height, textureWidth, textureHeight);
         *///?}

    }


    //? if < 1.21.3 {
    public static ColorHelper.Argb ColorHelper = new ColorHelper.Argb();
    //?} else {
    /*public static ColorHelper ColorHelper = new ColorHelper();
     *///?}


    // List.reversed method was added only in java 21, and it crashes mod (softly, just logs errors) when running on lower java version
    // this method reimplements that functionality
    public static <T> List<T> reversed(List<T> collection) {
        var reversedList = new LinkedList<T>();
        collection.forEach((item) -> reversedList.add(0, item));
        return new ArrayList<>(reversedList);
    }
}
