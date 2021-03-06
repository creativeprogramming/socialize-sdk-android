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
package com.socialize.auth.facebook;

import android.content.Context;
import android.content.Intent;

import com.socialize.api.SocializeAuthRequest;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.error.SocializeException;
import com.socialize.facebook.Facebook;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.ListenerHolder;
import com.socialize.log.SocializeLogger;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
public class FacebookAuthProvider implements AuthProvider<FacebookAuthProviderInfo> {
	
	private Context context;
	private ListenerHolder holder; // This is a singleton
	private SocializeLogger logger;
	private Drawables drawables;
	private FacebookSessionStore facebookSessionStore;
	
	public FacebookAuthProvider() {
		super();
	}

	public void init(Context context, ListenerHolder holder) {
		this.context = context;
		this.holder = holder;
	}
	
	@Override
	public void authenticate(FacebookAuthProviderInfo info, final AuthProviderListener listener) {

		final String listenerKey = "auth";
		
		holder.put(listenerKey, new AuthProviderListener() {
			
			@Override
			public void onError(SocializeException error) {
				holder.remove(listenerKey);
				listener.onError(error);
			}
			
			@Override
			public void onAuthSuccess(AuthProviderResponse response) {
				holder.remove(listenerKey);
				listener.onAuthSuccess(response);
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				holder.remove(listenerKey);
				listener.onAuthFail(error);
			}

			@Override
			public void onCancel() {
				holder.remove(listenerKey);
				listener.onCancel();
			}
		});
		
		Intent i = new Intent(context, FacebookActivity.class);
		i.putExtra("appId", info.getAppId());
		context.startActivity(i);		
	}


	/* (non-Javadoc)
	 * @see com.socialize.auth.AuthProvider#authenticate()
	 */
	@Deprecated
	@Override
	public void authenticate(SocializeAuthRequest authRequest, String appId, final AuthProviderListener listener) {
		FacebookAuthProviderInfo info = new FacebookAuthProviderInfo();
		info.setAppId(appId);
		authenticate(info, listener);
	}

	@Override
	public void clearCache(Context context, FacebookAuthProviderInfo info) {
		Facebook mFacebook = getFacebook(info.getAppId());
		
		try {
			mFacebook.logout(context);
		}
		catch (Exception e) {
			if(logger != null) {
				logger.error("Failed to log out of Facebook", e);
			}
			else {
				e.printStackTrace();
			}
		}
		finally {
			if(facebookSessionStore != null) {
				facebookSessionStore.clear(context);
			}
		}
	}

	@Deprecated
	@Override
	public void clearCache(Context context, String appId) {
		FacebookAuthProviderInfo info = new FacebookAuthProviderInfo();
		info.setAppId(appId);
		clearCache(context, info);
	}
	
	protected Facebook getFacebook(String appId) {
		return new Facebook(appId, drawables);
	}

	public SocializeLogger getLogger() {
		return logger;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setFacebookSessionStore(FacebookSessionStore facebookSessionStore) {
		this.facebookSessionStore = facebookSessionStore;
	}
}
