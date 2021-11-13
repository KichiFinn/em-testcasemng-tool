package com.testcasemng.tool.utils;

import java.util.Date;

public class ShortTestResult {
    private String id;
    private String name;
    private String result;
    private Date dateTest;
    private String testSpecificationLocation;

    public ShortTestResult() {
        this.id = "";
        this.name = "";
        this.result = "";
        this.testSpecificationLocation = "";
        this.dateTest = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getDateTest() {
        return dateTest;
    }

    public void setDateTest(Date dateTest) {
        this.dateTest = dateTest;
    }

    public String getTestSpecificationLocation() { return testSpecificationLocation; }

    public void setTestSpecificationLocation(String testSpecificationLocation) { this.testSpecificationLocation = testSpecificationLocation; }
}
