package com.testcasemng.tool.markdown;

import com.testcasemng.tool.utils.Constants;
import com.testcasemng.tool.utils.FileUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MarkdownLib {

    public static String createHeaderLink(String value, String link, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++)
            sb.append(Constants.MARKDOWN_HEADER);
        sb.append(" ")
                .append("[")
                .append(FileUtils.escapeMarkdownSpecialCharacter(value))
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
                .append(FileUtils.escapeMarkdownSpecialCharacter(value))
                .append("\r\n");
        return sb.toString();
    }

    public static String createUnorderedListItem(String value, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++)
            sb.append(Constants.MARKDOWN_LEVEL_SEPARATOR);
        sb.append(Constants.MARKDOWN_LIST_SIGNAL)
                .append(" ")
                .append(FileUtils.escapeMarkdownSpecialCharacter(value))
                .append("\r\n");
        return sb.toString();
    }

    public static String createUnorderedList(String value, int level) {
        StringBuilder sb = new StringBuilder();
        for (String item : value.split("\n")) {
            sb.append(createUnorderedListItem(item, level));
        }
        return sb.toString();
    }

    public static String createUnorderedListLink(String value, String link, int level) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++)
            sb.append(Constants.MARKDOWN_LEVEL_SEPARATOR);
        String o = URLEncoder.encode(link, StandardCharsets.UTF_8.toString());
        sb.append(Constants.MARKDOWN_LIST_SIGNAL)
                .append(" [")
                .append(FileUtils.escapeMarkdownSpecialCharacter(value))
                .append(" ](")
                .append(FileUtils.escapeMarkdownSpecialCharacter(URLEncoder.encode(link, StandardCharsets.UTF_8.toString()).replace("+", "%20")))
                .append(")\r\n");
        return sb.toString();
    }


    public static String createHeaderAndList(String header, int headerLevel, String list, int listLevel) {
        StringBuilder sb = new StringBuilder().
                append(createHeader(header, headerLevel))
                .append(createUnorderedList(list, listLevel));
        return sb.toString();
    }

    public static String createOrderedList(int number, String value, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++)
            sb.append(Constants.MARKDOWN_LEVEL_SEPARATOR);
        sb.append(number)
                .append(". ")
                .append(FileUtils.escapeMarkdownSpecialCharacter(value))
                .append("\r\n");
        return sb.toString();
    }


}
