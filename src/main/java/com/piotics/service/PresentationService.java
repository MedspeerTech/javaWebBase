package com.piotics.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.piotics.common.MailManager;
import com.piotics.constants.FileType;
import com.piotics.model.Conversion;
import com.piotics.model.EMail;
import com.piotics.model.FileMeta;
import com.piotics.repository.ConversionMongoRepository;

@Component
public class PresentationService {
	
	@Autowired
	MailManager mailManager;
	
	@Autowired 
	ConversionMongoRepository conversionMongoRepository;
	
	@Autowired
	PresentationProcessor presentationProcessor;

	private static boolean isConversionInProgress = false;

	
	public void initiatePresentationConversion() {

		Conversion con = new Conversion();
		try {
			while (!isConversionInProgress) {
//			List<Conversion> ppts = conversionJpaRepository.findByFileType(FileType.Presentation);
				List<Conversion> ppts = conversionMongoRepository.findByFileTypeAndLogNull(FileType.Presentation);

				if (!ppts.isEmpty()) {

					for (Conversion conversion : ppts) {
						isConversionInProgress = true;
						con = conversion;

						try {

							presentationProcessor.convetPptToImages(conversion);
							conversionMongoRepository.delete(conversion);

						} catch (Exception e) {

							if (e.getMessage() == null) {

								conversion.setLog("something went wrong");
							} else {
								conversion.setLog(e.getLocalizedMessage());
							}
							conversionMongoRepository.save(conversion);

							EMail eMail = mailManager.composeConversionFailureNotifcationMail(
									"PPT to images, conversionId : ", conversion.getId(),
									conversion.getSourceLocation());
							mailManager.sendEmail(eMail);
						}

					}

					isConversionInProgress = false;
					initiatePresentationConversion();
				} else {

					break;
				}

			}
		} catch (Exception e) {
			if (e.getMessage() == null) {

				con.setLog("something went wrong");
			} else {
				con.setLog(e.getLocalizedMessage());
			}
			EMail eMail = mailManager.composeConversionFailureNotifcationMail("PPT to images, conversionId : ", con.getId(),
					con.getSourceLocation());
			mailManager.sendEmail(eMail);
		}

	}


	public void generatePreview(FileMeta fileMeta) {

		presentationProcessor.generatePreviewAndSetTotalSlides(fileMeta);
	}

}
