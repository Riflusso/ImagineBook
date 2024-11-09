package io.github.jumperonjava.imaginebook;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ImageSerializer {


    public static byte[] serializeImageMetadata(List<ImageData> imagesDefinition) {
        var buf = ByteBuffer.allocate(1000);
        buf.putShort((short) (2+2+4+4+2)); //Format (size)
        buf.put((byte) imagesDefinition.size());
        for(var image : imagesDefinition) {
            var url = image.url.getBytes(StandardCharsets.UTF_8);
            buf.put((byte) url.length);
            buf.put(url, 0, url.length);
            //count from here
            buf.putShort((short) image.x);
            buf.putShort((short) image.y);
            buf.putFloat(image.width);
            buf.putFloat(image.height);
            buf.putShort((short) ((image.rotation-180)/180*Short.MAX_VALUE));
        }
        var len = buf.position();
        var shortBuf = new byte[len+1];

        for(int i = 0; i < len; i++) {
            shortBuf[i] = buf.get(i);
        }

        return shortBuf;
    }

    public static List<ImageData> deserializeImageMetadata(byte[] data) {
        var buf = ByteBuffer.wrap(data);
        var format = buf.getShort();

        var images = new ArrayList<ImageData>();
        var amount = buf.get();
        for (int i = 0; i < amount; i++) {
            var image = new ImageData();
            images.add(image);
            var urlsize = buf.get() & 0xFF;
            var urlbytes = new byte[urlsize];
            buf.get(urlbytes);
            image.url = new String(urlbytes, StandardCharsets.UTF_8);
            var usedBytes = 0;
            //count from here
            image.x = buf.getShort();
            usedBytes += 2;
            image.y = buf.getShort();
            usedBytes += 2;
            image.width = buf.getFloat();
            usedBytes += 4;
            image.height = buf.getFloat();
            usedBytes += 4;
            image.rotation = ((float) buf.getShort()) / Short.MAX_VALUE * 180 + 180;
            usedBytes += 2;

            while (format > usedBytes) {
                buf.get();
                usedBytes++;
            }
        }
        return images;
    }
}
