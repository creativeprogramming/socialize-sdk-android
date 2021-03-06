/*
 * Copyright (c) 2011 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.auth;


/**
 * @author Jason Polites
 */
public class AuthProviderData {
	
	private String userId3rdParty; 
	private String token3rdParty; 
	
	private AuthProviderInfo authProviderInfo;
	
	@Deprecated
	private String appId3rdParty;
	
	@Deprecated
	private AuthProviderType authProviderType = AuthProviderType.SOCIALIZE;
	
	public AuthProviderData() {
		super();
	}
	
	public String getUserId3rdParty() {
		return userId3rdParty;
	}
	public void setUserId3rdParty(String userId3rdParty) {
		this.userId3rdParty = userId3rdParty;
	}
	public String getToken3rdParty() {
		return token3rdParty;
	}
	public void setToken3rdParty(String token3rdParty) {
		this.token3rdParty = token3rdParty;
	}
	
	@Deprecated
	public String getAppId3rdParty() {
		return appId3rdParty;
	}
	
	@Deprecated
	public void setAppId3rdParty(String appId3rdParty) {
		this.appId3rdParty = appId3rdParty;
	}
	
	@Deprecated
	public AuthProviderType getAuthProviderType() {
		return authProviderType;
	}
	
	@Deprecated
	public void setAuthProviderType(AuthProviderType authProviderType) {
		this.authProviderType = authProviderType;
	}

	public AuthProviderInfo getAuthProviderInfo() {
		return authProviderInfo;
	}

	public void setAuthProviderInfo(AuthProviderInfo authProviderInfo) {
		this.authProviderInfo = authProviderInfo;
	}
}
