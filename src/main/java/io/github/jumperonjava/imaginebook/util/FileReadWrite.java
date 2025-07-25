package io.github.jumperonjava.imaginebook.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileReadWrite {

    /**
     * Writes text to file, throws runtime exception if something goes wrong
     *
     * @param path
     * @param text
     */
    public static void write(Path path, String text) {
        try {
            Files.createDirectories(path.getParent());
            byte[] strToBytes = text.getBytes();
            Files.write(path, strToBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tries to read file, creates empty file if it does not exist and returns empty string
     *
     * @param file
     * @return
     */
    public static String read(Path file) {
        try {
            return new String(Files.readAllBytes(file));
        } catch (IOException e) {
            write(file, "");
            return read(file);
        }
    }
}