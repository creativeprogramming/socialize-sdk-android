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
package com.socialize.sample.mocks;

import com.socialize.api.WritableSession;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.entity.User;

/**
 * @author Jason Polites
 *
 */
public class MockSocializeSession implements WritableSession {

	private static final long serialVersionUID = 2821519529554271142L;
	
	User user;
	String key = "all";
	String sec = "my";
	String tok = "base";
	String toksec = "belongs";
	String host = "to";
	
	public MockSocializeSession() {
		super();
		user = new User();
		user.setId(-1L);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getUser()
	 */
	@Override
	public User getUser() {
		return user;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getConsumerKey()
	 */
	@Override
	public String getConsumerKey() {
		return key;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getConsumerSecret()
	 */
	@Override
	public String getConsumerSecret() {
		return sec;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getConsumerToken()
	 */
	@Override
	public String getConsumerToken() {
		return tok;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getConsumerTokenSecret()
	 */
	@Override
	public String getConsumerTokenSecret() {
		return toksec;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getHost()
	 */
	@Override
	public String getHost() {
		return host;
	}

	@Deprecated
	@Override
	public String get3rdPartyUserId() {
		return null;
	}

	@Deprecated
	@Override
	public String get3rdPartyToken() {
		return null;
	}

	@Deprecated
	@Override
	public String get3rdPartyAppId() {
		return null;
	}

	@Deprecated
	@Override
	public AuthProviderType getAuthProviderType() {
		return AuthProviderType.SOCIALIZE;
	}

	@Deprecated
	@Override
	public AuthProvider<?> getAuthProvider() {
		return null;
	}

	@Override
	public UserProviderCredentialsMap getUserProviderCredentials() {
		return null;
	}

	@Override
	public UserProviderCredentials getUserProviderCredentials(AuthProviderType type) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#clear(com.socialize.auth.AuthProviderType)
	 */
	@Override
	public void clear(AuthProviderType type) {}

	@Override
	public void setConsumerToken(String token) {
	}

	@Override
	public void setConsumerTokenSecret(String secret) {
	}

	@Override
	public void setUser(User user) {
	}

	@Override
	public void setHost(String host) {
	}

	@Override
	public void setUserProviderCredentials(AuthProviderType type, UserProviderCredentials data) {
	}

	@Override
	public void set3rdPartyUserId(String userId) {
	}

	@Override
	public void set3rdPartyToken(String token) {
	}

	@Override
	public void set3rdAppId(String appId) {
	}

	@Override
	public void setAuthProviderType(AuthProviderType authProviderType) {
	}

	@Override
	public void setAuthProvider(AuthProvider<?> authProvider) {
	}
	
	

}
