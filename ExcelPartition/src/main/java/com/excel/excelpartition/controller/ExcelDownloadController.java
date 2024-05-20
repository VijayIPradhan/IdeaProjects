package com.excel.excelpartition.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/excel")
public class ExcelDownloadController {

    @PostMapping("/download")
    public ResponseEntity<String> processExcel(@RequestParam("file") MultipartFile file,
                                               @RequestParam("chunkSize") int chunkSize) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            List<String> header = new ArrayList<>(); // Store header row
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                Iterator<Cell> headerCellIterator = headerRow.cellIterator();
                while (headerCellIterator.hasNext()) {
                    Cell cell = headerCellIterator.next();
                    header.add(cell.getStringCellValue());
                }
            }
            List<List<String>> result = new ArrayList<>();
            List<String> chunk = new ArrayList<>(header); // Add header to the first chunk
            int rowCount = 0;
            int fileCount = 1;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();

                List<String> rowData = new ArrayList<>();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    rowData.add(cell.getStringCellValue());
                }

                chunk.addAll(rowData);
                rowCount++;
                if (rowCount % chunkSize == 0 || !rowIterator.hasNext()) {
                    result.add(new ArrayList<>(chunk));
                    String fileName = "chunk_" + fileCount + ".xlsx";
                    saveExcelFile(chunk, header, fileName);
                    chunk.clear();
                    chunk.addAll(header); // Add header to the next chunk
                    fileCount++;
                }
            }

            return ResponseEntity.ok()
                    .body("Excel files saved successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing Excel file");
        }
    }

    private void saveExcelFile(List<String> data, List<String> header, String fileName) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Processed Data");
            int rowNum = 0;

            // Add header row
            Row headerRow = sheet.createRow(rowNum++);
            int headerCellNum = 0;
            for (String columnHeader : header) {
                Cell headerCell = headerRow.createCell(headerCellNum++);
                headerCell.setCellValue(columnHeader);
            }

            // Add data rows
            for (String rowData : data) {
                Row currentRow = sheet.createRow(rowNum++);
                int cellNum = 0;
                for (String cellData : rowData.split(",")) {
                    Cell cell = currentRow.createCell(cellNum++);
                    cell.setCellValue(cellData);
                }
            }

            Path resourcesDir = getResourceDirectory();
            String filePath = resourcesDir.resolve(fileName).toString();

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    private Path getResourceDirectory() throws IOException {
        Resource resource = new ClassPathResource("");
        File directory = resource.getFile();
        return directory.toPath();
    }
}
