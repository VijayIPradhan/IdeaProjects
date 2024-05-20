package com.excel.excelchunk.controller;

import com.excel.excelchunk.service.ExcelService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Component
public class ExcelInterceptor implements HandlerInterceptor {

    private final ExcelService excelService=new ExcelService();
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        System.out.println("preHandler");
        String error=request.getParameter("error");
            if (request instanceof MultipartHttpServletRequest multipartRequest) {
                int chunkSize = Integer.parseInt(multipartRequest.getParameter("chunkSize"));
                MultipartFile file = multipartRequest.getFile("file");
                System.out.println("chunk size: "+chunkSize);
                assert file != null;
                //excelService.readExcelFile(file, chunkSize);
                //excelService.processExcelChunk(file,chunkSize);
                request.setAttribute("chunkSize", chunkSize);
                request.setAttribute("error", error);
                return true;
            }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Logic to handle the response after the controller has been executed
        System.out.println("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Logic to be executed after the request has been completed
    }
}
