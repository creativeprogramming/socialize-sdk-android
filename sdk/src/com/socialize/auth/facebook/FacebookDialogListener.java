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

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.socialize.auth.AuthProviderResponse;
import com.socialize.error.SocializeException;
import com.socialize.facebook.DialogError;
import com.socialize.facebook.Facebook;
import com.socialize.facebook.Facebook.DialogListener;
import com.socialize.facebook.FacebookError;
import com.socialize.listener.AuthProviderListener;

/**
 * @author Jason Polites
 *
 */
public abstract class FacebookDialogListener implements DialogListener {

	private FacebookSessionStore facebookSessionStore;
	private Facebook facebook;
	private Context context;
	private AuthProviderListener listener;

	public FacebookDialogListener(Context context, Facebook facebook, FacebookSessionStore facebookSessionStore, AuthProviderListener listener) {
		super();
		this.context = context;
		this.facebook = facebook;
		this.facebookSessionStore = facebookSessionStore;
		this.listener = listener;
	}

	@Override
	public void onComplete(Bundle values) {
		facebookSessionStore.save(facebook, context);
		try {
			String json = facebook.request("me");

			JSONObject obj = new JSONObject(json);
			
			String id = obj.getString("id");
			String token = values.getString("access_token");
			
			if(listener != null) {
				AuthProviderResponse response = new AuthProviderResponse();
				response.setUserId(id);
				response.setToken(token);
				listener.onAuthSuccess(response);
			}
			else {
				// TODO: log error
			}
		}
		catch (Exception e) {
			if(listener != null) {
				listener.onError(new SocializeException(e));
			}
			else {
				// TODO: log error
				e.printStackTrace();
			}
		}
		
		onFinish();
	}
	
	@Override
	public void onFacebookError(FacebookError e) {
		if(listener != null) {
			listener.onError(new SocializeException(e));
		}
		else {
			handleError(e);
		}
		
		onFinish();
	}
	
	@Override
	public void onError(DialogError e) {
		if(listener != null) {
			listener.onError(new SocializeException(e));
		}
		else {
			handleError(e);
		}
		
		onFinish();
	}

	@Override
	public void onCancel() {
		if(listener != null) {
			listener.onCancel();
		}
		else {
			Toast.makeText(context, "Request canceled", Toast.LENGTH_SHORT).show();
		}
		
		onFinish();
	}

	public abstract void onFinish();
	
	public abstract void handleError(Throwable error);
}
