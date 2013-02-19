package org.ubicollab.android.boxsynctest;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ListPeopleActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_people);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_people, menu);
		return true;
	}

}
