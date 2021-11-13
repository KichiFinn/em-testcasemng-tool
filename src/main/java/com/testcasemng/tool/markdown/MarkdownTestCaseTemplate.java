package com.testcasemng.tool.markdown;

import com.testcasemng.tool.utils.*;

import java.io.*;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MarkdownTestCaseTemplate {

    public static TestCaseTemplate readFromStream(InputStream stream) throws IOException {
        TestCaseTemplate template = new TestCaseTemplate();
        Parser parser = Parser.builder().build();
        Node document = parser.parseReader(new InputStreamReader(stream));
        Node child = document.getFirstChild();
        while (child.getNext() != null) {
            addDataToTemplate(child, template);
            child = child.getNext();
        }
        return template;
    }

    public static TestCaseTemplate readFromFile(File file) throws IOException {
        TestCaseTemplate template = new TestCaseTemplate();
        Parser parser = Parser.builder().build();
        Node document = parser.parseReader(new FileReader(file));
        Node child = document.getFirstChild();
        while (child.getNext() != null) {
            addDataToTemplate(child, template);
            child = child.getNext();
        }
        return template;
    }

    public static void addDataToTemplate(Node node, TestCaseTemplate template) {
        if (node instanceof Heading) {
            addHeaderLevel2(node, template);
        }
    }

    /*public static void addIdAndLink(Node node, TestCaseTemplate template) {
        Link link = (Link) node.getFirstChild();
        Text text = (Text) link.getFirstChild();
        template.setTestcaseID(text.getLiteral());
        template.setTestScriptLink(link.getDestination());
    }*/

    public static void addTestSteps(Node node, TestCaseTemplate template) {
        List<TestStep> steps = new ArrayList<>();
        Node listDetailHeader = node.getFirstChild();
        int no = 1;
        while (listDetailHeader != null) {
            TestStep step = new TestStep();
            Node bulletList = listDetailHeader.getLastChild().getFirstChild();
            while (bulletList instanceof ListItem) {
                Node header = bulletList.getFirstChild().getFirstChild();
                if (header instanceof Text) {
                    switch (((Text) header).getLiteral()) {
                        case Constants.TEST_CASE_STEP_DETAILS:
                            step.setDetails(getAllChildValue(bulletList.getLastChild().getFirstChild()));
                            break;
                        case Constants.TEST_CASE_STEP_DATA:
                            step.setData(getAllChildValue(bulletList.getLastChild().getFirstChild()));
                            break;
                        case Constants.TEST_CASE_STEP_EXPECTED_RESULTS:
                            step.setExpectedResults(getAllChildValue(bulletList.getLastChild().getFirstChild()));
                            break;
                        case Constants.TEST_CASE_STEP_ACTUAL_RESULTS:
                            step.setActualResults(getAllChildValue(bulletList.getLastChild().getFirstChild()));
                            break;
                        case Constants.TEST_CASE_STEP_FINAL_RESULTS:
                            step.setFinalResults(getAllChildValue(bulletList.getLastChild().getFirstChild()));
                            break;
                        default:
                            break;
                    }
                }
                bulletList = bulletList.getNext();
            }

            step.setNo(no);
            steps.add(step);
            listDetailHeader = listDetailHeader.getNext();
            no += 1;
        }
        template.setTestSteps(steps);
    }

    public static void addHeaderLevel2(Node node, TestCaseTemplate template) {
        Node firstChild = node.getFirstChild();
        if (firstChild instanceof Text) {
            switch (((Text) firstChild).getLiteral()) {
                case Constants.TEST_CASE_ID:
                    template.setTestcaseID(getAllChildValue(node.getNext().getFirstChild()));
                    break;
                case Constants.TEST_CASE_SCRIPT_LOCATION:
                    template.setTestScriptLink(getAllChildValue(node.getNext().getFirstChild()));
                    break;
                case Constants.TEST_CASE_NAME:
                    template.setTestcaseName(getAllChildValue(node.getNext().getFirstChild()));
                    break;
                case Constants.TEST_CASE_DESCRIPTION:
                    template.setTestcaseDesc(getAllChildValue(node.getNext().getFirstChild()));
                    break;
                case Constants.TEST_CASE_PRE_CONDITION:
                    template.setPreCondition(getAllChildValue(node.getNext().getFirstChild()));
                    break;
                case Constants.TEST_CASE_POST_CONDITION:
                    template.setPostCondition(getAllChildValue(node.getNext().getFirstChild()));
                    break;
                case Constants.TEST_CASE_DATE_TESTED:
                    template.setTestDate(getDate(getAllChildValue(node.getNext().getFirstChild())));
                    break;
                case Constants.TEST_CASE_RESULTS:
                    template.setTestResults(getAllChildValue(node.getNext().getFirstChild()));
                    break;
                case Constants.TEST_CASE_STEPS:
                    addTestSteps(node.getNext(), template);
                    break;
                default:
                    break;
            }
        }
    }

    public static Date getDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern(Constants.DATE_FORMAT);
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getAllChildValue(Node node) {
        StringBuilder sb = new StringBuilder();
        while (node != null) {
            if (node instanceof ListItem) {
                Node child = node.getFirstChild();
                if (child instanceof Paragraph) {
                    sb.append(((Text) child.getFirstChild()).getLiteral());
                }
            }

            if (node.getNext() != null)
                sb.append("\n");
            node = node.getNext();
        }
        return sb.toString();
    }

    public static void writeTemplateToFile(String fileName, String outFolder, TestCaseTemplate template) throws IOException {
        String markdownFileName = outFolder + File.separator + fileName + ".MD";
        StringBuilder sb = new StringBuilder()
                //.append(MarkdownLib.createHeaderLink(template.getTestcaseID(), "", 1))TEST_CASE_SCRIPT_LOCATION
                .append(MarkdownLib.createHeaderAndList(Constants.TEST_CASE_ID, 2, template.getTestcaseID(), 0))
                .append(MarkdownLib.createHeaderAndList(Constants.TEST_CASE_SCRIPT_LOCATION, 2, template.getTestScriptLink(), 0))
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
    }

    public static String buildTestSteps(List<TestStep> steps) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < steps.size(); i++) {
            TestStep step = steps.get(i);
            sb.append(MarkdownLib.createOrderedList(step.getNo(), Constants.TEST_CASE_STEP + step.getNo(), 0))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_CASE_STEP_DETAILS, 1))
                    .append(MarkdownLib.createUnorderedList(step.getDetails(), 2))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_CASE_STEP_DATA, 1))
                    .append(MarkdownLib.createUnorderedList(step.getData(), 2))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_CASE_STEP_EXPECTED_RESULTS, 1))
                    .append(MarkdownLib.createUnorderedList(step.getExpectedResults(), 2))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_CASE_STEP_ACTUAL_RESULTS, 1))
                    .append(MarkdownLib.createUnorderedList(step.getActualResults(), 2))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_CASE_STEP_FINAL_RESULTS, 1))
                    .append(MarkdownLib.createUnorderedList(step.getFinalResults(), 2));
        }
        return sb.toString();
    }

    public static void writeTotalAnalysisTemplateToFile(AnalysisTemplate template, String filePath) throws IOException {
        StringBuilder sb = new StringBuilder()
                .append(MarkdownLib.createHeader(Constants.REPORT_SUMMARY, 2))
                .append(MarkdownLib.createUnorderedList(Constants.TEST_RESULT_PASS + ": " + template.getPass(), 1))
                .append(MarkdownLib.createUnorderedList(Constants.TEST_RESULT_FAIL + ": " + template.getFail(), 1))
                .append(MarkdownLib.createUnorderedList(Constants.TEST_RESULT_NOT_EXECUTED + ": " + template.getNotExecuted(), 1))
                .append(MarkdownLib.createUnorderedList(Constants.TEST_RESULT_SUSPENDED + ": " + template.getSuspend(), 1))
                .append(MarkdownLib.createUnorderedList(Constants.TEST_RESULT_TOTAL + ": " + template.getTotal(), 1))
                .append(MarkdownLib.createHeader(Constants.REPORT_DETAILS, 2));

        for (int i = 0; i < template.getTests().size(); i++) {
            ShortTestResult result = template.getTests().get(i);
            sb.append(MarkdownLib.createOrderedList(i+1, result.getId(), 0))
                    .append(MarkdownLib.createUnorderedListLink(result.getName(), result.getTestSpecificationLocation(), 1))
                    .append(MarkdownLib.createUnorderedList(DateUtils.getDateFormat(result.getDateTest(), Constants.DATE_FORMAT), 1))
                    .append(MarkdownLib.createUnorderedList(result.getResult(), 1));
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(sb.toString());
        writer.close();
    }

    public static void writeAnAnalysisTemplateToFile(AnalysisTemplate template, String outfilePath) throws IOException {
        if (template.getTests().size() > 0 ) {
            StringBuilder sb = new StringBuilder()
                    .append(MarkdownLib.createHeader(Constants.REPORT_SUMMARY, 2))
                    .append(MarkdownLib.createUnorderedList(template.getTests().get(0).getId(), 1))
                    .append(MarkdownLib.createUnorderedList(template.getTests().get(0).getName(), 1))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_RESULT_PASS + ": " + template.getPass(), 1))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_RESULT_FAIL + ": " + template.getFail(), 1))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_RESULT_NOT_EXECUTED + ": " + template.getNotExecuted(), 1))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_RESULT_SUSPENDED + ": " + template.getSuspend(), 1))
                    .append(MarkdownLib.createUnorderedList(Constants.TEST_RESULT_TOTAL + ": " + template.getTotal(), 1))
                    .append(MarkdownLib.createHeader(Constants.REPORT_DETAILS, 2));
            for (int i = 0; i < template.getTests().size(); i++) {
                ShortTestResult result = template.getTests().get(i);
                sb.append(MarkdownLib.createOrderedList(i+1, DateUtils.getDateFormat(result.getDateTest(), Constants.DATE_FORMAT), 0))
                        .append(MarkdownLib.createUnorderedList(result.getResult(), 1));
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(outfilePath));
            writer.write(sb.toString());
            writer.close();
        }
    }
}
