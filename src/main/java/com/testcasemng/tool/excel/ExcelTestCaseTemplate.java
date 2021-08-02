package com.testcasemng.tool.excel;

import com.testcasemng.tool.markdown.MarkdownTestCaseTemplate;
import com.testcasemng.tool.utils.Constants;
import com.testcasemng.tool.utils.FileUtils;
import com.testcasemng.tool.utils.TestCaseTemplate;
import com.testcasemng.tool.utils.TestStep;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelTestCaseTemplate {
    public static boolean initialize(String fullPath) throws Exception {
        System.out.println("Initialize Test Case Specification Excel file: " + fullPath);
        InputStream stream = null;
        try {
            if (FileUtils.getFileNameExtension(fullPath).equalsIgnoreCase(Constants.OLD_EXCEL_EXTENSION))
                stream = MarkdownTestCaseTemplate.class.getClassLoader().getResourceAsStream("Template1.xls");
            else if (FileUtils.getFileNameExtension(fullPath).equalsIgnoreCase(Constants.NEW_EXCEL_EXTENSION))
                stream = MarkdownTestCaseTemplate.class.getClassLoader().getResourceAsStream("Template1.xlsx");
            Files.copy(stream, Paths.get(fullPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
        }
        return true;
    }

    public static List<TestCaseTemplate> readFromFile(File file) throws IOException {
        List<TestCaseTemplate> templates = new ArrayList<TestCaseTemplate>();
        try {
            Workbook workbook = WorkbookFactory.create(file);
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            for (Sheet sheet : workbook) {
                templates.add(parseSheet(sheet));
            }
        } catch (Exception ex) {
            throw ex;
        }
        return templates;
    }

    public static TestCaseTemplate parseSheet(Sheet sheet) {
        List<TestStep> steps = new ArrayList<TestStep>();
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
                        break;
                    case Constants.TEST_CASE_EMPTY:
                        break;
                    default:
                        if (StringUtils.isNumeric(cellValue))
                        {
                            TestStep step = new TestStep();
                            step.setNo(Integer. parseInt(cellValue));
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

    public static boolean writeToFile(List<TestCaseTemplate> templates, String fullName) {


        return true;
    }
}
