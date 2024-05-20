package com.excel.excelpartition.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/excel")
public class ExcelController {

    @PostMapping("/process")
    public ResponseEntity<List<List<String>>> processExcel(@RequestParam("file") MultipartFile file,
                                                           @RequestParam("chunkSize") int chunkSize) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
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
            List<String> chunk = new ArrayList<>(); // Add header to the first chunk
            chunk.add(header.toString());
            int rowCount = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();

                List<String> rowData = new ArrayList<>();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    rowData.add(cell.getStringCellValue());
                }

                chunk.add(rowData.toString()); // Add row as string to chunk
                rowCount++;
                if (rowCount % chunkSize == 0 || !rowIterator.hasNext()) {
                    result.add(new ArrayList<>(chunk));
                    chunk.clear();
                    chunk.add(header.toString());
                }
            }
            return ResponseEntity.ok().body(result);
        } 
        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

