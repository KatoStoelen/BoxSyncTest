package org.ubicollab.android.boxsynctest;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ShowCommunityActivity extends Activity {
	
	public static final String EXTRA_COMMUNITY = "extra_community";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_community);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_community, menu);
		return true;
	}

}
