package com.testcasemng.tool.utils;

import com.testcasemng.tool.excel.ExcelTestCaseTemplate;
import com.testcasemng.tool.markdown.MarkdownTestCaseTemplate;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Analysis {

    public static void analyze(String fullName) throws IOException, GitAPIException {
        File file = new File(fullName);
        if (file.isDirectory()) {
            analyzeTestCases(file);
        } else if (file.isFile()) {
            analyzeATestCase(file);
        }
    }

    public static void analyzeATestCase(File file) throws IOException, GitAPIException {
        AnalysisTemplate template = null;
        try {
            GitUtils git = new GitUtils(file);
            template = git.parseHistoricalResults();
        } catch (RepositoryNotFoundException | IllegalArgumentException e) {
            System.out.println("Error: file " + file.getPath() + " is not in Git directory.\n" +
                    "Can not get historical results");
        } catch (Exception e) {
            throw e;
        }

        if (template != null)
            ExcelTestCaseTemplate.writeAnAnalysisTemplateToFile(template, FileUtils.getFileNameWithoutExtension(file.getName())
                    + "Analysis.xlsx");
    }

    public static void analyzeTestCases(File directory) throws IOException {
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
        ExcelTestCaseTemplate.writeTotalAnalysisTemplateToFile(template);
    }
}
