package com.socialize.test.ui;

import android.view.View;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.view.AuthenticatedView;

public class AuthenticatedViewTest extends SocializeUIActivityTest {

	@UsesMocks ({IOCContainer.class, SocializeService.class, SocializeAuthListener.class, SocializeConfig.class})
	public void testOnViewLoad() {
		
		final IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		
		final String key = "foo";
		final String secret = "bar";
		final String fbId = "foobar";
		
		AndroidMock.expect(socialize.getConfig()).andReturn(config).anyTimes();
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY)).andReturn(key);
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET)).andReturn(secret);
		AndroidMock.expect(config.getProperty(SocializeConfig.FACEBOOK_APP_ID)).andReturn(fbId);
		
		socialize.authenticate(getContext(), key, secret, listener);
		
		AuthenticatedView view = new AuthenticatedView(getContext()) {
			
			@Override
			public View getView() {
				return null;
			}
			
			@Override
			public View getLoadingView() {
				return null;
			}

			@Override
			public SocializeService getSocialize() {
				return socialize;
			}

			@Override
			public SocializeAuthListener getAuthListener(IOCContainer container) {
				return listener;
			}
		};
		
		AndroidMock.replay(socialize);
		AndroidMock.replay(config);
		
		view.onViewLoad(container);
		
		AndroidMock.verify(socialize);
		AndroidMock.verify(config);
	}
}
