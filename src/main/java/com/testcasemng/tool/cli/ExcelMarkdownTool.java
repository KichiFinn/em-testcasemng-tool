package com.testcasemng.tool.cli;

import com.testcasemng.tool.utils.*;
import org.apache.commons.cli.*;

import java.io.File;

public class ExcelMarkdownTool {
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(Option.builder("i")
                .longOpt("init")
                .hasArg(false)
                .desc("Initialize test specifications template (Excel or Markdown)")
                .required(false)
                .build());
        options.addOption(Option.builder("c")
                .longOpt("convert")
                .hasArg(false)
                .desc("Convert test specifications between Excel and Markdown")
                .required(false)
                .build());
        options.addOption(Option.builder("a")
                .longOpt("analyze")
                .hasArg(false)
                .desc("Analyze test results")
                .required(false)
                .build());
        options.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg(true)
                .desc("[REQUIRED] input file (Excel or Markdown) or * (get all Excel or Markdown in this folder)")
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .required(true)
                .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("f") && cmd.hasOption("i")) {
                String fileName = cmd.getOptionValue("f");
                TemplateInitialization.initTemplate(fileName);
            } else if (cmd.hasOption("f") && cmd.hasOption("c")) {
                String fileName = cmd.getOptionValue("f");
                File file = new File(fileName);
                if (file.isDirectory()) {
                    Conversion.convertDirectory(file);
                } else if (file.isFile() && FileUtils.isMarkdownFile(fileName)) {
                    Conversion.convertMarkdownFileToExcelFile(file);
                } else if (file.isFile() && FileUtils.isExcelFile(fileName)) {
                    Conversion.convertExcelFileToMarkdownFile(file);
                } else
                    System.out.println("Error parsing command-line arguments!. Run -h for help");
            } else if (cmd.hasOption("f") && cmd.hasOption("a")) {
                String fileName = cmd.getOptionValue("f");
                System.out.println("fileName= " + fileName);
                Analysis.analyze(fileName);
            } else {
                System.out.println("Error parsing command-line arguments!. Run -h for help");
            }

        } catch (ParseException pe) {
            System.out.println("Error parsing command-line arguments!");
            System.out.println("Please, follow the instructions below:");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar em-testcasemng-tool.jar [options] -f <input>\n" +
                    "example: \n" +
                    "java -jar em-testcasemng-tool.jar -i -f /path/to/directory/TestCase1.MD: Create a Markdown test specifications file named TestCase1.MD\n" +
                    "java -jar em-testcasemng-tool.jar -i -f /path/to/directory/TestCase1.xls: Create a Excel test specifications file named TestCase1.xls\n" +
                    "java -jar em-testcasemng-tool.jar -c -f /path/to/directory/TestCase1.MD: Convert TestCase1.MD to TestCase1.xls\n" +
                    "java -jar em-testcasemng-tool.jar -c -f /path/to/directory/TestCase1.xls: Convert TestCase1.xls to TestCase1.MD\n" +
                    "java -jar em-testcasemng-tool.jar -c -f /path/to/directory/*.MD: Convert all Markdown files to Excel files\n" +
                    "java -jar em-testcasemng-tool.jar -a -f /path/to/directory/TestCase1.MD: Analyze all historical test results of TestCase1\n" +
                    "java -jar em-testcasemng-tool.jar -a -f /path/to/directory: Analyze all test results (Markdown) in the directory\n" +
                    "options:\n", options);
            System.exit(1);
        }
    }


}
