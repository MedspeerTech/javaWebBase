package com.piotics.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.piotics.common.TimeManager;
import com.piotics.constants.FileType;


@Document(collection = "filemeta")
public class FileMeta {

	@Id
	private String id;
	private String name;
	private String path;
	private FileType type;
	private String owner;
	private String parentId;
	private Double size;
	private Date creationDate;
	private String originalContentType;
	private long length;
	
	public  FileMeta() {
	}
	
	public 	FileMeta(String id, String originalFilename, String originalContentType) {
		 this.owner = id;
		this.name = originalFilename;
		this.originalContentType = originalContentType;
		this.creationDate= Date.from(TimeManager.getCurrentTimestamp().toInstant());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public FileType getType() {
		return type;
	}
	public void setType(FileType type) {
		this.type = type;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public Double getSize() {
		return size;
	}
	public void setSize(Double size) {
		this.size = size;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getOriginalContentType() {
		return originalContentType;
	}
	public void setOriginalContentType(String originalContentType) {
		this.originalContentType = originalContentType;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	
	
}
