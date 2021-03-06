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
package com.socialize.api.action;

import java.util.ArrayList;
import java.util.List;

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityListListener;
import com.socialize.listener.entity.EntityListener;
import com.socialize.provider.SocializeProvider;

/**
 * @author Jason Polites
 *
 */
public class SocializeEntitySystem extends SocializeApi<Entity, SocializeProvider<Entity>> implements EntitySystem {

	public SocializeEntitySystem(SocializeProvider<Entity> provider) {
		super(provider);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.EntitySystem#addEntity(com.socialize.api.SocializeSession, com.socialize.entity.Entity, com.socialize.listener.entity.EntityListener)
	 */
	@Override
	public void addEntity(SocializeSession session, Entity entity, EntityListener listener) {
		List<Entity> list = new ArrayList<Entity>(1);
		list.add(entity);
		postAsync(session, ENDPOINT, list, listener);
	}


	/* (non-Javadoc)
	 * @see com.socialize.api.action.EnitySystem#addEntity(com.socialize.api.SocializeSession, java.lang.String, java.lang.String, com.socialize.listener.entity.EntityListener)
	 */
	@Deprecated
	@Override
	public void addEntity(SocializeSession session, String key, String name, EntityListener listener) {
		Entity entity = new Entity();
		entity.setKey(key);
		entity.setName(name);
		addEntity(session, entity, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.EnitySystem#getEntity(com.socialize.api.SocializeSession, java.lang.String, com.socialize.listener.entity.EntityListener)
	 */
	@Override
	public void getEntity(SocializeSession session, final String key, final EntityListener listener) {
		
		listAsync(session, ENDPOINT, key, null, 0, 1, new EntityListListener() {
			
			@Override
			public void onError(SocializeException error) {
				listener.onError(error);
			}
			
			@Override
			public void onList(ListResult<Entity> entities) {
				boolean is404 = false;
				if(entities != null) {
					List<Entity> items = entities.getItems();
					if(items != null && items.size() > 0) {
						listener.onGet(items.get(0));
					}
					else {
						is404 = true;
					}
				}
				else {
					is404 = true;
				}
				
				if(is404) {
					onError(new SocializeApiError(404, "No entity found with key [" +
							key +
							"]"));
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.EnitySystem#listEntities(com.socialize.api.SocializeSession, com.socialize.listener.entity.EntityListener, java.lang.String)
	 */
	@Override
	public void listEntities(SocializeSession session, EntityListener listener, String...keys) {
		listAsync(session, ENDPOINT, null, keys, 0, SocializeConfig.MAX_LIST_RESULTS, listener);
	}
	
}
