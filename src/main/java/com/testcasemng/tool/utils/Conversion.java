package com.testcasemng.tool.utils;

import com.testcasemng.tool.excel.ExcelTestCaseTemplate;
import com.testcasemng.tool.markdown.MarkdownTestCaseTemplate;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Conversion {

    public static void convertDirectory(File directory) throws IOException, GitAPIException {

        for (File file: FileUtils.getRecursiveFilesWithExtension(directory, Constants.MARKDOWN_EXTENSION)) {
            convertMarkdownFileToExcelFile(file);
        }

        for (File file: FileUtils.getRecursiveFilesWithExtension(directory, Constants.NEW_EXCEL_EXTENSION)) {
            convertExcelFileToMarkdownFile(file);
        }

        for (File file: FileUtils.getRecursiveFilesWithExtension(directory, Constants.OLD_EXCEL_EXTENSION)) {
            convertExcelFileToMarkdownFile(file);
        }
    }

    public static void convertExcelFileToMarkdownFile(File excelInput) throws IOException {
        String markdownFileName = System.getProperty("user.dir") + File.separator + FileUtils.getFileNameWithoutExtension(excelInput.getName()) + ".md";
        System.out.println("Convert " + excelInput.getAbsolutePath() + " to " + markdownFileName);
        List<TestCaseTemplate> templates = ExcelTestCaseTemplate.readFromFile(excelInput);
        for (TestCaseTemplate template : templates) {
            MarkdownTestCaseTemplate.writeTemplateToFile(FileUtils.getFileNameWithoutExtension(excelInput.getName()), template);
        }
    }

    public static void convertMarkdownFileToExcelFile(File markdownInput) throws IOException, GitAPIException {
        String excelFileName = System.getProperty("user.dir") + File.separator + FileUtils.getFileNameWithoutExtension(markdownInput.getName()) + ".xls";
        System.out.println("Convert " + markdownInput.getAbsolutePath() + " to " + excelFileName);
        TestCaseTemplate template = MarkdownTestCaseTemplate.readFromFile(markdownInput);
        try {
            GitUtils git = new GitUtils(markdownInput);
            git.parseGit();
            git.parseLatestCommit();
            template.setCreatedBy(git.getCreateBy());
            template.setCreatedDate(git.getCreatedDate());
            template.setReviewedBy(git.getReviewedBy());
            template.setReviewedDate(git.getReviewedDate());
            template.setTesterName(git.getTestedBy());
            template.setLog(git.getLatestLog());
            template.setVersion(git.getLatestVersion());

        } catch (RepositoryNotFoundException | IllegalArgumentException e) {
            System.out.println("Warning: file " + markdownInput.getAbsolutePath() + " is not in Git directory.\n" +
                    "Missing information when convert to Excel.");
        } catch (Exception e) {
            throw e;
        }

        ExcelTestCaseTemplate.writeTemplateToFile(FileUtils.getFileNameWithoutExtension(markdownInput.getName()), template);
    }
}
