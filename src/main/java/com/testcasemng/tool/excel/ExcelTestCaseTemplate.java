package com.testcasemng.tool.excel;

import com.testcasemng.tool.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelTestCaseTemplate {
    public static List<TestCaseTemplate> readFromFile(File file) throws IOException {
        List<TestCaseTemplate> templates = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(file);
        for (Sheet sheet : workbook) {
            templates.add(parseSheet(sheet));
        }
        workbook.close();
        return templates;
    }

    public static TestCaseTemplate parseSheet(Sheet sheet) {
        List<TestStep> steps = new ArrayList<>();
        TestCaseTemplate template = new TestCaseTemplate();
        DataFormatter formatter = new DataFormatter();
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                String cellValue = formatter.formatCellValue(cell);

                switch (cellValue) {
                    case Constants.TEST_CASE_ID:
                        cell = cellIterator.next();
                        template.setTestcaseID(cell.getStringCellValue());
                        break;
                    case Constants.TEST_CASE_NAME:
                        cell = cellIterator.next();
                        template.setTestcaseName(cell.getStringCellValue());
                        break;
                    case Constants.TEST_CASE_DESCRIPTION:
                        cell = cellIterator.next();
                        template.setTestcaseDesc(cell.getStringCellValue());
                        break;
                    case Constants.TEST_CASE_CREATED_BY:
                        cell = cellIterator.next();
                        template.setCreatedBy(cell.getStringCellValue());
                        break;
                    case Constants.TEST_CASE_REVIEWED_BY:
                        cell = cellIterator.next();
                        template.setReviewedBy(cell.getStringCellValue());
                        break;
                    case Constants.TEST_CASE_VERSION:
                        cell = cellIterator.next();
                        template.setVersion(cell.getStringCellValue());
                        break;
                    case Constants.TEST_CASE_PRE_CONDITION:
                        cell = cellIterator.next();
                        template.setPreCondition(cell.getStringCellValue());
                        break;
                    case Constants.TEST_CASE_POST_CONDITION:
                        cell = cellIterator.next();
                        template.setPostCondition(cell.getStringCellValue());
                        break;
                    case Constants.TEST_CASE_SCRIPT_LOCATION:
                        cell = cellIterator.next();
                        template.setTestScriptLink(cell.getStringCellValue());
                        break;
                    case Constants.TEST_CASE_LOG:
                        cell = cellIterator.next();
                        template.setLog(cell.getStringCellValue());
                        break;
                    case Constants.TEST_CASE_TESTER_NAME:
                        cell = cellIterator.next();
                        template.setTesterName(cell.getStringCellValue());
                        break;
                    case Constants.TEST_CASE_DATE_TESTED:
                        cell = cellIterator.next();
                        template.setTestDate(cell.getDateCellValue());
                        break;
                    case Constants.TEST_CASE_RESULTS:
                        cell = cellIterator.next();
                        template.setTestResults(cell.getStringCellValue());
                        break;
                    case Constants.TEST_CASE_STEP_NO:
                    case Constants.TEST_CASE_EMPTY:
                        break;
                    default:
                        if (StringUtils.isNumeric(cellValue)) {
                            TestStep step = new TestStep();
                            step.setNo(Integer.parseInt(cellValue));
                            cell = cellIterator.next();
                            step.setDetails(cell.getStringCellValue());
                            cell = cellIterator.next();
                            step.setData(cell.getStringCellValue());
                            cell = cellIterator.next();
                            step.setExpectedResults(cell.getStringCellValue());
                            cell = cellIterator.next();
                            step.setActualResults(cell.getStringCellValue());
                            cell = cellIterator.next();
                            step.setFinalResults(cell.getStringCellValue());
                            steps.add(step);
                        }
                        break;
                }
            }
        }
        template.setTestSteps(steps);
        return template;
    }

    public static void writeTemplateToFile(String fileName, String outFolder, TestCaseTemplate template) throws IOException {
        String extension = "xlsx";
        String excelFileName = outFolder + File.separator + fileName + "." + extension;
        TemplateInitialization.initTemplate(excelFileName);
        FileInputStream inputStream = new FileInputStream(excelFileName);

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        addTemplateDataToSheet(template, sheet);
        FileOutputStream outputStream = new FileOutputStream(excelFileName);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public static void addTemplateDataToSheet(TestCaseTemplate template, Sheet sheet) {
        DataFormatter formatter = new DataFormatter();
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {

                Cell cell = cellIterator.next();
                String cellValue = formatter.formatCellValue(cell);

                switch (cellValue) {
                    case Constants.TEST_CASE_ID:
                        cell = cellIterator.next();
                        cell.setCellValue(template.getTestcaseID());
                        break;
                    case Constants.TEST_CASE_NAME:
                        cell = cellIterator.next();
                        cell.setCellValue(template.getTestcaseName());
                        break;
                    case Constants.TEST_CASE_DESCRIPTION:
                        cell = cellIterator.next();
                        cell.setCellValue(template.getTestcaseDesc());
                        break;
                    case Constants.TEST_CASE_CREATED_BY:
                        cell = cellIterator.next();
                        cell.setCellValue(template.getCreatedBy());
                        break;
                    case Constants.TEST_CASE_CREATED_DATE:
                        cell = cellIterator.next();
                        cell.setCellValue(DateUtils.getDateFormat(template.getCreatedDate(), Constants.DATE_FORMAT));
                        break;
                    case Constants.TEST_CASE_REVIEWED_BY:
                        cell = cellIterator.next();
                        cell.setCellValue(template.getReviewedBy());
                        break;
                    case Constants.TEST_CASE_REVIEWED_DATE:
                        cell = cellIterator.next();
                        cell.setCellValue(DateUtils.getDateFormat(template.getReviewedDate(), Constants.DATE_FORMAT));
                        break;
                    case Constants.TEST_CASE_SCRIPT_LOCATION:
                        cell = cellIterator.next();
                        cell.setCellValue(template.getTestScriptLink());
                        break;
                    case Constants.TEST_CASE_VERSION:
                        cell = cellIterator.next();
                        cell.setCellValue(template.getVersion());
                        break;
                    case Constants.TEST_CASE_PRE_CONDITION:
                        cell = cellIterator.next();
                        cell.setCellValue(template.getPreCondition());
                        break;
                    case Constants.TEST_CASE_POST_CONDITION:
                        cell = cellIterator.next();
                        cell.setCellValue(template.getPostCondition());
                        break;
                    case Constants.TEST_CASE_LOG:
                        cell = cellIterator.next();
                        cell.setCellValue(template.getLog());
                        break;
                    case Constants.TEST_CASE_TESTER_NAME:
                        cell = cellIterator.next();
                        cell.setCellValue(template.getTesterName());
                        break;
                    case Constants.TEST_CASE_DATE_TESTED:
                        cell = cellIterator.next();
                        cell.setCellValue(template.getTestDate());
                        break;
                    case Constants.TEST_CASE_RESULTS:
                        cell = cellIterator.next();
                        cell.setCellValue(template.getTestResults());
                        break;
                    case Constants.TEST_CASE_STEP_NO:
                        List<TestStep> steps = template.getTestSteps();
                        if (steps != null && steps.size() > 0) {
                            for (int i = 0; i < steps.size(); i++) {
                                TestStep step = steps.get(i);
                                if (row.getRowNum() == sheet.getLastRowNum()) {
                                    sheet.createRow(row.getRowNum() + 1);
                                    row = sheet.getRow(sheet.getLastRowNum());
                                    row.createCell(0).setCellValue(step.getNo());
                                    row.createCell(1).setCellValue(step.getDetails());
                                    row.createCell(2).setCellValue(step.getData());
                                    row.createCell(3).setCellValue(step.getExpectedResults());
                                    row.createCell(4).setCellValue(step.getActualResults());
                                    row.createCell(5).setCellValue(step.getFinalResults());
                                } else {
                                    row = rowIterator.next();
                                    row.getCell(0).setCellValue(step.getNo());
                                    row.getCell(1).setCellValue(step.getDetails());
                                    row.getCell(2).setCellValue(step.getData());
                                    row.getCell(3).setCellValue(step.getExpectedResults());
                                    row.getCell(4).setCellValue(step.getActualResults());
                                    row.getCell(5).setCellValue(step.getFinalResults());
                                }
                            }
                        }
                    default:
                        break;
                }
            }
        }
    }


    public static void writeTotalAnalysisTemplateToFile(AnalysisTemplate template, String filePath) throws IOException {
        System.out.println("Test results summary generated in " + filePath);

        Workbook workbook = WorkbookFactory.create(true);
        Sheet sheet = workbook.createSheet("Summary");

        List<ShortTestResult> tests = template.getTests();
        sheet.createRow(0);
        sheet.getRow(0).createCell(0).setCellValue(Constants.TEST_CASE_ID);
        sheet.getRow(0).createCell(1).setCellValue(Constants.TEST_CASE_NAME);
        sheet.getRow(0).createCell(2).setCellValue(Constants.TEST_CASE_DATE_TESTED);
        sheet.getRow(0).createCell(3).setCellValue(Constants.TEST_CASE_RESULTS);
        int i = 1;
        for (ShortTestResult test : tests) {
            if (sheet.getRow(i) == null)
                sheet.createRow(i);
            sheet.getRow(i).createCell(0).setCellValue(test.getId());
            sheet.getRow(i).createCell(1).setCellValue(test.getName());
            sheet.getRow(i).createCell(2).setCellValue(DateUtils.getDateFormat(test.getDateTest(), Constants.DATE_FORMAT));
            sheet.getRow(i).createCell(3).setCellValue(test.getResult());
            i++;
        }

        if (sheet.getRow(1) == null)
            sheet.createRow(1);
        sheet.getRow(1).createCell(5).setCellValue(Constants.TEST_RESULT_PASS);
        sheet.getRow(1).createCell(6).setCellValue(template.getPass());

        if (sheet.getRow(2) == null)
            sheet.createRow(2);
        sheet.getRow(2).createCell(5).setCellValue(Constants.TEST_RESULT_FAIL);
        sheet.getRow(2).createCell(6).setCellValue(template.getFail());

        if (sheet.getRow(3) == null)
            sheet.createRow(3);
        sheet.getRow(3).createCell(5).setCellValue(Constants.TEST_RESULT_NOT_EXECUTED);
        sheet.getRow(3).createCell(6).setCellValue(template.getNotExecuted());

        if (sheet.getRow(4) == null)
            sheet.createRow(4);
        sheet.getRow(4).createCell(5).setCellValue(Constants.TEST_RESULT_SUSPENDED);
        sheet.getRow(4).createCell(6).setCellValue(template.getSuspend());

        if (sheet.getRow(5) == null)
            sheet.createRow(5);
        sheet.getRow(5).createCell(5).setCellValue(Constants.TEST_RESULT_TOTAL);
        sheet.getRow(5).createCell(6).setCellValue(template.getTotal());

        FileOutputStream outputStream = new FileOutputStream(filePath);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

    }

    public static void writeAnAnalysisTemplateToFile(AnalysisTemplate template, String outfilePath) throws IOException {
        System.out.println("Test results summary generated in " + outfilePath);

        Workbook workbook = WorkbookFactory.create(true);
        Sheet sheet = workbook.createSheet("Summary");

        List<ShortTestResult> tests = template.getTests();
        sheet.createRow(0);
        sheet.getRow(0).createCell(0).setCellValue(Constants.TEST_CASE_DATE_TESTED);
        sheet.getRow(0).createCell(1).setCellValue(Constants.TEST_CASE_RESULTS);
        sheet.getRow(0).createCell(3).setCellValue(Constants.TEST_CASE_ID);
        sheet.getRow(0).createCell(4).setCellValue(Constants.TEST_CASE_NAME);

        int i = 1;
        for (ShortTestResult test : tests) {
            if (sheet.getRow(i) == null)
                sheet.createRow(i);
            if (i == 1) {
                sheet.getRow(1).createCell(3).setCellValue(test.getId());
                sheet.getRow(1).createCell(4).setCellValue(test.getName());
            }
            sheet.getRow(i).createCell(0).setCellValue(DateUtils.getDateFormat(test.getDateTest(), Constants.DATE_FORMAT));
            sheet.getRow(i).createCell(1).setCellValue(test.getResult());
            i++;
        }

        if (sheet.getRow(1) == null)
            sheet.createRow(1);
        sheet.getRow(1).createCell(6).setCellValue(Constants.TEST_RESULT_PASS);
        sheet.getRow(1).createCell(7).setCellValue(template.getPass());

        if (sheet.getRow(2) == null)
            sheet.createRow(2);
        sheet.getRow(2).createCell(6).setCellValue(Constants.TEST_RESULT_FAIL);
        sheet.getRow(2).createCell(7).setCellValue(template.getFail());

        if (sheet.getRow(3) == null)
            sheet.createRow(3);
        sheet.getRow(3).createCell(6).setCellValue(Constants.TEST_RESULT_NOT_EXECUTED);
        sheet.getRow(3).createCell(7).setCellValue(template.getNotExecuted());

        if (sheet.getRow(4) == null)
            sheet.createRow(4);
        sheet.getRow(4).createCell(6).setCellValue(Constants.TEST_RESULT_SUSPENDED);
        sheet.getRow(4).createCell(7).setCellValue(template.getSuspend());

        if (sheet.getRow(5) == null)
            sheet.createRow(5);
        sheet.getRow(5).createCell(6).setCellValue(Constants.TEST_RESULT_TOTAL);
        sheet.getRow(5).createCell(7).setCellValue(template.getTotal());

        FileOutputStream outputStream = new FileOutputStream(outfilePath);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
