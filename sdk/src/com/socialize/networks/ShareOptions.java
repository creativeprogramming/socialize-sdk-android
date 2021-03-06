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
package com.socialize.networks;

/**
 * Allows for the specification of options for sharing.
 * @author Jason Polites
 */
public class ShareOptions {
	
	private boolean shareLocation;
	private boolean autoAuth = false;
	
	private SocialNetwork[] shareTo;
	
	private SocialNetworkListener listener;
	
	/**
	 * If true and if available, the user's location is shared.
	 * @return
	 */
	public boolean isShareLocation() {
		return shareLocation;
	}
	
	/**
	 * If true and if available, the user's location is shared.
	 * @param shareLocation
	 */
	public void setShareLocation(boolean shareLocation) {
		this.shareLocation = shareLocation;
	}
	
	public SocialNetwork[] getShareTo() {
		return shareTo;
	}

	public void setShareTo(SocialNetwork...shareTo) {
		this.shareTo = shareTo;
	}

	public SocialNetworkListener getListener() {
		return listener;
	}

	/**
	 * Allows for the capture of events when sharing on social networks like facebook.
	 * @param listener
	 */
	public void setListener(SocialNetworkListener listener) {
		this.listener = listener;
	}

	/**
	 * @param autoPostToFacebook
	 * @deprecated
	 */
	@Deprecated
	public void setShareFacebook(boolean autoPostToFacebook) {
		setShareTo(SocialNetwork.FACEBOOK);
	}
	
	public boolean isShareTo(SocialNetwork destination) {
		if(shareTo != null) {
			for (SocialNetwork d : shareTo) {
				if(d.equals(destination)) {
					return true;
				}
			}
		}
		
		return false;
	}
	

	public boolean isAutoAuth() {
		return autoAuth;
	}

	/**
	 * If set to true any actions which can propagate will automatically attempt authentication on the target social network
	 * @param autoAuth
	 */
	public void setAutoAuth(boolean autoAuth) {
		this.autoAuth = autoAuth;
	}

	@Deprecated
	public boolean isShareFacebook() {
		return isShareTo(SocialNetwork.FACEBOOK);
	}	
}
