package medspeer.tech.model;

import medspeer.tech.common.FileType;

public class FileData {
	
	
	private int id;
	private FileType type;
	private String link;

	public FileData() {

	}

	public FileData(int id) {
		this.id = id;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FileType getType() {
		return type;
	}

	public void setType(FileType type) {
		this.type = type;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
