package com.socialize.ui.comment;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.Subscription;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.subscription.SubscriptionAddListener;
import com.socialize.listener.subscription.SubscriptionGetListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.ShareOptions;
import com.socialize.networks.SocialNetwork;
import com.socialize.notifications.NotificationType;
import com.socialize.ui.auth.AuthRequestDialogFactory;
import com.socialize.ui.auth.AuthRequestListener;
import com.socialize.ui.dialog.DialogFactory;
import com.socialize.ui.header.SocializeHeader;
import com.socialize.ui.slider.ActionBarSliderFactory;
import com.socialize.ui.slider.ActionBarSliderFactory.ZOrder;
import com.socialize.ui.slider.ActionBarSliderView;
import com.socialize.ui.view.CustomCheckbox;
import com.socialize.ui.view.LoadingListView;
import com.socialize.util.AppUtils;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;
import com.socialize.view.BaseView;

public class CommentListView extends BaseView {

	private int defaultGrabLength = 20;
	private CommentAdapter commentAdapter;
	private boolean loading = true; // Default to true
	
	private Entity entity;
	
	private int startIndex = 0;
	private int endIndex = defaultGrabLength;
	
	private SocializeLogger logger;
	private DialogFactory<ProgressDialog> progressDialogFactory;
	private DialogFactory<AlertDialog> alertDialogFactory;
	private Drawables drawables;
	private AppUtils appUtils;
	private ProgressDialog dialog = null;
	
	private IBeanFactory<SocializeHeader> commentHeaderFactory;
	private IBeanFactory<CommentEditField> commentEditFieldFactory;
	private IBeanFactory<LoadingListView> commentContentViewFactory;
	private IBeanFactory<CustomCheckbox> notificationEnabledOptionFactory;
	
	private View field;
	private SocializeHeader header;
	private LoadingListView content;
	private IBeanFactory<AuthRequestDialogFactory> authRequestDialogFactory;
	
	private IBeanFactory<CommentEntrySliderItem> commentEntryFactory;
	
	private ActionBarSliderView slider;
	private ActionBarSliderFactory<ActionBarSliderView> sliderFactory;
	
	private RelativeLayout layoutAnchor;
	private ViewGroup sliderAnchor;
	private CustomCheckbox notifyBox;
	
	private CommentEntrySliderItem commentEntryPage;
	
	private OnCommentViewActionListener onCommentViewActionListener;
	
	public CommentListView(Context context, Entity entity) {
		super(context);
		this.entity = entity;
	}
	
	@Deprecated
	public CommentListView(Context context, String entityKey, String entityName, boolean useLink) {
		this(context, Entity.newInstance(entityKey, entityName));
	}
	
	@Deprecated
	public CommentListView(Context context, String entityKey) {
		this(context, entityKey, null, true);
	}	
	
	@Deprecated
	public CommentListView(Context context) {
		super(context);
	}

	public void init() {

		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);

		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(fill);
		setBackgroundDrawable(drawables.getDrawable("crosshatch.png", true, true, true));
		setPadding(0, 0, 0, 0);
		
		layoutAnchor = new RelativeLayout(getContext());
		
		LayoutParams innerParams = new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		
		layoutAnchor.setLayoutParams(innerParams);
		
		LinearLayout top = new LinearLayout(getContext());
		LinearLayout middle = new LinearLayout(getContext());
		sliderAnchor = new LinearLayout(getContext());
		
		int topId = getNextViewId(getParentView());
		int middleId = getNextViewId(getParentView());
		int bottomId = getNextViewId(getParentView());
		
		top.setPadding(0, 0, 0, 0);
		middle.setPadding(0, 0, 0, 0);
		sliderAnchor.setPadding(0, 0, 0, 0);
		
		top.setId(topId);
		middle.setId(middleId);
		sliderAnchor.setId(bottomId);
		
		RelativeLayout.LayoutParams topParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams middleParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		topParams.setMargins(0, 0, 0, 0);
		middleParams.setMargins(0, 0, 0, 0);
		bottomParams.setMargins(0, 0, 0, 0);	
		
		topParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		middleParams.addRule(RelativeLayout.BELOW, topId);
		middleParams.addRule(RelativeLayout.ABOVE, bottomId);		
		
		top.setLayoutParams(topParams);
		middle.setLayoutParams(middleParams);
		sliderAnchor.setLayoutParams(bottomParams);

		header = commentHeaderFactory.getBean();
		field = commentEditFieldFactory.getBean();
		content = commentContentViewFactory.getBean();
		
		if(commentEntryFactory != null) {
			commentEntryPage = commentEntryFactory.getBean(getCommentAddListener());
		}
		
		boolean notificationsAvailable = appUtils.isNotificationsAvailable(getContext());		
		
		if(notificationsAvailable) {
			notifyBox = notificationEnabledOptionFactory.getBean();
			notifyBox.setEnabled(false); // Start disabled
			notifyBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					doNotificationStatusSave();
				}
			});
			
			User user = Socialize.getSocialize().getSession().getUser();
			
			if(user.isNotificationsEnabled()) {
				notifyBox.setVisibility(View.VISIBLE);
			}
			else {
				notifyBox.setVisibility(View.GONE);
			}
			
			
			sliderAnchor.addView(notifyBox);
		}		
		
		content.setListAdapter(commentAdapter);
		content.setScrollListener(getCommentScrollListener());

		top.addView(header);
		middle.addView(content);
		
		sliderAnchor.addView(field);
		
		layoutAnchor.addView(top);
		layoutAnchor.addView(middle);
		layoutAnchor.addView(sliderAnchor);
		
		addView(layoutAnchor);
	}
	
	protected CommentScrollListener getCommentScrollListener() {
		return new CommentScrollListener(new CommentScrollCallback() {
			@Override
			public void onGetNextSet() {
				getNextSet();
			}
			
			@Override
			public boolean isLoading() {
				return loading;
			}

			@Override
			public boolean hasMoreItems() {
				return !commentAdapter.isLast();
			}
		});
	}
	
	protected CommentAddButtonListener getCommentAddListener() {
		return new CommentAddButtonListener(getContext(), new CommentButtonCallback() {
			
			@Override
			public void onError(Context context, SocializeException e) {
				showError(getContext(), e);
				
				if(onCommentViewActionListener != null) {
					onCommentViewActionListener.onError(e);
				}				
			}

			@Override
			public void onCancel() {
				if(slider != null) {
					slider.close();
				}
			}

			@Override
			public void onComment(String text, boolean autoPostToFacebook, boolean shareLocation, boolean subscribe) {
				
				text = StringUtils.replaceNewLines(text, 3, 2);
				
				if(!getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
					// Check that FB is enabled for this installation
					if(getSocialize().isSupported(AuthProviderType.FACEBOOK)) {
						AuthRequestDialogFactory dialog = authRequestDialogFactory.getBean();
						dialog.create(getContext(), getCommentAuthListener(text, autoPostToFacebook, shareLocation, subscribe)).show();
					}
					else {
						// Just post as anon
						doPostComment(text, false, shareLocation, subscribe);
					}
				}
				else {
					doPostComment(text, autoPostToFacebook, shareLocation, subscribe);
				}
			}
		});
	}
	
	protected AuthRequestListener getCommentAuthListener(final String text, final boolean autoPostToFacebook, final boolean shareLocation, final boolean subscribe) {
		return new AuthRequestListener() {
			@Override
			public void onResult(Dialog dialog) {
				doPostComment(text, autoPostToFacebook, shareLocation, subscribe);
			}
		};
	}

	public void doPostComment(String text, boolean autoPostToFacebook, boolean shareLocation, final boolean subscribe) {
		
		dialog = progressDialogFactory.show(getContext(), "Posting comment", "Please wait...");
		
		ShareOptions options = newShareOptions();
		
		if(autoPostToFacebook) {
			options.setShareTo(SocialNetwork.FACEBOOK);
		}		
		
		options.setShareLocation(shareLocation);
		
		Comment comment = newComment();
		comment.setText(text);
		comment.setNotificationsEnabled(subscribe);
		comment.setEntity(entity);
		
		getSocialize().addComment(getActivity(), comment, null, options, new CommentAddListener() {

			@Override
			public void onError(SocializeException error) {
				showError(getContext(), error);
				if(dialog != null) {
					dialog.dismiss();
				}
				
				if(onCommentViewActionListener != null) {
					onCommentViewActionListener.onError(error);
				}						
			}

			@Override
			public void onCreate(Comment entity) {
				List<Comment> comments = commentAdapter.getComments();
				if(comments != null) {
					comments.add(0, entity);
				}
				else {
					// TODO: handle error!
				}
				
				startIndex++;
				endIndex++;
				
				if(slider != null) {
					slider.clearContent();
					slider.close();
				}
				
				commentAdapter.setTotalCount(commentAdapter.getTotalCount() + 1);
				commentAdapter.notifyDataSetChanged();
				
				header.setText(commentAdapter.getTotalCount() + " Comments");
				
				content.scrollToTop();
				
				if(dialog != null) {
					dialog.dismiss();
				}
				
				if(notifyBox != null) {
					notifyBox.setChecked(subscribe);
				}
				
				if(onCommentViewActionListener != null) {
					onCommentViewActionListener.onPostComment(entity);
				}
			}
		});
		
		// Won't persist.. but that's ok.
		SocializeSession session = getSocialize().getSession();
		
		if(session != null && session.getUser() != null) {
			session.getUser().setAutoPostCommentsFacebook(autoPostToFacebook);
			session.getUser().setShareLocation(shareLocation);
		}
	}
	
	public void reload() {
		content.showLoading();
		commentAdapter.reset();
		
		if(onCommentViewActionListener != null) {
			onCommentViewActionListener.onReload(this);
		}
		
		doListComments(true);
	}
	
	public CommentAdapter getCommentAdapter() {
		return commentAdapter;
	}

	public void doListComments(final boolean update) {

		startIndex = 0;
		endIndex = defaultGrabLength;

		loading = true;
		
		final List<Comment> comments = commentAdapter.getComments();

		if(update || comments == null || comments.size() == 0) {
			getSocialize().listCommentsByEntity(entity.getKey(), 
					startIndex,
					endIndex,
					new CommentListListener() {

				@Override
				public void onError(SocializeException error) {
					showError(getContext(), error);
					content.showList();
					
					if(dialog != null) {
						dialog.dismiss();
					}

					loading = false;
					
					if(onCommentViewActionListener != null) {
						onCommentViewActionListener.onError(error);
					}					
				}

				@Override
				public void onList(ListResult<Comment> entities) {
					
					if(entities != null) {
						int totalCount = entities.getTotalCount();
						header.setText(totalCount + " Comments");
						commentAdapter.setComments(entities.getItems());
						commentAdapter.setTotalCount(totalCount);

						if(totalCount <= endIndex) {
							commentAdapter.setLast(true);
						}

						if(update || comments == null) {
							commentAdapter.notifyDataSetChanged();
							content.scrollToTop();
						}
					}
					
					content.showList();

					loading = false;
					
					if(onCommentViewActionListener != null && entities != null) {
						onCommentViewActionListener.onCommentList(CommentListView.this, entities.getItems(), startIndex, endIndex);
					}
					
					if(dialog != null)  dialog.dismiss();
				}
			});
		}
		else {
			content.showList();
			header.setText(commentAdapter.getTotalCount() + " Comments");
			commentAdapter.notifyDataSetChanged();
			if(dialog != null) {
				dialog.dismiss();
			}

			loading = false;
			
			if(onCommentViewActionListener != null) {
				onCommentViewActionListener.onCommentList(CommentListView.this, comments, startIndex, endIndex);
			}			
		}
	}
	
	// SO we can mock
	protected Comment newComment() {
		return new Comment();
	}

	protected void doNotificationStatusSave() {
		
		final ProgressDialog dialog = progressDialogFactory.show(getContext(), "Notifications", "Please wait...");
		
		if(notifyBox.isChecked()) {
			getSocialize().subscribe(getContext(), entity, NotificationType.NEW_COMMENTS, new SubscriptionAddListener() {
				@Override
				public void onError(SocializeException error) {
					if(dialog != null) dialog.dismiss();
					showError(getContext(), error);
					notifyBox.setChecked(false);
				}
				
				@Override
				public void onCreate(Subscription entity) {
					if(commentEntryPage != null) {
						commentEntryPage.getCommentEntryView().setNotifySubscribeState(true);
					}
					if(dialog != null) dialog.dismiss();
					alertDialogFactory.show(getContext(), "Subscribe Successful", "We will notify you when someone posts a comment to this discussion.");
				}
			});
		}
		else {
			getSocialize().unsubscribe(getContext(), entity, NotificationType.NEW_COMMENTS, new SubscriptionAddListener() {
				@Override
				public void onError(SocializeException error) {
					if(dialog != null) dialog.dismiss();
					showError(getContext(), error);
					notifyBox.setChecked(true);
				}
				
				@Override
				public void onCreate(Subscription entity) {
					if(commentEntryPage != null) {
						commentEntryPage.getCommentEntryView().setNotifySubscribeState(false);
					}
					if(dialog != null) dialog.dismiss();
					alertDialogFactory.show(getContext(), "Unsubscribe Successful", "You will no longer receive notifications for updates to this discussion.");
				}
			});
		}		
	}
	
	protected void doNotificationStatusLoad() {
		if(notifyBox != null) {
			notifyBox.showLoading();
			
			// Now load the subscription status for the user
			getSocialize().getSubscription(entity, new SubscriptionGetListener() {
				
				@Override
				public void onGet(Subscription subscription) {
					
					if(subscription == null) {
						notifyBox.setChecked(false);
						
						if(commentEntryPage != null) {
							commentEntryPage.getCommentEntryView().setNotifySubscribeState(true); // Default to true
						}
					}
					else {
						notifyBox.setChecked(subscription.isSubscribed());
						
						if(commentEntryPage != null) {
							commentEntryPage.getCommentEntryView().setNotifySubscribeState(subscription.isSubscribed());
						}
					}
					
					notifyBox.hideLoading();
				}
				
				@Override
				public void onError(SocializeException error) {
					notifyBox.setChecked(false);
					
					if(logger != null) {
						logger.error("Error retrieving subscription info", error);
					}
					else {
						error.printStackTrace();
					}
					
					if(commentEntryPage != null) {
						commentEntryPage.getCommentEntryView().setNotifySubscribeState(true); // Default to true
					}
					
					notifyBox.hideLoading();
				}
			});
		}		
	}
	
	protected void getNextSet() {
		
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("getNextSet called on CommentListView");
		}
		
		loading = true; // Prevent continuous load

		startIndex+=defaultGrabLength;
		endIndex+=defaultGrabLength;

		if(endIndex > commentAdapter.getTotalCount()) {
			endIndex = commentAdapter.getTotalCount();

			if(startIndex >= endIndex) {
				commentAdapter.setLast(true);
				commentAdapter.notifyDataSetChanged();
				loading = false;
				return;
			}
		}

		getSocialize().listCommentsByEntity(entity.getKey(), 
				startIndex,
				endIndex,
				new CommentListListener() {

			@Override
			public void onError(SocializeException error) {

				// Don't show loading anymore
				if(logger != null) {
					logger.error("Error retrieving comments", error);
				}
				else {
					error.printStackTrace();
				}

				loading = false;
				
				if(onCommentViewActionListener != null) {
					onCommentViewActionListener.onError(error);
				}				
			}

			@Override
			public void onList(ListResult<Comment> entities) {
				List<Comment> comments = commentAdapter.getComments();
				comments.addAll(entities.getItems());
				commentAdapter.setComments(comments);
				commentAdapter.notifyDataSetChanged();
				loading = false;
			}
		});
	}
	
	@Override
	public void onViewLoad() {
		super.onViewLoad();
		if(onCommentViewActionListener != null) {
			onCommentViewActionListener.onCreate(this);
		}		
	}

	@Override
	public void onViewRendered(int width, int height) {
		if(sliderFactory != null && slider == null) {
			slider = sliderFactory.wrap(getSliderAnchor(), ZOrder.FRONT, height);
		}
		
		if(commentEntryPage != null) {
			slider.loadItem(commentEntryPage);
			
			field.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(slider != null) {
						slider.showSliderItem(commentEntryPage);
					}
				}
			});		
		}
		else if(field != null) {
			field.setVisibility(GONE);
		}

		if(getSocialize().isAuthenticated()) {
			doListComments(false);
			doNotificationStatusLoad();
		}
		else {
			SocializeException e = new SocializeException("Socialize not authenticated");
			
			showError(getContext(),e);
			
			if(onCommentViewActionListener != null) {
				onCommentViewActionListener.onError(e);
			}
			
			if(content != null) {
				content.showList();
			}
		}	
		
		if(onCommentViewActionListener != null) {
			onCommentViewActionListener.onRender(this);
		}		
	}

	public void setCommentAdapter(CommentAdapter commentAdapter) {
		this.commentAdapter = commentAdapter;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setProgressDialogFactory(DialogFactory<ProgressDialog> progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}

	public void setAlertDialogFactory(DialogFactory<AlertDialog> alertDialogFactory) {
		this.alertDialogFactory = alertDialogFactory;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setDefaultGrabLength(int defaultGrabLength) {
		this.defaultGrabLength = defaultGrabLength;
	}

	public void setCommentHeaderFactory(IBeanFactory<SocializeHeader> commentHeaderFactory) {
		this.commentHeaderFactory = commentHeaderFactory;
	}

	public void setCommentEditFieldFactory(IBeanFactory<CommentEditField> commentEditFieldFactory) {
		this.commentEditFieldFactory = commentEditFieldFactory;
	}

	public void setCommentContentViewFactory(IBeanFactory<LoadingListView> commentContentViewFactory) {
		this.commentContentViewFactory = commentContentViewFactory;
	}

	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}

	public void setNotificationEnabledOptionFactory(IBeanFactory<CustomCheckbox> notificationEnabledOptionFactory) {
		this.notificationEnabledOptionFactory = notificationEnabledOptionFactory;
	}

	@Deprecated
	public void setEntityKey(String entityKey) {
		if(entity == null) entity = new Entity();
		entity.setKey(entityKey);
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public boolean isLoading() {
		return loading;
	}
	
	protected void setLoading(boolean loading) {
		this.loading = loading;
	}

	protected void setField(View field) {
		this.field = field;
	}

	protected void setHeader(SocializeHeader header) {
		this.header = header;
	}

	protected void setContent(LoadingListView content) {
		this.content = content;
	}

	protected void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	protected void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}
	
	public void setCommentEntryFactory(IBeanFactory<CommentEntrySliderItem> commentEntryFactory) {
		this.commentEntryFactory = commentEntryFactory;
	}

	public int getTotalCount() {
		return commentAdapter.getTotalCount();
	}

	public void setAuthRequestDialogFactory(IBeanFactory<AuthRequestDialogFactory> authRequestDialogFactory) {
		this.authRequestDialogFactory = authRequestDialogFactory;
	}
	
	@Deprecated
	public void setUseLink(boolean useLink) {}

	@Deprecated
	public void setEntityName(String entityName) {
		if(entity == null) entity = new Entity();
		entity.setName(entityName);
	}
	
	/**
	 * Called when the current logged in user updates their profile.
	 */
	public void onProfileUpdate() {
		commentAdapter.notifyDataSetChanged();
		if(slider != null) {
			slider.clearContent();
		}
		
		User user = Socialize.getSocialize().getSession().getUser();
		
		if(notifyBox != null) {
			if(user.isNotificationsEnabled()) {
				notifyBox.setVisibility(View.VISIBLE);
			}
			else {
				notifyBox.setVisibility(View.GONE);
			}
		}
	}
	
	// So we can mock
	protected ShareOptions newShareOptions() {
		return new ShareOptions();
	}
	
	protected RelativeLayout getLayoutAnchor() {
		return layoutAnchor;
	}

	protected ViewGroup getSliderAnchor() {
		return sliderAnchor;
	}

	public void setSliderFactory(ActionBarSliderFactory<ActionBarSliderView> sliderFactory) {
		this.sliderFactory = sliderFactory;
	}

	public void setOnCommentViewActionListener(OnCommentViewActionListener onCommentViewActionListener) {
		this.onCommentViewActionListener = onCommentViewActionListener;
	}
	
	public ActionBarSliderView getCommentEntryViewSlider() {
		return slider;
	}
}
