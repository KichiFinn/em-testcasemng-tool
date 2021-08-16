package com.testcasemng.tool.utils;

import java.util.ArrayList;
import java.util.List;

public class AnalysisTemplate {

    private int pass;
    private int fail;
    private int suspend;
    private int notExecuted;
    private int others;
    private List<ShortTestResult> tests;

    public List<ShortTestResult> getTests() {
        return tests;
    }

    public void setTests(List<ShortTestResult> tests) {
        this.tests = tests;
    }

    public AnalysisTemplate() {
        this.pass = 0;
        this.fail = 0;
        this.suspend = 0;
        this.notExecuted = 0;
        this.others = 0;
        this.tests = new ArrayList<>();
    }

    public int getPass() {
        return pass;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }

    public int getFail() {
        return fail;
    }

    public void setFail(int fail) {
        this.fail = fail;
    }

    public int getSuspend() {
        return suspend;
    }

    public void setSuspend(int suspend) {
        this.suspend = suspend;
    }

    public int getNotExecuted() {
        return notExecuted;
    }

    public void setNotExecuted(int notExecuted) {
        this.notExecuted = notExecuted;
    }

    public int getOthers() {
        return others;
    }

    public void setOthers(int others) {
        this.others = others;
    }

    public int getTotal() {
        return (pass + fail + notExecuted + suspend);
    }
}
