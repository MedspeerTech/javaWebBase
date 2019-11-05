package com.piotics.resources;

import com.piotics.model.TenantShort;
import com.piotics.model.UserShort;

public class SessionUser {

	
	private UserShort userShort;
	private TenantShort tenantShort;
	
	public SessionUser(UserShort userShort, TenantShort tenantShort) {
		
		this.userShort = userShort;
		this.tenantShort = tenantShort;
	}
	public UserShort getUserShort() {
		return userShort;
	}
	public void setUserShort(UserShort userShort) {
		this.userShort = userShort;
	}
	public TenantShort getTenantShort() {
		return tenantShort;
	}
	public void setTenantShort(TenantShort tenantShort) {
		this.tenantShort = tenantShort;
	}
}
