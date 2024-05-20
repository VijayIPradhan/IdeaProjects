package com.excel.excelintercept.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XSSFEventProcessor extends DefaultHandler {
    private final StylesTable styles;
    private final ReadOnlySharedStringsTable sharedStrings;
    private final int chunkSize;
    private final File outputDirectory;
    private InputStream currentSheet;
    private int rowCounter = 0;
    private boolean inRow = false;
    private int fileCount = 1;
    private Workbook currentWorkbook;
    private Sheet currentSheetInWorkbook;
    private List<String> headerRow = new ArrayList<>();

    public XSSFEventProcessor(StylesTable styles, ReadOnlySharedStringsTable sharedStrings, int chunkSize, File outputDirectory) {
        this.styles = styles;
        this.sharedStrings = sharedStrings;
        this.chunkSize = chunkSize;
        this.outputDirectory = outputDirectory;
        this.headerRow = headerRow;
        this.outputDirectory.mkdirs(); // Create the directory if it doesn't exist
    }

    public void setCurrentSheet(InputStream sheet) {
        this.currentSheet = sheet;
        this.rowCounter = 1;
        this.fileCount = 1;
        this.currentWorkbook = null;
        this.currentSheetInWorkbook = null;
    }

    @Override
    public void startElement(String uri, String localName, String name, org.xml.sax.Attributes attributes) throws SAXException {
        if (name.equals("row")) {
            rowCounter++;
            inRow = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (name.equals("row") && inRow) {
            if (rowCounter % chunkSize == 0) {
                try {
                    inRow = false;
                    currentSheet.close(); // Close the current sheet to release resources
                    fileCount++;
                } catch (IOException e) {
                    throw new SAXException("Error closing current sheet", e);
                }
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // Process row content here
        if (inRow) {
            // Create new workbook and sheet for each chunk
            if (rowCounter % chunkSize == 1) {
                createNewWorkbook();
            }

            // Add row content to current sheet
            if (currentWorkbook != null && currentSheetInWorkbook != null) {
                String rowData = new String(ch, start, length);
                String[] cellValues = rowData.split("\t"); // Assuming tab-delimited data
                Row row = currentSheetInWorkbook.createRow(rowCounter % chunkSize - 1);
                for (int i = 0; i < cellValues.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(cellValues[i]);
                }
            }
        }
    }

    private void createNewWorkbook() {
        currentWorkbook = new XSSFWorkbook();
        currentSheetInWorkbook = currentWorkbook.createSheet("Chunk " + fileCount);

        // Add header row to current sheet
        Row header = currentSheetInWorkbook.createRow(0);
        for (int i = 0; i < headerRow.size(); i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headerRow.get(i));
        }

        // Save current workbook to file
        saveWorkbookToFile();
    }

    private void saveWorkbookToFile() {
        String fileName = "chunk_" + fileCount + ".xlsx";
        File outputFile = new File(outputDirectory, fileName);
        try (OutputStream fileOut = new FileOutputStream(outputFile)) {
            currentWorkbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }
}
