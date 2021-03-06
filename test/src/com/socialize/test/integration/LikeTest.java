package com.socialize.test.integration;
import android.widget.TextView;

public class LikeTest extends SocializeRobotiumTest {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		authenticateSocialize();
		
		robotium.clickOnButton("Like");
		robotium.waitForActivity("LikeActivity", DEFAULT_TIMEOUT_MILLISECONDS);
	}
	
	public void testListLikesSameEntity() {
		
		robotium.enterText(0, DEFAULT_ENTITY_URL);
		
		TextView txt = (TextView) robotium.getCurrentActivity().findViewById(com.socialize.sample.R.id.txtMisc);
		
		assertNotNull(txt);
		
		robotium.clickOnButton("Add Like");
		
		waitForSuccess();
		
		robotium.clickOnButton("List Likes");
		
		waitForSuccess();
		
		String value = txt.getText().toString();
		int countBefore = Integer.parseInt(value);
		
		robotium.clickOnButton("Add Like");
		
		waitForSuccess();
		
		robotium.clickOnButton("List Likes");
		
		waitForSuccess();

		String valueAfter = txt.getText().toString();
		int countAfter = Integer.parseInt(valueAfter);
		
		assertTrue(countAfter == countBefore);
	}
	
	public void testListLikesDiffEntity() {
		
		robotium.enterText(0, DEFAULT_ENTITY_URL);
		
		TextView txt = (TextView) robotium.getCurrentActivity().findViewById(com.socialize.sample.R.id.txtMisc);
		
		assertNotNull(txt);
		
		robotium.clickOnButton("Add Like");
		
		waitForSuccess();
		
		robotium.clickOnButton("List Likes");
		
		waitForSuccess();
		
		String value = txt.getText().toString();
		int countBefore = Integer.parseInt(value);
		
		robotium.enterText(0, DEFAULT_ENTITY_URL + "/" + Math.random());
		
		robotium.clickOnButton("Add Like");
		
		waitForSuccess();
		
		robotium.clickOnButton("List Likes");
		
		waitForSuccess();

		String valueAfter = txt.getText().toString();
		int countAfter = Integer.parseInt(valueAfter);
		
		assertTrue(countAfter == countBefore+1);
	}
		
	
	

	public void testCreateLike() {
		
		robotium.enterText(0, DEFAULT_ENTITY_URL);
		robotium.clickOnButton("Add Like");
		
		waitForSuccess();
		
		getLikeId();
		
		// Check the date
		TextView txt = (TextView) robotium.getView(com.socialize.sample.R.id.txtLikeDateCreated);
		String value = txt.getText().toString();
		
		assertNotNull(value);
		assertTrue(value.trim().length() > 0);
	}
	
	public void testDeleteLike() {
		// We need create first
		robotium.enterText(0, DEFAULT_ENTITY_URL);
		robotium.clickOnButton("Add Like");
		waitForSuccess();
		robotium.clickOnButton("Remove Like");
		waitForSuccess();
	}
	
	private int getLikeId() {
		
		sleep(2000); // Wait for text field to be updated
		
		TextView txt = (TextView) robotium.getCurrentActivity().findViewById(com.socialize.sample.R.id.txtLikeIdCreated);
		
		// This is ID, it should be integer
		assertNotNull(txt);
		
		String value = txt.getText().toString();
		return Integer.parseInt(value);
	}
}