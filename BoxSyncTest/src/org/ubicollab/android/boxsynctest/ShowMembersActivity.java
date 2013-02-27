package org.ubicollab.android.boxsynctest;

import java.util.List;

import org.ubicollab.android.boxsynctest.entitiy.Entity;
import org.ubicollab.android.boxsynctest.entitiy.Membership;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ShowMembersActivity extends Activity {
	
	public static final String EXTRA_COMMUNITY_ID = "extra_community_id";
	
	private ListView mMembersList;
	private long mCommunityId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_members);
		
		mMembersList = (ListView) findViewById(R.id.members_list_view);
		
		Intent intent = getIntent();
		mCommunityId = intent.getLongExtra(EXTRA_COMMUNITY_ID, Entity.ENTITY_DEFAULT_ID);
		
		Log.i("TETEUTHETUE", "Community ID: " + mCommunityId);
		
		populate();
	}
	
	private void populate() {
		if (mCommunityId != Entity.ENTITY_DEFAULT_ID) {
			try {
				List<Membership> memberships = Membership.getCommunityMembers(mCommunityId, getContentResolver());
				ArrayAdapter<Membership> adapter = new ArrayAdapter<Membership>(this, android.R.layout.simple_list_item_1, memberships);
				
				mMembersList.setAdapter(adapter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_members, menu);
		return true;
	}

	public void back(View view) {
		finish();
	}
}
