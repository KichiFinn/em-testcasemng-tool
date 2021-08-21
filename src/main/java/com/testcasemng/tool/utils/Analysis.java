package com.testcasemng.tool.utils;

import com.testcasemng.tool.excel.ExcelTestCaseTemplate;
import com.testcasemng.tool.markdown.MarkdownTestCaseTemplate;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Analysis {

    public static void analyze(String input, String output, boolean historical) throws IOException, GitAPIException {
        File inputFile = new File(input);
        FileUtils.createFolderIfNotExists(output);
        File outputFolder = new File(output);
        if (inputFile.isDirectory()) {
            if (historical == true)
                analyzeFolder(inputFile, outputFolder);
            else
                analyzeTestCases(inputFile, outputFolder);
        } else if (inputFile.isFile()) {
            analyzeATestCase(inputFile, outputFolder);
        }
    }



    public static void analyzeFolder(File inputFolder, File outputFolder) throws IOException, GitAPIException {
        List<File> files = FileUtils.getRecursiveFilesWithExtension(inputFolder, Constants.MARKDOWN_EXTENSION);
        for (File file : files) {
            String relativePath = FileUtils.getRelativePath(file, inputFolder);
            String relativeFolder = "";
            if (relativePath.contains("/"))
                relativeFolder = relativePath.substring(0, relativePath.lastIndexOf("/"));
            String out = outputFolder.getPath()+ File.separator + relativeFolder;
            FileUtils.createFolderIfNotExists(out);
            analyzeATestCase(file, new File(out));
        }
    }
    public static void analyzeATestCase(File inputFile, File outputFolder) throws IOException, GitAPIException {
        AnalysisTemplate template = null;
        try {
            GitUtils git = new GitUtils(inputFile);
            template = git.parseHistoricalResults();
        } catch (RepositoryNotFoundException | IllegalArgumentException e) {
            System.out.println("Error: file " + inputFile.getPath() + " is not in Git directory.\n" +
                    "Can not get historical results");
        } catch (Exception e) {
            throw e;
        }

        if (template != null) {
            String excelOutfilePath = outputFolder.getPath() + File.separator + FileUtils.getFileNameWithoutExtension(inputFile.getName())
                    + "Analysis.xlsx";
            String markdownOutfilePath = outputFolder.getPath() + File.separator + FileUtils.getFileNameWithoutExtension(inputFile.getName())
                    + "Analysis.MD";
            ExcelTestCaseTemplate.writeAnAnalysisTemplateToFile(template, excelOutfilePath);
            MarkdownTestCaseTemplate.writeAnAnalysisTemplateToFile(template, markdownOutfilePath);
        }
    }

    public static void analyzeTestCases(File directory, File outputFolder) throws IOException {
        System.out.println("Generating test result summary ...");
        AnalysisTemplate template = new AnalysisTemplate();
        List<File> files = FileUtils.getRecursiveFilesWithExtension(directory, Constants.MARKDOWN_EXTENSION);
        for (File file : files) {
            TestCaseTemplate testCaseTemplate = MarkdownTestCaseTemplate.readFromFile(file);

            if (!testCaseTemplate.getTestcaseID().equals("")) {
                ShortTestResult result = new ShortTestResult();
                result.setId(testCaseTemplate.getTestcaseID());
                result.setName(testCaseTemplate.getTestcaseName());
                result.setDateTest(testCaseTemplate.getTestDate());
                result.setResult(testCaseTemplate.getTestResults());
                template.getTests().add(result);
                switch (testCaseTemplate.getTestResults()) {
                    case Constants.TEST_RESULT_PASS:
                        template.setPass(template.getPass() + 1);
                        break;
                    case Constants.TEST_RESULT_FAIL:
                        template.setFail(template.getFail() + 1);
                        break;
                    case Constants.TEST_RESULT_NOT_EXECUTED:
                        template.setNotExecuted(template.getNotExecuted() + 1);
                        break;
                    case Constants.TEST_RESULT_SUSPENDED:
                        template.setSuspend(template.getSuspend() + 1);
                        break;
                    default:
                        template.setOthers(template.getOthers());
                        break;
                }
            }
        }
        String excelFilePath = outputFolder.getPath() + File.separator + "TotalAnalysis.xlsx";
        String markdownFilePath = outputFolder.getPath() + File.separator + "TotalAnalysis.MD";
        ExcelTestCaseTemplate.writeTotalAnalysisTemplateToFile(template, excelFilePath);
        MarkdownTestCaseTemplate.writeTotalAnalysisTemplateToFile(template, markdownFilePath);
    }
}
