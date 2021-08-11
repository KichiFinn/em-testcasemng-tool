package com.testcasemng.tool.utils;

import com.testcasemng.tool.excel.ExcelTestCaseTemplate;
import com.testcasemng.tool.markdown.MarkdownTestCaseTemplate;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Conversion {

    public static boolean convertExcelToMarkdown(String fullName) throws IOException {
        if (FileUtils.isRegular(fullName))  {
            for (File file : FileUtils.getAllFilesWithExtension(fullName)) {
                    convertExcelFileToMarkdownFile(file);
            }
        } else {
            convertExcelFileToMarkdownFile(new File(fullName));
        }
        return true;
    }

    public static boolean convertExcelFileToMarkdownFile(File excelInput) throws IOException {
        String markdownFileName = System.getProperty("user.dir") + File.separator + FileUtils.getFileNameWithoutExtension(excelInput.getName()) + ".md";
        System.out.println("Convert " + excelInput.getAbsolutePath() + " to " + markdownFileName);
        List<TestCaseTemplate> templates = ExcelTestCaseTemplate.readFromFile(excelInput);
        for (TestCaseTemplate template : templates) {
            MarkdownTestCaseTemplate.writeTemplateToFile(FileUtils.getFileNameWithoutExtension(excelInput.getName()), template);
        }
        return true;
    }

    public static boolean convertMarkdownToExcel(String fullName) throws IOException, GitAPIException {
        if (FileUtils.isRegular(fullName)) {
            for (File file : FileUtils.getAllFilesWithExtension(fullName)) {
                convertMarkdownFileToExcelFile(file);
            }
        } else {
            convertMarkdownFileToExcelFile(new File(fullName));
        }
        return true;
    }

    public static boolean convertMarkdownFileToExcelFile(File markdownInput) throws IOException, GitAPIException {
        String excelFileName = System.getProperty("user.dir") + File.separator + FileUtils.getFileNameWithoutExtension(markdownInput.getName()) + ".xls";
        System.out.println("Convert " + markdownInput.getAbsolutePath() + " to " + excelFileName);
        TestCaseTemplate template = MarkdownTestCaseTemplate.readFromFile(markdownInput);
        GitUtils git = new GitUtils(markdownInput);
        if (git.isInGitRepository()) {
            git.parseGit();
            git.parseLatestCommit();
            template.setCreatedBy(git.getCreateBy());
            template.setCreatedDate(git.getCreatedDate());
            template.setReviewedBy(git.getReviewedBy());
            template.setReviewedDate(git.getReviewedDate());
            template.setTesterName(git.getTestedBy());
            template.setLog(git.getLatestLog());
            template.setVersion(git.getLatestVersion());
        }
        else {
            System.out.println("Warning: file " + markdownInput.getAbsolutePath() + " is not in Git directory.\n" +
                    "Missing information when convert to Excel.");
        }
        ExcelTestCaseTemplate.writeTemplateToFile(FileUtils.getFileNameWithoutExtension(markdownInput.getName()), template);
        return true;
    }
}
