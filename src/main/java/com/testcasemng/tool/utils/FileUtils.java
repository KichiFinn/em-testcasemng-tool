package com.testcasemng.tool.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class FileUtils {

    public static boolean isAbsolute(String input) {
        return Paths.get(input).isAbsolute();
    }

    public static boolean isRegular(String input) {
        return input.contains("*");
    }

    public static String getRegularDirectory(String input) {
        return input.substring(0, input.lastIndexOf('*') -1);
    }

    public static String getFileName(String input) {
        return Paths.get(input).getFileName().toString();
    }

    public static String getFileNameWithoutExtension(String input) {
        return input.substring(0, input.indexOf('.') - 1);
    }

    public static String getFileNameExtension(String input) {
        return input.substring(input.lastIndexOf('.') + 1);
    }

    public static List<File> getAllFilesWithExtension(String path) {
        List<File> files = new ArrayList<File>();
        String extension = getFileNameExtension(path);
        File folder = new File(getRegularDirectory(path));
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String name = file.getName();
                if (name.substring(name.lastIndexOf(".")).equalsIgnoreCase(extension)) {
                    files.add(file);
                }
            }
        }
        return files;
    }

}
