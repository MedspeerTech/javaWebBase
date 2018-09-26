package medspeer.tech.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.Part;

import medspeer.tech.common.FileContentType;
import medspeer.tech.common.TimeManager;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import medspeer.tech.common.FileType;
import medspeer.tech.exception.FileUploadException;
import medspeer.tech.model.FileMeta;

@Repository
public class FileRepository {
	
	
	
	@Value("${meds.storage.location}")
	String storageLocationPath;

	@Value("${meds.storage.documents.location}")
	String documentStorageLocation;

	@Autowired
	FileMetaJpaRepository filemetaJpaReposiotry;

	@Autowired
	private MessageSource messageSource;

	@Autowired
    TimeManager timeManager;
	
	
	
	public FileMeta saveFile(MultipartFile file, FileMeta fileMeta)   {
		ByteArrayResource byteArrayResource = null;
		try {
			byteArrayResource = new ByteArrayResource(file.getBytes());
			return saveFile(byteArrayResource, fileMeta);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();

		}
		
		//return fileMeta;
		
	}

	
	public FileMeta saveFile(ByteArrayResource byteArrayResource, FileMeta fileMeta)   {
		  
		Part filePart = null;
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();

		parts.add("file", byteArrayResource);
		parts.add("fileMeta", fileMeta);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		fileMeta.setCreationDate(timeManager.getCurrentTimestamp());
		fileMeta.setId(generate9DigitNumber());

		checkFileFormatIsSupported(fileMeta);
		String filePath = getPathBasedFileType(fileMeta);
		writeFileToDisk(byteArrayResource, filePath);
		saveFileMeta(fileMeta);

		return fileMeta;
		  
	  }
	  

	private void saveFileMeta(FileMeta fileMeta) {
		
		filemetaJpaReposiotry.save(fileMeta);
	}
	
	public int generate9DigitNumber()
    {
        int aNumber = (int) ((Math.random() * 900000000) + 100000000); 
        return aNumber;
    }

	private String getPathBasedFileType(FileMeta fileMata) {
		
		String relativePath;

		if (fileMata.getType() == FileType.Document) {
			relativePath = documentStorageLocation;
		} else if (fileMata.getType() == FileType.PDF) {
			relativePath = documentStorageLocation;
		} else {
			throw new FileUploadException(
					messageSource.getMessage("file.format.not.supported", null, LocaleContextHolder.getLocale()));
		}
		relativePath = relativePath + fileMata.getId() + File.separator;

		fileMata.setPath(relativePath);

		String originalFileName = fileMata.getName();
		String fileExtension = FilenameUtils.getExtension(originalFileName);

		String fileLocation = storageLocationPath + relativePath;
		String filePath = fileLocation + "original." + fileExtension;

		new File(fileLocation).mkdirs();

		return filePath;

            
	//	return null;
	}
	
	 private void checkFileFormatIsSupported(FileMeta fileMeta) {

		FileType type;

		if (isPdfFile(fileMeta)) {
			type = FileType.PDF;
		} else if (isDocumentFile(fileMeta)) {
			type = FileType.Document;
		} else {
			throw new FileUploadException(
					messageSource.getMessage("file.format.not.supported", null, LocaleContextHolder.getLocale()));
		}

		fileMeta.setType(type);

	}
	 
	 private boolean isPdfFile(FileMeta file) {
		String contentType = file.getOriginalContentType();
		return contentType.equals(FileContentType.pdfDocument);
	}
	 
	 private boolean isDocumentFile(FileMeta file) {
		String contentType = file.getOriginalContentType();
		return contentType.equals(FileContentType.wordocument);
	}
	public void writeFileToDisk(ByteArrayResource byteArrayResource, String path) {
		OutputStream out = null;
		InputStream fileContent = null;

		try {
			out = new FileOutputStream(new File(path));
			fileContent = byteArrayResource.getInputStream();

			int read = 0;
			final byte[] bytes = new byte[1024];

			while ((read = fileContent.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new FileUploadException(
					messageSource.getMessage("file.read.exception", null, LocaleContextHolder.getLocale()));
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (fileContent != null) {
					fileContent.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new FileUploadException(
						messageSource.getMessage("file.read.exception", null, LocaleContextHolder.getLocale()));
			}
        }
    }
	  
	}

