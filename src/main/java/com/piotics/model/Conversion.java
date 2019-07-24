package com.piotics.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.piotics.constants.FileType;

@Document(collection="conversion")
public class Conversion {

	@Id
	private String id;
	private Date creationDate;
	private FileType fileType;
	private String sourceLocation;
	private String log;
	private FileMeta fileMeta;

	public Conversion() {
	}

	public Conversion(Date creationDate, FileType fileType, String sourceLocation) {
		super();
	
		this.creationDate = creationDate;
		this.fileType = fileType;
		this.sourceLocation = sourceLocation;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public String getSourceLocation() {
		return sourceLocation;
	}

	public void setSourceLocation(String sourceLocation) {
		this.sourceLocation = sourceLocation;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public FileMeta getFileMeta() {
		return fileMeta;
	}

	public void setFileMeta(FileMeta fileMeta) {
		this.fileMeta = fileMeta;
	}
	
	
}
