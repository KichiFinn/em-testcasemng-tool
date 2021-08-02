package com.testcasemng.tool.markdown;

import com.testcasemng.tool.utils.Constants;
import com.testcasemng.tool.utils.TestCaseTemplate;

public class MarkdownLib {

    public static String createHeaderLink(String value, String link, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++)
            sb.append(Constants.MARKDOWN_HEADER);
        sb.append(" ")
                .append("[")
                .append(value)
                .append("](")
                .append(link)
                .append(")")
                .append("\r\n");
        return sb.toString();
    }

    public static String createHeader(String value, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++)
            sb.append(Constants.MARKDOWN_HEADER);
        sb.append(" ")
                .append(value.equals("") ? "N/A" : value)
                .append("\r\n");
        return sb.toString();
    }

    public static String createUnorderedList(String value, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++)
            sb.append(Constants.MARKDOWN_LEVEL_SEPARATOR);
        sb.append(Constants.MARKDOWN_LIST_SIGNAL)
                .append(" ")
                .append(value.equals("") ? "N/A" : value)
                .append("\r\n");
        return sb.toString();
    }

    public static String createHeaderAndList(String header, int headerLevel, String list, int listLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append(createHeader(header, headerLevel))
                .append(createUnorderedList(list, listLevel));
        return sb.toString();
    }

    public static String createOrderedList(int number, String value, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++)
            sb.append(Constants.MARKDOWN_LEVEL_SEPARATOR);
        sb.append(Integer.toString(number))
                .append(". ")
                .append(value)
                .append("\r\n");
        return sb.toString();
    }


}
