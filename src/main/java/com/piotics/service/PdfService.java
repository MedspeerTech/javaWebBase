package com.piotics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.piotics.model.FileMeta;

@Component
public class PdfService {

	@Autowired
	PdfProcessor pdfProcessor;
	
	public void initiatePdfConversion() {
		
		pdfProcessor.initiatePdfConversion();
	}

	public void generatePreview(FileMeta fileMeta) {

		pdfProcessor.generatePreview(fileMeta);
	}

}
