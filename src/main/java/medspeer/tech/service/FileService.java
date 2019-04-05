package medspeer.tech.service;



import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.ImageIO;

import medspeer.tech.model.FileData;
import medspeer.tech.repository.FileRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import medspeer.tech.model.FileMeta;

@Service
public class FileService {	
	
	public FileService(FileRepository fileRepository, String storageLocationPath, String profileLocationUrl) {
		super();
		this.fileRepository = fileRepository;
		this.storageLocationPath = storageLocationPath;
		this.profileLocationUrl = profileLocationUrl;
	}

	@Autowired
    FileRepository fileRepository;
	
	@Value("${meds.storage.location}")
	String storageLocationPath;
	
	/*@Value("${meds.storage.temp.location}")
	String tempLocationUrl;*/
	
	@Value("${meds.storage.profile.location}")
    String profileLocationUrl;
	
	 private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(FileService.class);

	public FileData saveFile(MultipartFile file)   {
		//ApplicationUser applicationUser = null;
		FileData fileData=new FileData();
		//FileMeta fileMeta=new FileMeta(applfile.getOriginalFilename(),file.getContentType());
		FileMeta fileMeta=new FileMeta(fileData.getId(),file.getOriginalFilename(),file.getContentType());
		FileMeta updatedData=fileRepository.saveFile(file,fileMeta);
		return new FileData(updatedData.getId());
	}

	public String storeImageInStorageLocation(String imageData) {

		String fileName = null;

		String filePath = null;

		try {

			imageData = imageData.substring(imageData.indexOf(','), imageData.length());
			
			fileName = UUID.randomUUID().toString() + ".png";
			filePath = storageLocationPath + profileLocationUrl + fileName;

			byte[] imgByteArray = Base64.decodeBase64(imageData.getBytes());

			InputStream in = new ByteArrayInputStream(imgByteArray);
			BufferedImage bufferedImage = ImageIO.read(in);

			ImageIO.write(bufferedImage, "png", new File(filePath));

		} catch (Exception ex) {
			logger.error(filePath + "cannot be saved");
			ex.printStackTrace();
		}
		return fileName;
	}
}
