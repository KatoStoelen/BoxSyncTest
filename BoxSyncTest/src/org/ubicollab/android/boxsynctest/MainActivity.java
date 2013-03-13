package org.ubicollab.android.boxsynctest;

import java.util.List;

import org.societies.android.api.cis.SocialContract;
import org.ubicollab.android.boxsynctest.entitiy.Me;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;

/**
 * Main activity.
 * 
 * @author Kato
 */
public class MainActivity extends Activity {
	
	public static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		populateDatabase();
		fetchMeRecord();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		fetchMeRecord();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void createCommunity(View view) {
		startActivity(CreateCommunityActivity.class);
	}
	
	public void listCommunities(View view) {
		startActivity(ListCommunitiesActivity.class);
	}
	
	public void addPerson(View view) {
		startActivity(AddPersonActivity.class);
	}
	
	public void listPeople(View view) {
		startActivity(ListPeopleActivity.class);
	}
	
	private void startActivity(Class<?> clazz) {
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
	}
	
	private void fetchMeRecord() {
		try {
			List<Me> meEntries = Me.getMe(Constants.ACCOUNT_TYPE, getContentResolver());
			
			if (meEntries.size() > 0)
				Globals.ME_ENTRY = meEntries.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void populateDatabase() {
		SharedPreferences preferences = getSharedPreferences("test", Context.MODE_PRIVATE);
		boolean populated = preferences.getBoolean("populated", false);
		
		if (!populated) {
			Log.i(TAG, "Empty database. Populating...");
			
			ContentResolver resolver = getContentResolver();
			
			ContentValues initialValues = new ContentValues();
			initialValues.put(SocialContract.People.GLOBAL_ID , "kato.stoelen@gmail.com");
			initialValues.put(SocialContract.People.NAME , "Kato Stølen");
			initialValues.put(SocialContract.People.EMAIL , "kato.stoelen@gmail.com");
			initialValues.put(SocialContract.People.USER_NAME , "kato.stoelen@gmail.com");
			initialValues.put(SocialContract.People.DESCRIPTION , "Kato@BOX");
			resolver.insert(SocialContract.People.CONTENT_URI, initialValues);
			
			initialValues.clear();
			
			initialValues.put(SocialContract.People.GLOBAL_ID , "ubishare.test@gmail.com");
			initialValues.put(SocialContract.People.NAME , "UbiShare Test");
			initialValues.put(SocialContract.People.EMAIL , "ubishare.test@gmail.com");
			initialValues.put(SocialContract.People.USER_NAME , "ubishare.test@gmail.com");
			initialValues.put(SocialContract.People.DESCRIPTION , "UbiShare@BOX");
			resolver.insert(SocialContract.People.CONTENT_URI, initialValues);
			
			initialValues.clear();

			initialValues.put(SocialContract.People.GLOBAL_ID , "babak@farshchian.com");
			initialValues.put(SocialContract.People.NAME , "Babak Farshchian");
			initialValues.put(SocialContract.People.EMAIL , "babak@farshchian.com");
			initialValues.put(SocialContract.People.USER_NAME , "babak@farshchian.com");
			initialValues.put(SocialContract.People.DESCRIPTION , "Babak@BOX");
			resolver.insert(SocialContract.People.CONTENT_URI, initialValues);
			
			initialValues.clear();
			
			initialValues.put(SocialContract.Services.GLOBAL_ID , "s1xyz.societies.org");
			initialValues.put(SocialContract.Services.TYPE , "Disaster");
			initialValues.put(SocialContract.Services.NAME , "iJacket");
			initialValues.put(SocialContract.Services._ID_OWNER, 1);
			initialValues.put(SocialContract.Services.DESCRIPTION , "A service to communicate with rescuer jacket");
			initialValues.put(SocialContract.Services.AVAILABLE, "false");
			initialValues.put(SocialContract.Services.DEPENDENCY, "iJacketClient");
			initialValues.put(SocialContract.Services.CONFIG, "jacket version");
			initialValues.put(SocialContract.Services.URL, "http://files.ubicollab.net/apk/iJacket.apk");
			resolver.insert(SocialContract.Services.CONTENT_URI, initialValues);

			initialValues.clear();
			
			initialValues.put(SocialContract.Services.GLOBAL_ID , "s2xyz.societies.org");
			initialValues.put(SocialContract.Services.TYPE , "Disaster");
			initialValues.put(SocialContract.Services.NAME , "iJacketClient");
			initialValues.put(SocialContract.Services._ID_OWNER, 1);
			initialValues.put(SocialContract.Services.DESCRIPTION , "A service to communicate with iJacket");
			initialValues.put(SocialContract.Services.AVAILABLE, "false");
			initialValues.put(SocialContract.Services.DEPENDENCY, "iJacket");
			initialValues.put(SocialContract.Services.CONFIG, "iJacket version");
			initialValues.put(SocialContract.Services.URL, "http://files.ubicollab.net/apk/iJacketClient.apk");
			resolver.insert(SocialContract.Services.CONTENT_URI, initialValues);
			
			preferences.edit().putBoolean("populated", true).commit();
			
			Log.i(TAG, "DONE!");
		}
	}
}
