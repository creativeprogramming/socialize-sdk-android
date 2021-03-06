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
package com.socialize.ui.actionbar;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.Entity;
import com.socialize.entity.EntityStats;
import com.socialize.entity.Like;
import com.socialize.entity.View;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.ShareOptions;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.actionbar.OnActionBarEventListener.ActionBarEvent;
import com.socialize.ui.cache.CacheableEntity;
import com.socialize.ui.cache.EntityCache;
import com.socialize.ui.dialog.ProgressDialogFactory;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 */
public class ActionBarLayoutView extends BaseView {

	private ActionBarButton commentButton;
	private ActionBarButton likeButton;
	private ActionBarButton shareButton;
	private ActionBarTicker ticker;
	
	private ActionBarItem viewsItem;
	private ActionBarItem commentsItem;
	private ActionBarItem likesItem;
	private ActionBarItem sharesItem;
	
	private Drawables drawables;
	private EntityCache entityCache;
	private SocializeLogger logger;
	
	private Drawable likeIcon;
	private Drawable likeIconHi;
	
	private IBeanFactory<ActionBarButton> buttonFactory;
	private IBeanFactory<ActionBarTicker> tickerFactory;
	private IBeanFactory<ActionBarItem> itemFactory;
	
	private ProgressDialogFactory progressDialogFactory;
	
	private String entityKey;
	
	private DeviceUtils deviceUtils;
	
	private ActionBarView actionBarView;
	
	final String loadingText = "...";
	
	private OnActionBarEventListener onActionBarEventListener;
	
	public ActionBarLayoutView(Activity context, ActionBarView actionBarView) {
		super(context);
		this.actionBarView = actionBarView;
	}
	
	public void init() {
		
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("init called on " + getClass().getSimpleName());
		}
		
		likeIcon = drawables.getDrawable("icon_like.png");
		likeIconHi = drawables.getDrawable("icon_like_hi.png");

		Drawable commentIcon = drawables.getDrawable("icon_comment.png");
		Drawable viewIcon = drawables.getDrawable("icon_view.png");
		Drawable shareIcon = drawables.getDrawable("icon_share.png");
		
		Drawable commentBg = drawables.getDrawable("action_bar_button_hi.png#comment", true, false, true);
		Drawable shareBg = drawables.getDrawable("action_bar_button_hi.png#share", true, false, true);
		Drawable likeBg = drawables.getDrawable("action_bar_button_hi.png#like", true, false, true);
		
		int width = ActionBarView.ACTION_BAR_BUTTON_WIDTH;
		
		int likeWidth = width - 5;
		int commentWidth = width + 15;
		int shareWidth = width- 5;
		
		ticker = tickerFactory.getBean();
		
		viewsItem = itemFactory.getBean();
		commentsItem = itemFactory.getBean();
		likesItem = itemFactory.getBean();
		sharesItem = itemFactory.getBean();
		
		viewsItem.setIcon(viewIcon);
		commentsItem.setIcon(commentIcon);
		likesItem.setIcon(likeIcon);
		sharesItem.setIcon(shareIcon);
		
		ticker.addTickerView(viewsItem);
		ticker.addTickerView(commentsItem);
		ticker.addTickerView(likesItem);
		ticker.addTickerView(sharesItem);
		
		likeButton = buttonFactory.getBean();
		commentButton = buttonFactory.getBean();
		shareButton = buttonFactory.getBean();
		
		commentButton.setIcon(commentIcon);
		commentButton.setBackground(commentBg);
		
		likeButton.setIcon(likeIcon);
		likeButton.setBackground(likeBg);
		
		shareButton.setIcon(shareIcon);
		shareButton.setBackground(shareBg);
		
		commentButton.setListener(new ActionBarButtonListener() {
			@Override
			public void onClick(ActionBarButton button) {
				if(onActionBarEventListener != null) {
					onActionBarEventListener.onClick(actionBarView, ActionBarEvent.COMMENT);
				}
				Socialize.getSocializeUI().showCommentView(getActivity(), actionBarView.getEntity());
			}
		});
		
		likeButton.setListener(new ActionBarButtonListener() {
			@Override
			public void onClick(ActionBarButton button) {
				if(onActionBarEventListener != null) {
					onActionBarEventListener.onClick(actionBarView, ActionBarEvent.LIKE);
				}
				postLike(likeButton);
			}
		});
		
		shareButton.setListener(new ActionBarButtonListener() {
			@Override
			public void onClick(ActionBarButton button) {
				if(onActionBarEventListener != null) {
					onActionBarEventListener.onClick(actionBarView, ActionBarEvent.SHARE);
				}
			}
		});
		
		ticker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(android.view.View v) {
				if(onActionBarEventListener != null) {
					onActionBarEventListener.onClick(actionBarView, ActionBarEvent.VIEW);
				}				
				ticker.skipToNext();
			}
		});
		
		LayoutParams masterParams = new LayoutParams(LayoutParams.FILL_PARENT, deviceUtils.getDIP(ActionBarView.ACTION_BAR_HEIGHT));
		masterParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		setLayoutParams(masterParams);
		
		viewsItem.init();
		commentsItem.init();
		likesItem.init();
		sharesItem.init();
		
		ticker.init(LayoutParams.FILL_PARENT, 1.0f);
		likeButton.init(likeWidth, 0.0f);
		commentButton.init(commentWidth, 0.0f);
		shareButton.init(shareWidth, 0.0f);
		
		viewsItem.setText(loadingText);
		commentsItem.setText(loadingText);
		likesItem.setText(loadingText);
		sharesItem.setText(loadingText);
		
		likeButton.setText(loadingText);
		shareButton.setText("Share");
		commentButton.setText("Comment");
		
		addView(ticker);
		addView(likeButton);
		addView(shareButton);
		addView(commentButton);
	}
	
	@Override
	public void onViewLoad() {
		super.onViewLoad();
		doLoadSequence(false);
	}
	
	@Override
	public void onViewUpdate() {
		super.onViewUpdate();
		doLoadSequence(true);
	}
	
	protected void doLoadSequence(boolean reload) {
		final Entity userProvidedEntity = actionBarView.getEntity();
		this.entityKey = userProvidedEntity.getKey();
		
		if(reload) {
			ticker.resetTicker();
			viewsItem.setText(loadingText);
			commentsItem.setText(loadingText);
			likesItem.setText(loadingText);
			sharesItem.setText(loadingText);
			likeButton.setText(loadingText);
			
			if(onActionBarEventListener != null) {
				onActionBarEventListener.onUpdate(actionBarView);
			}	
		}
		else {
			ticker.startTicker();
			
			if(onActionBarEventListener != null) {
				onActionBarEventListener.onLoad(actionBarView);
			}	
		}
		
		updateEntity(userProvidedEntity, reload);
	}
	
	protected void updateEntity(final Entity entity, boolean reload) {

		CacheableEntity localEntity = getLocalEntity();
		
		if(localEntity == null) {
			getSocialize().view(getActivity(), entity, new ViewAddListener() {
				@Override
				public void onError(SocializeException error) {
					error.printStackTrace();
					getLike(entity.getKey());
				}
				
				@Override
				public void onCreate(View view) {
					// Entity will be set in like
					getLike(entity.getKey());
				}
			});
		}
		else {
			if(reload) {
				if(localEntity.isLiked()) {
					getLike(entity.getKey());
				}
				else {
					getEntity(entity.getKey());
				}
			}
			else {
				// Just set everything from the cached version
				setEntityData(localEntity);
			}
		}
	}
	
	public void reload() {
		final Entity realEntity = actionBarView.getEntity();
		entityCache.remove(realEntity.getKey());
		doLoadSequence(true);
		getLike(realEntity.getKey());
	}

	protected void postLike(final ActionBarButton button) {
		final CacheableEntity localEntity = getLocalEntity();
		
		if(localEntity != null) {
			
			button.showLoading();
			
			if(localEntity.isLiked()) {
				// Unlike
				getSocialize().unlike(localEntity.getLikeId(), new LikeDeleteListener() {
					
					@Override
					public void onError(SocializeException error) {
						logError("Error deleting like", error);
						localEntity.setLiked(false);
						setEntityData(localEntity);
						button.hideLoading();
					}
					
					@Override
					public void onDelete() {
						localEntity.setLiked(false);
						setEntityData(localEntity);
						button.hideLoading();
						if(onActionBarEventListener != null) {
							onActionBarEventListener.onPostUnlike(actionBarView);
						}
					}
				});
			}
			else {
				// Like
				ShareOptions options = new ShareOptions();
				
				if(getSocialize().getSession().getUser().isAutoPostLikesFacebook()) {
					options.setShareTo(SocialNetwork.FACEBOOK);
				}
				
				getSocialize().like(getActivity(), localEntity.getEntity(), options, new LikeAddListener() {
					
					@Override
					public void onError(SocializeException error) {
						logError("Error posting like", error);
						button.hideLoading();
					}
					
					@Override
					public void onCreate(Like entity) {
						localEntity.setLiked(true);
						localEntity.setLikeId(entity.getId());
						button.hideLoading();
						setEntityData(localEntity);
						
						if(onActionBarEventListener != null) {
							onActionBarEventListener.onPostLike(actionBarView, entity);
						}
					}
				});
			}
		}
	}
	
	protected CacheableEntity getLocalEntity() {
		return entityCache.get(this.entityKey);
	}
	
	protected CacheableEntity setLocalEntity(Entity entity) {
		return entityCache.putEntity(entity);
	}
	
	protected void getLike(final String entityKey) {
		
		// Get the like
		getSocialize().getLike(entityKey, new LikeGetListener() {
			
			@Override
			public void onGet(Like like) {
				if(like != null) {
					CacheableEntity putEntity = setLocalEntity(like.getEntity());
					putEntity.setLiked(true);
					putEntity.setLikeId(like.getId());
					setEntityData(putEntity);
					
					if(onActionBarEventListener != null) {
						onActionBarEventListener.onGetLike(actionBarView, like);
					}
					
					if(onActionBarEventListener != null) {
						onActionBarEventListener.onGetEntity(actionBarView, like.getEntity());
					}	
				}
				else {
					getEntity(entityKey);
				}
			}
			
			@Override
			public void onError(SocializeException error) {
				if(error instanceof SocializeApiError) {
					if(((SocializeApiError)error).getResultCode() == 404) {
						// no like
						getEntity(entityKey);
						// Don't log error
						return;
					}
				}
				
				logError("Error retrieving entity data", error);
			}
		});
	}
	
	protected void getEntity(String entityKey) {
		getSocialize().getEntity(entityKey, new EntityGetListener() {
			@Override
			public void onGet(Entity entity) {
				CacheableEntity putEntity = setLocalEntity(entity);
				setEntityData(putEntity);
				
				if(onActionBarEventListener != null) {
					onActionBarEventListener.onGetEntity(actionBarView, entity);
				}	
			}
			
			@Override
			public void onError(SocializeException error) {
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Error retrieving entity data.  This may be ok if the entity is new", error);
				}
			}
		});
	}
	
	protected void setEntityData(CacheableEntity ce) {
		Entity entity = ce.getEntity();
		
		// Set the entity back on the parent action bar
//		if(actionBarView.getEntity() != null) {
//			// TODO: Remove this once meta data is persisted
//			entity.setMetaData(actionBarView.getEntity().getMetaData());
//		}
		
		actionBarView.setEntity(entity);
		
		EntityStats stats = entity.getEntityStats();
		
		if(stats != null) {
			viewsItem.setText(getCountText(stats.getViews()));
			commentsItem.setText(getCountText(stats.getComments()));
			likesItem.setText(getCountText(stats.getLikes() + ((ce.isLiked()) ? 1 : 0)));
			sharesItem.setText(getCountText(stats.getShares()));
		}
		
		if(ce.isLiked()) {
			likeButton.setText("Unlike");
			likeButton.setIcon(likeIconHi);
		}
		else {
			likeButton.setText("Like");
			likeButton.setIcon(likeIcon);
		}
	}
	
	protected String getCountText(Integer value) {
		String viewText = "";
		int iVal = value.intValue();
		if(iVal > 999) {
			viewText = "999+";
		}
		else {
			viewText = value.toString();
		}
		return viewText;
	}
	
	protected void logError(String msg, Exception error) {
		if(logger != null) {
			logger.error(msg, error);
		}
		else {
			error.printStackTrace();	
		}
	}
	
	// So we can mock for tests
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	public ActionBarButton getShareButton() {
		return shareButton;
	}

	public ActionBarButton getCommentButton() {
		return commentButton;
	}

	public ActionBarButton getLikeButton() {
		return likeButton;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setButtonFactory(IBeanFactory<ActionBarButton> buttonFactory) {
		this.buttonFactory = buttonFactory;
	}

	public void setEntityCache(EntityCache entityCache) {
		this.entityCache = entityCache;
	}

	public ProgressDialogFactory getProgressDialogFactory() {
		return progressDialogFactory;
	}

	public void setProgressDialogFactory(ProgressDialogFactory progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setTickerFactory(IBeanFactory<ActionBarTicker> tickerFactory) {
		this.tickerFactory = tickerFactory;
	}

	public void setItemFactory(IBeanFactory<ActionBarItem> itemFactory) {
		this.itemFactory = itemFactory;
	}

	public void stopTicker() {
		ticker.stopTicker();
	}

	public void startTicker() {
		ticker.startTicker();
	}

	public void setOnActionBarEventListener(OnActionBarEventListener onActionBarEventListener) {
		this.onActionBarEventListener = onActionBarEventListener;
	}
}
