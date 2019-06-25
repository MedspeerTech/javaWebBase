package com.piotics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.piotics.model.FileMeta;

@Component
public class WordService {
	
	@Autowired
	WordProcessor wordProcessor;
	
	@Autowired
	PdfProcessor pdfProcessor;

	public void generatePreview(FileMeta fileMeta) {

		boolean isConverted = wordProcessor.convertWordToPDF(fileMeta);

		if (isConverted) {

			pdfProcessor.initiatePdfConversion();
			pdfProcessor.generatePreview(fileMeta);
		}

	}

}
