package org.ubicollab.android.boxsynctest;

import java.util.List;

import org.societies.android.api.cis.SocialContract;
import org.ubicollab.android.boxsynctest.entitiy.Community;
import org.ubicollab.android.boxsynctest.entitiy.CommunityActivity;
import org.ubicollab.android.boxsynctest.entitiy.Person;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowCommunityActivity extends Activity {
	
	public static final String EXTRA_COMMUNITY = "extra_community";
	private static final int EDIT_REQUEST_CODE = 100;
	
	private Community mCommunity;
	private Person mOwner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_community);
		
		Intent intent = getIntent();
		String serializedCommunity = intent.getStringExtra(EXTRA_COMMUNITY);
		
		if (serializedCommunity != null) {
			mCommunity = Community.deserialize(serializedCommunity, Community.class);
			
			try {
				mOwner = Person.getEntity(Person.class, mCommunity.getOwnerId(), getContentResolver());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		populate();
		
		getContentResolver().registerContentObserver(
				SocialContract.CommunityActivity.CONTENT_URI,
				true,
				new FeedObserver(new Handler(), this));
	}

	private void populate() {
		if (mCommunity == null || mOwner == null) {
			Toast.makeText(this, "No community or no owner.", Toast.LENGTH_LONG).show();
		} else {
			((TextView) findViewById(R.id.community_name_label)).setText(mCommunity.getName());
			((TextView) findViewById(R.id.community_type_label)).setText(mCommunity.getType());
			((TextView) findViewById(R.id.community_owner_label)).setText(mOwner.getName());
			((TextView) findViewById(R.id.community_description_label)).setText(mCommunity.getDescription());
			
			updateFeed();
		}
	}
	
	private void updateFeed() {
		try {
			List<CommunityActivity> feed = CommunityActivity.getCommunityFeed(mCommunity.getId(), getContentResolver());
			ArrayAdapter<CommunityActivity> adapter = new ArrayAdapter<CommunityActivity>(this, android.R.layout.simple_list_item_1, feed);
			
			((ListView) findViewById(R.id.community_activity_list_view)).setAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_community, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == EDIT_REQUEST_CODE) {
			if (resultCode == CreateCommunityActivity.RESULT_CODE_EDITED) {
				String serializedCommunity = data
						.getStringExtra(CreateCommunityActivity.EXTRA_COMMUNITY);
				if (serializedCommunity != null) {
					mCommunity = Community.deserialize(serializedCommunity, Community.class);

					try {
						mOwner = Person.getEntity(Person.class,
								mCommunity.getOwnerId(), getContentResolver());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				populate();
			} else if (resultCode == CreateCommunityActivity.RESULT_CODE_DELETED) {
				finish();
			}
		}
	}

	public void back(View view) {
		finish();
	}
	
	public void showMembers(View view) {
		if (mCommunity != null) {
			Intent intent = new Intent(this, ShowMembersActivity.class);
			intent.putExtra(ShowMembersActivity.EXTRA_COMMUNITY_ID, mCommunity.getId());
			startActivity(intent);
		}
	}
	
	public void edit(View view) {
		if (mCommunity != null) {
			Intent intent = new Intent(this, CreateCommunityActivity.class);
			intent.putExtra(CreateCommunityActivity.EXTRA_COMMUNITY, mCommunity.serialize());
			startActivityForResult(intent, EDIT_REQUEST_CODE);
		}
	}
	
	public void postMessage(View view) {
		if (mCommunity != null) {
			String message = ((EditText) findViewById(R.id.community_message_field)).getText().toString();
			
			CommunityActivity activity = new CommunityActivity();
			activity.setAccountName(Globals.ME_ENTRY.getAccountName());
			activity.setAccountType(Constants.ACCOUNT_TYPE);
			activity.setGlobalId(SocialContract.GLOBAL_ID_PENDING);
			activity.setActor(Globals.ME_ENTRY.getName());
			activity.setDirty(1);
			activity.setFeedOwnerId(mCommunity.getId());
			activity.setObject("object");
			activity.setTarget("target");
			activity.setVerb(message);
			
			activity.insert(getContentResolver());
			
			clearMessageField();
			updateFeed();
		}
	}
	
	private void clearMessageField() {
		((EditText) findViewById(R.id.community_message_field)).setText(new String());
	}
	
	private class FeedObserver extends ContentObserver {
		
		private ShowCommunityActivity mActivity;

		public FeedObserver(Handler handler, ShowCommunityActivity activity) {
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
			
			mActivity.updateFeed();
		}
	}
}
