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
package com.socialize.test.unit;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.location.DefaultLocationProvider;
import com.socialize.location.SocializeLocationListener;
import com.socialize.location.SocializeLocationManager;
import com.socialize.test.SocializeActivityTest;
import com.socialize.util.AppUtils;

/**
 * @author Jason Polites
 * 
 */
@UsesMocks({ AppUtils.class, Activity.class, SocializeLocationManager.class, SocializeLocationListener.class, Location.class, IBeanFactory.class })
public class DefaultLocationProviderTest extends SocializeActivityTest {

	AppUtils appUtils;
	Activity context;
	SocializeLocationManager locationManager;
	IBeanFactory<SocializeLocationListener> locationListenerFactory;
	SocializeLocationListener listener;

	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		appUtils = AndroidMock.createMock(AppUtils.class);
		context = AndroidMock.createMock(Activity.class);
		locationManager = AndroidMock.createMock(SocializeLocationManager.class, appUtils);
		locationListenerFactory = AndroidMock.createMock(IBeanFactory.class);
		listener = AndroidMock.createMock(SocializeLocationListener.class);
	}

	public void testDoesNotHavePermission() {

		AndroidMock.expect(appUtils.hasPermission(context, "android.permission.ACCESS_FINE_LOCATION")).andReturn(false).anyTimes();
		AndroidMock.expect(appUtils.hasPermission(context, "android.permission.ACCESS_COARSE_LOCATION")).andReturn(false).anyTimes();
		AndroidMock.expect(locationListenerFactory.getBean()).andReturn(listener);

		AndroidMock.replay(appUtils);
		AndroidMock.replay(locationListenerFactory);

		DefaultLocationProvider provider = new DefaultLocationProvider();
		provider.setAppUtils(appUtils);
		provider.setLocationListenerFactory(locationListenerFactory);
		provider.init(context);

		assertNull(provider.getLocation());

		AndroidMock.verify(appUtils);
		AndroidMock.verify(locationListenerFactory);
	}

	public void testLastKnownLocationAvailable() {

		final String strProvider = "foobar";

		Location location = AndroidMock.createMock(Location.class, strProvider);

		AndroidMock.expect(appUtils.hasPermission(context, "android.permission.ACCESS_FINE_LOCATION")).andReturn(true);
		AndroidMock.expect(locationManager.getBestProvider((Criteria) AndroidMock.anyObject(), AndroidMock.eq(true))).andReturn(strProvider);
		AndroidMock.expect(locationManager.getLastKnownLocation(strProvider)).andReturn(location);
		AndroidMock.expect(locationListenerFactory.getBean()).andReturn(listener);

		AndroidMock.replay(appUtils);
		AndroidMock.replay(locationManager);
		AndroidMock.replay(locationListenerFactory);

		DefaultLocationProvider provider = new DefaultLocationProvider();
		provider.setLocationManager(locationManager);
		provider.setAppUtils(appUtils);
		provider.setLocationListenerFactory(locationListenerFactory);
		provider.init(context);

		Location loc = provider.getLocation();

		AndroidMock.verify(appUtils);
		AndroidMock.verify(locationManager);
		AndroidMock.verify(locationListenerFactory);

		assertNotNull(loc);
		assertSame(location, loc);
	}

	public void testLastKnownLocationNotAvailable() {

		final String strProvider = "foobar";

		AndroidMock.expect(appUtils.hasPermission(context, "android.permission.ACCESS_FINE_LOCATION")).andReturn(true).anyTimes();
		AndroidMock.expect(locationManager.getBestProvider((Criteria) AndroidMock.anyObject(), AndroidMock.eq(true))).andReturn(strProvider).anyTimes();
		AndroidMock.expect(locationManager.getLastKnownLocation(strProvider)).andReturn(null).anyTimes();
		AndroidMock.expect(locationManager.isProviderEnabled(strProvider)).andReturn(true).anyTimes();
		AndroidMock.expect(locationListenerFactory.getBean()).andReturn(listener).anyTimes();

		locationManager.requestLocationUpdates(context, strProvider, 1L, 0.0f, listener);
		locationManager.requestLocationUpdates(context, strProvider, 1L, 0.0f, listener);

		AndroidMock.replay(locationListenerFactory);
		AndroidMock.replay(appUtils);
		AndroidMock.replay(locationManager);

		DefaultLocationProvider provider = new DefaultLocationProvider();
		provider.setLocationManager(locationManager);
		provider.setAppUtils(appUtils);
		provider.setLocationListenerFactory(locationListenerFactory);
		provider.init(context);

		Location loc = provider.getLocation();

		AndroidMock.verify(locationListenerFactory);
		AndroidMock.verify(appUtils);
		AndroidMock.verify(locationManager);

		assertNull(loc);
	}
}
