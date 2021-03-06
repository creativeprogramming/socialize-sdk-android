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
package com.socialize.api;

import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;

/**
 * @author Jason Polites
 *
 */
public interface SocializeSessionFactory {

	/**
	 * Creates a new session.
	 * @param key
	 * @param secret
	 * @param authProviderType
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public WritableSession create(String key, String secret, String userId3rdParty, String token3rdParty, String appId3rdParty, AuthProviderType authProviderType);
	
	/**
	 * Creates a new session.
	 * @param key
	 * @param secret
	 * @param userProviderCredentials
	 * @return
	 */
	public WritableSession create(String key, String secret, UserProviderCredentials userProviderCredentials);
	
	public WritableSession create(String key, String secret, UserProviderCredentialsMap userProviderCredentialsMap);
	
	/**
	 * Creates a new session.
	 * @param key
	 * @param secret
	 * @param data
	 * @return
	 */
	public WritableSession create(String key, String secret, AuthProviderData data);
	
}
