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

import android.content.Context;

import com.socialize.api.SocializeAuthRequest;
import com.socialize.listener.AuthProviderListener;

/**
 * @author Jason Polites
 *
 */
public interface AuthProvider<I extends AuthProviderInfo> {

	/**
	 * Authenticates using a 3rd party provider.
	 * @param authRequest
	 * @param appId The id of the account/app to be authenticated.
	 * @param listener A listener to handle the outcome.
	 * @deprecated use {@link #authenticate(SocializeAuthRequest, AuthProviderInfo, AuthProviderListener)}
	 */
	@Deprecated
	public void authenticate(SocializeAuthRequest authRequest, String appId, AuthProviderListener listener);
	
	/**
	 * Authenticates using a 3rd party provider.
	 * @param authRequest
	 * @param info The info for the account/app to be authenticated.
	 * @param listener A listener to handle the outcome.
	 */
	public void authenticate(I info, AuthProviderListener listener);
	
	/**
	 * Clears any cached data for this provider.
	 * @deprecated use {@link #clearCache(Context, AuthProviderInfo)}
	 */
	@Deprecated
	public void clearCache(Context context, String appId);
	
	/**
	 * Clears any cached data for this provider.
	 * @param context
	 * @param info
	 */
	public void clearCache(Context context, I info);
}
