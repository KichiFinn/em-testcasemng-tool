package com.testcasemng.tool.html;

import com.testcasemng.tool.utils.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class HTMLReportTemplate {

    public static void writeHistoricalAnalysisTemplateToFile(AnalysisTemplate template, String outfilePath) throws IOException {
        if (template.getTests().size() > 0) {
            String chartData = String.format(Constants.HTML_CHART_DATA, template.getPass(), template.getFail(), template.getNotExecuted(), template.getSuspend());
            String htmlTitle = String.format(Constants.HTML_TITLE, template.getTests().get(0).getId(), template.getTests().get(0).getName());
            StringBuilder tableData = new StringBuilder();
            for (int i = 0; i < template.getTests().size(); i++) {
                String row = String.format("['%s', '%s', '%s']", DateUtils.getDateFormat(template.getTests().get(i).getDateTest(), Constants.DATE_FORMAT), template.getTests().get(i).getResult(), template.getTests().get(i).getActualResults());
                tableData.append(row).append(", ");

            }
            InputStream inputStream = FileUtils.getFileFromResourceAsStream("historical_report_template.html");
            if (inputStream != null) {
                Scanner sc = new Scanner(inputStream);
                StringBuffer sb = new StringBuffer();
                while (sc.hasNext()) {
                    sb.append(sc.nextLine());
                }
                String data = tableData.toString();
                BufferedWriter writer = new BufferedWriter(new FileWriter(outfilePath));
                writer.write(String.format(sb.toString(), htmlTitle, template.getTotal(), template.getPass(), template.getFail(), template.getNotExecuted(), template.getSuspend(), chartData, data));
                writer.close();
            }
        }
    }

    public static void writeSummaryAnalysisTemplateToFile(AnalysisTemplate template, String outfilePath, String reportName) throws IOException {
        if (template.getTests().size() > 0) {
            String chartData = String.format(Constants.HTML_CHART_DATA, template.getPass(), template.getFail(), template.getNotExecuted(), template.getSuspend());
            InputStream inputStream = FileUtils.getFileFromResourceAsStream("summary_report_template.html");
            StringBuilder tableData = new StringBuilder();
            for (int i = 0; i < template.getTests().size(); i++) {
                ShortTestResult result = template.getTests().get(i);
                String testDetailLocation = result.getTestSpecificationLocation();
                if (testDetailLocation.indexOf("Tests") > 0) {
                    testDetailLocation = testDetailLocation.substring(testDetailLocation.indexOf("Tests") +5);
                    testDetailLocation = "." + testDetailLocation.substring(0, testDetailLocation.lastIndexOf('.')) + "Analysis.html";
                }
                // String testLocation = URLEncoder.encode(result.getTestSpecificationLocation(), StandardCharsets.UTF_8.toString()).replace("+", "%20");
                String row = String.format("['%s', '%s', '%s', '%s', '<a href=\"%s\">Click here</a>']", result.getId(), result.getName(), DateUtils.getDateFormat(result.getDateTest(), Constants.DATE_FORMAT), result.getResult(), testDetailLocation);
                tableData.append(row).append(", ");

            }
            if (inputStream != null) {
                Scanner sc = new Scanner(inputStream);
                StringBuffer sb = new StringBuffer();
                while (sc.hasNext()) {
                    sb.append(sc.nextLine());
                }
                String data = tableData.toString();
                BufferedWriter writer = new BufferedWriter(new FileWriter(outfilePath));
                writer.write(String.format(sb.toString(), reportName, template.getTotal(), template.getPass(), template.getFail(), template.getNotExecuted(), template.getSuspend(), chartData, template.getTests().size(), data));
                writer.close();
            }
        }
    }
}
