package medspeer.tech.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import medspeer.tech.common.FileType;


//@Entity
@Entity(name = "filemeta")
public class FileMeta {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	private String path;
	private FileType type;
	// private String owner;
	private Double size;
	private Date creationDate;
	private String originalContentType;
	private long length;

	public FileMeta() {

	}

	public FileMeta(int id, String originalFilename, String originalContentType) {
		// this.owner = id;
		this.name = originalFilename;
		this.originalContentType = originalContentType;
	}

	/*
	 * public FileMeta1(String userId,String originalFilename, String
	 * originalContentType) { this.owner=userId; this.name=originalFilename;
	 * this.originalContentType=originalContentType;
	 * 
	 * }
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	/*
	 * public String getOwner() { return owner; } public void setOwner(String owner)
	 * { this.owner = owner; }
	 */
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
