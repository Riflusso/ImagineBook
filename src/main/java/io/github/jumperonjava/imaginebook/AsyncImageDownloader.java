package io.github.jumperonjava.imaginebook;

import com.ibm.icu.impl.InvalidFormatException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;


/**
 * yay i have rewritten it
 */
public class AsyncImageDownloader {
    public static Image ERROR_IMAGE = new Image(Identifier.of("imaginebook", "textures/not_loaded.png"), new Image.ImageSize(128, 128));
    public static Image SIZE_ERROR = new Image(Identifier.of("imaginebook", "textures/size_error.png"), new Image.ImageSize(128, 128));
    public static Image DOWNLOADING_IMAGE = new Image(Identifier.of("imaginebook", "textures/downloading.png"), new Image.ImageSize(128, 128));
    public static Image URL_INVALID_ERROR = new Image(Identifier.of("imaginebook", "textures/url_invalid.png"), new Image.ImageSize(128, 128));

    public Map<URL, Image> downloadedImages = new HashMap<>();
    public List<URL> inProgressImages = Collections.synchronizedList(new LinkedList<>());
    public List<URL> inProgressRetryImages = Collections.synchronizedList(new LinkedList<>());
    public List<URL> unregisteredImages = Collections.synchronizedList(new LinkedList<>());
    public List<URL> errorImages = Collections.synchronizedList(new LinkedList<>());
    public List<URL> overLimitImages = Collections.synchronizedList(new LinkedList<>());

    public AsyncImageDownloader() {
        new Thread(this::downloadThread).start();
    }

    public Path urlPath(URL url) {
        return Imaginebook.getCachePath().resolve("%s/%d.cache".formatted(url.getHost().toLowerCase(), url.getPath().hashCode()));
    }

    public Identifier urlIdentifier(URL url) {
        return Identifier.of(Imaginebook.MOD_ID, "%s/%d.cache".formatted(url.getHost().toLowerCase(), url.getPath().hashCode()));
    }

    public void downloadThread() {
        while (true) {
            URL downloadUrl = null;
            if (!inProgressImages.isEmpty())
                downloadUrl = inProgressImages.remove(0);

            if (!inProgressRetryImages.isEmpty())
                downloadUrl = inProgressRetryImages.remove(0);

            if (downloadUrl == null) {
                continue;
            }
            var urlFile = urlPath(downloadUrl);
            try {
                Files.createDirectories(urlFile.getParent());
            } catch (IOException e) {
                Imaginebook.LOGGER.error("Failed to create folder for file: " + urlFile);
                continue;
            }
            try (InputStream urlIn = downloadUrl.openStream();
                 OutputStream fileOut = Files.newOutputStream(urlFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

                Imaginebook.createImagineBookFolder();

                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytesRead = 0;
                long maxSize = 8 * 1024 * 1024;

                var convertReader = new ByteArrayOutputStream();

                while ((bytesRead = urlIn.read(buffer)) != -1) {
                    totalBytesRead += bytesRead;
                    if (totalBytesRead > maxSize) {
                        overLimitImages.add(downloadUrl);
                        throw new InvalidFormatException("File size exceeds 1MB limit.");
                    }
                    convertReader.write(buffer, 0, bytesRead);
                }
                var convertWriter = new ByteArrayInputStream(convertReader.toByteArray());

                var converted = convertToPng(convertWriter);

                IOUtils.copy(converted, fileOut);

                unregisteredImages.add(downloadUrl);
            } catch (Exception e) {
                errorImages.add(downloadUrl);
                try {
                    Files.deleteIfExists(urlFile);
                } catch (IOException deleteException) {
                    Imaginebook.LOGGER.error("Failed to clean up incomplete file: " + urlFile);
                    deleteException.printStackTrace();
                }
            }
        }
    }

    public Image requestTexture(String urlStr) {

        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            return URL_INVALID_ERROR;
        }

        if (downloadedImages.containsKey(url)) {
            return downloadedImages.get(url);
        }

        if (inProgressImages.contains(url)) {
            return DOWNLOADING_IMAGE;
        }

        if (inProgressRetryImages.contains(url)) {
            return ERROR_IMAGE;
        }

        if (errorImages.contains(url)) {
            inProgressRetryImages.add(url);
            errorImages.remove(url);
            return ERROR_IMAGE;
        }

        if (overLimitImages.contains(url)) {
            return SIZE_ERROR;
        }

        if (Files.exists(urlPath(url))) {
            unregisteredImages.add(url);
        }

        if (unregisteredImages.contains(url)) {
            Image image = null;
            try {
                image = registerTexture(new FileInputStream(urlPath(url).toFile()), urlIdentifier(url));
                downloadedImages.put(url, image);
                unregisteredImages.remove(url);
            } catch (FileNotFoundException e) {
                errorImages.add(url);
                return ERROR_IMAGE;
            }


            return image;
        }

        inProgressImages.add(url);

        return ERROR_IMAGE;
    }

    public static Image registerTexture(InputStream texture, Identifier identifier) {
        NativeImage nativeImage = toNativeImage(texture);
        var backedTestTexture = new NativeImageBackedTexture(nativeImage);
        MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, backedTestTexture);
        return new Image(identifier, new Image.ImageSize(nativeImage.getWidth(), nativeImage.getHeight()));
    }

    public static NativeImage toNativeImage(InputStream file) {
        try {
            NativeImage nativeImage = NativeImage.read((file));
            (file).close();
            return nativeImage;
        } catch (Exception e) {
            throw new RuntimeException(String.format("problem registring texture"));
        }
    }

    public static InputStream convertToPng(InputStream inputStream) throws IOException {
        BufferedImage inputImage = ImageIO.read(inputStream);
        if (inputImage == null) {
            throw new IOException("The input stream does not contain a valid image.");
        }

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

        boolean success = ImageIO.write(inputImage, "png", pngOutputStream);
        if (!success) {
            throw new IOException("Failed to write the image in PNG format.");
        }

        return new ByteArrayInputStream(pngOutputStream.toByteArray());
    }

}
