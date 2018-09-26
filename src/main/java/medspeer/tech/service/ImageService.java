package medspeer.tech.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import medspeer.tech.exception.FileException;
import medspeer.tech.model.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImageService {
	
	/*@Value("${meds.storage.temp.location}")
	String tempLocationUrl;*/

	@Value("${meds.storage.location}")
	String storageLocationPath;

	@Value("${meds.storage.profile.location}")
	String profileLocationUrl;

	private static final Logger LOGGER = LogManager.getLogger(ImageService.class);

	public void createProfileImage(Attachment attachment) {

		try {

			// String
			// tempLocationUrl="/home/vignesh/Downloads/Software/eclipse/Workspace/storageweb/medsStorage/temp/3c94a46e-5736-4ab9-a623-99c09580567f.png";

			File sourceImageFile = new File(storageLocationPath + profileLocationUrl + attachment.attachmentName);
			BufferedImage img = ImageIO.read(sourceImageFile);
			BufferedImage p320Img = Scalr.resize(img, Scalr.Mode.AUTOMATIC, 320, 320);
			File p320ImgDest = new File(
					storageLocationPath + profileLocationUrl + "/pix_md/" + attachment.getAttachmentName() + ".jpg");
			ImageIO.write(p320Img, "jpg", p320ImgDest);
			sourceImageFile.delete();

		} catch (IOException e) {
			LOGGER.error(attachment.getAttachmentName() + "--cannot find file");
			e.printStackTrace();
			throw new FileException("Unable to read or write file");
		}

	}
	
	

}
