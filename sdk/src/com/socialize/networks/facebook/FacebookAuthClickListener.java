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
package com.socialize.networks.facebook;

import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.dialog.DialogFactory;

/**
 * @author Jason Polites
 *
 */
public class FacebookAuthClickListener implements OnClickListener {

	private SocializeConfig config;
	private SocializeAuthListener listener;
	private DialogFactory<ProgressDialog> dialogFactory;
	private ProgressDialog dialog; 

	@Override
	public void onClick(final View view) {
		
		view.setEnabled(false);
		
		dialog = dialogFactory.show(view.getContext(), "Facebook", "Authenticating...");
		
		String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
		String authProviderAppId = config.getProperty(SocializeConfig.FACEBOOK_APP_ID);
		
		FacebookAuthProviderInfo fbInfo = new FacebookAuthProviderInfo();
		fbInfo.setAppId(authProviderAppId);
		
		getSocialize().authenticate(view.getContext(), consumerKey, consumerSecret, fbInfo, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				dialog.dismiss();
				if(listener != null) {
					listener.onError(error);
				}
				view.setEnabled(true);
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				dialog.dismiss();
				if(listener != null) {
					listener.onAuthSuccess(session);
				}
				view.setEnabled(true);
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				dialog.dismiss();
				if(listener != null) {
					listener.onAuthFail(error);
				}
				view.setEnabled(true);
			}

			@Override
			public void onCancel() {
				dialog.dismiss();
				if(listener != null) {
					listener.onCancel();
				}
				view.setEnabled(true);
			}
		});
	}

	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	public void setDialogFactory(DialogFactory<ProgressDialog> dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	public void setListener(SocializeAuthListener listener) {
		this.listener = listener;
	}
}
