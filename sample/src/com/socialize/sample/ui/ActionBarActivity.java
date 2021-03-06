package com.socialize.sample.ui;

import android.app.Activity;
import android.os.Bundle;

import com.socialize.ui.SocializeUI;

@Deprecated
public abstract class ActionBarActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String entityKey = null;
		String entityName = null;
		boolean isEntityKeyUrl = true;
		
		Bundle extras = this.getIntent().getExtras();
		
		if(extras != null) {
			entityKey = extras.getString(SocializeUI.ENTITY_KEY);
			entityName = extras.getString(SocializeUI.ENTITY_NAME);
			isEntityKeyUrl = extras.getBoolean(SocializeUI.ENTITY_URL_AS_LINK);
		}
		
		onCreate(savedInstanceState, entityKey, entityName, isEntityKeyUrl);
	}

	protected abstract void onCreate(Bundle savedInstanceState, String entityKey, String entityName, boolean isEntityKeyUrl);
	
}
