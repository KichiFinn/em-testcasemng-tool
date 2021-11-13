package com.testcasemng.tool.utils;

import com.testcasemng.tool.excel.ExcelTestCaseTemplate;
import com.testcasemng.tool.markdown.MarkdownTestCaseTemplate;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Analysis {

    public static void analyze(Config config) throws IOException, GitAPIException {
        List<File> analyzedFiles = new ArrayList<File>();
        if (config.getMap() != null) {
            config.getMap().forEach((k, v) -> {
                try {
                    analyzeTestCases(new File(v), new File(config.getReportDir()), k , config.isGenerateExcelTests());
                    analyzeFolder(new File(v), new File(config.getReportDir()), analyzedFiles);
                } catch (IOException | GitAPIException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void analyze(String input, String output, boolean historical) throws IOException, GitAPIException {
        File inputFile = new File(input);
        FileUtils.createFolderIfNotExists(output);
        File outputFolder = new File(output);
        if (inputFile.isDirectory()) {
            if (historical)
                analyzeFolder(inputFile, outputFolder, new ArrayList<File>() );
            else
                analyzeTestCases(inputFile, outputFolder, "TotalAnalysis", true);
        } else if (inputFile.isFile()) {
            analyzeATestCase(inputFile, outputFolder);
        }
    }

    public static void analyzeFolder(File inputFolder, File outputFolder, List analyzedFiles) throws IOException, GitAPIException {
        List<File> files = FileUtils.getRecursiveFilesWithExtension(inputFolder, Constants.MARKDOWN_EXTENSION);
        for (File file : files) {
            if ( analyzedFiles!= null && analyzedFiles.contains(file))
                return;
            File rootTestsDir = FileUtils.findAncestralFolderWithName(inputFolder, "Tests");
            String relativePath = FileUtils.getRelativePath(file, rootTestsDir);
            String relativeFolder = "";
            if (relativePath.contains("/"))
                relativeFolder = relativePath.substring(0, relativePath.lastIndexOf("/"));
            String out = outputFolder.getPath()+ File.separator + relativeFolder;
            FileUtils.createFolderIfNotExists(out);
            analyzeATestCase(file, new File(out));
            analyzedFiles.add(file);
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
            System.out.println("Unknown error: " + e.getMessage() + " while process " + inputFile.getPath()+ "\n");
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

    public static void analyzeTestCases(File directory, File outputFolder, String reportName, boolean generateExcelReport) throws IOException {
        System.out.println("Generating test result summary ...");
        AnalysisTemplate template = new AnalysisTemplate();
        List<File> files = FileUtils.getRecursiveFilesWithExtension(directory, Constants.MARKDOWN_EXTENSION);
        for (File file : files) {
            File rootTestsDir = FileUtils.findAncestralFolderWithName(directory, "Tests");
            String relativePath = FileUtils.getRelativePath(file, rootTestsDir);

            TestCaseTemplate testCaseTemplate = MarkdownTestCaseTemplate.readFromFile(file);
            if (!testCaseTemplate.getTestcaseID().equals("")) {
                ShortTestResult result = new ShortTestResult();
                result.setId(testCaseTemplate.getTestcaseID());
                result.setName(testCaseTemplate.getTestcaseName());
                result.setTestSpecificationLocation(".." + File.separator + "Tests" + File.separator + relativePath);
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
        if (generateExcelReport) {
            String excelFilePath = outputFolder.getPath() + File.separator + reportName + ".xlsx";
            ExcelTestCaseTemplate.writeTotalAnalysisTemplateToFile(template, excelFilePath);
        }
        String markdownFilePath = outputFolder.getPath() + File.separator + reportName + ".MD";
        MarkdownTestCaseTemplate.writeTotalAnalysisTemplateToFile(template, markdownFilePath);
    }
}
