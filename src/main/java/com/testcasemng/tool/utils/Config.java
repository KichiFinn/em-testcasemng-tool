package com.testcasemng.tool.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {
    private boolean generateExcelTests = true;
    private boolean generateExcelReports = true;
    private boolean generateHtmlReports = true;
    private String testDir = "./Tests";
    private String reportDir = "./Reports";

    Map<String, String> map = new HashMap<String, String>();

    public boolean isGenerateExcelTests() {
        return generateExcelTests;
    }

    public void setGenerateExcelTests(boolean generateExcelTests) {
        this.generateExcelTests = generateExcelTests;
    }

    public boolean isGenerateExcelReports() {
        return generateExcelReports;
    }

    public void setGenerateExcelReports(boolean generateExcelReports) {
        this.generateExcelReports = generateExcelReports;
    }

    public boolean isGenerateHtmlReports() {
        return generateHtmlReports;
    }

    public void setGenerateHtmlReports(boolean generateHtmlReports) {
        this.generateHtmlReports = generateHtmlReports;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public String getReportDir() {
        return reportDir;
    }

    public void setReportDir(String reportDir) {
        this.reportDir = reportDir;
    }

    public String getTestDir() { return testDir; }

    public void setTestDir(String testDir) { this.testDir = testDir; }

    public Config(String configFile) {
        try (InputStream input = new FileInputStream(configFile)) {
            Properties prop = new Properties();
            // load a properties file
            Reader reader = new InputStreamReader(input, "UTF-8");
            prop.load(reader);
            // get the property value and print it out
            String dirsString = prop.getProperty("tm.tests_dirs");
            String namesString = prop.getProperty("tm.report_names");
            String[] dirs = dirsString.split(",");
            String[] names = namesString.split(",");
            if (dirs.length == names.length) {
                for (int i= 0; i < dirs.length; i++)
                    map.put(names[i], dirs[i]);
            }
            this.setGenerateExcelTests(Boolean.parseBoolean(prop.getProperty("tm.generate_excel_test_specification")));
            this.setGenerateHtmlReports(Boolean.parseBoolean(prop.getProperty("tm.generate_html_test_report")));
            this.setGenerateExcelReports(Boolean.parseBoolean(prop.getProperty("tm.generate_excel_test_report")));
            this.setReportDir(prop.getProperty("tm.report_dir"));
            this.setTestDir(prop.getProperty("tm.test_dir"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
