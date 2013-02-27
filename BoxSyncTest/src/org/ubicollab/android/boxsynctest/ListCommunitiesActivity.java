package org.ubicollab.android.boxsynctest;

import java.util.List;

import org.ubicollab.android.boxsynctest.entitiy.Community;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListCommunitiesActivity extends Activity {
	
	private ListView mCommunityList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_communities);
		
		mCommunityList = (ListView) findViewById(R.id.community_list_view);
		
		populate();
		
		final Context context = this;
		mCommunityList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				Community community = (Community) mCommunityList.getItemAtPosition(pos);
				
				Intent intent = new Intent(context, ShowCommunityActivity.class);
				intent.putExtra(ShowCommunityActivity.EXTRA_COMMUNITY, community.serialize());
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		populate();
	}

	private void populate() {
		try {
			List<Community> communities = Community.getAllCommunities(getContentResolver());
			ArrayAdapter<Community> adapter = new ArrayAdapter<Community>(this, android.R.layout.simple_list_item_1, communities);
			
			mCommunityList.setAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_communities, menu);
		return true;
	}

	public void back(View view) {
		finish();
	}
}
