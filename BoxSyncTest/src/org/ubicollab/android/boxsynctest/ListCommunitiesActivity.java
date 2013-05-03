package org.ubicollab.android.boxsynctest;

import java.util.List;

import org.societies.android.api.cis.SocialContract;
import org.ubicollab.android.boxsynctest.entitiy.Community;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListCommunitiesActivity extends Activity {
	
	public static final String TAG = "ListCommunitiesActivity";
	
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
		
		getContentResolver().registerContentObserver(
				SocialContract.Communities.CONTENT_URI,
				true,
				new CommunityObserver(new Handler(), this));
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
			Log.e(TAG, e.getMessage(), e);
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
	
	private class CommunityObserver extends ContentObserver {

		private ListCommunitiesActivity mActivity;
		
		public CommunityObserver(Handler handler, ListCommunitiesActivity activity) {
			super(handler);
			
			mActivity = activity;
		}
		
		@Override
		public boolean deliverSelfNotifications() {
			return false;
		}
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			
			mActivity.populate();
		}
	}
}
