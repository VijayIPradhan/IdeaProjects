package com.excel.excelproj;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ExcelInterceptApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExcelInterceptApplication.class, args);
    }

    @RestController
    public static class ExcelController {

        @PostMapping("/processExcel")
        public ResponseEntity<Resource> processExcel(@RequestParam("file") MultipartFile file, @RequestParam("chunkSize") int chunkSize, HttpServletRequest request) {
            List<byte[]> excelChunks = chunkExcel(file, chunkSize);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "data.xlsx");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for (byte[] chunk : excelChunks) {
                try {
                    outputStream.write(chunk);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(outputStream.size())
                    .body(resource);
        }
    }

    public static class ExcelInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            MultipartFile file = ((CommonsMultipartResolver) request.getAttribute("org.springframework.web.multipart.CommonsMultipartResolver.MultipartHttpServletRequest.FILE")).getFile("file");
            int chunkSize = Integer.parseInt(request.getParameter("chunkSize"));

            List<byte[]> excelChunks = chunkExcel(file, chunkSize);

            request.setAttribute("excelChunks", excelChunks);

            return true;
        }

        private List<byte[]> chunkExcel(MultipartFile file, int chunkSize) throws IOException {
            List<byte[]> chunks = new ArrayList<>();

            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum() + 1;
            int currentRow = 0;

            while (currentRow < rowCount) {
                Workbook chunkWorkbook = new XSSFWorkbook();
                Sheet chunkSheet = chunkWorkbook.createSheet(sheet.getSheetName());

                int rowsToCopy = Math.min(chunkSize, rowCount - currentRow);
                for (int i = 0; i < rowsToCopy; i++) {
                    Row sourceRow = sheet.getRow(currentRow++);
                    Row targetRow = chunkSheet.createRow(i);
                    for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
                        Cell sourceCell = sourceRow.getCell(j);
                        Cell targetCell = targetRow.createCell(j);
                        if (sourceCell != null) {
                            switch (sourceCell.getCellType()) {
                                case STRING:
                                    targetCell.setCellValue(sourceCell.getStringCellValue());
                                    break;
                                case NUMERIC:
                                    targetCell.setCellValue(sourceCell.getNumericCellValue());
                                    break;
                                // Handle other cell types as needed
                            }
                        }
                    }
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                chunkWorkbook.write(outputStream);
                chunks.add(outputStream.toByteArray());
            }

            return chunks;
        }
    }
}
