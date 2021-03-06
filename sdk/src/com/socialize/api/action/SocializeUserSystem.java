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
package com.socialize.api.action;

import android.content.Context;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.api.SocializeSessionPersister;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.SocializeAuthProviderInfo;
import com.socialize.auth.SocializeAuthProviderInfoFactory;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.user.UserListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.ui.profile.UserProfile;
import com.socialize.util.DeviceUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class SocializeUserSystem extends SocializeApi<User, SocializeProvider<User>> implements UserSystem {

	private IBeanFactory<AuthProviderData> authProviderDataFactory;
	private SocializeSessionPersister sessionPersister;
	private DeviceUtils deviceUtils;
	private SocializeAuthProviderInfoFactory socializeAuthProviderInfoFactory;
	private Context context;
	
	public SocializeUserSystem(SocializeProvider<User> provider) {
		super(provider);
	}
	
	@Deprecated
	public void init(Context context) {
		this.context = context;
	}
	
	@Deprecated
	@Override
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		authenticate(context, consumerKey, consumerSecret, listener, sessionConsumer);
	}

	@Deprecated
	@Override
	public void authenticate(String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth) {
		authenticate(context, consumerKey, consumerSecret, authProviderData, listener, sessionConsumer, do3rdPartyAuth);
	}

	@Override
	public SocializeSession authenticateSynchronous(Context ctx, String consumerKey, String consumerSecret, SocializeSessionConsumer sessionConsumer) throws SocializeException {
		if(ctx == null) ctx = context;
		String udid = deviceUtils.getUDID(ctx);
		
		SocializeSession session = authenticate(ctx, "/authenticate/",consumerKey, consumerSecret, udid);
		
		if(sessionConsumer != null) {
			sessionConsumer.setSession(session);
		}
		
		return session;
	}

	@Override
	public void authenticate(Context context, String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		AuthProviderData authProviderData = authProviderDataFactory.getBean();
		authProviderData.setAuthProviderInfo(socializeAuthProviderInfoFactory.newInstance());
		authenticate(context, consumerKey, consumerSecret, authProviderData, listener, sessionConsumer, false);	
	}
	
	// For mocking
	protected SocializeAuthProviderInfo newSocializeAuthProviderInfo() {
		return new SocializeAuthProviderInfo();
	}

	@Override
	public void authenticate(Context ctx, String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth) {
		if(ctx == null) ctx = context;
		
		String udid = deviceUtils.getUDID(ctx);
		
		// TODO: create test case for this
		if(StringUtils.isEmpty(udid)) {
			if(listener != null) {
				listener.onError(new SocializeException("No UDID provided"));
			}
		}
		else {
			// All Api instances have authenticate, so we can just use any old one
			authenticateAsync(ctx, consumerKey, consumerSecret, udid, authProviderData, listener, sessionConsumer, do3rdPartyAuth);
		}	
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.UserSystem#getUser(com.socialize.api.SocializeSession, long, com.socialize.listener.user.UserListener)
	 */
	@Override
	public void getUser(SocializeSession session, long id, UserListener listener) {
		getAsync(session, ENDPOINT, String.valueOf(id), listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.UserSystem#saveUserProfile(android.content.Context, com.socialize.api.SocializeSession, com.socialize.ui.profile.UserProfile, com.socialize.listener.user.UserListener)
	 */
	@Override
	public void saveUserProfile(final Context context, final SocializeSession session, UserProfile profile, final UserListener listener) {
		User user = session.getUser();
		user.setFirstName(profile.getFirstName());
		user.setLastName(profile.getLastName());
		user.setProfilePicData(profile.getEncodedImage());
		user.setAutoPostCommentsFacebook(profile.isAutoPostCommentsFacebook());
		user.setAutoPostLikesFacebook(profile.isAutoPostLikesFacebook());
		user.setNotificationsEnabled(profile.isNotificationsEnabled());
		saveUserProfile(context, session, user, listener);
	}
	
	protected void saveUserProfile(final Context context, final SocializeSession session, User user, final UserListener listener) {

		String endpoint = ENDPOINT + user.getId() + "/";
		
		putAsPostAsync(session, endpoint, user, new UserSaveListener() {

			@Override
			public void onError(SocializeException error) {
				listener.onError(error);
			}

			@Override
			public void onUpdate(User savedUser) {
				// Update local in-memory user
				User sessionUser = session.getUser();
				sessionUser.merge(savedUser);
				
				// Save this user to the local session for next load
				if(sessionPersister != null) {
					sessionPersister.saveUser(context, sessionUser);
				}
				
				listener.onUpdate(sessionUser);
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.UserSystem#saveUserProfile(android.content.Context, com.socialize.api.SocializeSession, java.lang.String, java.lang.String, java.lang.String, com.socialize.listener.user.UserListener)
	 */
	@Deprecated
	@Override
	public void saveUserProfile(final Context context, final SocializeSession session, String firstName, String lastName, String encodedImage, final UserListener listener) {
		User user = session.getUser();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setProfilePicData(encodedImage);
		saveUserProfile(context, session, user, listener);
	}

	public void setSessionPersister(SocializeSessionPersister sessionPersister) {
		this.sessionPersister = sessionPersister;
	}

	public void setAuthProviderDataFactory(IBeanFactory<AuthProviderData> authProviderDataFactory) {
		this.authProviderDataFactory = authProviderDataFactory;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setSocializeAuthProviderInfoFactory(SocializeAuthProviderInfoFactory socializeAuthProviderInfoFactory) {
		this.socializeAuthProviderInfoFactory = socializeAuthProviderInfoFactory;
	}
}
