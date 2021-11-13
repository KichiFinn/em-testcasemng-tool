package com.testcasemng.tool.cli;

import com.testcasemng.tool.utils.*;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.ArrayList;

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
        options.addOption(Option.builder("m2e")
                .longOpt("markdown2excel")
                .hasArg(false)
                .desc("Convert Markdown test specifications to Excel")
                .required(false)
                .build());
        options.addOption(Option.builder("e2m")
                .longOpt("excel2markdown")
                .hasArg(false)
                .desc("Convert Excel test specifications to Markdown")
                .required(false)
                .build());
        options.addOption(Option.builder("a")
                .longOpt("analyze")
                .hasArg(false)
                .desc("Analyze test results")
                .required(false)
                .build());
        options.addOption(Option.builder("config")
                .hasArg(false)
                .desc("Analyze tests with config file")
                .required(false)
                .build());
        options.addOption(Option.builder("h")
                .longOpt("history")
                .hasArg(false)
                .desc("Analyze historical test results")
                .required(false)
                .build());
        options.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg(true)
                .desc("[REQUIRED] input file (Excel or Markdown) or * (get all Excel or Markdown in this folder)")
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .required(true)
                .build());
        options.addOption(Option.builder("o")
                .longOpt("output")
                .hasArg(true)
                .desc("[REQUIRED] output directory")
                .required(false)
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
                String output = cmd.getOptionValue("o");
                File file = new File(fileName);
                if (file.isDirectory()) {
                    if(cmd.hasOption("m2e"))
                        Conversion.convertMarkdownDirectory(file, output, new ArrayList());
                    else if (cmd.hasOption("e2m"))
                        Conversion.convertExcelDirectory(file, output);
                    else
                        System.out.println("Error parsing command-line arguments!. Run -h for help");
                } else if (file.isFile() && FileUtils.isMarkdownFile(fileName)) {
                    Conversion.convertMarkdownFileToExcelFile(file, output, new ArrayList());
                } else if (file.isFile() && FileUtils.isExcelFile(fileName)) {
                    Conversion.convertExcelFileToMarkdownFile(file, output);
                } else
                    System.out.println("Error parsing command-line arguments!. Run -h for help");
            } else if (cmd.hasOption("f") && cmd.hasOption("a")) {
                String fileName = cmd.getOptionValue("f");
                String output = cmd.getOptionValue("o");
                Analysis.analyze(fileName, output, cmd.hasOption("h"));
            } else if (cmd.hasOption("f") && cmd.hasOption("config")) {
                String fileName = cmd.getOptionValue("f");
                Config config = new Config(fileName);
                Analysis.analyze(config);
                Conversion.convert(config);
            }else {
                System.out.println("Error parsing command-line arguments!. Run -h for help");
            }

        } catch (ParseException pe) {
            System.out.println("Error parsing command-line arguments!");
            System.out.println("Please, follow the instructions below:");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar em-testcasemng-tool.jar [options] -f <input>\n" +
                    "example: \n" +
                    "java -jar em-testcasemng-tool.jar -i -f /path/to/directory/SampleTestCase.MD: Create a Markdown test specifications file named SampleTestCase.MD\n" +
                    "java -jar em-testcasemng-tool.jar -i -f /path/to/directory/SampleTestCase.xls: Create a Excel test specifications file named SampleTestCase.xls\n" +
                    "java -jar em-testcasemng-tool.jar -c -f /path/to/directory/SampleTestCase.MD -o /path/to/output/: Convert SampleTestCase.MD to SampleTestCase.xlsx\n" +
                    "java -jar em-testcasemng-tool.jar -c -f /path/to/directory/SampleTestCase.xlsx -o /path/to/output/: Convert SampleTestCase.xlsx to SampleTestCase.MD\n" +
                    "java -jar em-testcasemng-tool.jar -c -m2e -f /path/to/directory -o /path/to/output/: Convert all Markdown files in directory to Excel files\n" +
                    "java -jar em-testcasemng-tool.jar -c -e2m -f /path/to/directory -o /path/to/output/: Convert all Excel files in directory to Markdown files\n" +
                    "java -jar em-testcasemng-tool.jar -a -h -f /path/to/directory/SampleTestCase.MD -o /path/to/output/: Analyze all historical test results of SampleTestCase.MD, result is in SampleTestCaseAnalysis.xlsx \n" +
                    "java -jar em-testcasemng-tool.jar -a -h -f /path/to/directory/ -o /path/to/output/: Analyze all historical test results of directory, result is in output directory \n" +
                    "java -jar em-testcasemng-tool.jar -a -f /path/to/directory -o /path/to/output: Analyze all test results in the directory, result is in /path/to/output/TotalAnalysis.xlsx\n" +
                    "options:\n", options);
            System.exit(1);
        }
    }


}
