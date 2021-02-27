package com.example.pdfdemo.service;

import com.example.pdfdemo.util.PdfTemplateDataProperties;

import java.util.List;

/**
 * @author TANG
 * @date 2021-02-21
 */
public interface IMerService {
    List<String> activePdfUpload(String merId);

    List<String> entryPdfUpload(String merId);

    List<String> entryPdfUpload2(String merId, PdfTemplateDataProperties pdfTemplateDataProperties);

    void threadPoolTest();
}
