package com.testcasemng.tool.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class FileUtils {

    public static String getFileName(String input) {
        return Paths.get(input).getFileName().toString();
    }

    public static String getFileNameWithoutExtension(String input) {
        String fileName = getFileName(input);
        return fileName.substring(0, fileName.indexOf('.'));
    }

    public static String getFileNameExtension(String input) {
        return input.substring(input.lastIndexOf('.') + 1);
    }

    public static boolean isMarkdownFile(String fullFileName) {
        return getFileNameExtension(fullFileName).equalsIgnoreCase(Constants.MARKDOWN_EXTENSION);
    }

    public static boolean isExcelFile(String fullFileName) {
        return (getFileNameExtension(fullFileName).equalsIgnoreCase(Constants.OLD_EXCEL_EXTENSION)
                || getFileNameExtension(fullFileName).equalsIgnoreCase(Constants.NEW_EXCEL_EXTENSION));
    }

    public static List<File> getRecursiveFilesWithExtension(File directory, String extension) {
        List<File> files = new ArrayList<>();
        File[] listOfFiles = directory.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String name = file.getName();
                    if (getFileNameExtension(name).equalsIgnoreCase(extension) &&
                            !name.equalsIgnoreCase("readme.md") &&
                            !name.equalsIgnoreCase("Template.md")) {
                        files.add(file);
                    }
                } else if (file.isDirectory() && !file.getName().equals(".git")) {
                    List<File> children = getRecursiveFilesWithExtension(file, extension);
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

    public static String getRelativePath(File file, File gitDirectory) {
        return gitDirectory.toURI().relativize(file.toURI()).getPath();
    }

    public static InputStream getFileFromResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    public static void createFolderIfNotExists (String path) throws IOException {
        File folder = new File(path);
        if(!folder.exists()) {
            Files.createDirectories(Paths.get(path));
        }
    }

    public static File findAncestralFolderWithName(File file, String name) throws IOException {
        File tmp = file;
        while (tmp != null && !tmp.getName().equals(name)) {
            tmp = tmp.getParentFile();
        }
        if (tmp == null)
            return file;
        else
            return tmp;
    }

    public static String replaceReportsToDocs(String reportsPath) throws IOException {
        if (reportsPath.contains("Reports"))
            return reportsPath.replaceFirst("Reports", "docs");
        else
            return reportsPath;
    }
}
