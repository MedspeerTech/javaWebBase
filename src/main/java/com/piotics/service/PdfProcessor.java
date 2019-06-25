package com.piotics.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.piotics.common.MailManager;
import com.piotics.constants.FileType;
import com.piotics.model.Conversion;
import com.piotics.model.EMail;
import com.piotics.model.FileMeta;
import com.piotics.repository.ConversionMongoRepository;
import com.piotics.repository.FileMetaMongoRepository;

import com.piotics.service.PdfProcessor;

@Component
public class PdfProcessor {

	@Value("${piotics.storage.location}")
	String storageLocationPath;
	
	@Autowired
	ConversionMongoRepository conversionMongoRepository;
	
	@Autowired
	FileMetaMongoRepository fileMetaMongoRepository;
	
	@Autowired
	MailManager mailManager;
	
	private static Logger LOGGER = LogManager.getLogger(PdfProcessor.class);
	
	private static boolean isConversionInProgress = false;
	
	@Async
	public void initiatePdfConversion() {

		while (!isConversionInProgress) {

			List<Conversion> pdfs = conversionMongoRepository.findByFileTypeAndLogNull(FileType.PDF);

			if (!pdfs.isEmpty()) {

				for (Conversion conversion : pdfs) {
					isConversionInProgress = true;

					try {

						String filePath = storageLocationPath + conversion.getSourceLocation() + "original.pdf";
						PDDocument document = PDDocument.load(new File(filePath));
						PDFRenderer pdfRenderer = new PDFRenderer(document);
						int pageCount = document.getNumberOfPages();

						for (int i = 0; i < pageCount; i++) {

							int pageNum = i + 1;

							BufferedImage bim = pdfRenderer.renderImageWithDPI(i, 300, ImageType.RGB);
							FileOutputStream out = new FileOutputStream(
									storageLocationPath + conversion.getSourceLocation() + pageNum + ".jpg");
							javax.imageio.ImageIO.write(bim, "jpg", out);
						}

						conversionMongoRepository.delete(conversion);
						document.close();

					} catch (Exception e) {

						if (e.getMessage() == null) {

							conversion.setLog("something went wrong");
						} else {
							conversion.setLog(e.getMessage());
						}
						conversionMongoRepository.save(conversion);

						EMail eMail = mailManager.composeConversionFailureNotifcationMail("pdf to images, conversionId : ",
								conversion.getId(), conversion.getSourceLocation());
						mailManager.sendEmail(eMail);
					}

				}

				isConversionInProgress = false;
				initiatePdfConversion();

			} else {

				break;
			}

		}

	}

	public void generatePreview(FileMeta fileMeta) {
		String filePath = fileMeta.getPath().substring(0, fileMeta.getPath().lastIndexOf("/") + 1);
		try {
			PDDocument document = PDDocument.load(new File(storageLocationPath + fileMeta.getPath() + "original.pdf"));
			int pagCount = document.getNumberOfPages();

			PDFRenderer pdfRenderer = new PDFRenderer(document);
			BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);

			FileOutputStream out = new FileOutputStream(storageLocationPath + filePath + "file_preview.jpg");
			javax.imageio.ImageIO.write(bim, "jpg", out);

			// updating page count in filemeta
			fileMeta.setLength(pagCount);
			fileMetaMongoRepository.save(fileMeta);

			document.close();

		} catch (IOException e) {
			LOGGER.error(e.getStackTrace());
		}
	}

}
