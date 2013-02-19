package org.ubicollab.android.boxsynctest;

import org.ubicollab.android.boxsynctest.entitiy.Community;
import org.ubicollab.android.boxsynctest.entitiy.Membership;
import org.ubicollab.android.boxsynctest.entitiy.Person;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class CreateCommunityActivity extends Activity {
	
	private Community mCommunity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_community);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_community, menu);
		return true;
	}
	
	private void populateFields(Community community) {
		mCommunity = community;
	}
	
	public void addCommunity(View view) {
		if (mCommunity == null)
			mCommunity = new Community();
		
		mCommunity.setAccountName(Globals.ACCOUNT_NAME);
		mCommunity.setAccountType(Constants.ACCOUNT_TYPE);
		mCommunity.setDirty(1);
		mCommunity.setName(((EditText) findViewById(R.id.community_name_field)).getText().toString());
		mCommunity.setType(((EditText) findViewById(R.id.community_type_field)).getText().toString());
		mCommunity.setDescription(((EditText) findViewById(R.id.community_description_field)).getText().toString());
		
		Spinner ownerSpinner = (Spinner) findViewById(R.id.community_owner_list);
		Person owner = (Person) ownerSpinner.getSelectedItem();
		mCommunity.setOwnerId(owner.getId());
		
		Uri communityUri = mCommunity.insert(getContentResolver());
		long communityId = Long.parseLong(communityUri.getLastPathSegment());
		
		if (mCommunity == null)
			addMembership(communityId, owner);
	}
	
	private void addMembership(long communityId, Person member) {
		Membership membership = new Membership();
		membership.setAccountName(Globals.ACCOUNT_NAME);
		membership.setAccountType(Constants.ACCOUNT_TYPE);
		membership.setDirty(1);
		membership.setCommunityId(communityId);
		membership.setMemberId(member.getId());
		membership.setType("owner");
		
		membership.insert(getContentResolver());
	}
	
	public void cancel(View view) {
		finish();
	}
}
