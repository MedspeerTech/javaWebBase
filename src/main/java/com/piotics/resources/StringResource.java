package com.piotics.resources;

import java.io.Serializable;
import java.util.List;

public class StringResource implements Serializable {
	
	List<String> strings;

	public StringResource(List<String> strings) {
		
		this.strings = strings;
	}

	public StringResource() {}

	public List<String> getStrings() {
		return strings;
	}

	public void setStrings(List<String> strings) {
		this.strings = strings;
	}
	
	

}
