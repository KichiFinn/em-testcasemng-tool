package com.testcasemng.tool.utils;

public class TestStep {

    private int no;
    private String details;
    private String data;
    private String expectedResults;
    private String actualResults;
    private String finalResults;

    public TestStep() {

    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getExpectedResults() {
        return expectedResults;
    }

    public void setExpectedResults(String expectedResults) {
        this.expectedResults = expectedResults;
    }

    public String getActualResults() {
        return actualResults;
    }

    public void setActualResults(String actualResults) {
        this.actualResults = actualResults;
    }

    public String getFinalResults() {
        return finalResults;
    }

    public void setFinalResults(String finalResults) {
        this.finalResults = finalResults;
    }
}
