package com.excel.excelchunk.controller;


import com.excel.excelchunk.service.ExcelProcessor;
import com.excel.excelchunk.service.ExcelService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;

@RestController
public class ExcelController {


    private final ExcelService service;
    private final ExcelProcessor processor;

    public ExcelController(ExcelService service, ExcelProcessor processor) {
        this.service = service;
        this.processor = processor;
    }

    @PostMapping("/excel/chunk")
    public ResponseEntity<String> processExcelChunk(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("chunkSize") int chunkSize,
                                                    HttpServletRequest request) throws IOException {
        System.out.println("controller");
        try{
            MultipartHttpServletRequest mr=(MultipartHttpServletRequest) request;
            MultipartFile mFile= mr.getFile("file");
            int mNo=Integer.parseInt(mr.getParameter("chunkSize"));
            assert mFile != null;
            service.readExcelFile(mFile, mNo);
            //service.processExcelChunk(mFile,mNo);
            return new ResponseEntity<>("Success Chunked Excel ", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
