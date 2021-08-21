package com.testcasemng.tool.utils;

import com.testcasemng.tool.excel.ExcelTestCaseTemplate;
import com.testcasemng.tool.markdown.MarkdownTestCaseTemplate;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Conversion {

    public static void convertExcelDirectory(File directory, String outFolder) throws IOException, GitAPIException {
        for (File file: FileUtils.getRecursiveFilesWithExtension(directory, Constants.NEW_EXCEL_EXTENSION)) {
            String relativePath = FileUtils.getRelativePath(file, directory);
            String relativeFolder = "";
            if (relativePath.contains("/"))
                relativeFolder = relativePath.substring(0, relativePath.lastIndexOf("/"));
            String out = outFolder+ File.separator + relativeFolder;
            convertExcelFileToMarkdownFile(file, out);
        }

        for (File file: FileUtils.getRecursiveFilesWithExtension(directory, Constants.OLD_EXCEL_EXTENSION)) {
            String relativePath = FileUtils.getRelativePath(file, directory);
            String relativeFolder = "";
            if (relativePath.contains("/"))
                relativeFolder = relativePath.substring(0, relativePath.lastIndexOf("/"));
            String out = outFolder+ File.separator + relativeFolder;
            convertExcelFileToMarkdownFile(file, out);
        }
    }

    public static void convertMarkdownDirectory(File directory, String outFolder) throws IOException, GitAPIException {
        for (File file: FileUtils.getRecursiveFilesWithExtension(directory, Constants.MARKDOWN_EXTENSION)) {
            String relativePath = FileUtils.getRelativePath(file, directory);
            String relativeFolder = "";
            if (relativePath.contains("/"))
                relativeFolder = relativePath.substring(0, relativePath.lastIndexOf("/"));
            String out = outFolder+ File.separator + relativeFolder;
            convertMarkdownFileToExcelFile(file, out);
        }
    }

    public static void convertExcelFileToMarkdownFile(File excelInput, String outFolder) throws IOException {
        FileUtils.createFolderIfNotExists(outFolder);
        //String markdownFileName = System.getProperty("user.dir") + File.separator + FileUtils.getFileNameWithoutExtension(excelInput.getName()) + ".md";
        System.out.println("Convert " + excelInput.getAbsolutePath() + " to " + outFolder);
        List<TestCaseTemplate> templates = ExcelTestCaseTemplate.readFromFile(excelInput);
        for (TestCaseTemplate template : templates) {
            MarkdownTestCaseTemplate.writeTemplateToFile(FileUtils.getFileNameWithoutExtension(excelInput.getName()), outFolder, template);
        }
    }

    public static void convertMarkdownFileToExcelFile(File markdownInput, String outFolder) throws IOException, GitAPIException {
        FileUtils.createFolderIfNotExists(outFolder);
        System.out.println("Convert " + markdownInput.getAbsolutePath() + " to " + outFolder);
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

        ExcelTestCaseTemplate.writeTemplateToFile(FileUtils.getFileNameWithoutExtension(markdownInput.getName()), outFolder, template);
    }
}
