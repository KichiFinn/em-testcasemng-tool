package com.testcasemng.tool.utils;

import java.util.Date;
import java.util.List;

public class TestCaseTemplate {
    private String testcaseID;
    private String testcaseName;
    private String testcaseDesc;
    private String createdBy;
    private String reviewedBy;
    private String version;
    private String preCondition;
    private String postCondition;
    private String log;
    private String testerName;
    private Date testDate;
    private String testResults;

    public TestCaseTemplate() {

    }

    public String getTestcaseID() {
        return testcaseID;
    }

    public void setTestcaseID(String testcaseID) {
        this.testcaseID = testcaseID;
    }

    public String getTestcaseName() {
        return testcaseName;
    }

    public void setTestcaseName(String testcaseName) {
        this.testcaseName = testcaseName;
    }

    public String getTestcaseDesc() {
        return testcaseDesc;
    }

    public void setTestcaseDesc(String testcaseDesc) {
        this.testcaseDesc = testcaseDesc;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPreCondition() {
        return preCondition;
    }

    public void setPreCondition(String preCondition) {
        this.preCondition = preCondition;
    }

    public String getPostCondition() {
        return postCondition;
    }

    public void setPostCondition(String postCondition) {
        this.postCondition = postCondition;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getTesterName() {
        return testerName;
    }

    public void setTesterName(String testerName) {
        this.testerName = testerName;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public String getTestResults() {
        return testResults;
    }

    public void setTestResults(String testResults) {
        this.testResults = testResults;
    }

    public List<TestStep> getTestSteps() {
        return testSteps;
    }

    public void setTestSteps(List<TestStep> testSteps) {
        this.testSteps = testSteps;
    }

    private List<TestStep> testSteps;
}
