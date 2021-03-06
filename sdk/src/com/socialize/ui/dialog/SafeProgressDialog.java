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
package com.socialize.ui.dialog;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * @author Jason Polites
 *
 */
public class SafeProgressDialog extends ProgressDialog {

	public SafeProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public SafeProgressDialog(Context context) {
		super(context);
	}

	@Override
	public void dismiss() {
		try {
			if(isShowing()) {
				super.dismiss();
			}
		}
		catch (Exception ignore) {
			ignore.printStackTrace();
		}
		
	}

	@Override
	public void show() {
		try {
			super.show();
		}
		catch (Exception ignore) {
			ignore.printStackTrace();
		}
	}
	
	
	public static ProgressDialog show(Context context, String title, String message) {
		ProgressDialog dialog = makeDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.show();
		return dialog;
	}
	
	protected static ProgressDialog makeDialog(Context context) {
		return new SafeProgressDialog(context);
	}

}
