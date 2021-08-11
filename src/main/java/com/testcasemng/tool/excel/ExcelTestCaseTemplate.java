package com.testcasemng.tool.excel;

import com.testcasemng.tool.markdown.MarkdownTestCaseTemplate;
import com.testcasemng.tool.utils.Constants;
import com.testcasemng.tool.utils.FileUtils;
import com.testcasemng.tool.utils.TestCaseTemplate;
import com.testcasemng.tool.utils.TestStep;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ExcelTestCaseTemplate {
    public static boolean initialize(String fullPath) throws Exception {
        System.out.println("Initialize Test Case Specification Excel file: " + fullPath);
        InputStream stream = null;
        if (FileUtils.getFileNameExtension(fullPath).equalsIgnoreCase(Constants.OLD_EXCEL_EXTENSION))
            stream = MarkdownTestCaseTemplate.class.getClassLoader().getResourceAsStream("Template1.xls");
        else if (FileUtils.getFileNameExtension(fullPath).equalsIgnoreCase(Constants.NEW_EXCEL_EXTENSION))
            stream = MarkdownTestCaseTemplate.class.getClassLoader().getResourceAsStream("Template1.xlsx");
        if (stream != null) {
            Files.copy(stream, Paths.get(fullPath), StandardCopyOption.REPLACE_EXISTING);
            stream.close();
        }
        return true;
    }

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
                    case Constants.TEST_CASE_LOCATION:
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

    public static boolean writeTemplateToFile(String fileName, TestCaseTemplate template) throws IOException {
        InputStream stream = null;
        String extension = "xlsx";
        String excelFileName = System.getProperty("user.dir") + File.separator + fileName + "." + extension;
        if (extension.equalsIgnoreCase(Constants.OLD_EXCEL_EXTENSION))
            stream = MarkdownTestCaseTemplate.class.getClassLoader().getResourceAsStream("Template1.xls");
        else if (extension.equalsIgnoreCase(Constants.NEW_EXCEL_EXTENSION))
            stream = MarkdownTestCaseTemplate.class.getClassLoader().getResourceAsStream("Template1.xlsx");

        if (stream != null) {
            Workbook workbook = WorkbookFactory.create(stream);
            Sheet sheet = workbook.getSheetAt(0);
            addTemplateDataToSheet(template, sheet);
            FileOutputStream outputStream = new FileOutputStream(excelFileName);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        }
        return true;
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
                        cell.setCellValue(getDateFormat(template.getCreatedDate(), Constants.DATE_FORMAT));
                        break;
                    case Constants.TEST_CASE_REVIEWED_BY:
                        cell = cellIterator.next();
                        cell.setCellValue(template.getReviewedBy());
                        break;
                    case Constants.TEST_CASE_REVIEWED_DATE:
                        cell = cellIterator.next();
                        cell.setCellValue(getDateFormat(template.getReviewedDate(), Constants.DATE_FORMAT));
                        break;
                    case Constants.TEST_CASE_LOCATION:
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
                                    sheet.createRow(row.getRowNum()+1);
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

    public static String getDateFormat(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        if (date == null)
            return "";
        return dateFormat.format(date);
    }
}
