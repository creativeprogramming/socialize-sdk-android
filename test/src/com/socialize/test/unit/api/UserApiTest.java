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
package com.socialize.test.unit.api;

import android.content.Context;
import android.test.mock.MockContext;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionPersister;
import com.socialize.api.action.SocializeUserSystem;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.user.UserListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.profile.UserProfile;

/**
 * @author Jason Polites
 */
@UsesMocks ({SocializeSession.class, UserListener.class, SocializeProvider.class})
public class UserApiTest extends SocializeUnitTest {

	SocializeProvider<User> provider;
	SocializeSession session;
	UserListener listener;
	
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception {
		super.setUp();
		provider = AndroidMock.createMock(SocializeProvider.class);
		session = AndroidMock.createMock(SocializeSession.class);
		listener = AndroidMock.createMock(UserListener.class);
	}

	public void testGetUser() {
		
		final int userId = 69;
		
		SocializeUserSystem api = new SocializeUserSystem(provider) {
			@Override
			public void getAsync(SocializeSession session, String endpoint, String id, SocializeActionListener listener) {
				addResult(id);
			}
		};
		
		api.getUser(session, userId, listener);
		
		String gotten = getNextResult();
		
		assertNotNull(gotten);
		assertEquals(userId, Integer.parseInt(gotten));
	}
	
	/**
	 * Tests save user profile but does NOT test that listener methods are correct.
	 */
	@UsesMocks ({User.class})
	public void testSaveUserProfile() {
		
		final long id = 69;
		
		Context context = new MockContext();
		String firstName = "foo";
		String lastName = "bar";
		String encodedImage = "foobar_encoded";
		
		User user = AndroidMock.createMock(User.class);
		
		AndroidMock.expect(session.getUser()).andReturn(user);
		AndroidMock.expect(user.getId()).andReturn(id);
		
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setProfilePicData(encodedImage);
		user.setAutoPostCommentsFacebook(true);
		user.setAutoPostLikesFacebook(true);
		user.setNotificationsEnabled(true);
		
		SocializeUserSystem api = new SocializeUserSystem(provider) {
			@Override
			public void putAsPostAsync(SocializeSession session, String endpoint, User object, SocializeActionListener listener) {
				addResult(object);
			}
		};
		
		AndroidMock.replay(session);
		AndroidMock.replay(user);
		
		UserProfile profile = new UserProfile();
		profile.setFirstName(firstName);
		profile.setLastName(lastName);
		profile.setEncodedImage(encodedImage);
		profile.setAutoPostCommentsFacebook(true);
		profile.setAutoPostLikesFacebook(true);
		profile.setNotificationsEnabled(true);
		
		
		api.saveUserProfile(context, session, profile, listener);
		
		AndroidMock.verify(session);
		AndroidMock.verify(user);
		
		User gotten = getNextResult();
		assertSame(user, gotten);
	}
	
	/**
	 * Tests that the listener created in saveUserProfile behaves correctly.
	 */
	@UsesMocks ({SocializeException.class, SocializeSessionPersister.class})
	public void testSaveUserProfileListener() {
		
		final long id = 69;
		
		Context context = new MockContext();
		String firstName = "foo";
		String lastName = "bar";
		String encodedImage = "foobar_encoded";
		
		User user = AndroidMock.createMock(User.class);
		SocializeSessionPersister sessionPersister = AndroidMock.createMock(SocializeSessionPersister.class);
		SocializeException exception = AndroidMock.createMock(SocializeException.class);
		
		AndroidMock.expect(session.getUser()).andReturn(user).times(2);
		AndroidMock.expect(user.getId()).andReturn(id);
		
		user.merge(user);
		
		listener.onUpdate(user);
		listener.onError(exception);
		
		sessionPersister.saveUser(context, user);
		
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setProfilePicData(encodedImage);
		user.setAutoPostCommentsFacebook(true);
		user.setAutoPostLikesFacebook(true);
		user.setNotificationsEnabled(true);
		
		SocializeUserSystem api = new SocializeUserSystem(provider) {
			@Override
			public void putAsPostAsync(SocializeSession session, String endpoint, User object, SocializeActionListener listener) {
				addResult(listener);
			}
		};
		
		api.setSessionPersister(sessionPersister);
		
		AndroidMock.replay(session);
		AndroidMock.replay(listener);
		AndroidMock.replay(sessionPersister);
		AndroidMock.replay(user);
		
		UserProfile profile = new UserProfile();
		profile.setFirstName(firstName);
		profile.setLastName(lastName);
		profile.setEncodedImage(encodedImage);
		profile.setAutoPostCommentsFacebook(true);
		profile.setAutoPostLikesFacebook(true);
		profile.setNotificationsEnabled(true);
		
		api.saveUserProfile(context, session, profile, listener);
		
		// This will fail if it's the wrong type
		UserSaveListener listenerFound = getNextResult();
		
		assertNotNull(listenerFound);
		
		// Call each listener methods to check the behaviour
		listenerFound.onUpdate(user);
		listenerFound.onError(exception);
		
		AndroidMock.verify(session);
		AndroidMock.verify(listener);
		AndroidMock.verify(sessionPersister);
		AndroidMock.verify(user);
		
	}
}
