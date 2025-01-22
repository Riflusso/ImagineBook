package io.github.jumperonjava.imaginebook;

import com.ibm.icu.impl.InvalidFormatException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;

/*? if fabric {*/
import net.fabricmc.loader.api.FabricLoader;
/*?} elif neoforge {*/
/*import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
*//*?}*/


/**
 * To future coders
 * If you see any weird stuff like TextureMap field please
 * beware that I've made most of this class logic around 2
 * years ago. I'm too lazy to rewrite this at this moment,
 * and it works good enough
 */
public class ImageRequest {
    public static Identifier EMPTY_TEXTURE = Identifier.of("imaginebook","textures/not_loaded.png");
    public static Image EMPTY_IMAGE = new Image(EMPTY_TEXTURE,new Image.ImageSize(128,128),"");

    public static int maxDownloadAttepts = 5;

    public static int currentDownloads = 0;

    public static int maxParralelDownloads=4;

    private static HashMap<Integer, TextureRequestInfo> TextureMap = new HashMap<>();

    private final String link;

    public ImageRequest(String link) {
        this.link = link;
    }

    private Path getFile() {
        /*? if fabric {*/
        return FabricLoader.getInstance().getGameDir().resolve("imaginebook").resolve(getIdentifierString());
        /*?} elif neoforge {*/
        /*return FMLPaths.GAMEDIR.get().resolve("imaginebook").resolve(getIdentifierString());
        *//*?}*/
    }

    private String getIdentifierString() {
        return String.valueOf(link.hashCode());
    }

    private Identifier getIdentifier() {
        return Identifier.of(Imaginebook.MOD_ID, getIdentifierString());
    }

    private String getDownloadLink() {
        return this.link;
    }

    public Image getTexture() {

        var info = TextureMap.containsKey(hashCode()) ? TextureMap.get(hashCode()) : new TextureRequestInfo();
        switch (info.status)
        {
            case INIT -> {
                info.status =
                    Files.exists(getFile()) ?
                            TextureRequestInfo.DownloadStatus.DOWNLOADED_NOT_REGISTERED :
                            TextureRequestInfo.DownloadStatus.NOT_DOWNLOADED;
                }
            case NOT_DOWNLOADED -> {
                if(currentDownloads>maxParralelDownloads)
                    break;
                new Thread(() -> {
                    currentDownloads++;
                    info.status = TextureRequestInfo.DownloadStatus.DOWNLOADING;
                    LoggerFactory.getLogger("ImagineBook").info(String.format("Downloading %s",link));

                    try (InputStream in = new URL(getDownloadLink()).openStream();
                         OutputStream out = Files.newOutputStream(getFile(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

                        Files.createDirectories(getFile().getParent());

                        byte[] buffer = new byte[8192];
                        byte[] pngHeader = new byte[8];
                        int bytesRead;
                        long totalBytesRead = 0;
                        long maxSize = 8 * 1024 * 1024;

                        if (in.read(pngHeader) != 8 || !isPngHeaderValid(pngHeader)) {
                            info.identifier = Identifier.of("imaginebook","textures/png_error.png");
                            throw new InvalidFormatException("File is not a valid PNG.");
                        }
                        out.write(pngHeader);
                        totalBytesRead += pngHeader.length;
                        LoggerFactory.getLogger("ImagineBook").info(String.format("Download size %d", totalBytesRead));

                        while ((bytesRead = in.read(buffer)) != -1) {
                            totalBytesRead += bytesRead;
                            if (totalBytesRead > maxSize) {
                                info.identifier = Identifier.of("imaginebook","textures/size_error.png");
                                throw new InvalidFormatException("File size exceeds 1MB limit.");
                            }
                            out.write(buffer, 0, bytesRead);
                        }

                        info.status = TextureRequestInfo.DownloadStatus.DOWNLOADED_NOT_REGISTERED;

                    }
                    catch (InvalidFormatException e){
                        TextureMap.put(hashCode(), info);
                        info.status = TextureRequestInfo.DownloadStatus.CUSTOM_ERROR;
                        e.printStackTrace();
                        try {
                            Files.deleteIfExists(getFile());
                        } catch (IOException deleteException) {
                            System.err.println("Failed to clean up incomplete file: " + getFile());
                            deleteException.printStackTrace();
                        }
                        currentDownloads--;
                        return;
                    }
                    catch (Exception e) {
                        info.status = TextureRequestInfo.DownloadStatus.DOWNLOAD_ERROR;
                        e.printStackTrace();

                        try {
                            Files.deleteIfExists(getFile());
                        } catch (IOException deleteException) {
                            System.err.println("Failed to clean up incomplete file: " + getFile());
                            deleteException.printStackTrace();
                        }
                    }

                    currentDownloads--;
                }).start();
            }
            case DOWNLOADING -> {
            }
            case CUSTOM_ERROR ->{
                return new Image(info.identifier,new Image.ImageSize(128,128),"");
            }
            case DOWNLOAD_ERROR ->
            {
                if(info.attempt<maxDownloadAttepts){
                    info.status= TextureRequestInfo.DownloadStatus.NOT_DOWNLOADED;
                    info.attempt++;
                }
            }
            case DOWNLOADED_NOT_REGISTERED -> {
                if(!Files.exists(getFile()))
                {
                    info.status = TextureRequestInfo.DownloadStatus.NOT_DOWNLOADED;
                    break;
                }
                info.identifier = getIdentifier();
                try {
                    info.registered = registerTexture(this.getFile().toFile(), info.identifier,link);
                }
                catch(Exception e)
                {
                    break;
                }
                info.status = TextureRequestInfo.DownloadStatus.READY;
            }
            case READY -> {
                return TextureMap.get(hashCode()).registered;
            }
        }

        TextureMap.put(hashCode(), info);
        return new Image(EMPTY_TEXTURE,new Image.ImageSize(100,100), link);

    }
    private boolean isPngHeaderValid(byte[] header) {
        byte[] validPngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        return Arrays.equals(header, validPngHeader);
    }

    @Override
    public int hashCode() {
        return getIdentifierString().hashCode();
    }
    private class TextureRequestInfo
    {
        public int attempt;
        public  Image registered;

        public TextureRequestInfo()
        {
            attempt=0;
            status = DownloadStatus.INIT;
        }
        public DownloadStatus status;
        public Identifier identifier;
        public enum DownloadStatus
        {
            INIT,
            NOT_DOWNLOADED,
            DOWNLOADING,
            DOWNLOAD_ERROR,
            DOWNLOADED_NOT_REGISTERED,
            READY, CUSTOM_ERROR, SIZE_ERROR,
        }
    }

    public static Image registerTexture(File texture, Identifier identifier, String link)
    {
        NativeImage nativeImage = toNativeImage(texture);
        var backedTestTexture = new NativeImageBackedTexture(nativeImage);
        MinecraftClient.getInstance().getTextureManager().registerTexture(identifier,backedTestTexture);
        return new Image(identifier,new Image.ImageSize(nativeImage.getWidth(),nativeImage.getHeight()),link);
    }
    public static NativeImage toNativeImage(File file){
        try {
            InputStream inputStream = new FileInputStream(file);
            NativeImage nativeImage = NativeImage.read(inputStream);
            inputStream.close();
            return nativeImage;
        }
        catch(Exception e) {
            throw new RuntimeException(String.format("problem registring %s",file.toPath().toString()));
        }
    }

}
