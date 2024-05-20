package com.excel.excelintercept.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Component
public class LargeExcelProcessor {

    public void processLargeExcel(MultipartFile excelFile, int chunkSize) throws IOException, SAXException {
        try (InputStream inputStream = excelFile.getInputStream()) {
            int n=1000000000+1000000000;
            IOUtils.setByteArrayMaxOverride(n);
            OPCPackage opcPackage = OPCPackage.open(inputStream);
            XSSFReader reader = new XSSFReader(opcPackage);
            StylesTable styles = reader.getStylesTable();
            ReadOnlySharedStringsTable sharedStrings = new ReadOnlySharedStringsTable(opcPackage);
            XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            XSSFEventProcessor handler = new XSSFEventProcessor(styles, sharedStrings, chunkSize, new File("C:\\Users\\Allapu.Srikanth\\IdeaProjects\\ExcelIntercept\\target"));
            parser.setContentHandler(handler);

            Iterator<InputStream> sheets = reader.getSheetsData();
            while (sheets.hasNext()) {
                try (InputStream sheet = sheets.next()) {
                    handler.setCurrentSheet(sheet);
                    parser.parse(new InputSource(sheet));
                }
            }
        } catch (OpenXML4JException e) {
            throw new RuntimeException(e);
        }
    }
}
