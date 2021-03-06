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
package com.socialize.auth.twitter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.ViewGroup.LayoutParams;

/**
 * @author Jason Polites
 *
 */
public class TwitterUtils {
	public AlertDialog showAuthDialog(Context context, TwitterAuthProviderInfo info, final TwitterAuthListener listener) {
		AlertDialog.Builder builder = newAlertDialogBuilder(context);
		
		builder.setTitle("Twitter Authentication").setCancelable(true).setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if(listener != null) {
					listener.onCancel();
				}
			}
		});
		
		TwitterAuthView view = new TwitterAuthView(context);
		view.setConsumerKey(info.getConsumerKey());
		view.setConsumerSecret(info.getConsumerSecret());
	
		view.init();

		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		view.setLayoutParams(params);
		builder.setView(view);
		
		AlertDialog dialog = builder.create();
		
		view.setTwitterAuthListener(new TwitterAuthDialogListener(dialog) {
			
			@Override
			public void onError(Dialog dialog, Exception e) {
				dialog.dismiss();
				if(listener != null) {
					listener.onError(e);
				}
			}
			
			@Override
			public void onCancel(Dialog dialog) {
				if(listener != null) {
					listener.onCancel();
				}
			}
			
			@Override
			public void onAuthSuccess(Dialog dialog, String token, String secret, String screenName, String userId) {
				dialog.dismiss();
				if(listener != null) {
					listener.onAuthSuccess(token, secret, screenName, userId);
				}
			}
		});
		
		view.authenticate();
		
		dialog.show();
		
		return dialog;
	}
	
	protected AlertDialog.Builder newAlertDialogBuilder(Context context) {
		return new AlertDialog.Builder(context);
	}
	
	protected TwitterAuthView newTwitterAuthView(Context context) {
		return new TwitterAuthView(context);
	}
}
