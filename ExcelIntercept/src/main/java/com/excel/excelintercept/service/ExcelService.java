package com.excel.excelintercept.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileOutputStream;
import java.io.IOException;


@Service
public class ExcelService {

    public void processExcelChunk(MultipartFile file, int chunkSize) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            int n=1000000000+1000000000;
            IOUtils.setByteArrayMaxOverride(n);
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            int totalRows = sheet.getPhysicalNumberOfRows();
            Row headerRow = sheet.getRow(0);
            int currentRow = 1;
            int fileCount = 1;

            while (currentRow < totalRows) {
                int endRow = Math.min(currentRow + chunkSize, totalRows);

                // Use SXSSFWorkbook for better memory management
                try (SXSSFWorkbook chunkWorkbook = new SXSSFWorkbook()) {
                    Sheet chunkSheet = chunkWorkbook.createSheet("Chunk " + fileCount);

                    // Copy header row to chunked workbook
                    Row chunkHeaderRow = chunkSheet.createRow(0);
                    for (int cellNum = 0; cellNum < headerRow.getLastCellNum(); cellNum++) {
                        Cell sourceCell = headerRow.getCell(cellNum);
                        Cell destinationCell = chunkHeaderRow.createCell(cellNum);

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

                    for (int rowNum = currentRow; rowNum < endRow; rowNum++) {
                        Row sourceRow = sheet.getRow(rowNum);
                        Row destinationRow = chunkSheet.createRow(rowNum - currentRow + 1); // Offset by 1 for header row

                        if (sourceRow != null) {
                            for (int cellNum = 0; cellNum < sourceRow.getLastCellNum(); cellNum++) {
                                Cell sourceCell = sourceRow.getCell(cellNum);
                                Cell destinationCell = destinationRow.createCell(cellNum);

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
                    }

                    // Save the chunked Excel file
                    String fileName = "chunk_" + fileCount + ".xlsx";
                    try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                        chunkWorkbook.write(fileOut);
                    }

                    System.out.println("Chunk " + fileCount + " saved as " + fileName);
                }

                fileCount++;
                currentRow = endRow;
            }
        }
    }
}
