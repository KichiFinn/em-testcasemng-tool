package com.testcasemng.tool.utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class FileUtils {

    public static boolean isRegular(String input) {
        return input.contains("*");
    }

    public static String getRegularDirectory(String input) {
        return input.substring(0, input.lastIndexOf('*') - 1);
    }

    public static String getFileName(String input) {
        return Paths.get(input).getFileName().toString();
    }

    public static String getFileNameWithoutExtension(String input) {
        return input.substring(0, input.indexOf('.'));
    }

    public static String getFileNameExtension(String input) {
        return input.substring(input.lastIndexOf('.') + 1);
    }

    public static List<File> getAllFilesWithExtension(String path) {
        String extension = getFileNameExtension(path);
        String directory = getRegularDirectory(path);
        return getRecursiveFilesWithExtension(directory, extension);
    }

    public static List<File> getRecursiveFilesWithExtension(String directory, String extension) {
        List<File> files = new ArrayList<>();
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String name = file.getName();
                    System.out.println(name + "\n");
                    if (getFileNameExtension(name).equalsIgnoreCase(extension) && !name.equalsIgnoreCase("readme.md")) {
                        files.add(file);
                    }
                } else if (file.isDirectory() && !file.getName().equals(".git")) {
                    List<File> children = getRecursiveFilesWithExtension(file.getPath(), extension);
                    files.addAll(children);
                }
            }
        }
        return files;
    }

    public static String escapeMarkdownSpecialCharacter(String input) {
        if (input == null || input.equalsIgnoreCase(""))
            input = "N/A";
        String regex = "([+\\-!\\(\\){}\\[\\]`_*#\\\\]|[&\\|]{2})";
        return input.replaceAll(regex, "\\\\$1");
    }

    public static String getRelativePathToGit(File file, File gitDirectory) {
        return gitDirectory.getParentFile().toURI().relativize(file.toURI()).getPath();
    }
}
