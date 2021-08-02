package com.testcasemng.tool.markdown;

import com.testcasemng.tool.utils.Constants;
import com.testcasemng.tool.utils.FileUtils;
import com.testcasemng.tool.utils.TestCaseTemplate;
import com.testcasemng.tool.utils.TestStep;
import net.steppschuh.markdowngenerator.list.UnorderedList;
import net.steppschuh.markdowngenerator.text.heading.Heading;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class MarkdownTestCaseTemplate {
    public static boolean initialize(String fullPath) throws Exception {
        System.out.println("Initialize Test Case Specification Markdown file: " + fullPath);
        InputStream stream = null;
        try {
            stream = MarkdownTestCaseTemplate.class.getClassLoader().getResourceAsStream("Template1.md");
            Files.copy(stream, Paths.get(fullPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
        }
        return true;
    }

    public static boolean writeTemplateToFile(String fileName, TestCaseTemplate template) throws IOException {
        String markdownFileName = System.getProperty("user.dir") + File.separator + fileName + ".MD";
        StringBuilder sb = new StringBuilder()
                .append(MarkdownLib.createHeaderLink(template.getTestcaseID(), "", 1))
                .append(MarkdownLib.createHeaderAndList(Constants.TEST_CASE_NAME, 2, template.getTestcaseName(), 0))
                .append(MarkdownLib.createHeaderAndList(Constants.TEST_CASE_DESCRIPTION, 2, template.getTestcaseDesc(), 0))
                .append(MarkdownLib.createHeaderAndList(Constants.TEST_CASE_PRE_CONDITION, 2, template.getPreCondition(), 0))
                .append(MarkdownLib.createHeaderAndList(Constants.TEST_CASE_POST_CONDITION, 2, template.getPostCondition(), 0))
                .append(MarkdownLib.createHeaderAndList(Constants.TEST_CASE_RESULTS, 2, template.getTestResults(), 0))
                .append("---\r\n")
                .append(MarkdownLib.createHeader(Constants.TEST_CASE_STEPS, 2))
                .append(buildTestSteps(template.getTestSteps()))
                .append("---\r\n");

        BufferedWriter writer = new BufferedWriter(new FileWriter(markdownFileName));
        writer.write(sb.toString());
        writer.close();
        return true;
    }

    public static String buildTestSteps(List<TestStep> steps) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < steps.size(); i++) {
            TestStep step = steps.get(i);
            sb.append(MarkdownLib.createOrderedList(step.getNo(), Constants.TEST_CASE_STEP + Integer.toString(step.getNo()), 0))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_CASE_STEP_DETAILS, 1))
                    .append(MarkdownLib.createUnorderedList(step.getDetails(), 2))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_CASE_STEP_DATA, 1))
                    .append(MarkdownLib.createUnorderedList(step.getData(), 2))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_CASE_STEP_EXPECTED_RESULTS, 1))
                    .append(MarkdownLib.createUnorderedList(step.getActualResults(), 2))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_CASE_STEP_ACTUAL_RESULTS, 1))
                    .append(MarkdownLib.createUnorderedList(step.getActualResults(), 2))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_CASE_STEP_FINAL_RESULTS, 1))
                    .append(MarkdownLib.createUnorderedList(step.getFinalResults(), 2));
        }
        return sb.toString();
    }

}
