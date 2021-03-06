/*
 * Copyright (c) 2012 Socialize Inc.
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
package com.socialize.test.ui.facebook;

import android.app.Activity;
import android.content.Context;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.api.ShareMessageBuilder;
import com.socialize.api.action.ActionType;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.facebook.FacebookSharer;
import com.socialize.networks.facebook.FacebookWallPoster;
import com.socialize.test.PublicSocialize;
import com.socialize.test.ui.SocializeActivityTestCase;

/**
 * @author Jason Polites
 *
 */
public class FacebookSharerTest extends SocializeActivityTestCase {

	@UsesMocks ({SocializeService.class, SocializeConfig.class, FacebookAuthProviderInfo.class})
	public void testShareNotAuthenticated() {
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		final FacebookAuthProviderInfo facebookAuthProviderInfo = AndroidMock.createMock(FacebookAuthProviderInfo.class);
		
		final String consumerKey = "foo";
		final String consumerSecret = "bar";
		final String fbId = "foobar_id";
		final boolean autoAuth = true;

		AndroidMock.expect(socialize.isSupported(AuthProviderType.FACEBOOK)).andReturn(true);
		AndroidMock.expect(socialize.isAuthenticated(AuthProviderType.FACEBOOK)).andReturn(false);
		
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY)).andReturn(consumerKey);
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET)).andReturn(consumerSecret);
		AndroidMock.expect(config.getProperty(SocializeConfig.FACEBOOK_APP_ID)).andReturn(fbId);
		
		socialize.authenticate(
				AndroidMock.eq ( getContext() ), 
				AndroidMock.eq (consumerKey), 
				AndroidMock.eq (consumerSecret),
				AndroidMock.eq (facebookAuthProviderInfo),
				(SocializeAuthListener) AndroidMock.anyObject());
		
		PublicFacebookSharer sharer = new PublicFacebookSharer() {
			@Override
			public SocializeService getSocialize() {
				return socialize;
			}

			@Override
			protected FacebookAuthProviderInfo newFacebookAuthProviderInfo() {
				return facebookAuthProviderInfo;
			}
		};
		
		sharer.setConfig(config);
		
		AndroidMock.replay(socialize);
		AndroidMock.replay(config);
		
		// Params can be null.. not in the path to test
		sharer.share(getContext(), null, null, null, ActionType.COMMENT, autoAuth);
		
		AndroidMock.verify(socialize);
		AndroidMock.verify(config);
	}
	@UsesMocks ({SocializeConfig.class})
	public void testShareAuthListener() {
		
		final PublicSocialize socialize = new PublicSocialize() {
			@Override
			public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderType authProviderType, String authProviderAppId, SocializeAuthListener authListener) {
				fail();
			}
			
			@Override
			public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderInfo authProviderInfo, SocializeAuthListener authListener) {
				addResult(authListener);
			}

			@Override
			public boolean isSupported(AuthProviderType type) {
				return true;
			}

			@Override
			public boolean isAuthenticated(AuthProviderType providerType) {
				return false;
			}
		};
		
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		
		final String consumerKey = "foo";
		final String consumerSecret = "bar";
		final String fbId = "foobar_id";
		final boolean autoAuth = true;
		
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY)).andReturn(consumerKey);
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET)).andReturn(consumerSecret);
		AndroidMock.expect(config.getProperty(SocializeConfig.FACEBOOK_APP_ID)).andReturn(fbId);
		
		PublicFacebookSharer sharer = new PublicFacebookSharer() {

			@Override
			public SocializeService getSocialize() {
				return socialize;
			}

			@Override
			public void doError(SocializeException e, Activity parent, SocialNetworkListener listener) {
				addResult("doError");
			}

			@Override
			public void doShare(Activity context, Entity entity, String comment, SocialNetworkListener listener, ActionType type) {
				addResult("doShare");
			}
		};
		
		sharer.setConfig(config);
		
		AndroidMock.replay(config);
		
		// Params can be null.. not in the path to test
		sharer.share(getContext(), null, null, null, ActionType.COMMENT, autoAuth);
		
		AndroidMock.verify(config);		

		// Get the listener
		SocializeAuthListener listener = getResult(0);
		
		clearResults();
		
		assertNotNull(listener);
		
		listener.onAuthSuccess(null);
		listener.onAuthFail(null);
		listener.onError(null);
		
		String doShare = getNextResult();
		String doError0 = getNextResult();
		String doError1 = getNextResult();
		
		assertNotNull(doShare);
		assertNotNull(doError0);
		assertNotNull(doError1);
		
		assertEquals("doShare", doShare);
		assertEquals("doError", doError0);
		assertEquals("doError", doError1);
	}
	
	@UsesMocks ({SocialNetworkListener.class, SocializeLogger.class})
	public void testDoError() {
		
		final String msg = "Error sharing to Facebook";
		
		SocializeLogger logger = AndroidMock.createMock(SocializeLogger.class);
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		SocializeException error = new SocializeException("foobar");
		
		logger.error(msg, error);
		listener.onError(getActivity(), SocialNetwork.FACEBOOK, msg, error);
		
		AndroidMock.replay(logger);
		AndroidMock.replay(listener);
		
		PublicFacebookSharer sharer = new PublicFacebookSharer();
		sharer.setLogger(logger);
		
		sharer.doError(error, getActivity(), listener);
		
		AndroidMock.verify(logger);
		AndroidMock.verify(listener);
	}
	
	@UsesMocks({SocialNetworkListener.class, FacebookWallPoster.class}) 
	public void testDoShareComment() {
		FacebookWallPoster facebookWallPoster = AndroidMock.createMock(FacebookWallPoster.class);
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		
		PublicFacebookSharer sharer = new PublicFacebookSharer();
		sharer.setFacebookWallPoster(facebookWallPoster);
		
		final String comment = "foobar";
		final Entity entity = Entity.newInstance("blah", null);
		
		listener.onBeforePost(getActivity(), SocialNetwork.FACEBOOK);
		facebookWallPoster.postComment(getActivity(), entity, comment, listener);
	}
	
	@UsesMocks({SocialNetworkListener.class, FacebookWallPoster.class, ShareMessageBuilder.class}) 
	public void testDoShareShare() {
		FacebookWallPoster facebookWallPoster = AndroidMock.createMock(FacebookWallPoster.class);
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		ShareMessageBuilder shareMessageBuilder = AndroidMock.createMock(ShareMessageBuilder.class);
		
		PublicFacebookSharer sharer = new PublicFacebookSharer();
		sharer.setFacebookWallPoster(facebookWallPoster);
		
		final String comment = "foobar";
		final String body = "foobar_body";
		final Entity entity = Entity.newInstance("blah", null);
		
		listener.onBeforePost(getActivity(), SocialNetwork.FACEBOOK);
		AndroidMock.expect(shareMessageBuilder.buildShareMessage( entity, comment, false, true)).andReturn(body);
		facebookWallPoster.post(getActivity(), body, listener);
	}
	
	@UsesMocks({SocialNetworkListener.class, FacebookWallPoster.class}) 
	public void testDoShareLike() {
		FacebookWallPoster facebookWallPoster = AndroidMock.createMock(FacebookWallPoster.class);
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		
		PublicFacebookSharer sharer = new PublicFacebookSharer();
		sharer.setFacebookWallPoster(facebookWallPoster);
		
		final String comment = "foobar";
		final Entity entity = Entity.newInstance("blah", null);
		
		listener.onBeforePost(getActivity(), SocialNetwork.FACEBOOK);
		facebookWallPoster.postLike(getActivity(), entity, comment, listener);
	}	
	
	public class PublicFacebookSharer extends FacebookSharer {

		@Override
		public void share(Activity context, Entity entity, String comment, SocialNetworkListener listener, ActionType type, boolean autoAuth) {
			super.share(context, entity, comment, listener, type, autoAuth);
		}

		@Override
		public void doError(SocializeException e, Activity parent, SocialNetworkListener listener) {
			super.doError(e, parent, listener);
		}

		@Override
		public void doShare(Activity context, Entity entity, String comment, SocialNetworkListener listener, ActionType type) {
			super.doShare(context, entity, comment, listener, type);
		}

		@Override
		public SocializeService getSocialize() {
			return super.getSocialize();
		}
	}
}
