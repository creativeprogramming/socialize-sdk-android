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
package com.socialize.test.ui.integrationtest;

import java.util.ArrayList;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;
import com.socialize.Socialize;
import com.socialize.sample.ui.SampleActivity2;
import com.socialize.test.ui.ResultHolder;

/**
 * @author Jason Polites
 */
public abstract class SocializeUIRobotiumTest extends ActivityInstrumentationTestCase2<SampleActivity2> {

	protected final ResultHolder holder = new ResultHolder();
	
	public static final int DEFAULT_TIMEOUT_SECONDS = 100;
	public static final String DEFAULT_ENTITY_URL = "http://socialize.integration.tests.com?somekey=somevalue&anotherkey=anothervalue";
	public static final String DEFAULT_GET_ENTITY = "http://entity1.com";
	public static final String SOCIALIZE_FACEBOOK_ID = "209798315709193";
	
	
	public static final int BTN_FACEBOOK_SSO = 0;
	public static final int BTN_MOCK_FACEBOOK = 1;
	public static final int BTN_MOCK_SOCIALIZE = 2;
	public static final int BTN_NOTIFICATIONS_ENABLED = 3;
	public static final int BTN_CLEAR_CACHE = 4;
	public static final int BTN_SHOW_COMMENTS = 5;
	public static final int BTN_SHOW_ACTION_BAR_AUTO = 6;
	public static final int BTN_SHOW_ACTION_BAR_MANUAL = 7;
		
	protected Solo robotium;
	protected InputMethodManager imm = null;

	public SocializeUIRobotiumTest() {
		super("com.socialize.sample.ui", SampleActivity2.class);
	}

	public void setUp() throws Exception {
		holder.setUp();
		imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		robotium = new Solo(getInstrumentation(), getActivity());
		robotium.waitForActivity("SampleActivity", 5000);
		hideKeyboard();
	}
	

	@Override
	protected void tearDown() throws Exception {
		
		Socialize.getSocialize().destroy(true);
		
		try {
			robotium.finish();
		} 
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		super.tearDown();
	}	
	
	protected void toggleFacebookSSO(boolean on) {
		if(!on) {
			robotium.clickOnButton(BTN_FACEBOOK_SSO);
		}
	}
	
	protected void toggleMockedFacebook(boolean on) {
		if(on) {
			robotium.clickOnButton(BTN_MOCK_FACEBOOK);
		}
	}
	
	protected void toggleMockedSocialize(boolean on) {
		if(on) {
			robotium.clickOnButton(BTN_MOCK_SOCIALIZE);
		}
	}
	
	protected void toggleNotificationsEnabled(boolean on) {
		if(on) {
			robotium.clickOnButton(BTN_NOTIFICATIONS_ENABLED);
		}
	}		
	
	protected void clearAuthCache() {
		robotium.clickOnButton(BTN_CLEAR_CACHE);
		robotium.waitForDialogToClose(5000);
		assertNull(Socialize.getSocialize().getSession());
	}
	
	protected void showComments() {
		robotium.clickOnButton(BTN_SHOW_COMMENTS);
	}
	
	protected void showActionBarAuto() {
		robotium.clickOnButton(BTN_SHOW_ACTION_BAR_AUTO);
	}
	
	protected void showActionBarManual() {
		robotium.clickOnButton(BTN_SHOW_ACTION_BAR_MANUAL);
	}
	
	protected void startWithFacebook(boolean sso) {
		robotium.clearEditText(0);
		robotium.enterText(0, DEFAULT_GET_ENTITY);
		robotium.clearEditText(2);
		robotium.enterText(2, SOCIALIZE_FACEBOOK_ID);
		toggleFacebookSSO(sso);
		toggleMockedFacebook(true);
		clearAuthCache();
	}
	
	protected void startWithoutFacebook() {
		startWithoutFacebook(true);
	}
	protected void startWithoutFacebook(boolean clearCache) {
		robotium.clearEditText(0);
		robotium.enterText(0, DEFAULT_GET_ENTITY);
		robotium.clearEditText(2);
		if(clearCache) clearAuthCache();
	}

	protected void addResult(Object obj) {
		holder.addResult(obj);
	}
	
	protected void addResult(int index, Object obj) {
		holder.addResult(index, obj);
	}
	
	protected <T extends Object> T getResult(int index) {
		return holder.getResult(index);
	}
	
	protected <T extends Object> T getNextResult() {
		return holder.getNextResult();
	}
	

	protected final void sleep(int milliseconds) {
		synchronized (this) {
			try {
				wait(milliseconds);
			}
			catch (InterruptedException ignore) {}
		}
	}

	protected final void hideKeyboard() {
		// Hide keyboard for all
		ArrayList<EditText> currentEditTexts = robotium.getCurrentEditTexts();

		if(currentEditTexts != null) {
			for (EditText editText : currentEditTexts) {
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends View> T findView(Class<T> viewClass) {
		
		ArrayList<View> currentViews = robotium.getCurrentViews();
		for (View view : currentViews) {
			if(viewClass.isAssignableFrom(view.getClass())) {
				return (T) view;
			}
		}
		return null;		
//		return TestUtils.findView(getActivity(), viewClass, 20000);
	}

}
