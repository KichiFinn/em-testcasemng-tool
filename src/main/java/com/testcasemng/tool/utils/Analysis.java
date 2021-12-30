package com.testcasemng.tool.utils;

import com.testcasemng.tool.excel.ExcelTestCaseTemplate;
import com.testcasemng.tool.html.HTMLReportTemplate;
import com.testcasemng.tool.markdown.MarkdownTestCaseTemplate;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Analysis {

    public static void analyze(Config config) throws IOException, GitAPIException {
        List<File> analyzedFiles = new ArrayList<File>();
        if (config.getMap() != null) {
            config.getMap().forEach((k, v) -> {
                try {
                    analyzeTestCases(new File(v), new File(config.getReportDir()), k, config.isGenerateExcelTests(), config.isGenerateHtmlReports());
                    analyzeFolder(new File(v), new File(config.getReportDir()), analyzedFiles, config.isGenerateExcelTests(), config.isGenerateHtmlReports());
                } catch (IOException | GitAPIException e) {
                    e.printStackTrace();
                }
            });
            if (config.isGenerateHtmlReports()) {
                generateHomePage("./docs");
            }
        }
    }

    public static void generateHomePage(String home) throws IOException {
        File[] listOfFiles =  (new File(home)).listFiles();
        String reports = "";
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().contains(".html") && !file.getName().equals("index.html"))
                reports += String.format(Constants.HREF, "./" + file.getName(), file.getName());
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(home + File.separator + "index.html"));
        writer.write(String.format(Constants.INDEX, reports));
        writer.close();
    }

    public static void analyze(String input, String output, boolean historical) throws IOException, GitAPIException {
        File inputFile = new File(input);
        FileUtils.createFolderIfNotExists(output);
        File outputFolder = new File(output);
        if (inputFile.isDirectory()) {
            if (historical)
                analyzeFolder(inputFile, outputFolder, new ArrayList<File>(), true, true);
            else
                analyzeTestCases(inputFile, outputFolder, "TotalAnalysis", true, true);
        } else if (inputFile.isFile()) {
            analyzeATestCase(inputFile, outputFolder, true, true);
        }
    }

    public static void analyzeFolder(File inputFolder, File outputFolder, List analyzedFiles, boolean generateExcelReport, boolean generateHTMLReport) throws IOException, GitAPIException {
        List<File> files = FileUtils.getRecursiveFilesWithExtension(inputFolder, Constants.MARKDOWN_EXTENSION);
        for (File file : files) {
            if (analyzedFiles != null && analyzedFiles.contains(file))
                return;
            File rootTestsDir = FileUtils.findAncestralFolderWithName(inputFolder, "Tests");
            String relativePath = FileUtils.getRelativePath(file, rootTestsDir);
            String relativeFolder = "";
            if (relativePath.contains("/"))
                relativeFolder = relativePath.substring(0, relativePath.lastIndexOf("/"));
            String out = outputFolder.getPath() + File.separator + relativeFolder;
            FileUtils.createFolderIfNotExists(out);
            analyzeATestCase(file, new File(out), generateExcelReport, generateHTMLReport);
            analyzedFiles.add(file);
        }
    }

    public static void analyzeATestCase(File inputFile, File outputFolder, boolean generateExcelReport, boolean generateHTMLReport) throws IOException, GitAPIException {
        AnalysisTemplate template = null;
        try {
            GitUtils git = new GitUtils(inputFile);
            template = git.parseHistoricalResults();
        } catch (RepositoryNotFoundException | IllegalArgumentException e) {
            System.out.println("Error: file " + inputFile.getPath() + " is not in Git directory.\n" +
                    "Can not get historical results");
        } catch (Exception e) {
            System.out.println("Unknown error: " + e.getMessage() + " while process " + inputFile.getPath() + "\n");
            throw e;
        }

        if (template != null) {
            if (generateExcelReport) {
                String excelOutfilePath = outputFolder.getPath() + File.separator + FileUtils.getFileNameWithoutExtension(inputFile.getName())
                        + "Analysis.xlsx";
                ExcelTestCaseTemplate.writeAnAnalysisTemplateToFile(template, excelOutfilePath);
            }

            if (generateHTMLReport) {
                String htmlDirPath = FileUtils.replaceReportsToDocs(outputFolder.getPath());
                FileUtils.createFolderIfNotExists(htmlDirPath);
                String htmlOutfilePath = htmlDirPath + File.separator + FileUtils.getFileNameWithoutExtension(inputFile.getName()) + "Analysis.html";
                HTMLReportTemplate.writeHistoricalAnalysisTemplateToFile(template, htmlOutfilePath);
            }

            String markdownOutfilePath = outputFolder.getPath() + File.separator + FileUtils.getFileNameWithoutExtension(inputFile.getName())
                    + "Analysis.MD";
            MarkdownTestCaseTemplate.writeAnAnalysisTemplateToFile(template, markdownOutfilePath);
        }
    }

    public static void analyzeTestCases(File directory, File outputFolder, String reportName, boolean generateExcelReport, boolean generateHTMLReport) throws IOException {
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
                String note = "";
                for (TestStep step : testCaseTemplate.getTestSteps()) {
                    note = "Step " + Integer.toString(step.getNo()) + ": " + step.getActualResults() + "\n";
                }
                result.setActualResults(note.trim());
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

        if (generateHTMLReport) {
            String htmlDirPath = FileUtils.replaceReportsToDocs(outputFolder.getPath());
            FileUtils.createFolderIfNotExists(htmlDirPath);
            String htmlOutfilePath = htmlDirPath + File.separator + reportName + ".html";
            HTMLReportTemplate.writeSummaryAnalysisTemplateToFile(template, htmlOutfilePath, reportName);
        }

        String markdownFilePath = outputFolder.getPath() + File.separator + reportName + ".MD";
        MarkdownTestCaseTemplate.writeTotalAnalysisTemplateToFile(template, markdownFilePath);
    }
}
