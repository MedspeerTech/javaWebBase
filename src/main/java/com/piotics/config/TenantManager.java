package com.piotics.config;

import org.springframework.stereotype.Component;

@Component
public class TenantManager {

	private String id = "5d384332aa511b04541d8803";
	
	public String getTenantId() {
		return this.id+"_";
	}
	
//	public void set
}
