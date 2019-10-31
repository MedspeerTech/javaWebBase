package com.piotics.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

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
import com.piotics.model.Session;
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

	@Value("${piotics.storage.profile.location}")
	String userProfileImageStorageLocation;

	@Value("#{'${piotics.file.types.suppoted}'.split(',')}")
	private String[] fileTypesSupported;

	public FileMeta saveFile(Session session, MultipartFile file) throws IOException {

		List<String> supportedFormats = new ArrayList<>(Arrays.asList(fileTypesSupported));

		if (!supportedFormats.contains("all") && !supportedFormats.contains(file.getContentType()))
			throw new FileException("unsuppoted file format");

		FileMeta updatedFileMeta = new FileMeta();
		updatedFileMeta.setOriginalContentType(file.getContentType());

		if (isImageFile(updatedFileMeta)) {

			return saveFileToDisk(session, file, imageStorageLocation);

		} else if (isVideoFile(updatedFileMeta)) {

			updatedFileMeta = saveFileToDisk(session, file, videoStorageLocation);

		} else if (isPdfFile(updatedFileMeta)) {

			return saveFileToDisk(session, file, documentStorageLocation);

		} else if (isDoc(updatedFileMeta)) {

			return saveFileToDisk(session, file, documentStorageLocation);

		} else if (isPresentation(updatedFileMeta)) {

			return saveFileToDisk(session, file, presentationsStorageLocation);
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

	public FileMeta saveFileToDisk(Session session, MultipartFile file, String storageLocation)
			throws IOException {

		int year = getCurrentYear();
		String month = getCurrentMonth();

		try {

			return saveToDisc(session, file, storageLocation);

		} catch (NoSuchFileException e) {

			if (!hasFolder(storageLocationPath + year))
				(new File(storageLocationPath + year)).mkdir();

			if (!hasFolder(storageLocationPath + year + File.separator + month)) {
				(new File(storageLocationPath + year + File.separator + month)).mkdir();
				generateFolderSturctureInsideMonth(storageLocationPath + year + File.separator + month);
			}

			return saveToDisc(session, file, storageLocation);
		}

	}

	private FileMeta saveToDisc(Session session, MultipartFile file, String storageLocation)
			throws IOException {

		int year = getCurrentYear();
		String month = getCurrentMonth();

		try {

			String fileNameWithExt = file.getOriginalFilename();

			String fileName = getRandomUUID();
			String fileExtention = fileNameWithExt.substring(fileNameWithExt.lastIndexOf('.'));

			String folderRelativePath = year + File.separator + month + File.separator + storageLocation + fileName
					+ File.separator;
			String folderFullPath = storageLocationPath + folderRelativePath;
			String fileFullPath = folderFullPath + "/original" + fileExtention;

			(new File(folderFullPath)).mkdir();
			byte[] bytes = file.getBytes();

			Path path = Paths.get(fileFullPath);

			Files.write(path, bytes);

			return saveFilemeta(session, "original" + fileExtention, file, folderRelativePath);
		} catch (NoSuchFileException e) {

			throw new NoSuchFileException(e.getMessage());
		}
	}

	private void generateFolderSturctureInsideMonth(String path) {

		(new File(path + File.separator + imageStorageLocation)).mkdir();
		(new File(path + File.separator + videoStorageLocation)).mkdir();
		(new File(path + File.separator + presentationsStorageLocation)).mkdir();
		(new File(path + File.separator + documentStorageLocation)).mkdir();
		(new File(path + File.separator + userProfileImageStorageLocation)).mkdir();
	}

	private boolean hasFolder(String path) {
		File file = new File(path);
		return (file.exists() && file.isDirectory());
	}

	private String getRandomUUID() {

		return UUID.randomUUID().toString().replace("-", "");
	}

	public FileMeta saveFilemeta(Session session, String fileName, MultipartFile file, String path) {

		FileMeta fileMeta = new FileMeta(session.getId(), fileName, file.getContentType());
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

			fileType = FileType.VIDEO;
		} else if (isImageFile(fileMeta)) {

			fileType = FileType.IMAGE;
		} else if (isPdfFile(fileMeta)) {

			fileType = FileType.PDF;
		} else if (isDoc(fileMeta)) {

			fileType = FileType.DOCUMENT;

		} else if (isPresentation(fileMeta)) {

			fileType = FileType.PRESENTATION;
		} else {

			throw new FileUploadException(
					messageSource.getMessage("file.format.not.supported", null, LocaleContextHolder.getLocale()));
		}

		fileMeta.setType(fileType);

	}

	private void saveFileToConversion(FileMeta updatedFileMeta) {

		Conversion conversion = new Conversion(updatedFileMeta.getCreationDate(), updatedFileMeta.getType(),
				updatedFileMeta.getPath());
		conversionMongoRepository.save(conversion);
	}

	public FileMeta getFileMeta(String id) {
		FileMeta fileMeta = new FileMeta();
		Optional<FileMeta> fileMetaOptional = fileMetaMongoRepository.findById(id);
		if (fileMetaOptional.isPresent())
			fileMeta = fileMetaOptional.get();
		return fileMeta;
	}

	public Path getVideoPath(String fileId) {
		FileMeta fileMeta = this.getFileMeta(fileId);
		String folderRelativePath = fileMeta.getPath();
		String folderFullPath = storageLocationPath + folderRelativePath;
		String contentType = fileMeta.getOriginalContentType();

		String fileName = File.separator + "file.mp4";
		if (contentType.equals("video/mp4")) {
			fileName = File.separator + "file.mp4";
		} else if (contentType.equals("video/webm")) {
			fileName = File.separator + "file.webm";
		}
		String fileFullPath = folderFullPath + fileName;
		return Paths.get(fileFullPath);
	}

	public byte[] getImage(Integer fileId) {
		Optional<FileMeta> ff = fileMetaMongoRepository.findById(fileId);
		if (ff.isPresent()) {
			FileMeta fileMeta = ff.get();
			String folderFullPath = storageLocationPath + fileMeta.getPath();
			File file = null;
			File dir = new File(folderFullPath);
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				if (child.getName().contains("original"))
					file = child;
			}

			try {
				if (file!=null)
					return Files.readAllBytes(file.toPath());
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
			String path = fileMeta.getPath().substring(0, fileMeta.getPath().lastIndexOf('/') + 1) + "file_preview.jpg";
			File file = new File(storageLocationPath + path);
			try {
				return Files.readAllBytes(file.toPath());
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
			String path = storageLocationPath + fileMeta.getPath().substring(0, fileMeta.getPath().lastIndexOf('/') + 1)
					+ slideNo + ".jpg";
			File file = new File(path);
			try {
				return Files.readAllBytes(file.toPath());
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

			String fileName = fileMeta.getName();
			String path = storageLocationPath + relativePath + fileName;
			File file = new File(path);
			try {
				return Files.readAllBytes(file.toPath());
			} catch (IOException e) {
				throw new ResourceNotFoundException();
			}
		}
		throw new ResourceNotFoundException();
	}

	public FileMeta getFileById(String id) {

		Optional<FileMeta> fileMetaOptional = fileMetaMongoRepository.findById(id);
		FileMeta fileMeta = new FileMeta();
		if (fileMetaOptional.isPresent())
			fileMeta = fileMetaOptional.get();
		return fileMeta;
	}

	private int getCurrentYear() {

		return Calendar.getInstance().get(Calendar.YEAR);
	}

	private String getCurrentMonth() {

		return Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
	}

}
