package com.testcasemng.tool.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TemplateInitialization {

    public static void initTemplate(String fullPath) throws IOException {
        if (FileUtils.getFileNameExtension(fullPath).equalsIgnoreCase(Constants.OLD_EXCEL_EXTENSION)
                || FileUtils.getFileNameExtension(fullPath).equalsIgnoreCase(Constants.NEW_EXCEL_EXTENSION))
            initExcelTemplate(fullPath);
        else if (FileUtils.getFileNameExtension(fullPath).equalsIgnoreCase(Constants.MARKDOWN_EXTENSION))
            initMarkdownTemplate(fullPath);
        else
            System.out.println("Error! This file type is not support. Run -h for help");
    }

    private static void initExcelTemplate(String fullPath) throws IOException {
        System.out.println("Initialize Test Case Specification Excel file: " + fullPath);
        InputStream stream = null;
        if (FileUtils.getFileNameExtension(fullPath).equalsIgnoreCase(Constants.OLD_EXCEL_EXTENSION))
            stream = FileUtils.getFileFromResourceAsStream("Template1.xls");
        else if (FileUtils.getFileNameExtension(fullPath).equalsIgnoreCase(Constants.NEW_EXCEL_EXTENSION))
            stream = FileUtils.getFileFromResourceAsStream("Template1.xlsx");
        if (stream != null) {
            Files.copy(stream, Paths.get(fullPath), StandardCopyOption.REPLACE_EXISTING);
            stream.close();
        }
    }

    private static void initMarkdownTemplate(String fullPath) throws IOException {
        System.out.println("Initialize Test Case Specification Markdown file: " + fullPath);
        InputStream stream = FileUtils.getFileFromResourceAsStream("Template1.md");
        if (stream != null) {
            Files.copy(stream, Paths.get(fullPath), StandardCopyOption.REPLACE_EXISTING);
            stream.close();
        }
    }

}
