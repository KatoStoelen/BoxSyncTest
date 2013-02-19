package org.ubicollab.android.boxsynctest;

import org.ubicollab.android.boxsynctest.entitiy.Me;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

/**
 * Main activity.
 * 
 * @author Kato
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try {
			Globals.ACCOUNT_NAME = Me.getAccountName(
					Constants.ACCOUNT_TYPE, getContentResolver());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
}
