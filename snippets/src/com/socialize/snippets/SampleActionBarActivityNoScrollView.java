package com.socialize.snippets;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.ui.actionbar.ActionBarOptions;

/**
 * Sample snippets from documentation, just here for reference.
 * @author jasonpolites
 */
public class SampleActionBarActivityNoScrollView extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Your entity key. May be passed as a Bundle parameter to your activity
        String entityKey = "http://www.getsocialize.com";

        // Create an entity object including a name
        // The Entity object is Serializable, so you could also store the whole object in the Intent
        Entity entity = Entity.newInstance(entityKey, "Socialize");
        
        // Add more options
        ActionBarOptions options = new ActionBarOptions();
        options.setAddScrollView(false); // Disable scroll view        

		// Wrap your existing view with the action bar.
		// your_layout refers to the resource ID of your current layout.
		View actionBarWrapped = Socialize.getSocializeUI().showActionBar(this, R.layout.your_layout, entity, options);

		// Now set the view for your activity to be the wrapped view.
		setContentView(actionBarWrapped);
	}
}