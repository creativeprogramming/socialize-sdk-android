package com.socialize.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.action.ActionType;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.SocializeListener;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.comment.OnCommentViewActionListener;
import com.socialize.util.Drawables;

@Deprecated
public class SocializeUI {

	private static final SocializeUI instance = new SocializeUI();
	
	public static final String LOG_KEY = "Socialize";
	
	public static final String USER_ID = "socialize.user.id";
	public static final String COMMENT_ID = "socialize.comment.id";
	
	public static final String ENTITY = "socialize.entity";
	
	@Deprecated
	public static final String ENTITY_KEY = "socialize.entity.key";
	
	@Deprecated
	public static final String ENTITY_NAME = "socialize.entity.name";
	
	@Deprecated
	public static final String ENTITY_URL_AS_LINK = "socialize.entity.entityKey.link";
	
	public static final String DEFAULT_USER_ICON = "default_user_icon.png";
	public static final String BG_ACCENT = "bg_accent.png";
	
	public static final Map<String, SocializeListener> STATIC_LISTENERS = new HashMap<String, SocializeListener>();
	
	private IOCContainer container;
	private Drawables drawables;
	
	private SocializeEntityLoader entityLoader;
	
	public static SocializeUI getInstance() {
		return instance;
	}
	
	public SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	public com.socialize.SocializeUI getSocializeUI() {
		return Socialize.getSocializeUI();
	}
	
	@Deprecated
	public void initSocialize(Context context) {
		String[] config = getConfig();
		getSocialize().init(context,config);
	}
	
	@Deprecated
	public void initSocializeAsync(Context context, final SocializeInitListener listener) {
		String[] config = getConfig();
		getSocialize().initAsync(context, listener, config);
	}
	
	@Deprecated
	protected String[] getConfig() {
		return Socialize.getSocialize().getSystem().getBeanConfig();
	}

	public void initUI(IOCContainer container) {
		this.container = container;
		if(container != null) {
			drawables = container.getBean("drawables");
		}
	}
	
	public void setContainer(IOCContainer container) {
		this.container = container;
	}

	public void destroy(Context context) {
		destroy(context, false);
	}
	
	public void destroy(Context context, boolean force) {
		getSocialize().destroy(force);
	}
	
	public View getView(String name) {
		return (View) container.getBean(name);
	}
	
	public Drawable getDrawable(String name, int density, boolean eternal) {
		return drawables.getDrawable(name, density, eternal);
	}
	
	public Drawable getDrawable(String name, boolean eternal) {
		return drawables.getDrawable(name, eternal);
	}
	
	/**
	 * Sets the credentials for your Socialize App.
	 * @param consumerKey Your consumer key, obtained via registration at http://getsocialize.com
	 * @param consumerSecret Your consumer secret, obtained via registration at http://getsocialize.com
	 */
	@Deprecated
	public void setSocializeCredentials(String consumerKey, String consumerSecret) {
		getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY, consumerKey);
		getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET, consumerSecret);
	}
	
	/**
	 * Sets the FB credentials for the current user if available.
	 * @param userId
	 * @param token
	 */
	@Deprecated
	public void setFacebookUserCredentials(String userId, String token) {
		getSocialize().getConfig().setProperty(SocializeConfig.FACEBOOK_USER_ID, userId);
		getSocialize().getConfig().setProperty(SocializeConfig.FACEBOOK_USER_TOKEN, token);
	}
	
	@Deprecated
	public void setDebugMode(boolean debug) {
		getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_DEBUG_MODE, String.valueOf(debug));
	}
	
	/**
	 * Sets the Facebook ID for FB authentication.  
	 * @param appId Your Facebook App Id, obtained from https://developers.facebook.com/
	 * @see "https://developers.facebook.com/"
	 */
	@Deprecated
	public void setFacebookAppId(String appId) {
		getSocialize().getConfig().setFacebookAppId(appId);
	}
	
	@Deprecated
	protected void setCustomProperty(String key, String value) {
		getSocialize().getConfig().setProperty(key, value);
	}
	
	/**
	 * Enables/disables Single Sign On for Facebook.
	 * @param enabled True if enabled.  Default is true.
	 */
	@Deprecated
	public void setFacebookSingleSignOnEnabled(boolean enabled) {
		getSocialize().getConfig().setFacebookSingleSignOnEnabled(enabled);
	}
	
	/**
	 * Returns true if a Facebook ID has been set.
	 * @return true if a Facebook ID has been set.
	 * @deprecated use Socialize instance.
	 */
	@Deprecated
	public boolean isFacebookSupported() {
		return getSocialize().isSupported(AuthProviderType.FACEBOOK);
	}
	
	@Deprecated
	public String getCustomConfigValue(String key) {
		return getSocialize().getConfig().getProperty(key);
	}
	
	/**
	 * Shows the comments list for the given entity.
	 * @param context
	 * @param entity
	 * @param listener
	 */
	@Deprecated
	public void showCommentView(Activity context, Entity entity, OnCommentViewActionListener listener) {
		getSocializeUI().showCommentView(context, entity, listener);
	}
	
	/**
	 * Shows the comments list for the given entity.
	 * @param context
	 * @param entity
	 */
	@Deprecated
	public void showCommentView(Activity context, Entity entity) {
		showCommentView(context, entity, null);
	}
	
	/**
	 * Shows the comments list for the given entity
	 * @param context
	 * @param entityKey
	 * @param entityName
	 * @param listener
	 * @deprecated
	 */
	@Deprecated
	public void showCommentView(Activity context, String entityKey, String entityName, OnCommentViewActionListener listener) {
		getSocializeUI().showCommentView(context, Entity.newInstance(entityKey, entityName), listener);
	}
	
	@Deprecated
	public void showCommentView(Activity context, String entityKey, String entityName, boolean entityKeyIsUrl, OnCommentViewActionListener listener) {
		getSocializeUI().showCommentView(context, Entity.newInstance(entityKey, entityName), listener);
	}
	
	@Deprecated
	public void showCommentView(Activity context, String entityKey, OnCommentViewActionListener listener) {
		getSocializeUI().showCommentView(context, Entity.newInstance(entityKey, null), listener);
	}

	@Deprecated
	public void showCommentView(Activity context, String entityKey) {
		getSocializeUI().showCommentView(context, Entity.newInstance(entityKey, null));
	}
	
	@Deprecated
	public void showCommentView(Activity context, String entityKey, String entityName, boolean entityKeyIsUrl) {
		getSocializeUI().showCommentView(context, Entity.newInstance(entityKey, entityName));
	}
	
	@Deprecated
	public void showUserProfileView(Activity context, String userId) {
		getSocializeUI().showUserProfileView(context, Long.valueOf(userId));
	}
	
	public void showUserProfileViewForResult(Activity context, String userId, int requestCode) {
		getSocializeUI().showUserProfileViewForResult(context, Long.valueOf(userId), requestCode);
	}
	
	/**
	 * Displays the detail view for a single Socialize action.
	 * @param context
	 * @param user
	 * @param action
	 * @param requestCode
	 */
	public void showActionDetailViewForResult(Activity context, User user, SocializeAction action, int requestCode) {
		getSocializeUI().showActionDetailViewForResult(context, user, action, requestCode);
	}	
	
	/**
	 * 
	 * @param context
	 * @param userId
	 * @param commentId
	 * @param requestCode
	 * @deprecated Use showActionDetailViewForResult
	 */
	@Deprecated
	public void showCommentDetailViewForResult(Activity context, String userId, String commentId, int requestCode) {
		
		User user = new User();
		user.setId(Long.parseLong(userId));
		
		SocializeAction action = new SocializeAction() {
			
			private static final long serialVersionUID = 4117561505244633323L;

			@Override
			public ActionType getActionType() {
				return ActionType.COMMENT;
			}
		};
		
		action.setId(Long.parseLong(commentId));
		showActionDetailViewForResult(context, user, action, requestCode);
	}
	
	protected Intent newIntent(Activity context, Class<?> cls) {
		return new Intent(context, cls);
	}
	
	protected RelativeLayout newRelativeLayout(Activity parent) {
		return new RelativeLayout(parent);
	}
	
	protected ActionBarView newActionBarView(Activity parent) {
		return new ActionBarView(parent);
	}
	
	protected LayoutParams newLayoutParams(int width, int height) {
		return new LayoutParams(width, height);
	}
	
	protected ScrollView newScrollView(Activity parent) {
		return new ScrollView(parent);
	}
	
	@Deprecated
	public void setEntityName(Activity context, String name) {
		Intent intent = context.getIntent();
		Bundle extras = getExtras(intent);
		extras.putString(ENTITY_NAME, name);
		intent.putExtras(extras);
	}
	
	@Deprecated
	public void setUseEntityUrlAsLink(Activity context, boolean asLink) {
		Intent intent = context.getIntent();
		Bundle extras = getExtras(intent);
		extras.putBoolean(ENTITY_URL_AS_LINK, asLink);
		intent.putExtras(extras);
	}

	/**
	 * 
	 * @param context
	 * @param intent
	 * @param entityKey
	 * @deprecated Use setEntityKey
	 */
	@Deprecated
	public void setEntityUrl(Activity context, Intent intent, String entityKey) {
		setEntityKey(context, intent, entityKey);
	}
	
	/**
	 * 
	 * @param context
	 * @param entityKey
	 * @deprecated Use setEntityKey
	 */
	@Deprecated
	public void setEntityUrl(Activity context, String entityKey) {
		setEntityKey(context, entityKey);
	}
	
	@Deprecated
	public void setEntityKey(Activity context, Intent intent, String entityKey) {
		Bundle extras = getExtras(intent);
		extras.putString(ENTITY_KEY, entityKey);
		intent.putExtras(extras);
	}
	
	@Deprecated
	public void setEntityKey(Activity context, String entityKey) {
		Intent intent = context.getIntent();
		setEntityKey(context, intent, entityKey);
	}	
	
	@Deprecated
	public void setUserId(Activity context, String userId) {
		Intent intent = context.getIntent();
		Bundle extras = getExtras(intent);
		extras.putString(USER_ID, userId);
		intent.putExtras(extras);
	}
	
	@Deprecated
	public View showActionBar(Activity parent, int resId, String entityKey) {
		return getSocializeUI().showActionBar(parent, resId, Entity.newInstance(entityKey, null));
	}
	
	@Deprecated
	public View showActionBar(Activity parent, int resId, String entityKey, ActionBarListener listener) {
		return getSocializeUI().showActionBar(parent, resId, Entity.newInstance(entityKey, null), listener);
	}
	
	@Deprecated
	public View showActionBar(Activity parent, int resId, String entityKey, ActionBarOptions options) {
		return getSocializeUI().showActionBar(parent, resId, Entity.newInstance(entityKey, null), options, null);
	}
		
	@Deprecated
	public View showActionBar(Activity parent, int resId, String entityKey, ActionBarOptions options, ActionBarListener listener) {
		return getSocializeUI().showActionBar(parent, resId, Entity.newInstance(entityKey, null), options, listener);
	}
		
	
	@Deprecated
	public View showActionBar(Activity parent, View original, String entityKey) {
		return getSocializeUI().showActionBar(parent, original, Entity.newInstance(entityKey, null));
	}
	
	@Deprecated
	public View showActionBar(Activity parent, View original, String entityKey, ActionBarListener listener) {
		return getSocializeUI().showActionBar(parent, original, Entity.newInstance(entityKey, null), listener);
	}
	
	@Deprecated
	public View showActionBar(Activity parent, View original, String entityKey, ActionBarOptions options, ActionBarListener listener) {
		return getSocializeUI().showActionBar(parent, original, Entity.newInstance(entityKey, null), options, listener);
	}
	
	@Deprecated
	public View showActionBar(Activity parent, View original, String entityKey, String entityName, boolean isEntityKeyUrl, boolean addScrollView, ActionBarListener listener) {
		ActionBarOptions options = new ActionBarOptions();
		options.setAddScrollView(addScrollView);
		return getSocializeUI().showActionBar(parent, original, Entity.newInstance(entityKey, entityName), options, listener);
	}
	
	@Deprecated
	public View showActionBar(Activity parent, int resId, String entityKey, String entityName, boolean isEntityKeyUrl, boolean addScrollView, ActionBarListener listener) {
		ActionBarOptions options = new ActionBarOptions();
		options.setAddScrollView(addScrollView);
		return getSocializeUI().showActionBar(parent, resId, Entity.newInstance(entityKey, entityName), options, listener);
	}

	@Deprecated
	public View showActionBar(Activity parent, View original, Entity entity) {
		return getSocializeUI().showActionBar(parent, original, entity);
	}
	
	@Deprecated
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarListener listener) {
		return Socialize.getSocializeUI().showActionBar(parent, original, entity, listener);
	}
	
	@Deprecated
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options, ActionBarListener listener) {
		return Socialize.getSocializeUI().showActionBar(parent, original, entity, options, listener);
	}
	
	@Deprecated
	public View showActionBar(Activity parent, int resId, Entity entity) {
		return Socialize.getSocializeUI().showActionBar(parent, resId, entity);
	}
	
	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarListener listener) {
		return showActionBar(parent, resId, entity, true, listener);
	}
	
	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options) {
		return Socialize.getSocializeUI().showActionBar(parent, resId, entity, options);
	}
		
	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options, ActionBarListener listener) {
		return Socialize.getSocializeUI().showActionBar(parent, resId, entity, options, listener);
	}
	
	protected View showActionBar(Activity parent, int resId, Entity entity, boolean addScrollView, ActionBarListener listener) {
		ActionBarOptions options = new ActionBarOptions();
		options.setAddScrollView(addScrollView);
		return Socialize.getSocializeUI().showActionBar(parent, resId, entity, options, listener);
	}
	
	protected Bundle getExtras(Intent intent) {
		Bundle extras = intent.getExtras();
		if(extras == null) {
			extras = new Bundle();
		}	
		return extras;
	}
	
	/**
	 * EXPERT ONLY (Not documented)
	 * @param beanOverride
	 */
	void setBeanOverrides(String...beanOverrides) {
		throw new UnsupportedOperationException("Not supported");
	}
	
	/**
	 * EXPERT ONLY (Not documented)
	 * @return
	 */
	IOCContainer getContainer() {
		return container;
	}
	
	public SocializeEntityLoader getEntityLoader() {
		return entityLoader;
	}

	public void setEntityLoader(SocializeEntityLoader entityLoader) {
		this.entityLoader = entityLoader;
	}

}
