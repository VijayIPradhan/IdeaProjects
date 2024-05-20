package com.excel.excelintercept.controller;

import com.excel.excelintercept.service.ExcelService;
import com.excel.excelintercept.service.LargeExcelProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;
    @Autowired
    private LargeExcelProcessor excelProcessor;

    @PostMapping("/chunk")
    public ResponseEntity<String> processExcelChunk(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("chunkSize") int chunkSize) {
        try {
            //excelService.processExcelChunk(file, chunkSize);
            excelProcessor.processLargeExcel(file,chunkSize);
            return ResponseEntity.ok("Excel chunking process completed successfully.");
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error processing Excel file: " + e);
        }
    }
}
