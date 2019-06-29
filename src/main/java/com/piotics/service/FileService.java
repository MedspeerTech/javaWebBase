package com.piotics.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.piotics.common.FileContentType;
import com.piotics.constants.FileType;
import com.piotics.exception.FileException;
import com.piotics.exception.FileUploadException;
import com.piotics.exception.ResourceNotFoundException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Conversion;
import com.piotics.model.FileMeta;
import com.piotics.repository.ConversionMongoRepository;
import com.piotics.repository.FileMetaMongoRepository;

@Service
public class FileService {

	@Autowired
	FileMetaMongoRepository fileMetaMongoRepository;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	ConversionMongoRepository conversionMongoRepository;

	@Autowired
	ConversionsService conversionsService;

	@Value("${piotics.storage.location}")
	String storageLocationPath;

	@Value("${piotics.storage.video.location}")
	String videoStorageLocation;

	@Value("${piotics.storage.image.location}")
	String imageStorageLocation;

	@Value("${piotics.storage.presentations.location}")
	String presentationsStorageLocation;

	@Value("${piotics.storage.documents.location}")
	String documentStorageLocation;

	public FileMeta saveFile(ApplicationUser applicationUser, MultipartFile file) {

		FileMeta updatedFileMeta = new FileMeta();
		updatedFileMeta.setOriginalContentType(file.getContentType());

		if (isImageFile(updatedFileMeta)) {

			return saveFileToDisk(applicationUser, file, imageStorageLocation);

		} else if (isVideoFile(updatedFileMeta)) {

			updatedFileMeta = saveFileToDisk(applicationUser, file, videoStorageLocation);
//			conversionsService.convertVideos();

		} else if (isPdfFile(updatedFileMeta)) {

			return saveFileToDisk(applicationUser, file, documentStorageLocation);

		} else if (isDoc(updatedFileMeta)) {

			return saveFileToDisk(applicationUser, file, documentStorageLocation);

		} else if (isPresentation(updatedFileMeta)) {

			return saveFileToDisk(applicationUser, file, presentationsStorageLocation);
		}
		return updatedFileMeta;
	}

	public boolean isImageFile(FileMeta file) {
		String contentType = file.getOriginalContentType();
		return contentType.equals(FileContentType.jpegImage) || contentType.equals(FileContentType.pngImage)
				|| contentType.equals(FileContentType.gifImage);
	}

	private boolean isVideoFile(FileMeta fileMeta) {

		String contentType = fileMeta.getOriginalContentType();
		return contentType.equals(FileContentType.mp4Video) || contentType.equals(FileContentType.webMVideo)
				|| contentType.equals(FileContentType.flvVideo) || contentType.equals(FileContentType.aviVideo)
				|| contentType.equals(FileContentType.gp3Video) || contentType.equals(FileContentType.wmvVideo)
				|| contentType.equals(FileContentType.mpegVideo) || contentType.equals(FileContentType.msVideo)
				|| contentType.equals(FileContentType.quicktimeVideo) || contentType.equals(FileContentType.mkvVideo)
				|| contentType.equals(FileContentType.oggVideo);

	}

	private boolean isPdfFile(FileMeta file) {
		String contentType = file.getOriginalContentType();
		return contentType.equals(FileContentType.pdfDocument);
	}

	private boolean isDoc(FileMeta fileMeta) {
		String contentType = fileMeta.getOriginalContentType();
		return contentType.equals(FileContentType.wordocument) || contentType.equals(FileContentType.textDocument)
				|| contentType.equals(FileContentType.msWord);
	}

	private boolean isPresentation(FileMeta fileMeta) {

		String contentType = fileMeta.getOriginalContentType();
		return contentType.equals(FileContentType.pptDocument) || contentType.equals(FileContentType.pptxDocument);
	}

	public FileMeta saveFileToDisk(ApplicationUser applicationUser, MultipartFile file, String storageLocation) {
		try {

			String fileNameWithExt = file.getOriginalFilename();

			String fileName = getRandomUUID();
			String fileExtention = fileNameWithExt.substring(fileNameWithExt.lastIndexOf("."));
			String folderRelativePath = storageLocation + fileName + "/";

			String folderFullPath = storageLocationPath + storageLocation + fileName;
			String fileFullPath = folderFullPath + "/original" + fileExtention;

			(new File(folderFullPath)).mkdir();
			byte[] bytes = file.getBytes();

			Path path = Paths.get(fileFullPath);

			Files.write(path, bytes);
//			return saveFilemeta(applicationUser, fileNameWithExt, file, folderRelativePath);
			return saveFilemeta(applicationUser, "original" + fileExtention, file, folderRelativePath);

		} catch (IOException e) {
			e.printStackTrace();
			throw new FileException("Unable to save File");
		}

	}

	private String getRandomUUID() {

		final String uuid = UUID.randomUUID().toString().replace("-", "");
		return uuid;

	}

	public FileMeta saveFilemeta(ApplicationUser applicationUser, String fileName, MultipartFile file, String path) {

		FileMeta fileMeta = new FileMeta(applicationUser.getId(), fileName, file.getContentType());
		fileMeta.setPath(path);
		fileMeta.setSize((double) file.getSize());
		fileMeta.setOriginalContentType(file.getContentType());

		isFormatSupported(fileMeta);

		FileMeta updatedFileMeta = fileMetaMongoRepository.save(fileMeta);

		saveFileToConversion(updatedFileMeta);

		if (!isImageFile(updatedFileMeta)) {

			conversionsService.genratePreview(updatedFileMeta);
		}

		return updatedFileMeta;
	}

	public void isFormatSupported(FileMeta fileMeta) {

		FileType fileType;
		if (isVideoFile(fileMeta)) {

			fileType = FileType.Video;
		} else if (isImageFile(fileMeta)) {

			fileType = FileType.Image;
		} else if (isPdfFile(fileMeta)) {

			fileType = FileType.PDF;
		} else if (isDoc(fileMeta)) {

			fileType = FileType.Document;

		} else if (isPresentation(fileMeta)) {

			fileType = FileType.Presentation;
		} else {

			throw new FileUploadException(
					messageSource.getMessage("file.format.not.supported", null, LocaleContextHolder.getLocale()));
		}

		fileMeta.setType(fileType);

	}

	private void saveFileToConversion(FileMeta updatedFileMeta) {

		final ZoneId systemDefault = ZoneId.systemDefault();
		Conversion conversion = new Conversion(updatedFileMeta.getCreationDate(), updatedFileMeta.getType(),
				updatedFileMeta.getPath());

		conversionMongoRepository.save(conversion);

	}

	public Optional<FileMeta> getFileMeta(String id) {
		Optional<FileMeta> fileMeta = fileMetaMongoRepository.findById(id);
		return fileMeta;
	}
	
	public Path getVideoPath(String fileId, HttpServletRequest httprequest, HttpServletResponse httpresponse) {
		Optional<FileMeta> fileMeta = this.getFileMeta(fileId);
		String folderRelativePath = fileMeta.get().getPath();
		String folderFullPath = storageLocationPath + folderRelativePath;
		String contentType = fileMeta.get().getOriginalContentType();

		String fileName = "/file.mp4";
		if (contentType.equals("video/mp4")) {
			fileName = "/file.mp4";
		} else if (contentType.equals("video/webm")) {
			fileName = "/file.webm";
		}

		String fileFullPath = folderFullPath + fileName;
		Path path = Paths.get(fileFullPath);

		return path;
	}

	public byte[] getImage(Integer fileId) {
		Optional<FileMeta> ff = fileMetaMongoRepository.findById(fileId);
		if (ff.isPresent()) {
			FileMeta fileMeta = ff.get();
			String folderFullPath = storageLocationPath + fileMeta.getPath();
			String fileExt = ".png";

			if (fileMeta.getOriginalContentType().equals("image/png")) {
				fileExt = ".png";
			} else if (fileMeta.getOriginalContentType().equals("image/jpeg")) {
				fileExt = ".jpeg";
			} else if (fileMeta.getOriginalContentType().equals("image/jpg")) {
				fileExt = ".jpg";
			}

//			String fileFullPath = folderFullPath + "original" + fileExt;

			File file = null;
			File dir = new File(folderFullPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				if (child.getName().contains("original"))
					file = child;
			}

//			File file = new File(fileFullPath);
			try {
				byte[] fileContent = Files.readAllBytes(file.toPath());
				return fileContent;
			} catch (IOException e) {
				throw new ResourceNotFoundException();
			}
		}
		throw new ResourceNotFoundException();
	}

	public byte[] getPreview(String fileId) {

		Optional<FileMeta> ff = fileMetaMongoRepository.findById(fileId);
		if (ff.isPresent()) {
			FileMeta fileMeta = ff.get();
			String path = fileMeta.getPath().substring(0, fileMeta.getPath().lastIndexOf("/") + 1) + "file_preview.jpg";
			File file = new File(storageLocationPath + path);
			try {
				byte[] fileContent = Files.readAllBytes(file.toPath());
				return fileContent;
			} catch (IOException e) {
				throw new ResourceNotFoundException();
			}
		}
		throw new ResourceNotFoundException();
	}

	public byte[] getSlide(String fileId, int slideNo) {
		Optional<FileMeta> ff = fileMetaMongoRepository.findById(fileId);
		if (ff.isPresent()) {
			FileMeta fileMeta = ff.get();
			String path = storageLocationPath + fileMeta.getPath().substring(0, fileMeta.getPath().lastIndexOf("/") + 1)
					+ slideNo + ".jpg";
			File file = new File(path);
			try {
				byte[] fileContent = Files.readAllBytes(file.toPath());
				return fileContent;
			} catch (IOException e) {
				throw new ResourceNotFoundException();
			}
		}
		throw new ResourceNotFoundException();
	}

	public byte[] getFile(String fileId) {
		Optional<FileMeta> ff = fileMetaMongoRepository.findById(fileId);
		if (ff.isPresent()) {
			FileMeta fileMeta = ff.get();
			String relativePath = fileMeta.getPath();

			String fileExt = ".pdf";
			if (fileMeta.getOriginalContentType().equals("application/pdf")) {
				fileExt = ".pdf";
			}

			String fileName = "original" + fileExt;
			String path = storageLocationPath + relativePath + fileName;
			File file = new File(path);
			try {
				byte[] fileContent = Files.readAllBytes(file.toPath());
				return fileContent;
			} catch (IOException e) {
				throw new ResourceNotFoundException();
			}
		}
		throw new ResourceNotFoundException();
	}

	

}
