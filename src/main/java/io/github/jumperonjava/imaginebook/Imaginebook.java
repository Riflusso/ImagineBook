package io.github.jumperonjava.imaginebook;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.jumperonjava.imaginebook.util.VersionFunctions;
import net.minecraft.client.gui.DrawContext;
import org.joml.Quaternionf;



/*? if fabric {*/
import net.fabricmc.loader.api.FabricLoader;
/*?} elif neoforge {*/
/*import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
*//*?}*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Imaginebook {
    public static final String MOD_ID = "imaginebook";
    public static final String TEST_BALLER = "https://i.kym-cdn.com/photos/images/original/002/461/188/20d.png";

    public static void init() {
        createImagineBookFolder();
    }

    public static int LENGTH = 1023;
    public static boolean cancelledFinalize = false;

    public static String fixImgurLink(String link) {
        if (link.startsWith("https://imgur.com/")) {
            String imageId = link.substring("https://imgur.com/".length());
            return "https://i.imgur.com/" + imageId + ".png";
        }
        return link;
    }

    public static void createImagineBookFolder() {
        Path imagineBookPath;

        //? if fabric {
        imagineBookPath = FabricLoader.getInstance().getGameDir().resolve("imaginebook");
        //?} elif neoforge {
        /*imagineBookPath = FMLPaths.GAMEDIR.get().resolve("imaginebook");
         *///?}

        try {
            if (!Files.exists(imagineBookPath)) {
                Files.createDirectories(imagineBookPath);
                LOGGER.info("imaginebook folder created at: " + imagineBookPath.toString());
            } else {
                //LOGGER.info("imaginebook folder already exists at: " + imagineBookPath.toString());
            }
        } catch (IOException e) {
            LOGGER.info("Failed to create imaginebook folder: " + e.getMessage());
        }
    }


    public static void renderImage(DrawContext context, int bookX, int bookY, ImageData imageData, ImageRequest image, Image.ImageSize nativeImage) {
        var w = imageData.width(nativeImage);
        var h = imageData.height(nativeImage);
        context.getMatrices().push();

        context.getMatrices().translate(
                bookX + imageData.x(),
                bookY + imageData.y(),
                0);


        context.getMatrices().translate(w / 2, h / 2, 0);
        context.getMatrices().multiply(new Quaternionf().rotateZ((float) Math.toRadians(imageData.rotation)));
        context.getMatrices().translate(-w / 2, -h / 2, 0);

        VersionFunctions.drawTexture(context, image.getTexture().getLeft(),
                0,
                0,
                (float) 0,
                (float) 0,
                (int) imageData.width(nativeImage),
                (int) imageData.height(nativeImage),
                (int) imageData.width(nativeImage),
                (int) imageData.height(nativeImage)
        );
        context.getMatrices().pop();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();

    }

    public static Logger LOGGER = LoggerFactory.getLogger("ImagineBook");
}
