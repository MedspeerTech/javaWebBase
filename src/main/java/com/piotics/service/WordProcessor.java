package com.piotics.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.piotics.common.FileContentType;
import com.piotics.common.MailManager;
import com.piotics.common.TimeManager;
import com.piotics.constants.FileType;
import com.piotics.model.Conversion;
import com.piotics.model.EMail;
import com.piotics.model.FileMeta;
import com.piotics.repository.ConversionMongoRepository;
import com.piotics.repository.FileMetaMongoRepository;

@Component
public class WordProcessor {

	@Value("${piotics.storage.location}")
	String storageLocationPath;

	@Autowired
	TimeManager timeManager;

	@Autowired
	ConversionMongoRepository conversionMongoRepository;

	@Autowired
	FileMetaMongoRepository fileMetaMongoRepository;

	@Autowired
	MailManager mailManager;

	public boolean convertWordToPDF(FileMeta fileMeta) {

		String s = null;
		FileOutputStream fileForPdf = null;

		int pageCount = 0;

		try {

			String fileName = storageLocationPath + fileMeta.getPath() + fileMeta.getName();
			if (isDoc(fileMeta)) {
				HWPFDocument doc = new HWPFDocument(new FileInputStream(fileName));
				WordExtractor we = new WordExtractor(doc);
				s = we.getText();

				pageCount = doc.getSummaryInformation().getPageCount();

				fileForPdf = new FileOutputStream(new File(storageLocationPath + fileMeta.getPath() + "original.pdf"));
				we.close();

			} else if (isDocx(fileMeta)) {

				XWPFDocument docx = new XWPFDocument(new FileInputStream(fileName));

				XWPFWordExtractor we = new XWPFWordExtractor(docx);
				s = we.getText();

				pageCount = docx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();

				fileForPdf = new FileOutputStream(new File(storageLocationPath + fileMeta.getPath() + "original.pdf"));
				we.close();

			}

			Document document = new Document();
			PdfWriter.getInstance(document, fileForPdf);

			document.open();

			document.add(new Paragraph(s));

			document.close();
			fileForPdf.close();

			fileMeta.setLength(pageCount);
			fileMeta = fileMetaMongoRepository.save(fileMeta);

			this.saveToConversion(fileMeta);

			return true;

		} catch (Exception e) {
			
			e.printStackTrace();
			EMail eMail = mailManager.composeConversionFailureNotifcationMail("word to pdf, fileMetaId : ", fileMeta.getId(),
					fileMeta.getPath());
			mailManager.sendEmail(eMail);
			
			return false;
		}

	}

	private boolean isDoc(FileMeta fileMeta) {

		if (fileMeta.getOriginalContentType().equals(FileContentType.msWord)) {

			return true;
		} else {

			return false;
		}
	}

	private boolean isDocx(FileMeta fileMeta) {

		if (fileMeta.getOriginalContentType().equals(FileContentType.wordocument)) {

			return true;
		} else {

			return false;
		}
	}

	private void saveToConversion(FileMeta fileMeta) {

		Date now = Date.from(timeManager.getCurrentTimestamp().toInstant());

		Conversion conversion = new Conversion(now, fileMeta.getType(), fileMeta.getPath());
		conversion.setFileType(FileType.PDF);
		conversion.setFileMeta(fileMeta);

		conversionMongoRepository.save(conversion);
	}

}
