package org.ubicollab.android.boxsynctest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.societies.android.api.cis.SocialContract;
import org.ubicollab.android.boxsynctest.entitiy.Community;
import org.ubicollab.android.boxsynctest.entitiy.CommunityActivity;
import org.ubicollab.android.boxsynctest.entitiy.Entity;
import org.ubicollab.android.boxsynctest.entitiy.EntityComparator;
import org.ubicollab.android.boxsynctest.entitiy.Person;
import org.ubicollab.android.boxsynctest.entitiy.Service;
import org.ubicollab.android.boxsynctest.entitiy.Sharing;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ShowCommunityActivity extends Activity {
	
	public static final String TAG = "ShowCommunityActivity";
	
	public static final String EXTRA_COMMUNITY = "extra_community";
	private static final int EDIT_REQUEST_CODE = 100;
	
	private Community mCommunity;
	private Person mOwner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_community);
		
		Spinner serviceSpinner = (Spinner) findViewById(R.id.service_list);
		
		Intent intent = getIntent();
		String serializedCommunity = intent.getStringExtra(EXTRA_COMMUNITY);
		
		if (serializedCommunity != null) {
			mCommunity = Community.deserialize(serializedCommunity, Community.class);
			
			try {
				mOwner = Person.getEntity(Person.class, mCommunity.getOwnerId(), getContentResolver());
				
				List<Service> services = Service.getAllServices(getContentResolver());
				ArrayAdapter<Service> serviceAdapter = new ArrayAdapter<Service>(this, android.R.layout.simple_spinner_item, services);
				
				serviceSpinner.setAdapter(serviceAdapter);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
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
		if (mCommunity != null) {
			try {
				List<Entity> feed = new ArrayList<Entity>();
				List<CommunityActivity> activities = CommunityActivity
						.getCommunityFeed(mCommunity.getId(),
								getContentResolver());
				List<Sharing> sharings = Sharing.getSharingOfCommunity(
						mCommunity.getId(), getContentResolver());
				feed.addAll(activities);
				feed.addAll(sharings);
				Collections.sort(feed, new EntityComparator());

				ArrayAdapter<Entity> adapter = new ArrayAdapter<Entity>(this,
						android.R.layout.simple_list_item_1, feed);

				((ListView) findViewById(R.id.community_activity_list_view))
						.setAdapter(adapter);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
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
						Log.e(TAG, e.getMessage(), e);
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
	
	public void shareService(View view) {
		if (mCommunity != null) {
			Service service = (Service) ((Spinner) findViewById(R.id.service_list)).getSelectedItem();
			
			if (service == null) {
				Toast.makeText(this, "No service selected", Toast.LENGTH_SHORT).show();
			} else {
				Sharing sharing = new Sharing();
				sharing.setAccountName(Globals.ME_ENTRY.getAccountName());
				sharing.setAccountType(Constants.ACCOUNT_TYPE);
				sharing.setGlobalId(SocialContract.GLOBAL_ID_PENDING);
				sharing.setDirty(1);
				sharing.setType("monitor");
				sharing.setOwnerId(Globals.ME_ENTRY.getPersonId());
				sharing.setServiceId(service.getId());
				sharing.setCommunityId(mCommunity.getId());
				
				sharing.insert(getContentResolver());
				
				updateFeed();
			}
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
