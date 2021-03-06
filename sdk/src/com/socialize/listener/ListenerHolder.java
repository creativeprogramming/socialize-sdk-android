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
package com.socialize.listener;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.socialize.Socialize;
import com.socialize.log.SocializeLogger;

/**
 * Singleton (application scoped) container to retain a reference to a listener between activities.
 * @author Jason Polites
 */
public class ListenerHolder {

	private Map<String, SocializeListener> listeners = new TreeMap<String, SocializeListener>();
	
	private SocializeLogger logger;
	
	public void init() {
		Map<String, SocializeListener> statics = Socialize.STATIC_LISTENERS;
		if(!statics.isEmpty()) {
			Set<Entry<String, SocializeListener>> entrySet = statics.entrySet();
			for (Entry<String, SocializeListener> entry : entrySet) {
				
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Registering listener [" +
							entry.getKey() +
							"] from static scope");
				}
				
				listeners.put(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public void put(String key, SocializeListener listener) {
		listeners.put(key, listener);
	}
	
	@SuppressWarnings("unchecked")
	public <L extends SocializeListener> L get(String key) {
		return (L) listeners.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <L extends SocializeListener> L remove(String key) {
		return (L) listeners.remove(key);
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	
}