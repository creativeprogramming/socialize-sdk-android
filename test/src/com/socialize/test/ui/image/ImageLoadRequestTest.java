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
package com.socialize.test.ui.image;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.image.ImageLoadListener;
import com.socialize.ui.image.ImageLoadRequest;
import com.socialize.util.SafeBitmapDrawable;

/**
 * @author Jason Polites
 *
 */
public class ImageLoadRequestTest extends SocializeUnitTest {

	public void testEquality() {
		
		String url0 = "foobar0";
		String url1 = "foobar1";
		
		ImageLoadRequest request0 = new ImageLoadRequest();
		ImageLoadRequest request1 = new ImageLoadRequest();
		ImageLoadRequest request2 = new ImageLoadRequest();
		
		request0.setUrl(url0);
		request1.setUrl(url0);
		request2.setUrl(url1);
		
		assertTrue(request0.equals(request1));
		assertTrue(request1.equals(request0));
		
		assertFalse(request0.equals(request2));
		assertFalse(request1.equals(request2));
		
		assertFalse(request2.equals(request0));
		assertFalse(request2.equals(request1));
	}
	
	@UsesMocks({ImageLoadListener.class, SafeBitmapDrawable.class})
	public void testNotifyListeners() {
		
		ImageLoadListener listener0 = AndroidMock.createMock(ImageLoadListener.class);
		ImageLoadListener listener1 = AndroidMock.createMock(ImageLoadListener.class);
		SafeBitmapDrawable drawable = AndroidMock.createMock(SafeBitmapDrawable.class);
		
		final ImageLoadRequest request = new ImageLoadRequest() {
			@Override
			public boolean isCanceled() {
				return false;
			}
		};
		
		listener0.onImageLoad(request, drawable, true);
		listener1.onImageLoad(request, drawable, true);
		
		AndroidMock.replay(listener0, listener1);
		
		request.addListener(listener0);
		request.addListener(listener1);
		
		request.notifyListeners(drawable);
		
		AndroidMock.verify(listener0, listener1);
		
	}
	
}
