package com.testcasemng.tool.utils;

import com.testcasemng.tool.excel.ExcelTestCaseTemplate;
import com.testcasemng.tool.markdown.MarkdownTestCaseTemplate;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Analysis {

    public static void analyze(String fullName) throws IOException, GitAPIException {
        if (FileUtils.isRegular(fullName)) {
            analyzeTestCases(fullName);
        } else {
            analyzeATestCase(fullName);
        }
    }

    public static void analyzeATestCase(String fullName) throws IOException, GitAPIException {
        AnalysisTemplate template = null;
        File file = new File(fullName);

        GitUtils git = new GitUtils(file);
        if (git.isInGitRepository()) {
            template = git.parseHistoricalResults();
        } else {
            System.out.println("Warning: file " + fullName + " is not in Git directory.\n" +
                    "Missing information when analyze historical results.");
        }

        if (template != null)
            ExcelTestCaseTemplate.writeAnAnalysisTemplateToFile(template, FileUtils.getFileName(FileUtils.getFileNameWithoutExtension(fullName))
                    + "Analysis.xlsx");
    }

    public static void analyzeTestCases(String fullName) throws IOException {
        System.out.println("Generating test result summary ...");
        AnalysisTemplate template = new AnalysisTemplate();
        List<File> files = FileUtils.getAllFilesWithExtension(fullName);
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
