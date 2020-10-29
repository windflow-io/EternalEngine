package io.windflow.eternalengine.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TextFileReader {

    public static String getText(String path) throws IOException {
        if (path == null) return null;
        Resource resource = new ClassPathResource(path);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

    public static boolean checkDirectory(String directory) {
        return new ClassPathResource(directory).exists();
    }
}
