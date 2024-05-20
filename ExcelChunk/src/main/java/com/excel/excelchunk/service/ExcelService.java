package com.excel.excelchunk.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
public class ExcelService {
    private static final Logger logger = Logger.getLogger(ExcelService.class.getName());
    Integer fileCount=0;
    public void readExcelFile(MultipartFile file, int chunkSize) throws Exception {

        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);

        int numProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numProcessors);
        List<Future<FileOutputStream>>futures=new ArrayList<>();

        int endRow = sheet.getLastRowNum();
        int totalTransactions=0;
        int currentRowNumber=-1;
        int chunkStart=0;
        int chunkEnd;
        Row headerRow = sheet.getRow(0);
        long startTime = System.currentTimeMillis();
        for (int i=0;i<=endRow;i++) {
            Row row= sheet.getRow(i);
            if (i == endRow && chunkStart < endRow) {
                int finalChunkStart = chunkStart;

                futures.add((Future<FileOutputStream>) executorService.submit(() -> {
                    try {
                        processRows(sheet, finalChunkStart, endRow,headerRow);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }))
                ;
            }
            currentRowNumber++;
            if(currentRowNumber==0){
                continue;
            }
            if(row.getRowNum()==1){
                chunkStart=row.getRowNum();
                continue;
            }
            Cell firstCell = row.getCell(1);
            if (firstCell != null && firstCell.getCellType() != CellType.BLANK) {
                totalTransactions++;
                chunkEnd =row.getRowNum()-1;

                if(totalTransactions % chunkSize==0 && totalTransactions!=0){
                    int finalChunkStart = chunkStart;
                    int finalChunkEnd = chunkEnd;
                    futures.add((Future<FileOutputStream>) executorService.submit(() -> {
                        try {
                            processRows(sheet, finalChunkStart, finalChunkEnd,headerRow);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }));
                    chunkStart=row.getRowNum()-1;
                }
            }
        }
        for(Future<FileOutputStream> future:futures){
            future.get();
        }
        long endTime = System.currentTimeMillis();
        double executionTimeSeconds = (endTime - startTime) / 1000.0;
        logger.info("Excel reading time: "+ executionTimeSeconds);
        executorService.shutdown();
        workbook.close();
    }

    private FileOutputStream processRows(Sheet sheet, int finalChunkStart, int endRow, Row headerRow) throws IOException {
        System.out.println(fileCount+" -0");
        fileCount++;
        String fileName = "chunk_" + fileCount + ".xlsx";
        try (SXSSFWorkbook chunkWorkbook = new SXSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(fileName)) {
            Sheet chunkSheet = chunkWorkbook.createSheet("Chunk" + fileCount);
            System.out.println(fileCount+" -1");
            // Create header row in chunk sheet
            Row chunkHeaderRow = chunkSheet.createRow(0);
            for (int cellNum = 0; cellNum < headerRow.getLastCellNum(); cellNum++) {
                Cell sourceCell = headerRow.getCell(cellNum);
                Cell destinationCell = chunkHeaderRow.createCell(cellNum);
                setRow(sourceCell, destinationCell);
            }
            System.out.println(fileCount+" -2");
            // Copy rows to chunk sheet
            for (int i = finalChunkStart + 1; i <= endRow; i++) {

                if(fileCount==1)
                    continue;
                Row sourceRow = sheet.getRow(i);
                Row destinationRow = chunkSheet.createRow(i - finalChunkStart); // Adjust index for destination row
                for (int cellNum = 0; cellNum < sourceRow.getLastCellNum(); cellNum++) {
                    Cell sourceCell = sourceRow.getCell(cellNum);
                    Cell destinationCell = destinationRow.createCell(cellNum);
                    setRow(sourceCell, destinationCell);
                }
            }
            chunkWorkbook.write(fileOut);
            System.out.println(fileName);
            return fileOut;

        }
    }


    private void setRow(Cell sourceCell, Cell destinationCell) {
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


    public void processExcelChunk(MultipartFile file, int chunkSize) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            int n = 2146500000; // Adjust the value as needed
            IOUtils.setByteArrayMaxOverride(n);
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
            int lastRowNum = sheet.getLastRowNum();
            System.out.println("last:"+lastRowNum);
            int totalRows = sheet.getPhysicalNumberOfRows();
            System.out.println(totalRows);
            Row headerRow = sheet.getRow(0);
            int currentRow = 1;
            int fileCount = 1;

            while (currentRow < totalRows) {
                int endRow = Math.min(currentRow + chunkSize, totalRows);
                System.out.println("endRow:"+endRow);

                // Use SXSSFWorkbook for better memory management
                try (SXSSFWorkbook chunkWorkbook = new SXSSFWorkbook()) {
                    Sheet chunkSheet = chunkWorkbook.createSheet("Chunk " + fileCount);

                    // Copy header row to chunked workbook
                    Row chunkHeaderRow = chunkSheet.createRow(0);
                    for (int cellNum = 0; cellNum < headerRow.getLastCellNum(); cellNum++) {
                        Cell sourceCell = headerRow.getCell(cellNum);
                        Cell destinationCell = chunkHeaderRow.createCell(cellNum);

                        setRow(sourceCell, destinationCell);
                    }

                    for (int rowNum = currentRow; rowNum < endRow; rowNum++) {
                        Row sourceRow = sheet.getRow(rowNum);
                        Row destinationRow = chunkSheet.createRow(rowNum - currentRow + 1); // Offset by 1 for header row

                        if (sourceRow != null) {
                            for (int cellNum = 0; cellNum < sourceRow.getLastCellNum(); cellNum++) {
                                Cell sourceCell = sourceRow.getCell(cellNum);
                                Cell destinationCell = destinationRow.createCell(cellNum);

                                setRow(sourceCell, destinationCell);
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
