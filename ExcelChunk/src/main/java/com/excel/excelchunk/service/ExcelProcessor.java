package com.excel.excelchunk.service;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Service
public class ExcelProcessor {
    private static final Logger logger = Logger.getLogger(ExcelProcessor.class.getName());

    int numProcessors = Runtime.getRuntime().availableProcessors();
    ExecutorService executorService = Executors.newFixedThreadPool(numProcessors);

    static Integer fileCount = 0;

    public void readExcelFile(MultipartFile file, int chunkSize) throws Exception {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);
        int endRow = sheet.getLastRowNum();
        Row headerRow = sheet.getRow(0);

        long startTime = System.currentTimeMillis();
        List<Future<Void>> futures = new ArrayList<>();

        int startRow = 1; // Start from the second row since first row is header
        while (startRow <= endRow) {
            int endTransactionRow = findEndOfTransaction(sheet, startRow);
            final int finalStartRow = startRow;
            final int finalEndRow = endTransactionRow;

            futures.add((Future<Void>) executorService.submit(() -> {
                try {
                    processTransaction(sheet, finalStartRow, finalEndRow, headerRow);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));

            startRow = endTransactionRow + 1; // Move to the row after the current transaction
        }

        for (Future<Void> future : futures) {
            future.get();
        }

        long endTime = System.currentTimeMillis();
        double executionTimeSeconds = (endTime - startTime) / 1000.0;
        logger.info("Excel reading time: " + executionTimeSeconds);

        workbook.close();
    }

    private int findEndOfTransaction(Sheet sheet, int startRow) {
        int currentRow = startRow;
        int lastRow = sheet.getLastRowNum();
        while (currentRow <= lastRow) {
            Row row = sheet.getRow(currentRow);
            Cell firstCell = row.getCell(0); // Assuming first cell of each row is the trigger

            if (firstCell == null || firstCell.getCellType() == CellType.BLANK) {
                return currentRow - 1; // Return the row before the empty row
            }

            currentRow++;
        }
        return lastRow; // Return the last row if no empty row is found
    }


    private void processTransaction(Sheet sheet, int startRow, int endRow, Row headerRow) throws IOException {
        synchronized (fileCount) {
            fileCount++;
        }

        String fileName = "chunk_" + fileCount + ".xlsx";
        try (SXSSFWorkbook chunkWorkbook = new SXSSFWorkbook(); FileOutputStream fileOut = new FileOutputStream(fileName)) {
            Sheet chunkSheet = chunkWorkbook.createSheet("Chunk" + fileCount);

            // Create header row in chunk sheet
            Row chunkHeaderRow = chunkSheet.createRow(0);
            for (int cellNum = 0; cellNum < headerRow.getLastCellNum(); cellNum++) {
                Cell sourceCell = headerRow.getCell(cellNum);
                Cell destinationCell = chunkHeaderRow.createCell(cellNum);
                setCell(sourceCell, destinationCell);
            }

            // Copy rows to chunk sheet
            for (int i = startRow; i <= endRow; i++) {
                Row sourceRow = sheet.getRow(i);
                Row destinationRow = chunkSheet.createRow(i - startRow + 1); // Adjust index for destination row
                for (int cellNum = 0; cellNum < sourceRow.getLastCellNum(); cellNum++) {
                    Cell sourceCell = sourceRow.getCell(cellNum);
                    Cell destinationCell = destinationRow.createCell(cellNum);
                    setCell(sourceCell, destinationCell);
                }
            }

            chunkWorkbook.write(fileOut);
        }
    }

    private void setCell(Cell sourceCell, Cell destinationCell) {
        if (sourceCell != null) {
            switch (sourceCell.getCellType()) {
                case STRING:
                    destinationCell.setCellValue(sourceCell.getRichStringCellValue().getString());
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(sourceCell)) {
                        destinationCell.setCellValue(sourceCell.getDateCellValue());
                    } else {
                        destinationCell.setCellValue(sourceCell.getNumericCellValue());
                    }
                    break;
                case BOOLEAN:
                    destinationCell.setCellValue(sourceCell.getBooleanCellValue());
                    break;
                case FORMULA:
                    destinationCell.setCellFormula(sourceCell.getCellFormula());
                    break;
                default:
                    destinationCell.setCellValue("");
            }
        }
    }
}
