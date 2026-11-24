package com.stockwise.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Standard ASCII table generator for structured console rendering.
 */
public class TableFormatter {

    public enum Alignment { LEFT, RIGHT, CENTER }

    private final List<String> headers = new ArrayList<>();
    private final List<Alignment> alignments = new ArrayList<>();
    private final List<List<String>> rows = new ArrayList<>();

    public TableFormatter(String... headers) {
        this.headers.addAll(Arrays.asList(headers));
        for (int i = 0; i < headers.length; i++) {
            this.alignments.add(Alignment.LEFT);
        }
    }

    public TableFormatter setAlignment(int colIndex, Alignment alignment) {
        if (colIndex >= 0 && colIndex < alignments.size()) {
            alignments.set(colIndex, alignment);
        }
        return this;
    }

    public TableFormatter addRow(Object... values) {
        List<String> row = new ArrayList<>();
        for (Object val : values) {
            row.add(val == null ? "N/A" : String.valueOf(val));
        }
        rows.add(row);
        return this;
    }

    public void print() {
        if (headers.isEmpty() && rows.isEmpty()) {
            System.out.println("  (No data to display)");
            return;
        }

        int numCols = headers.size();
        for (List<String> r : rows) {
            if (r.size() > numCols) numCols = r.size();
        }

        int[] colWidths = new int[numCols];
        for (int c = 0; c < headers.size(); c++) {
            colWidths[c] = headers.get(c).length();
        }

        for (List<String> row : rows) {
            for (int c = 0; c < row.size(); c++) {
                int len = row.get(c).length();
                if (len > colWidths[c]) {
                    colWidths[c] = len;
                }
            }
        }

        // Add padding
        for (int c = 0; c < numCols; c++) {
            colWidths[c] += 2;
        }

        String border = buildBorder(colWidths);

        System.out.println(border);
        printRow(headers, colWidths, alignments);
        System.out.println(border);

        if (rows.isEmpty()) {
            System.out.println("| " + padCenter("(No records found)", getInnerWidth(colWidths)) + " |");
        } else {
            for (List<String> row : rows) {
                printRow(row, colWidths, alignments);
            }
        }
        System.out.println(border);
    }

    private int getInnerWidth(int[] colWidths) {
        int w = 0;
        for (int cw : colWidths) w += cw + 3;
        return Math.max(0, w - 3);
    }

    private String buildBorder(int[] colWidths) {
        StringBuilder sb = new StringBuilder("+");
        for (int w : colWidths) {
            sb.append("-".repeat(w + 2)).append("+");
        }
        return sb.toString();
    }

    private void printRow(List<String> values, int[] colWidths, List<Alignment> aligns) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < colWidths.length; i++) {
            String val = (i < values.size()) ? values.get(i) : "";
            int width = colWidths[i];
            Alignment align = (i < aligns.size()) ? aligns.get(i) : Alignment.LEFT;

            sb.append(" ");
            if (align == Alignment.RIGHT) {
                sb.append(padLeft(val, width));
            } else if (align == Alignment.CENTER) {
                sb.append(padCenter(val, width));
            } else {
                sb.append(padRight(val, width));
            }
            sb.append(" |");
        }
        System.out.println(sb);
    }

    private static String padRight(String s, int width) {
        if (s.length() >= width) return s;
        return s + " ".repeat(width - s.length());
    }

    private static String padLeft(String s, int width) {
        if (s.length() >= width) return s;
        return " ".repeat(width - s.length()) + s;
    }

    private static String padCenter(String s, int width) {
        if (s.length() >= width) return s;
        int left = (width - s.length()) / 2;
        int right = width - s.length() - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }
}
