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
package com.socialize.ui.profile;

import android.graphics.Bitmap;

/**
 * @author Jason Polites
 *
 */
public class UserProfile {

	private String fullName;
	private String firstName;
	private String lastName;
	
	private Bitmap image;
	
	private String encodedImage;
	
	private boolean autoPostLikesFacebook = true;
	private boolean autoPostCommentsFacebook = true;
	private boolean notificationsEnabled = true;
	
	private boolean autoPostFacebook;
	
	public String getFullName() {
		if(fullName == null) {
			return joinName();
		}
		return fullName;
	}

	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
	
	public String getFirstName() {
		if(firstName == null) splitName();
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		if(lastName == null) splitName();
		
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getEncodedImage() {
		return encodedImage;
	}
	
	public void setEncodedImage(String encodedImage) {
		this.encodedImage = encodedImage;
	}
	protected void splitName() {
		if(fullName != null) {
			String name = fullName;
			
			firstName = name;
			
			// Split the name.
			name = name.trim();
			
			String[] names = name.split("\\s+");
			
			if(names.length > 1) {
				firstName = names[0];
				
				// Last name is everything else
				lastName = name.substring(firstName.length(), name.length()).trim();
			}
		}
	}
	
	protected String joinName() {
		if (firstName != null || lastName != null) {
			if(firstName == null) {
				fullName = lastName;
			}
			else if (lastName == null) {
				fullName = firstName;
			}
			else {
				fullName = firstName + " " + lastName;
			}
		}
		
		return fullName;
	}
	
	@Deprecated
	public boolean isAutoPostFacebook() {
		return autoPostFacebook;
	}
	@Deprecated
	public void setAutoPostFacebook(boolean autoPostFacebook) {
		this.autoPostFacebook = autoPostFacebook;
	}

	public boolean isAutoPostLikesFacebook() {
		return autoPostLikesFacebook;
	}

	public void setAutoPostLikesFacebook(boolean autoPostLikesFacebook) {
		this.autoPostLikesFacebook = autoPostLikesFacebook;
	}

	public boolean isAutoPostCommentsFacebook() {
		return autoPostCommentsFacebook;
	}

	public void setAutoPostCommentsFacebook(boolean autoPostCommentsFacebook) {
		this.autoPostCommentsFacebook = autoPostCommentsFacebook;
	}

	public boolean isNotificationsEnabled() {
		return notificationsEnabled;
	}

	public void setNotificationsEnabled(boolean notificationsEnabled) {
		this.notificationsEnabled = notificationsEnabled;
	}
}
