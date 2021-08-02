package com.testcasemng.tool.utils;

import com.testcasemng.tool.excel.ExcelTestCaseTemplate;
import com.testcasemng.tool.markdown.MarkdownTestCaseTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Conversion {

    public static boolean convertExcelToMarkdown(String fullName) throws IOException {

        if (FileUtils.isRegular(fullName)) {
            for (File file : FileUtils.getAllFilesWithExtension(fullName)) {
                convertExcelFileToMarkdownFile(file);
            }
        } else {
            convertExcelFileToMarkdownFile(new File(fullName));
        }
        return true;
    }

    public static boolean convertExcelFileToMarkdownFile(File excelInput) throws IOException {
        List<TestCaseTemplate> templates = ExcelTestCaseTemplate.readFromFile(excelInput);
        for (TestCaseTemplate template: templates) {
            MarkdownTestCaseTemplate.writeTemplateToFile(FileUtils.getFileNameWithoutExtension(excelInput.getName()), template);
        }
        return true;
    }

    public static boolean convertMarkdownToExcel(String fullName) {
        if (FileUtils.isRegular(fullName)) {

        } else {

        }
        return true;
    }
}
